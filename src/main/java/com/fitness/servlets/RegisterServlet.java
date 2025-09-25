package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO dao = new UserDAO();
        if (dao.isEmailTaken(email)) {
            request.setAttribute("message", "Email is already registered");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        User user = new User(name, email, password);
        dao.createUser(user);

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        response.sendRedirect("profile.jsp");
    }
}