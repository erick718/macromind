package com.fitness.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitness.model.FoodEntry;
import com.fitness.util.DBConnection;

public class FoodEntryDAO {

    // Add a new food entry to the database
    public void addFoodEntry(FoodEntry entry) {
        String sql = "INSERT INTO food_entries " +
                     "(user_id, food_name, calories, protein, carbs, fat, consumed_oz, date_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entry.getUserId());
            stmt.setString(2, entry.getFoodName());
            stmt.setInt(3, entry.getCalories());
            stmt.setFloat(4, (float) entry.getProtein());
            stmt.setFloat(5, (float) entry.getCarbs());
            stmt.setFloat(6, (float) entry.getFat());
            stmt.setFloat(7, (float) entry.getConsumedOz());
            stmt.setTimestamp(8, Timestamp.valueOf(entry.getDateTime()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all entries for a user on a specific date
    public List<FoodEntry> getEntriesByUserAndDate(int userId, LocalDate date) {
        List<FoodEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM food_entries WHERE user_id = ? AND DATE(date_time) = ? ORDER BY date_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FoodEntry entry = new FoodEntry();
                entry.setEntryId(rs.getInt("id"));
                entry.setUserId(rs.getInt("user_id"));
                entry.setFoodName(rs.getString("food_name"));
                entry.setCalories(rs.getInt("calories"));
                entry.setProtein(rs.getFloat("protein")); // cast to float already in DB
                entry.setCarbs(rs.getFloat("carbs"));
                entry.setFat(rs.getFloat("fat"));
                entry.setConsumedOz(rs.getFloat("consumed_oz"));
                entry.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }
}