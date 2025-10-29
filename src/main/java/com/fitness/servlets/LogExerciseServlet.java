package com.fitness.servlets;

import com.fitness.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class LogExerciseServlet extends HttpServlet {

    // Rough MET estimates for categories
    private double getCaloriesBurnedPerMinute(String type, double weightKg) {
        double met = switch (type.toLowerCase()) {
            case "cardio" -> 8;        // e.g., running or cycling
            case "weightlifting" -> 6; // moderate lifting
            case "hiit" -> 10;         // high intensity interval training
            default -> 5;              // general
        };
        // Calories burned per minute = (MET * 3.5 * weightKg) / 200
        return (met * 3.5 * weightKg) / 200.0;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String exerciseName = request.getParameter("exercise_name");
        String exerciseType = request.getParameter("exercise_type");
        int duration = Integer.parseInt(request.getParameter("duration")); // in minutes

        // Get user weight from session for calorie estimation
        double weightKg = session.getAttribute("weight") != null ? 
                          Double.parseDouble(session.getAttribute("weight").toString()) : 70;

        int caloriesBurned = (int) Math.round(getCaloriesBurnedPerMinute(exerciseType, weightKg) * duration);

        try (Connection conn = com.fitness.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO exercise_log (user_id, exercise_name, calories_burned, date_logged) VALUES (?, ?, ?, CURDATE())"
            );
            ps.setInt(1, user.getUserId());
            ps.setString(2, exerciseName);
            ps.setInt(3, caloriesBurned);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/calorieBalance"); // redirect to calorie balance
    }
}