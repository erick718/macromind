package com.fitness.Model;

import java.sql.Date;
import java.sql.Timestamp;

public class DailyFitnessSummary {
    private int summaryId;
    private int userId;
    private Date summaryDate;
    private int totalWorkouts;
    private int totalDurationMinutes;
    private double totalCaloriesBurned;
    private double caloriesIntake;
    private double calorieBalance;
    private int workoutStreakDays;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public DailyFitnessSummary() {}

    // Constructor
    public DailyFitnessSummary(int userId, Date summaryDate) {
        this.userId = userId;
        this.summaryDate = summaryDate;
        this.totalWorkouts = 0;
        this.totalDurationMinutes = 0;
        this.totalCaloriesBurned = 0.0;
        this.caloriesIntake = 0.0;
        this.calorieBalance = 0.0;
        this.workoutStreakDays = 0;
    }

    // Getters and Setters
    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(Date summaryDate) {
        this.summaryDate = summaryDate;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public int getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(int totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public double getCaloriesIntake() {
        return caloriesIntake;
    }

    public void setCaloriesIntake(double caloriesIntake) {
        this.caloriesIntake = caloriesIntake;
        updateCalorieBalance();
    }

    public double getCalorieBalance() {
        return calorieBalance;
    }

    public void setCalorieBalance(double calorieBalance) {
        this.calorieBalance = calorieBalance;
    }

    public int getWorkoutStreakDays() {
        return workoutStreakDays;
    }

    public void setWorkoutStreakDays(int workoutStreakDays) {
        this.workoutStreakDays = workoutStreakDays;
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

    // Utility methods
    
    /**
     * Update calorie balance based on intake and burned calories
     */
    public void updateCalorieBalance() {
        this.calorieBalance = this.caloriesIntake - this.totalCaloriesBurned;
    }

    /**
     * Add a workout to the daily summary
     * 
     * @param workout The workout to add
     */
    public void addWorkout(Workout workout) {
        this.totalWorkouts++;
        this.totalDurationMinutes += workout.getDurationMinutes();
        this.totalCaloriesBurned += workout.getCaloriesBurned();
        updateCalorieBalance();
    }

    /**
     * Get average workout duration
     * 
     * @return Average duration in minutes
     */
    public double getAverageWorkoutDuration() {
        if (totalWorkouts == 0) return 0.0;
        return (double) totalDurationMinutes / totalWorkouts;
    }

    /**
     * Get calories burned per minute
     * 
     * @return Calories burned per minute of exercise
     */
    public double getCaloriesBurnedPerMinute() {
        if (totalDurationMinutes == 0) return 0.0;
        return totalCaloriesBurned / totalDurationMinutes;
    }

    /**
     * Check if calorie goal is met (negative balance means calorie deficit)
     * 
     * @param targetDeficit Target calorie deficit (positive number)
     * @return True if deficit goal is met
     */
    public boolean isCalorieDeficitGoalMet(double targetDeficit) {
        return calorieBalance <= -targetDeficit;
    }

    /**
     * Get workout intensity level for the day
     * 
     * @return Intensity level based on total duration
     */
    public String getDailyIntensityLevel() {
        if (totalDurationMinutes == 0) return "Rest Day";
        if (totalDurationMinutes < 30) return "Light";
        if (totalDurationMinutes < 60) return "Moderate";
        if (totalDurationMinutes < 90) return "High";
        return "Very High";
    }

    @Override
    public String toString() {
        return "DailyFitnessSummary{" +
                "summaryId=" + summaryId +
                ", userId=" + userId +
                ", summaryDate=" + summaryDate +
                ", totalWorkouts=" + totalWorkouts +
                ", totalDurationMinutes=" + totalDurationMinutes +
                ", totalCaloriesBurned=" + totalCaloriesBurned +
                ", caloriesIntake=" + caloriesIntake +
                ", calorieBalance=" + calorieBalance +
                ", workoutStreakDays=" + workoutStreakDays +
                '}';
    }
}