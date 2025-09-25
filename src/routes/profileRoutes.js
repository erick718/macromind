import { Router } from 'express';
import { requireAuth } from '../middleware/auth.js';
import { createProfile, getMyProfile, updateProfile } from '../controllers/profileController.js';

const r = Router();
r.use(requireAuth);

r.post('/', createProfile);
r.get('/me', getMyProfile);
r.put('/', updateProfile);

export default r;
