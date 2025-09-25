package com.fitness.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

import com.fitness.model.User;

public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Forward to the JSP; profile data is already in session
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}