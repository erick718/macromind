package com.fitness.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fitness.model.Exercise;
import com.fitness.model.WorkoutPlan;
import com.fitness.dao.ExerciseDAO;
import com.fitness.dao.WorkoutPlanDAO;

public class WorkoutPlanService {
    
    private ExerciseDAO exerciseDAO;
    private WorkoutPlanDAO workoutPlanDAO;
    private Random random;
    
    public WorkoutPlanService() {
        this.exerciseDAO = new ExerciseDAO();
        this.workoutPlanDAO = new WorkoutPlanDAO();
        this.random = new Random();
    }
    
    /**
     * Generate a personalized workout plan based on user's goals and fitness level
     */
    public WorkoutPlan generateWorkoutPlan(int userId, String goal, String activityLevel, 
                                          int age, int weight) {
        // Determine difficulty based on activity level
        String difficulty = determineDifficulty(activityLevel, age);
        
        // Create workout plan
        WorkoutPlan plan = new WorkoutPlan();
        plan.setUserId(userId);
        plan.setPlanName(generatePlanName(goal, difficulty));
        plan.setGoal(goal);
        plan.setDifficulty(difficulty);
        plan.setCreatedDate(new java.util.Date()); // Set current date
        
        // Set duration and frequency based on goal
        setDurationAndFrequency(plan, goal, difficulty);
        
        // Generate exercises based on goal
        List<Exercise> exercises = selectExercisesForGoal(goal, difficulty);
        plan.setExercises(exercises);
        
        // Calculate total calories
        plan.calculateTotalCalories();
        
        return plan;
    }
    
    /**
     * Determine difficulty level based on activity level and age
     */
    private String determineDifficulty(String activityLevel, int age) {
        if (age > 50 || "low".equals(activityLevel)) {
            return "beginner";
        } else if ("moderate".equals(activityLevel)) {
            return "intermediate";
        } else {
            return "advanced";
        }
    }
    
