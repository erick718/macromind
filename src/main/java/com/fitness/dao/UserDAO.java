package com.fitness.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.fitness.model.User;
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

        String query = "INSERT INTO users (username, email, hashed_password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
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
                
                // Load profile data if available
                user.setAge(rs.getInt("age"));
                user.setWeight(rs.getFloat("weight"));
                user.setHeight(rs.getInt("height"));
                user.setGoal(rs.getString("goal"));
                user.setDietaryPreference(rs.getString("dietary_preference"));
                user.setFitnessLevel(rs.getString("fitness_level"));
                user.setAvailability(rs.getInt("availability"));
                
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
    public void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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

    public void savePasswordResetToken(int userId, String token, LocalDateTime expiryTime){
    
        // NOTE: Your database column names must match: reset_token and token_expiry
        String sql = "UPDATE users SET reset_token = ?, token_expiry = ? WHERE user_id = ?";

        try (Connection conn = getConnection(); // Use your method to get a connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            // setObject handles modern Java Time types for most JDBC drivers
            stmt.setObject(2, expiryTime); 
            stmt.setInt(3, userId);

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    public boolean updatePasswordAndClearToken(String token, String newRawPassword) {
        
        // 1. HASH THE NEW PASSWORD (using your utility class)
        String newHashedPassword = SecurityUtil.hashPassword(newRawPassword);

        // 2. The SQL statement updates the password and CLEARS the token fields.
        String sql = "UPDATE users SET hashed_password = ?, reset_token = NULL, token_expiry = NULL "
                   + "WHERE reset_token = ? AND token_expiry > ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, token);
            stmt.setObject(3, LocalDateTime.now()); // Check against current time

            int rowsUpdated = stmt.executeUpdate();
            
            // Returns true if exactly one row was updated (meaning the token was valid and not expired)
            return rowsUpdated == 1; 
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByResetToken(String token) {
    // Select user data where the token matches
    String sql = "SELECT user_id, email, hashed_password, token_expiry, reset_token FROM users WHERE reset_token = ?";
    User user = null;

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, token);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                
                // CRITICAL: Fetch the expiry time to check validity in the servlet
                user.setTokenExpiry(rs.getObject("token_expiry", LocalDateTime.class));
                
                user.setResetToken(rs.getString("reset_token")); 
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return user;
}

}