package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();

    // Handles the link click from the email: /resetPassword?token=XYZ
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        
        if (token == null || token.isEmpty()) {
            request.setAttribute("error", "Invalid or missing reset token.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // 1. Validate the Token in the database (You need this DAO method)
        User user = userDAO.getUserByResetToken(token);

        if (user == null) {
            request.setAttribute("error", "The reset token is not valid.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // 2. Check for Expiry
        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            request.setAttribute("error", "The password reset link has expired.");
            // OPTIONAL: Clear the expired token in the DB here
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Token is valid and unexpired: Display the password change form.
        request.setAttribute("token", token); // Pass token to the JSP via a hidden field
        request.getRequestDispatcher("reset_password_form.jsp").forward(request, response);
    }
    
    // Handles the form submission with the new password
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // 1. Basic Validation (Check for empty fields, password match)
        if (token == null || newPassword == null || !newPassword.equals(confirmPassword)) {
             request.setAttribute("error", "Passwords do not match or fields are empty.");
             request.getRequestDispatcher("reset_password_form.jsp").forward(request, response);
             return;
        }

        // 2. Update Password and Clear Token (You need this DAO method)
        boolean success = userDAO.updatePasswordAndClearToken(token, newPassword); 

        if (success) {
            request.setAttribute("message", "Password successfully reset! You can now log in.");
            response.sendRedirect("login.jsp");
        } else {
            // This happens if the token expired between GET and POST, or was already used.
            request.setAttribute("error", "Password reset failed. Please request a new link.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
