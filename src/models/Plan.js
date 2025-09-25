import mongoose from 'mongoose';

const MealSchema = new mongoose.Schema(
  {
    day: { type: Number, min: 1, max: 7, required: true },
    type: { type: String, enum: ['breakfast', 'lunch', 'dinner', 'snack'], required: true },
    name: String,
    calories: Number,
    protein: Number,
    carbs: Number,
    fat: Number
  },
  { _id: false }
);

const WorkoutSchema = new mongoose.Schema(
  {
    day: { type: Number, min: 1, max: 7, required: true },
    activity: String,
    minutes: Number,
    caloriesBurned: Number
  },
  { _id: false }
);

const PlanSchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, required: true, index: true },
    weekStart: { type: String, required: true }, // YYYY-MM-DD
    meals: { type: [MealSchema], default: [] },
    workouts: { type: [WorkoutSchema], default: [] },
    totals: {
      cal: Number, protein: Number, carbs: Number, fat: Number
    }
  },
  { timestamps: true }
);

PlanSchema.index({ userId: 1, weekStart: 1 }, { unique: true });

export default mongoose.model('Plan', PlanSchema);
