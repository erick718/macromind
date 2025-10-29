package com.fitness.model;

import java.sql.Timestamp;

public class ExerciseType {
    private int exerciseTypeId;
    private String exerciseName;
    private String category;
    private double metValue;
    private String description;
    private Timestamp createdAt;

    // Default constructor
    public ExerciseType() {}

    // Constructor
    public ExerciseType(String exerciseName, String category, double metValue, String description) {
        this.exerciseName = exerciseName;
        this.category = category;
        this.metValue = metValue;
        this.description = description;
    }

    // Getters and Setters
    public int getExerciseTypeId() {
        return exerciseTypeId;
    }

    public void setExerciseTypeId(int exerciseTypeId) {
        this.exerciseTypeId = exerciseTypeId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getMetValue() {
        return metValue;
    }

    public void setMetValue(double metValue) {
        this.metValue = metValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ExerciseType{" +
                "exerciseTypeId=" + exerciseTypeId +
                ", exerciseName='" + exerciseName + '\'' +
                ", category='" + category + '\'' +
                ", metValue=" + metValue +
                ", description='" + description + '\'' +
                '}';
    }
}