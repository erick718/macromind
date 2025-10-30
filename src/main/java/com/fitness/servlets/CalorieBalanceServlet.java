package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.fitness.dao.ExerciseLogDAO;
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

    private final FoodEntryDAO foodEntryDAO = new FoodEntryDAO();
    private final ExerciseLogDAO exerciseLogDAO = new ExerciseLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // --- Daily recommended calories (Mifflin-St Jeor formula) ---
        double weight = user.getWeight(); // kg
        int height = user.getHeight();    // cm
        int age = user.getAge();          // years
        String activity = user.getFitnessLevel() != null ? user.getFitnessLevel() : "moderate";
        String goal = user.getGoal() != null ? user.getGoal() : "maintain";

        // Basic BMR (assuming male for simplicity)
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

        switch (goal.toLowerCase()) {
            case "lose":
                recommendedCalories -= 500;
                break;
            case "gain":
                recommendedCalories += 500;
                break;
            default:
                // maintain
                break;
        }

        // --- Get today's data ---
        LocalDate today = LocalDate.now();
        List<FoodEntry> entries = foodEntryDAO.getFoodEntriesByUser(user.getUserId(), today);
        int totalIntake = entries.stream().mapToInt(FoodEntry::getCalories).sum();
        int totalBurned = exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(user.getUserId(), today);
        int netCalories = totalIntake - totalBurned;
        double remainingCalories = recommendedCalories - netCalories;

        // --- Set attributes for JSP ---
        request.setAttribute("entries", entries);
        request.setAttribute("recommendedCalories", (int) recommendedCalories);
        request.setAttribute("totalIntake", totalIntake);
        request.setAttribute("totalBurned", totalBurned);
        request.setAttribute("netCalories", netCalories);
        request.setAttribute("remainingCalories", (int) remainingCalories);

        request.getRequestDispatcher("/calorieBalance.jsp").forward(request, response);
    }
}