package com.fitness.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fitness.Model.User;
import com.fitness.util.DBConnection;
import com.fitness.util.SecurityUtil;

public class UserDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Check if email already exists
    public boolean isEmailTaken(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection error");
            return false; // fallback if DB not reachable
        }
    }

    // Create new user
    public void createUser(User user) {
        String rawPassword = user.getPassword();
        String hashedPassword = SecurityUtil.hashPassword(rawPassword);
        
        // Hash security answer if provided
        String securityAnswerHash = null;
        if (user.getSecurityAnswerHash() != null && !user.getSecurityAnswerHash().isEmpty()) {
            securityAnswerHash = SecurityUtil.hashPassword(user.getSecurityAnswerHash().toLowerCase().trim());
        }
        
        String query = "INSERT INTO users (username, email, hashed_password, security_question, security_answer_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getSecurityQuestion());
            ps.setString(5, securityAnswerHash);
            ps.executeUpdate();
            

            // Get generated user_id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setUserId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get user by email
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("hashed_password"));
                
                // Load security question and answer hash if available
                try {
                    user.setSecurityQuestion(rs.getString("security_question"));
                    user.setSecurityAnswerHash(rs.getString("security_answer_hash"));
                } catch (SQLException e) {
                    // Security columns don't exist yet - that's okay
                }
                
                // Load profile data if available (check if columns exist)
                try {
                    user.setAge(rs.getInt("age"));
                    user.setWeight(rs.getFloat("weight"));
                    user.setHeight(rs.getInt("height"));
                    user.setGoal(rs.getString("goal"));
                    user.setDietaryPreference(rs.getString("dietary_preference"));
                    user.setFitnessLevel(rs.getString("fitness_level"));
                    user.setAvailability(rs.getInt("availability"));
                } catch (SQLException e) {
                    // Profile columns don't exist yet - that's okay, skip them
                    System.out.println("Note: Profile columns not found in users table. Run database setup script to add them.");
                }
                
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting user by email: " + e.getMessage());
        }
        return null;
    }

    // Update user
    public void updateUser(User user) {
        String query = "UPDATE users SET username=?, email=? WHERE user_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getUserId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete user
public boolean deleteUser(int userId) {
    String sql = "DELETE FROM users WHERE user_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        int rows = stmt.executeUpdate();

        return rows > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
    // Update user profile information
    public void updateUserProfile(User user) {
        String query = "UPDATE users SET age=?, weight=?, height=?, goal=?, dietary_preference=?, fitness_level=?, availability=? WHERE user_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, user.getAge());
            ps.setFloat(2, user.getWeight());
            ps.setInt(3, user.getHeight());
            ps.setString(4, user.getGoal());
            ps.setString(5, user.getDietaryPreference());
            ps.setString(6, user.getFitnessLevel());
            ps.setInt(7, user.getAvailability());
            ps.setInt(8, user.getUserId());
            
            int rowsUpdated = ps.executeUpdate();
            System.out.println("Profile updated: " + rowsUpdated + " rows affected");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating user profile: " + e.getMessage());
        }
    }


    // Reset user password
    public boolean resetPassword(String email, String newPassword) {
        String hashedPassword = SecurityUtil.hashPassword(newPassword);
        String query = "UPDATE users SET hashed_password=? WHERE email=?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            int rowsUpdated = ps.executeUpdate();
            
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }

    // Save profile picture to database
    public void saveProfilePicture(int userId, byte[] imageData, String contentType) {
        String query = "UPDATE users SET profile_picture=?, profile_picture_type=? WHERE user_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setBytes(1, imageData);
            ps.setString(2, contentType);
            ps.setInt(3, userId);
            
            int rowsUpdated = ps.executeUpdate();
            System.out.println("Profile picture saved: " + rowsUpdated + " rows affected");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving profile picture: " + e.getMessage());
        }
    }

    // Get profile picture from database
    public User getProfilePicture(int userId) {
        String query = "SELECT profile_picture, profile_picture_type FROM users WHERE user_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(userId);
                user.setProfilePicture(rs.getBytes("profile_picture"));
                user.setProfilePictureType(rs.getString("profile_picture_type"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving profile picture: " + e.getMessage());
        }
        return null;
    }
}
