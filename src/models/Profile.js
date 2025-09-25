import mongoose from 'mongoose';

const ProfileSchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, required: true, unique: true, index: true },
    goal: {
      type: String,
      enum: ['Lose Weight', 'Build Muscle', 'Maintain'],
      required: true
    },
    preferences: { type: [String], default: [] }, // e.g., ["Vegetarian","Low Carb"]
    availabilityPerWeek: { type: Number, min: 1, max: 7, required: true },
    dietaryRestrictions: { type: [String], default: [] }
  },
  { timestamps: true }
);

export default mongoose.model('Profile', ProfileSchema);
