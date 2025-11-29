package com.fitness.dao;

import java.sql.Connection;
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
    public void createFoodEntry(FoodEntry entry) {
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
            stmt.setDouble(7, (float) entry.getConsumedOz());
            stmt.setTimestamp(8, Timestamp.valueOf(entry.getEntryDate()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all entries for a user on a specific date
    public List<FoodEntry> getFoodEntriesByUser(int userId, LocalDate date) {
        List<FoodEntry> entries = new ArrayList<>();
        // Using the exact same query as the working test servlet
        String sql = "SELECT id, user_id, food_name, calories, protein, carbs, fat, consumed_oz, date_time FROM food_entries WHERE user_id = ? ORDER BY date_time DESC";

        System.out.println("DEBUG DAO: Executing query: " + sql);
        System.out.println("DEBUG DAO: Parameters - userId: " + userId + ", date: " + date);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("DEBUG DAO: Connection URL: " + conn.getMetaData().getURL());
            System.out.println("DEBUG DAO: Setting parameter 1 to userId: " + userId);
            
            stmt.setInt(1, userId);
            
            System.out.println("DEBUG DAO: About to execute query...");
            ResultSet rs = stmt.executeQuery();
            System.out.println("DEBUG DAO: Query executed successfully");
            
            int count = 0;
            
            while (rs.next()) {
                count++;
                System.out.println("DEBUG DAO: Processing row " + count);
                
                FoodEntry entry = new FoodEntry();
                entry.setEntryId(rs.getInt("id"));
                entry.setUserId(rs.getInt("user_id"));
                entry.setFoodName(rs.getString("food_name"));
                entry.setCalories(rs.getInt("calories"));
                entry.setProtein(rs.getFloat("protein")); 
                entry.setCarbs(rs.getFloat("carbs"));
                entry.setFat(rs.getFloat("fat"));
                entry.setConsumedOz(rs.getFloat("consumed_oz"));
                entry.setEntryDate(rs.getTimestamp("date_time").toLocalDateTime());
                entries.add(entry);
                
                System.out.println("DEBUG DAO: Added entry ID " + entry.getEntryId() + ": " + entry.getFoodName() + " (" + entry.getCalories() + " cal)");
            }
            
            System.out.println("DEBUG DAO: Finished processing. Total entries found: " + count);
            System.out.println("DEBUG DAO: Final list size: " + entries.size());
            
        } catch (Exception e) {
            System.out.println("DEBUG DAO: Exception occurred: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
        return entries;
    }
}