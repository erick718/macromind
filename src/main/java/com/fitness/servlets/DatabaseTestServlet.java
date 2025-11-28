package com.fitness.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fitness.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/database-test")
public class DatabaseTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Database Test</title></head><body>");
        out.println("<h1>Database Connection Test</h1>");
        
        // Test 1: Basic connection
        out.println("<h2>Test 1: Database Connection</h2>");
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                out.println("<p style='color: green;'>✓ Database connection successful</p>");
                out.println("<p>Connection URL: " + conn.getMetaData().getURL() + "</p>");
                conn.close();
            } else {
                out.println("<p style='color: red;'>✗ Database connection is null</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color: red;'>✗ Database connection failed: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }
        
        // Test 2: Check if food_entries table exists
        out.println("<h2>Test 2: Table Structure</h2>");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SHOW TABLES LIKE 'food_entries'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                out.println("<p style='color: green;'>✓ food_entries table exists</p>");
            } else {
                out.println("<p style='color: red;'>✗ food_entries table does not exist</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color: red;'>✗ Error checking table: " + e.getMessage() + "</p>");
        }
        
        // Test 3: Count total records
        out.println("<h2>Test 3: Record Count</h2>");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) as total FROM food_entries";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                out.println("<p>Total food entries in database: " + total + "</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color: red;'>✗ Error counting records: " + e.getMessage() + "</p>");
        }
        
        // Test 4: Count records for user ID 6
        out.println("<h2>Test 4: Records for User ID 6</h2>");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) as user_total FROM food_entries WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 6);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userTotal = rs.getInt("user_total");
                out.println("<p>Food entries for user ID 6: " + userTotal + "</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color: red;'>✗ Error counting user records: " + e.getMessage() + "</p>");
        }
        
        // Test 5: Sample records for user ID 6
        out.println("<h2>Test 5: Sample Records for User ID 6</h2>");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, food_name, calories, date_time FROM food_entries WHERE user_id = ? ORDER BY date_time DESC LIMIT 5";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 6);
            ResultSet rs = stmt.executeQuery();
            
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Food Name</th><th>Calories</th><th>Date Time</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("food_name") + "</td>");
                out.println("<td>" + rs.getInt("calories") + "</td>");
                out.println("<td>" + rs.getTimestamp("date_time") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
        } catch (Exception e) {
            out.println("<p style='color: red;'>✗ Error retrieving sample records: " + e.getMessage() + "</p>");
        }
        
        out.println("<p><a href='food-history'>Back to food history</a></p>");
        out.println("</body></html>");
    }
}