package com.fitness.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fitness.model.User;

public class UserDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/macromind";
        String user = "root";
        String password = "ed0924!";
        return DriverManager.getConnection(url, user, password);
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
            return false; // fallback if DB not reachable
        }
    }

    // Create new user
    public void createUser(User user) {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
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
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update user
    public void updateUser(User user) {
        String query = "UPDATE users SET name=?, email=? WHERE user_id=?";
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
}