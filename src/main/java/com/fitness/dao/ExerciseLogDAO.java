package com.fitness.dao;

import com.fitness.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class ExerciseLogDAO {

    // Get total calories burned for a user on a specific date
    public int getTotalCaloriesBurnedByUserAndDate(int userId, LocalDate date) {
        String sql = "SELECT SUM(calories_burned) AS total_burned FROM exercise_log WHERE user_id = ? AND DATE(date_logged) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_burned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}