package com.fitness.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitness.model.ExerciseType;
import com.fitness.model.Workout;
import com.fitness.util.DBConnection;

public class WorkoutDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ========== WORKOUT CRUD OPERATIONS ==========

    /**
     * Create a new workout entry
     */
    public void createWorkout(Workout workout) {
        String query = "INSERT INTO workouts (user_id, exercise_name, exercise_type, sets_count, " +
                      "reps_per_set, weight_kg, duration_minutes, calories_burned, workout_date, " +
                      "workout_time, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, workout.getUserId());
            ps.setString(2, workout.getExerciseName());
            ps.setString(3, workout.getExerciseType());
            ps.setInt(4, workout.getSetsCount());
            ps.setInt(5, workout.getRepsPerSet());
            ps.setDouble(6, workout.getWeightKg());
            ps.setInt(7, workout.getDurationMinutes());
            ps.setDouble(8, workout.getCaloriesBurned());
            ps.setDate(9, workout.getWorkoutDate());
            ps.setTime(10, workout.getWorkoutTime());
            ps.setString(11, workout.getNotes());

            ps.executeUpdate();

            // Get generated workout_id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                workout.setWorkoutId(rs.getInt(1));
            }

            System.out.println("Workout created successfully with ID: " + workout.getWorkoutId());

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating workout: " + e.getMessage());
        }
    }

    /**
     * Get all workouts for a specific user
     */
    public List<Workout> getWorkoutsByUserId(int userId) {
        List<Workout> workouts = new ArrayList<>();
        String query = "SELECT * FROM workouts WHERE user_id = ? ORDER BY workout_date DESC, workout_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                workouts.add(mapResultSetToWorkout(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching workouts for user " + userId + ": " + e.getMessage());
        }

        return workouts;
    }

    /**
     * Get workouts for a user within a date range
     */
    public List<Workout> getWorkoutsByDateRange(int userId, Date startDate, Date endDate) {
        List<Workout> workouts = new ArrayList<>();
        String query = "SELECT * FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ? " +
                      "ORDER BY workout_date DESC, workout_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                workouts.add(mapResultSetToWorkout(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching workouts by date range: " + e.getMessage());
        }

        return workouts;
    }

    /**
     * Get workout by ID
     */
    public Workout getWorkoutById(int workoutId) {
        String query = "SELECT * FROM workouts WHERE workout_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, workoutId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToWorkout(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching workout by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Update an existing workout
     */
    public void updateWorkout(Workout workout) {
        String query = "UPDATE workouts SET exercise_name=?, exercise_type=?, sets_count=?, " +
                      "reps_per_set=?, weight_kg=?, duration_minutes=?, calories_burned=?, " +
                      "workout_date=?, workout_time=?, notes=? WHERE workout_id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, workout.getExerciseName());
            ps.setString(2, workout.getExerciseType());
            ps.setInt(3, workout.getSetsCount());
            ps.setInt(4, workout.getRepsPerSet());
            ps.setDouble(5, workout.getWeightKg());
            ps.setInt(6, workout.getDurationMinutes());
            ps.setDouble(7, workout.getCaloriesBurned());
            ps.setDate(8, workout.getWorkoutDate());
            ps.setTime(9, workout.getWorkoutTime());
            ps.setString(10, workout.getNotes());
            ps.setInt(11, workout.getWorkoutId());

            int rowsUpdated = ps.executeUpdate();
            System.out.println("Workout updated: " + rowsUpdated + " rows affected");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating workout: " + e.getMessage());
        }
    }

    /**
     * Delete a workout
     */
    public void deleteWorkout(int workoutId) {
        String query = "DELETE FROM workouts WHERE workout_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, workoutId);
            int rowsDeleted = ps.executeUpdate();
            System.out.println("Workout deleted: " + rowsDeleted + " rows affected");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting workout: " + e.getMessage());
        }
    }

    // ========== EXERCISE TYPE OPERATIONS ==========

    /**
     * Get all exercise types
     */
    public List<ExerciseType> getAllExerciseTypes() {
        List<ExerciseType> exerciseTypes = new ArrayList<>();
        String query = "SELECT * FROM exercise_types ORDER BY category, exercise_name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ExerciseType exerciseType = new ExerciseType();
                exerciseType.setExerciseTypeId(rs.getInt("exercise_type_id"));
                exerciseType.setExerciseName(rs.getString("exercise_name"));
                exerciseType.setCategory(rs.getString("category"));
                exerciseType.setMetValue(rs.getDouble("met_value"));
                exerciseType.setDescription(rs.getString("description"));
                exerciseType.setCreatedAt(rs.getTimestamp("created_at"));
                exerciseTypes.add(exerciseType);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching exercise types: " + e.getMessage());
        }

        return exerciseTypes;
    }

    /**
     * Get MET value for a specific exercise
     */
    public double getMetValueForExercise(String exerciseName) {
        String query = "SELECT met_value FROM exercise_types WHERE exercise_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, exerciseName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("met_value");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching MET value: " + e.getMessage());
        }

        return 5.0; // Default MET value
    }

    // ========== PROGRESS AND ANALYTICS ==========

    /**
     * Calculate and update daily fitness summary
     */
    public void updateDailyFitnessSummary(int userId, Date summaryDate) {
        // First, get all workouts for the date
        List<Workout> dailyWorkouts = getWorkoutsByDateRange(userId, summaryDate, summaryDate);
        
        // Calculate totals
        int totalWorkouts = dailyWorkouts.size();
        int totalDuration = 0;
        double totalCalories = 0.0;
        
        for (Workout workout : dailyWorkouts) {
            totalDuration += workout.getDurationMinutes();
            totalCalories += workout.getCaloriesBurned();
        }

        // Update or create daily summary
        String upsertQuery = "INSERT INTO daily_fitness_summary (user_id, summary_date, total_workouts, " +
                            "total_duration_minutes, total_calories_burned) VALUES (?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE total_workouts=?, total_duration_minutes=?, " +
                            "total_calories_burned=?, updated_at=CURRENT_TIMESTAMP";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(upsertQuery)) {

            ps.setInt(1, userId);
            ps.setDate(2, summaryDate);
            ps.setInt(3, totalWorkouts);
            ps.setInt(4, totalDuration);
            ps.setDouble(5, totalCalories);
            ps.setInt(6, totalWorkouts);
            ps.setInt(7, totalDuration);
            ps.setDouble(8, totalCalories);

            ps.executeUpdate();
            System.out.println("Daily fitness summary updated for " + summaryDate);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating daily fitness summary: " + e.getMessage());
        }
    }

    /**
     * Get weekly progress summary
     */
    public Map<String, Object> getWeeklyProgressSummary(int userId, Date startDate, Date endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        String query = "SELECT " +
                      "COUNT(*) as total_workouts, " +
                      "SUM(duration_minutes) as total_duration, " +
                      "SUM(calories_burned) as total_calories, " +
                      "AVG(duration_minutes) as avg_duration, " +
                      "COUNT(DISTINCT workout_date) as workout_days " +
                      "FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                summary.put("totalWorkouts", rs.getInt("total_workouts"));
                summary.put("totalDuration", rs.getInt("total_duration"));
                summary.put("totalCalories", rs.getDouble("total_calories"));
                summary.put("avgDuration", rs.getDouble("avg_duration"));
                summary.put("workoutDays", rs.getInt("workout_days"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting weekly progress: " + e.getMessage());
        }

        return summary;
    }

    /**
     * Get monthly progress summary
     */
    public Map<String, Object> getMonthlyProgressSummary(int userId, Date startDate, Date endDate) {
        return getWeeklyProgressSummary(userId, startDate, endDate); // Same logic, different time range
    }

    /**
     * Get workout streak for user
     */
    public int getWorkoutStreak(int userId) {
        String query = "SELECT COUNT(*) as streak FROM (" +
                      "SELECT workout_date FROM workouts WHERE user_id = ? " +
                      "GROUP BY workout_date ORDER BY workout_date DESC" +
                      ") consecutive_days";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("streak");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error calculating workout streak: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get exercise type distribution for analytics
     */
    public Map<String, Integer> getExerciseTypeDistribution(int userId, Date startDate, Date endDate) {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT exercise_type, COUNT(*) as count FROM workouts " +
                      "WHERE user_id = ? AND workout_date BETWEEN ? AND ? " +
                      "GROUP BY exercise_type";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                distribution.put(rs.getString("exercise_type"), rs.getInt("count"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting exercise distribution: " + e.getMessage());
        }

        return distribution;
    }

    // ========== HELPER METHODS ==========

    /**
     * Map ResultSet to Workout object
     */
    private Workout mapResultSetToWorkout(ResultSet rs) throws SQLException {
        Workout workout = new Workout();
        workout.setWorkoutId(rs.getInt("workout_id"));
        workout.setUserId(rs.getInt("user_id"));
        workout.setExerciseName(rs.getString("exercise_name"));
        workout.setExerciseType(rs.getString("exercise_type"));
        workout.setSetsCount(rs.getInt("sets_count"));
        workout.setRepsPerSet(rs.getInt("reps_per_set"));
        workout.setWeightKg(rs.getDouble("weight_kg"));
        workout.setDurationMinutes(rs.getInt("duration_minutes"));
        workout.setCaloriesBurned(rs.getDouble("calories_burned"));
        workout.setWorkoutDate(rs.getDate("workout_date"));
        workout.setWorkoutTime(rs.getTime("workout_time"));
        workout.setNotes(rs.getString("notes"));
        workout.setCreatedAt(rs.getTimestamp("created_at"));
        workout.setUpdatedAt(rs.getTimestamp("updated_at"));
        return workout;
    }

    /**
     * Calculate calories burned for a workout using user weight and MET value
     */
    public double calculateCaloriesBurned(String exerciseName, int durationMinutes, double userWeight) {
        double metValue = getMetValueForExercise(exerciseName);
        double durationHours = durationMinutes / 60.0;
        return metValue * userWeight * durationHours;
    }
}