package com.fitness.servlets;

import com.fitness.dao.FoodEntryDAO;
import com.fitness.dao.FoodItemDAO;
import com.fitness.model.FoodEntry;
import com.fitness.model.FoodItem;
import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/FoodEntryServlet")
public class FoodEntryServlet extends HttpServlet {
    private FoodEntryDAO foodEntryDAO;
    private FoodItemDAO foodItemDAO;

    @Override
    public void init() {
        foodEntryDAO = new FoodEntryDAO();
        foodItemDAO = new FoodItemDAO(); // your existing DAO with hardcoded meals
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

        String foodName = request.getParameter("foodName");
        double consumedOunces = parseDoubleSafe(request.getParameter("consumedOunces"));

        int calories = parseIntSafe(request.getParameter("calories"));
        double protein = parseDoubleSafe(request.getParameter("protein"));
        double carbs = parseDoubleSafe(request.getParameter("carbs"));
        double fat = parseDoubleSafe(request.getParameter("fat"));

        // Check if this is a predefined meal
        FoodItem predefined = foodItemDAO.findByName(foodName);
        if (predefined != null) {
            double multiplier = consumedOunces / predefined.getServingSize();
            calories = (int)(predefined.getCalories() * multiplier);
            protein = predefined.getProtein() * multiplier;
            carbs = predefined.getCarbs() * multiplier;
            fat = predefined.getFat() * multiplier;
        }

        // Create entry (predefined scaled or custom)
        FoodEntry entry = new FoodEntry();
        entry.setUserId(user.getUserId());
        entry.setFoodName(foodName);
        entry.setCalories(calories);
        entry.setProtein((float) protein);
        entry.setCarbs((float) carbs);
        entry.setFat((float) fat);
        entry.setConsumedOz(consumedOunces);  // <-- matches the model now
        entry.setDateTime(LocalDateTime.now());

        foodEntryDAO.addFoodEntry(entry);
        response.sendRedirect(request.getContextPath() + "/calorieBalance");
    }

    private int parseIntSafe(String value) {
        try { return Integer.parseInt(value); } catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(String value) {
        try { return Double.parseDouble(value); } catch (Exception e) { return 0; }
    }

    private double parseOunces(String servingSize) {
        if (servingSize == null || servingSize.isEmpty()) return 1;
        String[] parts = servingSize.split(" ");
        try { return Double.parseDouble(parts[0]); }
        catch (Exception e) { return 1; }
    }
}