package com.fitness.servlets;

import com.fitness.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get user session
        User user = (User) session.getAttribute("user");

        // Get profile form values
        String age = request.getParameter("age");
        String height = request.getParameter("height");
        String weight = request.getParameter("weight");
        String activity = request.getParameter("activity");
        String goal = request.getParameter("goal");

        // Save in session for now (later store in DB)
        session.setAttribute("age", age);
        session.setAttribute("height", height);
        session.setAttribute("weight", weight);
        session.setAttribute("activity", activity);
        session.setAttribute("goal", goal);

        // Redirect to dashboard
        response.sendRedirect("dashboard.jsp");
    }
}