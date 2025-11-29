package com.fitness.servlets;

import java.io.IOException;
import java.io.InputStream;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "SaveProfilePictureServlet", urlPatterns = {"/api/profile/picture/save"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 64,        // 64 KB
        maxFileSize = 1024 * 1024 * 10,       // 10 MB per file
        maxRequestSize = 1024 * 1024 * 12     // 12 MB request
)
public class SaveProfilePictureServlet extends HttpServlet {

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
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        int userId = resolveUserId(req);
        
        if (userId == -1) {
            resp.setStatus(401);
            resp.getWriter().write("{\"message\":\"User not logged in\"}");
            return;
        }

        Part filePart = req.getPart("file"); // <input name="file">

        if (filePart == null || filePart.getSize() == 0) {
            resp.setStatus(400);
            resp.getWriter().write("{\"message\":\"No file uploaded\"}");
            return;
        }

        // === Validation (Task #108) ===
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            resp.setStatus(400);
            resp.getWriter().write("{\"message\":\"File must be an image\"}");
            return;
        }

        if (!(contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/png"))) {
            resp.setStatus(400);
            resp.getWriter().write("{\"message\":\"Only JPEG or PNG images are allowed\"}");
            return;
        }

        if (filePart.getSize() > 10L * 1024 * 1024) {
            resp.setStatus(400);
            resp.getWriter().write("{\"message\":\"Max file size is 10 MB\"}");
            return;
        }

        // Read the image data
        byte[] imageData;
        try (InputStream in = filePart.getInputStream()) {
            imageData = in.readAllBytes();
        }

        // Save to database
        UserDAO userDAO = new UserDAO();
        userDAO.saveProfilePicture(userId, imageData, contentType);

        resp.getWriter().write("{\"message\":\"Profile picture saved\"}");
    }
}
