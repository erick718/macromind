package com.fitness.servlets;

import java.io.IOException;

import com.fitness.Model.User;
import com.fitness.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ForgotPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        if (email != null) {
            email = email.trim();
        }
        
        // Validate input
        if (email == null || email.isEmpty()) {
            request.setAttribute("message", "Email address is required");
            request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
            return;
        }
        
        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);
        
        // Check if user exists
        if (user == null) {
            request.setAttribute("message", "No account found with that email address");
            request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
            return;
        }
        
        // Check if user has a security question set
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isEmpty()) {
            request.setAttribute("message", "This account does not have a security question set. Please contact support.");
            request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
            return;
        }
        
        // Store user in session for the next step
        HttpSession session = request.getSession();
        session.setAttribute("reset_user", user);
        session.removeAttribute("security_verified"); // Clear any previous verification
        
        // Redirect to security question page
        response.sendRedirect("security_question.jsp");
    }
}
