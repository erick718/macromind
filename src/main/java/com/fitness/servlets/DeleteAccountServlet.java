package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/DeleteAccountServlet")
public class DeleteAccountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Case-insensitive confirmation ("DELETE" or "delete")
        String confirm = request.getParameter("confirm");
        if (confirm == null || !"delete".equalsIgnoreCase(confirm)) {
            session.setAttribute("error", "You must type DELETE to confirm account deletion.");
            response.sendRedirect(request.getContextPath() + "/profile.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        UserDAO dao = new UserDAO();
        boolean deleted = dao.deleteUser(userId);

        if (deleted) {
            session.invalidate(); // end session
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            session.setAttribute("error", "Failed to delete account.");
            response.sendRedirect(request.getContextPath() + "/profile.jsp");
        }
    }
}