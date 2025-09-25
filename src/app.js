import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import './config/db.js';

import profileRoutes from './routes/profileRoutes.js';
import planRoutes from './routes/planRoutes.js';

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

app.get('/', (_req, res) => res.json({ ok: true, service: 'fitness-backend', ts: new Date().toISOString() }));

app.use('/api/profile', profileRoutes);
app.use('/api/plan', planRoutes);

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(err.status || 500).json({ message: err.message || 'Server error' });
});

export default app;

