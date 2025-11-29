package com.fitness.servlets;

import java.io.IOException;

import com.fitness.Model.User;
import com.fitness.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RemoveProfilePictureServlet", urlPatterns = {"/api/profile/picture/remove"})
public class RemoveProfilePictureServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (user == null) {
            response.setStatus(401);
            response.getWriter().write("{\"message\":\"User not logged in\"}");
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            
            // Remove profile picture by setting it to null
            userDAO.saveProfilePicture(user.getUserId(), null, null);
            
            // Update the user object in session
            user.setProfilePicture(null);
            user.setProfilePictureType(null);
            session.setAttribute("user", user);
            
            response.getWriter().write("{\"message\":\"Profile picture removed\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"message\":\"Error removing profile picture: " + e.getMessage() + "\"}");
        }
    }
}
