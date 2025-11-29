package com.fitness.servlets;

import java.io.IOException;
import java.io.OutputStream;

import com.fitness.dao.UserDAO;
import com.fitness.Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GetProfilePictureServlet", urlPatterns = {"/api/profile/picture"})
public class GetProfilePictureServlet extends HttpServlet {

    private int resolveUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                return user.getUserId();
            }
        }
        return -1;
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        int userId = resolveUserId(req);
        
        if (userId == -1) {
            // No user logged in, return placeholder
            sendPlaceholder(resp);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getProfilePicture(userId);
        
        if (user == null || user.getProfilePicture() == null) {
            // No picture in database, return placeholder
            sendPlaceholder(resp);
            return;
        }

        // Return the stored image
        resp.setContentType(user.getProfilePictureType());
        try (OutputStream out = resp.getOutputStream()) {
            out.write(user.getProfilePicture());
        }
    }
    
    private void sendPlaceholder(HttpServletResponse resp) throws IOException {
        // Tiny transparent placeholder PNG
        resp.setContentType("image/png");
        try (OutputStream out = resp.getOutputStream()) {
            byte[] png = new byte[]{
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                    0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4, (byte) 0x89,
                    0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
                    0x78, (byte) 0xDA, 0x63, 0x00, 0x01, 0x00, 0x00,
                    0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4,
                    0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
                    (byte) 0xAE, 0x42, 0x60, (byte) 0x82
            };
            out.write(png);
        }
    }
}
