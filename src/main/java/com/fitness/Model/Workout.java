package com.fitness.Model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Workout {
    private int workoutId;
    private int userId;
    private String exerciseName;
    private String exerciseType;
    private int setsCount;
    private int repsPerSet;
    private double weightKg;
    private int durationMinutes;
    private double caloriesBurned;
    private Date workoutDate;
    private Time workoutTime;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Workout() {}

    // Constructor for creating new workouts
    public Workout(int userId, String exerciseName, String exerciseType, 
                   int setsCount, int repsPerSet, double weightKg, 
                   int durationMinutes, Date workoutDate) {
        this.userId = userId;
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseType;
        this.setsCount = setsCount;
        this.repsPerSet = repsPerSet;
        this.weightKg = weightKg;
        this.durationMinutes = durationMinutes;
        this.workoutDate = workoutDate;
    }

    // Getters and Setters
    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getSetsCount() {
        return setsCount;
    }

    public void setSetsCount(int setsCount) {
        this.setsCount = setsCount;
    }

    public int getRepsPerSet() {
        return repsPerSet;
    }

    public void setRepsPerSet(int repsPerSet) {
        this.repsPerSet = repsPerSet;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Date getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(Date workoutDate) {
        this.workoutDate = workoutDate;
    }

    public Time getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(Time workoutTime) {
        this.workoutTime = workoutTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Calculate calories burned based on exercise type, duration, and user weight
     * Formula: Calories = (MET value × weight in kg × duration in hours)
     * 
     * @param userWeight User's weight in kg
     * @param metValue Metabolic Equivalent of Task value for the exercise
     * @return Calculated calories burned
     */
    public double calculateCaloriesBurned(double userWeight, double metValue) {
        double durationHours = this.durationMinutes / 60.0;
        return metValue * userWeight * durationHours;
    }

    /**
     * Get estimated MET value based on exercise type
     * This is a fallback method when MET value is not available from database
     * 
     * @return Estimated MET value
     */
    public double getEstimatedMetValue() {
        if (exerciseType == null) return 3.0; // Default moderate activity
        
        switch (exerciseType.toLowerCase()) {
            case "cardio":
                // Different cardio exercises have different MET values
                if (exerciseName != null) {
                    String name = exerciseName.toLowerCase();
                    if (name.contains("running")) return 10.0;
                    if (name.contains("cycling")) return 8.0;
                    if (name.contains("swimming")) return 8.0;
                    if (name.contains("walking")) return 4.3;
                    if (name.contains("elliptical")) return 7.0;
                    if (name.contains("jump rope")) return 12.3;
                }
                return 7.0; // Default cardio MET
                
            case "strength":
                if (weightKg > 0) {
                    return 8.0; // Vigorous weight lifting
                }
                return 6.0; // Moderate weight lifting
                
            case "flexibility":
                if (exerciseName != null && exerciseName.toLowerCase().contains("yoga")) {
                    return 3.0; // Power yoga
                }
                return 2.5; // General stretching
                
            case "sports":
                return 8.0; // Average sports activity
                
            default:
                return 5.0; // General moderate activity
        }
    }

    /**
     * Get total volume for strength exercises (sets × reps × weight)
     * 
     * @return Total volume in kg
     */
    public double getTotalVolume() {
        if ("strength".equalsIgnoreCase(exerciseType)) {
            return setsCount * repsPerSet * weightKg;
        }
        return 0.0;
    }

    /**
     * Get workout intensity level based on duration and exercise type
     * 
     * @return Intensity level: "Low", "Moderate", or "High"
     */
    public String getIntensityLevel() {
        if (durationMinutes <= 15) return "Low";
        if (durationMinutes <= 45) return "Moderate";
        return "High";
    }

    @Override
    public String toString() {
        return "Workout{" +
                "workoutId=" + workoutId +
                ", userId=" + userId +
                ", exerciseName='" + exerciseName + '\'' +
                ", exerciseType='" + exerciseType + '\'' +
                ", setsCount=" + setsCount +
                ", repsPerSet=" + repsPerSet +
                ", weightKg=" + weightKg +
                ", durationMinutes=" + durationMinutes +
                ", caloriesBurned=" + caloriesBurned +
                ", workoutDate=" + workoutDate +
                ", workoutTime=" + workoutTime +
                '}';
    }
}