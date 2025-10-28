package com.fitness.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fitness.model.FoodEntry;
import com.fitness.util.DBConnection;

public class FoodEntryDAO {

    public void createFoodEntry(FoodEntry entry) {
        String sql = "INSERT INTO food_entries (user_id, food_name, calories, protein, carbs, fat, serving_size, entry_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entry.getUserId());
            ps.setString(2, entry.getFoodName());
            ps.setInt(3, entry.getCalories());
            ps.setFloat(4, entry.getProtein());
            ps.setFloat(5, entry.getCarbs());
            ps.setFloat(6, entry.getFat());
            ps.setString(7, entry.getServingSize());
            ps.setDate(8, new java.sql.Date(entry.getEntryDate().getTime()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                entry.setEntryId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FoodEntry> getFoodEntriesByUser(int userId, Date date) {
        List<FoodEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM food_entries WHERE user_id = ? AND entry_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, new java.sql.Date(date.getTime()));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FoodEntry entry = new FoodEntry();
                entry.setEntryId(rs.getInt("entry_id"));
                entry.setUserId(rs.getInt("user_id"));
                entry.setFoodName(rs.getString("food_name"));
                entry.setCalories(rs.getInt("calories"));
                entry.setProtein(rs.getFloat("protein"));
                entry.setCarbs(rs.getFloat("carbs"));
                entry.setFat(rs.getFloat("fat"));
                entry.setServingSize(rs.getString("serving_size"));
                entry.setEntryDate(rs.getDate("entry_date"));
                entries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }
}