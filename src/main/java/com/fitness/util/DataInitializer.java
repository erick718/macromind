package com.fitness.util;

import java.util.ArrayList;
import java.util.List;

import com.fitness.Model.Exercise;
import com.fitness.dao.ExerciseDAO;

public class DataInitializer {
    
    private ExerciseDAO exerciseDAO;
    
    public DataInitializer() {
        this.exerciseDAO = new ExerciseDAO();
    }
    
    /**
     * Initialize database with sample exercises if none exist
     */
    public void initializeSampleData() {
        List<Exercise> existingExercises = exerciseDAO.getAllExercises();
        
        // Only initialize if database is empty
        if (existingExercises.isEmpty()) {
            System.out.println("Initializing database with sample exercises...");
            
            List<Exercise> sampleExercises = createSampleExercises();
            for (Exercise exercise : sampleExercises) {
                exerciseDAO.addExercise(exercise);
            }
            
            System.out.println("Sample exercises initialized successfully!");
        } else {
            System.out.println("Database already contains exercises. Skipping initialization.");
        }
    }
    
    /**
     * Create a comprehensive list of sample exercises
     */
    private List<Exercise> createSampleExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Cardio Exercises
        exercises.add(new Exercise("Running (Beginner)", "Light jogging at comfortable pace", "cardio", "beginner", "none", 20, 200));
        exercises.add(new Exercise("Running (Intermediate)", "Moderate pace running", "cardio", "intermediate", "none", 30, 350));
        exercises.add(new Exercise("Running (Advanced)", "High-intensity running", "cardio", "advanced", "none", 45, 500));
        exercises.add(new Exercise("Jumping Jacks", "Full body cardio exercise", "cardio", "beginner", "none", 10, 100));
        exercises.add(new Exercise("High Knees", "Running in place with high knees", "cardio", "intermediate", "none", 10, 120));
        exercises.add(new Exercise("Burpees", "High-intensity full body exercise", "cardio", "advanced", "none", 10, 200));
        exercises.add(new Exercise("Walking", "Brisk walking exercise", "cardio", "beginner", "none", 30, 150));
        exercises.add(new Exercise("Cycling", "Stationary or outdoor cycling", "cardio", "intermediate", "bicycle", 30, 300));
        exercises.add(new Exercise("Swimming", "Full body swimming workout", "cardio", "advanced", "pool", 30, 400));
        
        // Full Body Exercises
        exercises.add(new Exercise("Push-ups (Modified)", "Knee push-ups for beginners", "full_body", "beginner", "none", 10, 80));
        exercises.add(new Exercise("Push-ups (Standard)", "Traditional push-ups", "full_body", "intermediate", "none", 15, 120));
        exercises.add(new Exercise("Push-ups (Advanced)", "Diamond or one-arm push-ups", "full_body", "advanced", "none", 20, 180));
        exercises.add(new Exercise("Squats (Bodyweight)", "Basic bodyweight squats", "full_body", "beginner", "none", 15, 100));
        exercises.add(new Exercise("Squats (Weighted)", "Squats with dumbbells or kettlebell", "full_body", "intermediate", "dumbbells", 20, 150));
        exercises.add(new Exercise("Jump Squats", "Explosive squat jumps", "full_body", "advanced", "none", 15, 200));
        exercises.add(new Exercise("Mountain Climbers", "Dynamic full body exercise", "full_body", "intermediate", "none", 10, 150));
        exercises.add(new Exercise("Bear Crawls", "Quadrupedal movement pattern", "full_body", "advanced", "none", 10, 180));
        
        // Core Exercises
        exercises.add(new Exercise("Plank (Modified)", "Knee plank hold", "core", "beginner", "none", 5, 40));
        exercises.add(new Exercise("Plank (Standard)", "Traditional plank hold", "core", "intermediate", "none", 8, 60));
        exercises.add(new Exercise("Plank (Advanced)", "Single-arm or leg plank variations", "core", "advanced", "none", 12, 100));
        exercises.add(new Exercise("Crunches", "Basic abdominal crunches", "core", "beginner", "none", 10, 60));
        exercises.add(new Exercise("Bicycle Crunches", "Dynamic core rotation exercise", "core", "intermediate", "none", 12, 90));
        exercises.add(new Exercise("Russian Twists", "Seated core rotation exercise", "core", "advanced", "none", 15, 120));
        exercises.add(new Exercise("Dead Bug", "Core stability exercise", "core", "beginner", "none", 8, 50));
        exercises.add(new Exercise("Bird Dog", "Core and back stability", "core", "intermediate", "none", 10, 70));
        
