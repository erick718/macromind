package com.fitness.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fitness.Model.Exercise;
import com.fitness.Model.WorkoutPlan;
import com.fitness.util.DBConnection;

public class WorkoutPlanDAO {
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // Save workout plan
    public void saveWorkoutPlan(WorkoutPlan plan) {
        System.out.println("DEBUG: Saving workout plan for user " + plan.getUserId() + " with " + plan.getExercises().size() + " exercises");
        String query = "INSERT INTO workout_plans (user_id, plan_name, goal, difficulty, duration_weeks, sessions_per_week, total_calories_burned, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, plan.getUserId());
            ps.setString(2, plan.getPlanName());
            ps.setString(3, plan.getGoal());
            ps.setString(4, plan.getDifficulty());
            ps.setInt(5, plan.getDurationWeeks());
            ps.setInt(6, plan.getSessionsPerWeek());
            ps.setInt(7, plan.getTotalCaloriesBurned());
            
            // Ensure created date is set
            if (plan.getCreatedDate() == null) {
                plan.setCreatedDate(new java.util.Date());
            }
            ps.setTimestamp(8, new Timestamp(plan.getCreatedDate().getTime()));
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int planId = rs.getInt(1);
                plan.setPlanId(planId);
                
                // Save exercises for this plan
                saveWorkoutPlanExercises(planId, plan.getExercises());
            }
        } catch (SQLException e) {
            System.err.println("Error saving workout plan: " + e.getMessage());
        }
    }
    
    // Save exercises for a workout plan
    private void saveWorkoutPlanExercises(int planId, List<Exercise> exercises) {
        String query = "INSERT INTO workout_plan_exercises (plan_id, exercise_id) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            for (Exercise exercise : exercises) {
                System.out.println("DEBUG: Saving exercise with ID " + exercise.getExerciseId() + " for plan " + planId);
                if (exercise.getExerciseId() <= 0) {
                    System.err.println("ERROR: Exercise has invalid ID: " + exercise.getExerciseId() + " - " + exercise.getName());
                    continue; // Skip exercises with invalid IDs
                }
                ps.setInt(1, planId);
                ps.setInt(2, exercise.getExerciseId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving workout plan exercises: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Get workout plans by user ID
    public List<WorkoutPlan> getWorkoutPlansByUserId(int userId) {
        List<WorkoutPlan> plans = new ArrayList<>();
        System.out.println("DEBUG: Getting workout plans for user " + userId);
        String query = "SELECT * FROM workout_plans WHERE user_id = ? ORDER BY created_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("DEBUG: Executing query: " + query + " with userId=" + userId);
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("DEBUG: Found plan " + count + " - ID: " + rs.getInt("plan_id"));
                WorkoutPlan plan = new WorkoutPlan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setUserId(rs.getInt("user_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plan.setGoal(rs.getString("goal"));
                plan.setDifficulty(rs.getString("difficulty"));
                plan.setDurationWeeks(rs.getInt("duration_weeks"));
                plan.setSessionsPerWeek(rs.getInt("sessions_per_week"));
                plan.setTotalCaloriesBurned(rs.getInt("total_calories_burned"));
                plan.setCreatedDate(rs.getTimestamp("created_date"));
                
                // Load exercises for this plan
                plan.setExercises(getExercisesForPlan(plan.getPlanId()));
                plans.add(plan);
            }
            System.out.println("DEBUG: Found " + count + " workout plans for user " + userId);
        } catch (SQLException e) {
            System.err.println("Error getting workout plans: " + e.getMessage());
        }
        
        System.out.println("DEBUG: Returning " + plans.size() + " plans");
        return plans;
    }
    
    // Get exercises for a specific workout plan
    public List<Exercise> getExercisesForPlan(int planId) {
        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT e.* FROM exercises e " +
                      "JOIN workout_plan_exercises wpe ON e.exercise_id = wpe.exercise_id " +
                      "WHERE wpe.plan_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Exercise exercise = new Exercise();
                exercise.setExerciseId(rs.getInt("exercise_id"));
                exercise.setName(rs.getString("name"));
                exercise.setDescription(rs.getString("description"));
                exercise.setMuscleGroup(rs.getString("muscle_group"));
                exercise.setDifficulty(rs.getString("difficulty"));
                exercise.setEquipment(rs.getString("equipment"));
                exercise.setDurationMinutes(rs.getInt("duration_minutes"));
                exercise.setCaloriesBurned(rs.getInt("calories_burned"));
                exercise.setGoal(rs.getString("goal") != null ? rs.getString("goal") : "general");
                exercises.add(exercise);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exercises for plan: " + e.getMessage());
        }
        
        return exercises;
    }
    
    // Get workout plan by ID
    public WorkoutPlan getWorkoutPlanById(int planId) {
        String query = "SELECT * FROM workout_plans WHERE plan_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                WorkoutPlan plan = new WorkoutPlan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setUserId(rs.getInt("user_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plan.setGoal(rs.getString("goal"));
                plan.setDifficulty(rs.getString("difficulty"));
                plan.setDurationWeeks(rs.getInt("duration_weeks"));
                plan.setSessionsPerWeek(rs.getInt("sessions_per_week"));
                plan.setTotalCaloriesBurned(rs.getInt("total_calories_burned"));
                plan.setCreatedDate(rs.getTimestamp("created_date"));
                
                // Load exercises for this plan
                plan.setExercises(getExercisesForPlan(planId));
                return plan;
            }
        } catch (SQLException e) {
            System.err.println("Error getting workout plan by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Delete workout plan
    public void deleteWorkoutPlan(int planId) {
        try (Connection conn = getConnection()) {
            // Delete exercises first (foreign key constraint)
            String deleteExercises = "DELETE FROM workout_plan_exercises WHERE plan_id = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(deleteExercises)) {
                ps1.setInt(1, planId);
                ps1.executeUpdate();
            }
            
            // Delete the plan
            String deletePlan = "DELETE FROM workout_plans WHERE plan_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(deletePlan)) {
                ps2.setInt(1, planId);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting workout plan: " + e.getMessage());
        }
    }
}