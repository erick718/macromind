import mongoose from 'mongoose';

const uri = process.env.MONGO_URI;
if (!uri) {
  console.error('Missing MONGO_URI in env');
  process.exit(1);
}
mongoose
  .connect(uri, { autoIndex: true })
  .then(() => console.log('MongoDB connected'))
  .catch((e) => {
    console.error('Mongo connection error:', e.message);
    process.exit(1);
  });
