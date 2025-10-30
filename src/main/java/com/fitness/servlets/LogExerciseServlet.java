package com.fitness.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogExerciseServlet extends HttpServlet {

    // Rough MET estimates for categories
    private double getCaloriesBurnedPerMinute(String type, double weightKg) {
        double met;
        switch (type.toLowerCase()) {
            case "cardio":
                met = 8; // e.g., running or cycling
                break;
            case "weightlifting":
                met = 6; // moderate lifting
                break;
            case "hiit":
                met = 10; // high intensity interval training
                break;
            default:
                met = 5; // general
                break;
        }

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
        double weightKg = session.getAttribute("weight") != null
                ? Double.parseDouble(session.getAttribute("weight").toString())
                : 70;

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

        // Redirect to calorie balance page after logging exercise
        response.sendRedirect(request.getContextPath() + "/calorieBalance");
    }
}