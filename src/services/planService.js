import Plan from '../models/Plan.js';

/**
 * Naive deterministic plan generator for Sprint 1 demo.
 * In Sprint 2 you’ll replace with real rules/data.
 */
export async function generateWeeklyPlan(userId, profile, weekStartISO) {
  const baseCal = profile.goal === 'Build Muscle' ? 2300 :
                  profile.goal === 'Lose Weight' ? 1800 : 2000;

  const meals = [];
  const workouts = [];
  let totals = { cal: 0, protein: 0, carbs: 0, fat: 0 };

  const sampleMeals = [
    { name: 'Oatmeal & Berries', cal: 400, p: 15, c: 60, f: 10 },
    { name: 'Grilled Chicken Salad', cal: 500, p: 35, c: 35, f: 18 },
    { name: 'Tofu Stir Fry', cal: 550, p: 30, c: 55, f: 20 },
    { name: 'Greek Yogurt', cal: 180, p: 18, c: 12, f: 5 }
  ];

  for (let d = 1; d <= 7; d++) {
    // 3 meals + snack
    const dayMeals = [
      { type: 'breakfast', ...sampleMeals[0] },
      { type: 'lunch',     ...sampleMeals[1] },
      { type: 'dinner',    ...sampleMeals[2] },
      { type: 'snack',     ...sampleMeals[3] }
    ].map(m => ({ day: d, type: m.type, name: m.name, calories: m.cal, protein: m.p, carbs: m.c, fat: m.f }));

    meals.push(...dayMeals);

    // simple workout suggestion on availability days
    if (d <= profile.availabilityPerWeek) {
      workouts.push({ day: d, activity: 'Run/Walk', minutes: 30, caloriesBurned: 250 });
    }
  }

  totals = {
    cal: meals.reduce((s,m)=>s+m.calories,0),
    protein: meals.reduce((s,m)=>s+m.protein,0),
    carbs: meals.reduce((s,m)=>s+m.carbs,0),
    fat: meals.reduce((s,m)=>s+m.fat,0)
  };

  const plan = await Plan.findOneAndUpdate(
    { userId, weekStart: weekStartISO },
    { userId, weekStart: weekStartISO, meals, workouts, totals },
    { new: true, upsert: true }
  );
  return plan;
}

export function currentWeekStartISO(d = new Date()) {
  const dt = new Date(Date.UTC(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate()));
  const day = dt.getUTCDay(); // 0 Sun … 6 Sat
  const mondayOffset = (day + 6) % 7;
  dt.setUTCDate(dt.getUTCDate() - mondayOffset);
  return dt.toISOString().slice(0,10);
}
