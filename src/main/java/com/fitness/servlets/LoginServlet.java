package com.fitness.servlets;

import java.io.IOException;

import com.fitness.Model.User;
import com.fitness.dao.UserDAO;
import com.fitness.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Trim whitespace from input
        if (email != null) email = email.trim();
        if (password != null) password = password.trim();

        // Validate input
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("message", "Email and password are required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);

        // Debug logging (remove in production)
        System.out.println("Login attempt for email: " + email);
        System.out.println("User found: " + (user != null));

        if (user != null && SecurityUtil.checkPassword(password, user.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("dashboard");
        } else {
            String message = "Invalid email or password";
            if (user == null) {
                message = "No account found with that email address";
            } else if (user.getPassword() == null) {
                message = "Account error: password not set. Please contact support.";
            }
            request.setAttribute("message", message);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}