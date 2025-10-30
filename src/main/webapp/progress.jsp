<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Progress Charts</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <!-- Chart.js + Zoom plugin from CDN -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-zoom@2.0.1/dist/chartjs-plugin-zoom.umd.min.js"></script>
  <style>
    body { font-family: system-ui, Arial, sans-serif; margin: 24px; }
    .controls { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
    .chart-wrap { margin: 18px 0; }
    canvas { max-width: 100%; }
    .info { color: #666; font-size: 0.9rem; }
    button { padding: 6px 10px; }
    label { font-weight: 600; }
  </style>
</head>
<body>
  <h1>Progress</h1>

  <div class="controls">
    <label>Range:</label>
    <select id="range">
      <option value="7d">7 days</option>
      <option value="30d">30 days</option>
      <option value="90d">90 days</option>
    </select>
    <button id="reloadBtn">Reload</button>
    <button id="resetZoomBtn">Reset Zoom</button>
    <span class="info">Tip: Drag to zoom, scroll/pinch to zoom, double-click to reset axis.</span>
  </div>

  <div class="chart-wrap">
    <h3>Weight Trend (kg)</h3>
    <canvas id="weightChart" height="120"></canvas>
  </div>

  <div class="chart-wrap">
    <h3>Daily Calories vs Target</h3>
    <canvas id="caloriesChart" height="120"></canvas>
  </div>

  <div class="chart-wrap">
    <h3>Workout Frequency & Duration</h3>
    <canvas id="workoutsChart" height="120"></canvas>
  </div>

  <script src="js/progress.js"></script>
</body>
</html>
