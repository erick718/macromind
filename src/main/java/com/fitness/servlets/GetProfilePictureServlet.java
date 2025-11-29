package com.fitness.servlets;

import com.fitness.services.FileStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet(name = "GetProfilePictureServlet", urlPatterns = {"/api/profile/picture"})
public class GetProfilePictureServlet extends HttpServlet {

    private String resolveUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        return session.getId();
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        String userId = resolveUserId(req);

        InputStream in = FileStorage.readProfilePic(userId);
        if (in == null) {
            // No picture yet → tiny transparent placeholder PNG
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
            return;
        }

        resp.setContentType("image/jpeg"); // we normalize to JPG in storage
        try (OutputStream out = resp.getOutputStream(); InputStream inStream = in) {
            FileStorage.copy(inStream, out);
        }
    }
}
