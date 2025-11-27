package com.fitness.servlets;

import java.io.IOException;
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

@WebServlet("/food-history") 
public class FoodEntryHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        FoodEntryDAO dao = new FoodEntryDAO();
        // Call the DAO method to get food entries for today
        List<FoodEntry> foodHistory = dao.getFoodEntriesByUser(user.getUserId(), java.time.LocalDate.now()); 

        request.setAttribute("foodHistory", foodHistory);

        // Forward to the new JSP file
        request.getRequestDispatcher("food-history.jsp").forward(request, response);
    }
}