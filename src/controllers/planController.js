import Plan from '../models/Plan.js';
import { currentWeekStartISO, generateWeeklyPlan } from '../services/planService.js';
import Profile from '../models/Profile.js';

export async function getWeeklyPlan(req, res, next) {
  try {
    const weekStart = (req.query.start || currentWeekStartISO()).slice(0,10);
    let plan = await Plan.findOne({ userId: req.user.id, weekStart });
    if (!plan) {
      const profile = await Profile.findOne({ userId: req.user.id });
      if (!profile) return res.status(404).json({ message: 'Profile not found' });
      plan = await generateWeeklyPlan(req.user.id, profile, weekStart);
    }
    res.json(plan);
  } catch (e) { next(e); }
}
