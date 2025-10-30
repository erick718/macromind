package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fitness.dao.FoodEntryDAO;
import com.fitness.model.FoodEntry;
import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FoodEntryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String foodName = request.getParameter("foodName");
            int calories = Integer.parseInt(request.getParameter("calories"));
            float protein = Float.parseFloat(request.getParameter("protein"));
            float carbs = Float.parseFloat(request.getParameter("carbs"));
            float fat = Float.parseFloat(request.getParameter("fat"));
            double consumed_oz = Double.parseDouble(request.getParameter("consumed_oz"));
            LocalDateTime entryDate = LocalDateTime.now();

            FoodEntry entry = new FoodEntry(user.getUserId(), foodName, calories, protein, carbs, fat, consumed_oz, entryDate);
            FoodEntryDAO dao = new FoodEntryDAO();
            dao.createFoodEntry(entry);

            session.setAttribute("message", "Food entry logged successfully!");
            response.sendRedirect("dashboard");
        } catch (NumberFormatException e) {
            System.err.println("Error parsing food entry data: " + e.getMessage());
            request.setAttribute("error", "Please enter valid numbers for calories, protein, carbs, fat, and serving size.");
            request.getRequestDispatcher("food_entry.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error saving food entry: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Failed to save food entry. Please try again.");
            request.getRequestDispatcher("food_entry.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        FoodEntryDAO dao = new FoodEntryDAO();
        List<FoodEntry> entries = dao.getFoodEntriesByUser(user.getUserId(), LocalDate.now())
                .stream()
                .map(o -> (FoodEntry) o)
                .collect(Collectors.toList());
        request.setAttribute("entries", entries);
        request.getRequestDispatcher("food_entry.jsp").forward(request, response);
        
    }
}
