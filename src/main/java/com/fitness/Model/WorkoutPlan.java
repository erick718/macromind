package com.fitness.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutPlan {
    private int planId;
    private int userId;
    private String planName;
    private String goal; // weight_loss, strength, endurance
    private String difficulty;
    private int durationWeeks;
    private Date createdDate;
    private List<Exercise> exercises;
    private int totalCaloriesBurned;
    private int sessionsPerWeek;
    
    public WorkoutPlan() {
        this.exercises = new ArrayList<>();
    }
    
    public WorkoutPlan(int userId, String planName, String goal, String difficulty, 
                      int durationWeeks, int sessionsPerWeek) {
        this();
        this.userId = userId;
        this.planName = planName;
        this.goal = goal;
        this.difficulty = difficulty;
        this.durationWeeks = durationWeeks;
        this.sessionsPerWeek = sessionsPerWeek;
        this.createdDate = new Date();
    }
    
    // Getters and Setters
    public int getPlanId() {
        return planId;
    }
    
    public void setPlanId(int planId) {
        this.planId = planId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getGoal() {
        return goal;
    }
    
    public void setGoal(String goal) {
        this.goal = goal;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public int getDurationWeeks() {
        return durationWeeks;
    }
    
    public void setDurationWeeks(int durationWeeks) {
        this.durationWeeks = durationWeeks;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public List<Exercise> getExercises() {
        return exercises;
    }
    
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
    
    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }
    
    public int getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }
    
    public void setTotalCaloriesBurned(int totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }
    
    public int getSessionsPerWeek() {
        return sessionsPerWeek;
    }
    
    public void setSessionsPerWeek(int sessionsPerWeek) {
        this.sessionsPerWeek = sessionsPerWeek;
    }
    
    // Helper method to calculate total calories
    public void calculateTotalCalories() {
        this.totalCaloriesBurned = exercises.stream()
                .mapToInt(Exercise::getCaloriesBurned)
                .sum();
    }
}