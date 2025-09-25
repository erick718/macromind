export function requireFields(obj, fields = []) {
  const missing = fields.filter(f => obj[f] === undefined || obj[f] === null || obj[f] === '');
  if (missing.length) {
    const err = new Error(`Missing field(s): ${missing.join(', ')}`);
    err.status = 400;
    throw err;
  }
}

export function clampAvailability(n) {
  const v = Number(n);
  if (!Number.isInteger(v) || v < 1 || v > 7) {
    const err = new Error('availabilityPerWeek must be an integer 1–7');
    err.status = 400;
    throw err;
  }
  return v;
}

export const GOALS = ['Lose Weight', 'Build Muscle', 'Maintain'];
export function validateGoal(goal) {
  if (!GOALS.includes(goal)) {
    const err = new Error('goal must be one of: ' + GOALS.join(', '));
    err.status = 400;
    throw err;
  }
}
