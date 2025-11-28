package com.fitness.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.fitness.util.DBConnection;

public class ExerciseLogDAO {

    // Get total calories burned for a user on a specific date from daily_fitness_summary
    public int getTotalCaloriesBurnedByUserAndDate(int userId, LocalDate date) {
        String sql = "SELECT total_calories_burned FROM daily_fitness_summary WHERE user_id = ? AND summary_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return (int) Math.round(rs.getDouble("total_calories_burned"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}