import Profile from '../models/Profile.js';
import { requireFields, clampAvailability, validateGoal } from '../utils/validate.js';
import { generateWeeklyPlan, currentWeekStartISO } from '../services/planService.js';

export async function createProfile(req, res, next) {
  try {
    const { goal, preferences = [], availabilityPerWeek, dietaryRestrictions = [] } = req.body;
    requireFields(req.body, ['goal', 'availabilityPerWeek']);
    validateGoal(goal);
    const avail = clampAvailability(availabilityPerWeek);

    const profile = await Profile.create({
      userId: req.user.id,
      goal,
      preferences,
      availabilityPerWeek: avail,
      dietaryRestrictions
    });

    // generate plan asynchronously (non-blocking for Sprint 1)
    setImmediate(async () => {
      try {
        await generateWeeklyPlan(req.user.id, profile, currentWeekStartISO());
      } catch (e) { console.error('plan generation error:', e.message); }
    });

    return res.status(201).json({ message: 'Profile completed.', profile });
  } catch (e) { next(e); }
}

export async function getMyProfile(req, res, next) {
  try {
    const profile = await Profile.findOne({ userId: req.user.id });
    if (!profile) return res.status(404).json({ message: 'Profile not found' });
    res.json(profile);
  } catch (e) { next(e); }
}

export async function updateProfile(req, res, next) {
  try {
    const allowed = ['goal','preferences','availabilityPerWeek','dietaryRestrictions'];
    const data = {};
    for (const k of allowed) if (k in req.body) data[k] = req.body[k];
    if (data.goal) validateGoal(data.goal);
    if (data.availabilityPerWeek !== undefined) data.availabilityPerWeek = clampAvailability(data.availabilityPerWeek);

    const profile = await Profile.findOneAndUpdate(
      { userId: req.user.id },
      data,
      { new: true }
    );
    if (!profile) return res.status(404).json({ message: 'Profile not found' });

    setImmediate(async () => {
      try {
        await generateWeeklyPlan(req.user.id, profile, currentWeekStartISO());
      } catch (e) { console.error('plan generation error:', e.message); }
    });

    res.json({ message: 'Profile updated. A new personalized plan is ready.', profile });
  } catch (e) { next(e); }
}
