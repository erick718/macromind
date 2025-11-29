package com.fitness.servlets;

import com.fitness.services.FileStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "SaveProfilePictureServlet", urlPatterns = {"/api/profile/picture/save"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 64,        // 64 KB
        maxFileSize = 1024 * 1024 * 3,        // 3 MB per file
        maxRequestSize = 1024 * 1024 * 4      // 4 MB request
)
public class SaveProfilePictureServlet extends HttpServlet {

    private String resolveUserId(HttpServletRequest req) {
        // Sprint 1: stub user id = session id
        HttpSession session = req.getSession(true);
        return session.getId();
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        String userId = resolveUserId(req);

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

        if (filePart.getSize() > 3L * 1024 * 1024) {
            resp.setStatus(400);
            resp.getWriter().write("{\"message\":\"Max file size is 3 MB\"}");
            return;
        }

        // If we reach here, file is valid → save/replace existing
        try (InputStream in = filePart.getInputStream()) {
            FileStorage.saveProfilePic(userId, in);
        }

        resp.getWriter().write("{\"message\":\"Profile picture saved\"}");
    }
}
