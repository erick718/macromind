package com.fitness.servlets;

import java.io.IOException;

import com.fitness.Model.User;
import com.fitness.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class VerifySecurityAnswerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("reset_user");
        
        // Verify user is in session
        if (user == null || user.getSecurityQuestion() == null) {
            response.sendRedirect("forgot_password.jsp");
            return;
        }
        
        String securityAnswer = request.getParameter("security_answer");
        
        if (securityAnswer != null) {
            securityAnswer = securityAnswer.trim();
        }
        
        // Validate input
        if (securityAnswer == null || securityAnswer.isEmpty()) {
            request.setAttribute("message", "Security answer is required");
            request.getRequestDispatcher("security_question.jsp").forward(request, response);
            return;
        }
        
        // Verify the security answer (case-insensitive)
        String normalizedAnswer = securityAnswer.toLowerCase().trim();
        boolean isCorrect = SecurityUtil.checkPassword(normalizedAnswer, user.getSecurityAnswerHash());
        
        if (isCorrect) {
            // Mark as verified in session
            session.setAttribute("security_verified", true);
            response.sendRedirect("reset_password.jsp");
        } else {
            request.setAttribute("message", "Incorrect answer. Please try again.");
            request.getRequestDispatcher("security_question.jsp").forward(request, response);
        }
    }
}