        // Chest Exercises
        exercises.add(new Exercise("Wall Push-ups", "Standing push-ups against wall", "chest", "beginner", "none", 10, 60));
        exercises.add(new Exercise("Incline Push-ups", "Push-ups with hands elevated", "chest", "beginner", "bench", 12, 80));
        exercises.add(new Exercise("Chest Press (Light)", "Dumbbell chest press with light weight", "chest", "intermediate", "dumbbells", 15, 100));
        exercises.add(new Exercise("Chest Press (Heavy)", "Heavy dumbbell or barbell chest press", "chest", "advanced", "barbell", 20, 150));
        exercises.add(new Exercise("Chest Flyes", "Dumbbell chest fly exercise", "chest", "intermediate", "dumbbells", 15, 110));
        
        // Back Exercises
        exercises.add(new Exercise("Resistance Band Rows", "Seated rows with resistance band", "back", "beginner", "resistance band", 12, 80));
        exercises.add(new Exercise("Bent-over Rows", "Dumbbell bent-over rows", "back", "intermediate", "dumbbells", 15, 120));
        exercises.add(new Exercise("Pull-ups (Assisted)", "Pull-ups with assistance band", "back", "intermediate", "pull-up bar", 10, 100));
        exercises.add(new Exercise("Pull-ups (Standard)", "Unassisted pull-ups", "back", "advanced", "pull-up bar", 15, 150));
        exercises.add(new Exercise("Superman", "Back extension exercise", "back", "beginner", "none", 10, 60));
        
        // Leg Exercises
        exercises.add(new Exercise("Wall Sit", "Isometric squat hold against wall", "legs", "beginner", "none", 5, 50));
        exercises.add(new Exercise("Lunges (Bodyweight)", "Forward or reverse lunges", "legs", "beginner", "none", 12, 90));
        exercises.add(new Exercise("Lunges (Weighted)", "Lunges with dumbbells", "legs", "intermediate", "dumbbells", 15, 130));
        exercises.add(new Exercise("Bulgarian Split Squats", "Single-leg squat variation", "legs", "advanced", "none", 15, 150));
        exercises.add(new Exercise("Calf Raises", "Standing calf raises", "legs", "beginner", "none", 10, 60));
        exercises.add(new Exercise("Single-leg Glute Bridges", "Hip bridge with one leg", "legs", "intermediate", "none", 12, 80));
        
        // Arms Exercises
        exercises.add(new Exercise("Arm Circles", "Dynamic arm warm-up exercise", "arms", "beginner", "none", 5, 30));
        exercises.add(new Exercise("Tricep Dips (Chair)", "Chair-assisted tricep dips", "arms", "beginner", "chair", 10, 70));
        exercises.add(new Exercise("Tricep Dips (Bench)", "Bench tricep dips", "arms", "intermediate", "bench", 12, 90));
        exercises.add(new Exercise("Bicep Curls (Light)", "Light dumbbell bicep curls", "arms", "beginner", "dumbbells", 10, 60));
        exercises.add(new Exercise("Bicep Curls (Heavy)", "Heavy dumbbell bicep curls", "arms", "intermediate", "dumbbells", 15, 100));
        exercises.add(new Exercise("Hammer Curls", "Neutral grip bicep curls", "arms", "intermediate", "dumbbells", 15, 110));
        exercises.add(new Exercise("Tricep Extensions", "Overhead tricep extensions", "arms", "advanced", "dumbbells", 15, 120));
        
        // Flexibility Exercises
        exercises.add(new Exercise("Basic Stretching", "General flexibility routine", "flexibility", "beginner", "none", 15, 50));
        exercises.add(new Exercise("Yoga Flow (Beginner)", "Gentle yoga sequence", "flexibility", "beginner", "yoga mat", 20, 80));
        exercises.add(new Exercise("Yoga Flow (Intermediate)", "Moderate yoga sequence", "flexibility", "intermediate", "yoga mat", 30, 120));
        exercises.add(new Exercise("Yoga Flow (Advanced)", "Advanced yoga sequence", "flexibility", "advanced", "yoga mat", 45, 200));
        exercises.add(new Exercise("Foam Rolling", "Self-myofascial release", "flexibility", "beginner", "foam roller", 15, 60));
        exercises.add(new Exercise("Dynamic Stretching", "Movement-based stretching", "flexibility", "intermediate", "none", 20, 80));
        
        return exercises;
    }
    
    /**
     * Main method for standalone execution
     */
    public static void main(String[] args) {
        try {
            DataInitializer initializer = new DataInitializer();
            initializer.initializeSampleData();
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
}