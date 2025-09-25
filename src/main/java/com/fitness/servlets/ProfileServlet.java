package com.fitness.servlets;

import com.fitness.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        session.setAttribute("age", request.getParameter("age"));
        session.setAttribute("height", request.getParameter("height"));
        session.setAttribute("weight", request.getParameter("weight"));
        session.setAttribute("activity", request.getParameter("activity"));
        session.setAttribute("goal", request.getParameter("goal"));

        response.sendRedirect("dashboard.jsp");
    }
}