package com.fitapp.model;

import java.time.LocalDate;

public class ProgressPoint {
    private LocalDate date;
    // weight in kg (nullable when not applicable)
    private Double weightKg;
    // calories consumed (nullable when not applicable)
    private Integer calories;
    // workouts done that day (count) & minutes (nullable if N/A)
    private Integer workoutCount;
    private Integer workoutMinutes;

    public ProgressPoint() {}

    public ProgressPoint(LocalDate date, Double weightKg, Integer calories, Integer workoutCount, Integer workoutMinutes) {
        this.date = date;
        this.weightKg = weightKg;
        this.calories = calories;
        this.workoutCount = workoutCount;
        this.workoutMinutes = workoutMinutes;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Integer getWorkoutCount() { return workoutCount; }
    public void setWorkoutCount(Integer workoutCount) { this.workoutCount = workoutCount; }

    public Integer getWorkoutMinutes() { return workoutMinutes; }
    public void setWorkoutMinutes(Integer workoutMinutes) { this.workoutMinutes = workoutMinutes; }
}
