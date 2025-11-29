package com.fitness.Model;

public class Exercise {
    private int exerciseId;
    private String name;
    private String description;
    private String muscleGroup;
    private String difficulty; // beginner, intermediate, advanced
    private String equipment;
    private int durationMinutes;
    private int caloriesBurned; // per session
    private String goal; // lose, gain, maintain, general
    
    public Exercise() {}
    
    public Exercise(String name, String description, String muscleGroup, 
                   String difficulty, String equipment, int durationMinutes, int caloriesBurned) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.goal = "general"; // default value
    }
    
    public Exercise(String name, String description, String muscleGroup, 
                   String difficulty, String equipment, int durationMinutes, int caloriesBurned, String goal) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.goal = goal;
    }
    
    // Getters and Setters
    public int getExerciseId() {
        return exerciseId;
    }
    
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMuscleGroup() {
        return muscleGroup;
    }
    
    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getEquipment() {
        return equipment;
    }
    
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public int getCaloriesBurned() {
        return caloriesBurned;
    }
    
    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    
    public String getGoal() {
        return goal;
    }
    
    public void setGoal(String goal) {
        this.goal = goal;
    }
}