package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;
import com.fitness.util.SecurityUtil; 

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/forgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String submittedSecurityAnswer = request.getParameter("security_nswer");
        
        User user = userDAO.getUserByEmailForSecurityCheck(email);

        if (user != null) {
            String securityAnswerFromUser = user.getSecurityAnswer();
            
            if (submittedSecurityAnswer != null && securityAnswerFromUser != null &&
                submittedSecurityAnswer.equalsIgnoreCase(securityAnswerFromUser)) {
                
                String token = SecurityUtil.generateSecureToken();
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15); 
                userDAO.savePasswordResetToken(user.getUserId(), token, expiryTime);

                response.sendRedirect(request.getContextPath() + "/resetPassword?token=" + token);
            return;
            } else {
            // Failed verification
            request.setAttribute("message", "Verification failed: Invalid email or security answer.");
        }
        } else {
            // User not found
            request.setAttribute("message", "Verification failed: Invalid email or security answer.");
        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
            
            
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
    }
    
}
    
