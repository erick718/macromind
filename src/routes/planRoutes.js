import { Router } from 'express';
import { requireAuth } from '../middleware/auth.js';
import { getWeeklyPlan } from '../controllers/planController.js';

const r = Router();
r.use(requireAuth);

r.get('/week', getWeeklyPlan);

export default r;
