package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.fitness.dao.FoodEntryDAO;
import com.fitness.model.FoodEntry;
import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/calorieBalance")
public class CalorieBalanceServlet extends HttpServlet {

    private FoodEntryDAO foodEntryDAO = new FoodEntryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // --- Recommended daily calories calculation (Mifflin-St Jeor) ---
        double weight = user.getWeight(); // kg
        int height = user.getHeight();    // cm
        int age = user.getAge();          // years
        String activity = user.getFitnessLevel() != null ? user.getFitnessLevel() : "moderate";
        String goal = user.getGoal() != null ? user.getGoal() : "maintain";

        // Male assumption
        double bmr = 10 * weight + 6.25 * height - 5 * age + 5;

        double activityFactor;
        switch (activity.toLowerCase()) {
            case "low":
                activityFactor = 1.2;
                break;
            case "high":
                activityFactor = 1.9;
                break;
            default:
                activityFactor = 1.55;
                break;
        }

        double recommendedCalories = bmr * activityFactor;

        // Adjust based on goal
        switch (goal.toLowerCase()) {
            case "lose":
                recommendedCalories -= 500;
                break;
            case "gain":
                recommendedCalories += 500;
                break;
            default:
                break; // maintain
        }

        // --- Get today’s entries ---
        LocalDate today = LocalDate.now();
        List<FoodEntry> entries = foodEntryDAO.getFoodEntriesByUser(user.getUserId(), today);

        int totalCalories = 0;
        float totalProtein = 0;
        float totalCarbs = 0;
        float totalFat = 0;

        for (FoodEntry e : entries) {
            totalCalories += e.getCalories();
            totalProtein += e.getProtein();
            totalCarbs += e.getCarbs();
            totalFat += e.getFat();
        }

        double remainingCalories = recommendedCalories - totalCalories;

        // Set attributes for JSP
        request.setAttribute("entries", entries);
        request.setAttribute("totalCalories", totalCalories);
        request.setAttribute("totalProtein", totalProtein);
        request.setAttribute("totalCarbs", totalCarbs);
        request.setAttribute("totalFat", totalFat);
        request.setAttribute("recommendedCalories", (int) recommendedCalories);
        request.setAttribute("remainingCalories", (int) remainingCalories);

        request.getRequestDispatcher("/calorieBalance.jsp").forward(request, response);
    }
}