(async function () {
  const rangeSel = document.getElementById('range');
  const reloadBtn = document.getElementById('reloadBtn');
  const resetZoomBtn = document.getElementById('resetZoomBtn');

  let weightChart, caloriesChart, workoutsChart;

  async function fetchProgress(range) {
    const res = await fetch(`api/progress?range=${encodeURIComponent(range)}`);
    if (!res.ok) {
      const e = await res.json().catch(() => ({}));
      throw new Error(e.message || 'Failed to load progress data');
    }
    return res.json();
  }

  async function fetchMilestones(start, end) {
    const url = new URL('api/milestones', window.location.href);
    url.searchParams.set('start', start);
    url.searchParams.set('end', end);
    const res = await fetch(url.toString());
    if (!res.ok) return { milestones: [] };
    return res.json();
  }

  function toLabels(points) {
    return points.map(p => p.date);
  }

  function datasetForWeight(points) {
    return points.map(p => p.weightKg ?? null);
  }

  function datasetForCalories(points) {
    return points.map(p => p.calories ?? null);
  }

  function datasetForWorkoutCount(points) {
    return points.map(p => p.workoutCount ?? 0);
  }

  function datasetForWorkoutMinutes(points) {
    return points.map(p => p.workoutMinutes ?? 0);
  }

  function buildMilestoneDataset(labels, milestones, forType) {
    // Represent milestones as scatter points on matching chart type
    const indicesByDate = new Map(labels.map((d, i) => [d, i]));
    const data = milestones
      .filter(m => m.type === forType)
      .map(m => ({ x: labels[indicesByDate.get(m.date)], y: undefined, title: m.title, details: m.details }));
    return data;
  }

  function destroyCharts() {
    [weightChart, caloriesChart, workoutsChart].forEach(ch => ch && ch.destroy());
  }

  function commonOptions(title) {
    return {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'nearest', intersect: false },
      plugins: {
        legend: { display: true },
        title: { display: false, text: title },
        tooltip: {
          callbacks: {
            label: (ctx) => {
              const dsLabel = ctx.dataset.label || '';
              const val = ctx.parsed.y;
              return `${dsLabel}: ${val}`;
            }
          }
        },
        zoom: {
          pan: { enabled: true, mode: 'x' },
          zoom: {
            wheel: { enabled: true },
            pinch: { enabled: true },
            drag: { enabled: true },
            mode: 'x'
          }
        }
      },
      scales: {
        x: { ticks: { autoSkip: true, maxRotation: 0 } },
        y: { beginAtZero: false }
      }
    };
  }

  function addMilestoneHover(chart, milestoneData, label) {
    // Add a scatter dataset with star points and custom tooltip
    if (!milestoneData.length) return;
    chart.data.datasets.push({
      type: 'scatter',
      label: `${label} milestones`,
      data: milestoneData.map(m => ({ x: m.x, y: chart.data.datasets[0].data.find(v => v !== null) || 0, _m: m })),
      pointStyle: 'star',
      pointRadius: 6,
      borderWidth: 0
    });
    chart.options.plugins.tooltip.callbacks.afterBody = (items) => {
      const raw = items?.[0]?.raw?._m;
      if (raw) {
        return [`Milestone: ${raw.title}`, raw.details];
      }
      return '';
    };
  }

  async function load(range) {
    destroyCharts();

    const json = await fetchProgress(range);
    const labels = toLabels(json.data);
    const milestonesJson = await fetchMilestones(json.start, json.end);
    const milestones = milestonesJson.milestones || [];

    // Weight
    {
      const ctx = document.getElementById('weightChart');
      weightChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'Weight (kg)',
            data: datasetForWeight(json.data),
            tension: 0.3,
            spanGaps: true
          }]
        },
        options: commonOptions('Weight')
      });
      addMilestoneHover(weightChart, buildMilestoneDataset(labels, milestones, 'weight'), 'Weight');
    }

    // Calories vs Target
    {
      const target = 2200; // could be dynamic from profile later
      const ctx = document.getElementById('caloriesChart');
      caloriesChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: 'Calories (daily)',
              data: datasetForCalories(json.data),
              tension: 0.3,
              spanGaps: true
            },
            {
              label: 'Target',
              data: labels.map(() => target),
              borderDash: [6, 6]
            }
          ]
        },
        options: commonOptions('Calories')
      });
      addMilestoneHover(caloriesChart, buildMilestoneDataset(labels, milestones, 'calories'), 'Calories');
    }

    // Workouts: count (bar) + minutes (line)
    {
      const ctx = document.getElementById('workoutsChart');
      workoutsChart = new Chart(ctx, {
        data: {
          labels,
          datasets: [
            {
              type: 'bar',
              label: 'Workout Count',
              data: datasetForWorkoutCount(json.data)
            },
            {
              type: 'line',
              label: 'Workout Minutes',
              data: datasetForWorkoutMinutes(json.data),
              tension: 0.3,
              yAxisID: 'y1'
            }
          ]
        },
        options: {
          ...commonOptions('Workouts'),
          scales: {
            x: { ticks: { autoSkip: true, maxRotation: 0 } },
            y: { beginAtZero: true, title: { display: true, text: 'Count' } },
            y1: { beginAtZero: true, position: 'right', title: { display: true, text: 'Minutes' } }
          }
        }
      });
      addMilestoneHover(workoutsChart, buildMilestoneDataset(labels, milestones, 'workout'), 'Workout');
    }
  }

  reloadBtn.addEventListener('click', () => load(rangeSel.value));
  resetZoomBtn.addEventListener('click', () => {
    [weightChart, caloriesChart, workoutsChart].forEach(ch => ch && ch.resetZoom());
  });

  await load(rangeSel.value);
})();