    /**
     * Generate a descriptive plan name
     */
    private String generatePlanName(String goal, String difficulty) {
        String goalName;
        switch (goal) {
            case "lose":
                goalName = "Weight Loss";
                break;
            case "gain":
                goalName = "Muscle Building";
                break;
            case "maintain":
                goalName = "Maintenance";
                break;
            default:
                goalName = "Fitness";
        }
        
        String difficultyName = difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1);
        return difficultyName + " " + goalName + " Plan";
    }
    
    /**
     * Set duration and frequency based on goal and difficulty
     */
    private void setDurationAndFrequency(WorkoutPlan plan, String goal, String difficulty) {
        switch (goal) {
            case "lose": // Weight Loss
                plan.setDurationWeeks(12);
                plan.setSessionsPerWeek(difficulty.equals("beginner") ? 3 : 4);
                break;
            case "gain": // Muscle Building
                plan.setDurationWeeks(16);
                plan.setSessionsPerWeek(difficulty.equals("beginner") ? 3 : 5);
                break;
            case "maintain": // Maintenance/Endurance
                plan.setDurationWeeks(8);
                plan.setSessionsPerWeek(difficulty.equals("beginner") ? 2 : 3);
                break;
            default:
                plan.setDurationWeeks(10);
                plan.setSessionsPerWeek(3);
        }
    }
    
    /**
     * Select appropriate exercises based on goal and difficulty
     */
    private List<Exercise> selectExercisesForGoal(String goal, String difficulty) {
        List<Exercise> selectedExercises = new ArrayList<>();
        
        switch (goal) {
            case "lose": // Weight Loss - Focus on cardio and full body
                selectedExercises = getWeightLossExercises(difficulty);
                break;
            case "gain": // Strength/Muscle Building
                selectedExercises = getStrengthExercises(difficulty);
                break;
            case "maintain": // Endurance/Maintenance
                selectedExercises = getEnduranceExercises(difficulty);
                break;
            default:
                selectedExercises = getGeneralFitnessExercises(difficulty);
        }
        
        return selectedExercises;
    }
    
    /**
     * Get exercises for weight loss (high cardio, some strength)
     */
    private List<Exercise> getWeightLossExercises(String difficulty) {
        List<Exercise> exercises = new ArrayList<>();
        
        // Add cardio exercises (goal-specific)
        exercises.addAll(getExercisesByTypeAndGoal("cardio", difficulty, 3, "lose"));
        // Add some strength exercises
        exercises.addAll(getExercisesByTypeAndGoal("full_body", difficulty, 2, "lose"));
        // Add core exercises
        exercises.addAll(getExercisesByTypeAndGoal("core", difficulty, 2, "lose"));
        
        return exercises;
    }
    
    /**
     * Get exercises for strength/muscle building
     */
    private List<Exercise> getStrengthExercises(String difficulty) {
        List<Exercise> exercises = new ArrayList<>();
        
        // Balanced muscle groups for strength
        exercises.addAll(getExercisesByTypeAndGoal("chest", difficulty, 2, "gain"));
        exercises.addAll(getExercisesByTypeAndGoal("back", difficulty, 2, "gain"));
        exercises.addAll(getExercisesByTypeAndGoal("legs", difficulty, 2, "gain"));
        exercises.addAll(getExercisesByTypeAndGoal("arms", difficulty, 2, "gain"));
        exercises.addAll(getExercisesByTypeAndGoal("core", difficulty, 1, "gain"));
        
        return exercises;
    }
    
    /**
     * Get exercises for endurance
     */
    private List<Exercise> getEnduranceExercises(String difficulty) {
        List<Exercise> exercises = new ArrayList<>();
        
        // Focus on endurance and functional movements
        exercises.addAll(getExercisesByTypeAndGoal("cardio", difficulty, 4, "maintain"));
        exercises.addAll(getExercisesByTypeAndGoal("full_body", difficulty, 2, "maintain"));
        exercises.addAll(getExercisesByTypeAndGoal("flexibility", difficulty, 1, "maintain"));
        
        return exercises;
    }
    
    /**
     * Get general fitness exercises
     */
    private List<Exercise> getGeneralFitnessExercises(String difficulty) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.addAll(getExercisesByTypeAndGoal("cardio", difficulty, 2, null));
        exercises.addAll(getExercisesByTypeAndGoal("full_body", difficulty, 2, null));
        exercises.addAll(getExercisesByTypeAndGoal("core", difficulty, 2, null));
        exercises.addAll(getExercisesByTypeAndGoal("flexibility", difficulty, 1, null));
        
        return exercises;
    }
    
    /**
     * Get exercises by muscle group/type with goal-based selection and fallback to sample exercises
     */
    private List<Exercise> getExercisesByTypeAndGoal(String muscleGroup, String difficulty, int count, String goal) {
        List<Exercise> exercises = new ArrayList<>();
        
        // First, try to get goal-specific exercises from database
        if (goal != null) {
            exercises = exerciseDAO.getExercisesByMuscleGroupAndGoal(muscleGroup, difficulty, goal);
        }
        
        // If not enough goal-specific exercises, get general exercises for muscle group
        if (exercises.size() < count) {
            List<Exercise> generalExercises = exerciseDAO.getExercisesByMuscleGroup(muscleGroup, difficulty);
            for (Exercise ex : generalExercises) {
                if (!exercises.contains(ex)) {
                    exercises.add(ex);
                }
            }
        }
        
        // If still no exercises found in DB, create sample exercises
        if (exercises.isEmpty()) {
            exercises = createSampleExercises(muscleGroup, difficulty, goal);
            
            // Save sample exercises to database for future use
            for (Exercise exercise : exercises) {
                exerciseDAO.addExercise(exercise);
            }
        }
        
        // Shuffle and limit to requested count
        List<Exercise> selected = new ArrayList<>();
        List<Exercise> shuffledExercises = new ArrayList<>(exercises);
        
        for (int i = 0; i < Math.min(count, shuffledExercises.size()); i++) {
            int randomIndex = random.nextInt(shuffledExercises.size());
            selected.add(shuffledExercises.remove(randomIndex));
        }
        
        return selected;
    }
    
    /**
     * Create sample exercises if database is empty
     */
    private List<Exercise> createSampleExercises(String muscleGroup, String difficulty, String goal) {
        List<Exercise> exercises = new ArrayList<>();
        
        String exerciseGoal = goal != null ? goal : "general";
        
        switch (muscleGroup) {
            case "cardio":
                exercises.add(new Exercise("Running", "Steady-state running", "cardio", difficulty, "none", 30, 300, exerciseGoal));
                exercises.add(new Exercise("Jumping Jacks", "Full body cardio exercise", "cardio", difficulty, "none", 15, 150, exerciseGoal));
                exercises.add(new Exercise("Burpees", "High-intensity full body exercise", "cardio", difficulty, "none", 10, 200, exerciseGoal));
                break;
            case "full_body":
                exercises.add(new Exercise("Push-ups", "Upper body strength exercise", "full_body", difficulty, "none", 15, 100, exerciseGoal));
                exercises.add(new Exercise("Squats", "Lower body strength exercise", "full_body", difficulty, "none", 20, 120, exerciseGoal));
                exercises.add(new Exercise("Mountain Climbers", "Dynamic full body exercise", "full_body", difficulty, "none", 15, 150, exerciseGoal));
                break;
            case "core":
                exercises.add(new Exercise("Plank", "Core stability exercise", "core", difficulty, "none", 5, 50, exerciseGoal));
                exercises.add(new Exercise("Crunches", "Abdominal strengthening", "core", difficulty, "none", 10, 80, exerciseGoal));
                exercises.add(new Exercise("Bicycle Crunches", "Dynamic core exercise", "core", difficulty, "none", 15, 100, exerciseGoal));
                break;
            case "chest":
                exercises.add(new Exercise("Push-ups", "Chest and tricep exercise", "chest", difficulty, "none", 15, 100, exerciseGoal));
                exercises.add(new Exercise("Chest Press", "Chest strengthening", "chest", difficulty, "dumbbells", 20, 120, exerciseGoal));
                break;
            case "back":
                exercises.add(new Exercise("Pull-ups", "Back and bicep exercise", "back", difficulty, "pull-up bar", 10, 120, exerciseGoal));
                exercises.add(new Exercise("Rowing", "Back strengthening", "back", difficulty, "resistance band", 15, 100, exerciseGoal));
                break;
            case "legs":
                exercises.add(new Exercise("Squats", "Quad and glute exercise", "legs", difficulty, "none", 20, 120, exerciseGoal));
                exercises.add(new Exercise("Lunges", "Leg strengthening", "legs", difficulty, "none", 15, 100, exerciseGoal));
                break;
            case "arms":
                exercises.add(new Exercise("Tricep Dips", "Tricep strengthening", "arms", difficulty, "chair", 12, 80, exerciseGoal));
                exercises.add(new Exercise("Bicep Curls", "Bicep strengthening", "arms", difficulty, "dumbbells", 15, 90, exerciseGoal));
                break;
            case "flexibility":
                exercises.add(new Exercise("Yoga Flow", "Full body flexibility", "flexibility", difficulty, "yoga mat", 20, 100, exerciseGoal));
                exercises.add(new Exercise("Stretching", "General flexibility routine", "flexibility", difficulty, "none", 15, 50, exerciseGoal));
                break;
        }
        
        return exercises;
    }
    
    /**
     * Save a generated workout plan
     */
    public void saveWorkoutPlan(WorkoutPlan plan) {
        workoutPlanDAO.saveWorkoutPlan(plan);
    }
    
    /**
     * Get user's workout plans
     */
    public List<WorkoutPlan> getUserWorkoutPlans(int userId) {
        return workoutPlanDAO.getWorkoutPlansByUserId(userId);
    }
    
    /**
     * Get workout plan by ID
     */
    public WorkoutPlan getWorkoutPlanById(int planId) {
        return workoutPlanDAO.getWorkoutPlanById(planId);
    }
    
    /**
     * Delete workout plan
     */
    public void deleteWorkoutPlan(int planId) {
        workoutPlanDAO.deleteWorkoutPlan(planId);
    }
}