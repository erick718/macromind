package com.fitness.servlets;

import java.io.IOException;

import com.fitness.Model.User;
import com.fitness.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ResetPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("reset_user");
        Boolean verified = (Boolean) session.getAttribute("security_verified");
        
        // Verify user is in session and security question was answered correctly
        if (user == null || verified == null || !verified) {
            response.sendRedirect("forgot_password.jsp");
            return;
        }
        
        String newPassword = request.getParameter("new_password");
        String confirmPassword = request.getParameter("confirm_password");
        
        if (newPassword != null) {
            newPassword = newPassword.trim();
        }
        if (confirmPassword != null) {
            confirmPassword = confirmPassword.trim();
        }
        
        // Validate input
        if (newPassword == null || newPassword.isEmpty() || 
            confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("message", "Both password fields are required");
            request.getRequestDispatcher("reset_password.jsp").forward(request, response);
            return;
        }
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Passwords do not match");
            request.getRequestDispatcher("reset_password.jsp").forward(request, response);
            return;
        }
        
        // Check minimum password length
        if (newPassword.length() < 6) {
            request.setAttribute("message", "Password must be at least 6 characters long");
            request.getRequestDispatcher("reset_password.jsp").forward(request, response);
            return;
        }
        
        // Reset the password
        UserDAO dao = new UserDAO();
        boolean success = dao.resetPassword(user.getEmail(), newPassword);
        
        if (success) {
            // Clear session data
            session.removeAttribute("reset_user");
            session.removeAttribute("security_verified");
            
            // Redirect to success page
            response.sendRedirect("password_reset_success.jsp");
        } else {
            request.setAttribute("message", "Failed to reset password. Please try again.");
            request.getRequestDispatcher("reset_password.jsp").forward(request, response);
        }
    }
}
