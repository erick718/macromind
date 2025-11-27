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
        
        User user = userDAO.getUserByEmail(email);

        if (user != null) {
            
            // 2. Generate Token and Expiry Time
            String token = SecurityUtil.generateSecureToken();
            // Token is valid for 30 minutes
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30); 

            userDAO.savePasswordResetToken(user.getUserId(),token, expiryTime);

            // 4. Send Reset Link (CRITICAL STEP - MOCK FOR NOW)
            // In a real application, you would use JavaMail API here.
            
            // The reset link the user receives in their email:
            String resetLink = request.getScheme() + "://" + request.getServerName() + 
                               ":" + request.getServerPort() + request.getContextPath() + 
                               "/resetPassword?token=" + token;
            
            // --- MOCK ACTION ---
            // For testing/development, print the link to the console instead of emailing:
            System.out.println("--- PASSWORD RESET LINK GENERATED ---");
            System.out.println("Send to: " + email);
            System.out.println("Link: " + resetLink);
            System.out.println("-------------------------------------");
            
            // 5. Provide User Feedback
            request.setAttribute("message", "A password reset link has been sent to your email address.");
            
        } else {
            // Important Security Note: Do NOT tell the user if the email doesn't exist.
            // This prevents attackers from guessing valid emails. Send a generic success message.
            request.setAttribute("message", "If an account exists for that email, a password reset link has been sent.");
        }
        
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
    
    // You may also want a doGet method to display the initial email submission form.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
    }
}
    
