package com.fitness.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fitness.model.Exercise;
import com.fitness.util.DBConnection;

public class ExerciseDAO {
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // Get exercises by goal and difficulty
    public List<Exercise> getExercisesByGoalAndDifficulty(String goal, String difficulty) {
        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT * FROM exercises WHERE goal = ? AND difficulty = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, goal);
            ps.setString(2, difficulty);
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
            e.printStackTrace();
        }
        
        return exercises;
    }
    
    // Get exercises by muscle group
    public List<Exercise> getExercisesByMuscleGroup(String muscleGroup, String difficulty) {
        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT * FROM exercises WHERE muscle_group = ? AND difficulty = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, muscleGroup);
            ps.setString(2, difficulty);
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
            System.err.println("Error getting exercises by muscle group: " + e.getMessage());
        }
        
        return exercises;
    }
    
    // Get exercises by muscle group and goal
    public List<Exercise> getExercisesByMuscleGroupAndGoal(String muscleGroup, String difficulty, String goal) {
        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT * FROM exercises WHERE muscle_group = ? AND difficulty = ? AND (goal = ? OR goal = 'general')";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, muscleGroup);
            ps.setString(2, difficulty);
            ps.setString(3, goal);
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
            System.err.println("Error getting exercises by muscle group and goal: " + e.getMessage());
        }
        
        return exercises;
    }
    
    // Get all exercises
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT * FROM exercises";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
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
            System.err.println("Error getting all exercises: " + e.getMessage());
        }
        
        return exercises;
    }
    
    // Add new exercise
    public void addExercise(Exercise exercise) {
        String query = "INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, exercise.getName());
            ps.setString(2, exercise.getDescription());
            ps.setString(3, exercise.getMuscleGroup());
            ps.setString(4, exercise.getDifficulty());
            ps.setString(5, exercise.getEquipment());
            ps.setInt(6, exercise.getDurationMinutes());
            ps.setInt(7, exercise.getCaloriesBurned());
            ps.setString(8, exercise.getGoal() != null ? exercise.getGoal() : "general");
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                exercise.setExerciseId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error adding exercise: " + e.getMessage());
        }
    }
}