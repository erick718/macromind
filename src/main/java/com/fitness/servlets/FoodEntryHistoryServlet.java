package com.fitness.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fitness.dao.FoodEntryDAO;
import com.fitness.Model.FoodEntry;
import com.fitness.Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FoodEntryHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        System.out.println("DEBUG SERVLET: Creating FoodEntryDAO instance...");
        FoodEntryDAO dao = new FoodEntryDAO();
        System.out.println("DEBUG SERVLET: DAO created successfully");
        
        // Call the DAO method to get food entries for today
        java.time.LocalDate today = java.time.LocalDate.now();
        System.out.println("DEBUG SERVLET: Getting food entries for user " + user.getUserId() + " on date " + today);
        
        List<FoodEntry> foodHistory = null;
        try {
            foodHistory = dao.getFoodEntriesByUser(user.getUserId(), today);
            System.out.println("DEBUG SERVLET: DAO method returned, list size: " + (foodHistory != null ? foodHistory.size() : "null"));
            
            if (foodHistory != null) {
                System.out.println("DEBUG SERVLET: Found " + foodHistory.size() + " food entries");
                for (FoodEntry entry : foodHistory) {
                    System.out.println("DEBUG SERVLET: Food entry - " + entry.getFoodName() + ", " + entry.getCalories() + " cal");
                }
            }
            
        } catch (Exception e) {
            System.out.println("DEBUG SERVLET: Exception calling DAO: " + e.getMessage());
            e.printStackTrace();
            // Set empty list so page doesn't crash
            foodHistory = new ArrayList<FoodEntry>();
        }

        request.setAttribute("foodHistory", foodHistory);
        request.getRequestDispatcher("food-history.jsp").forward(request, response);
    }
}