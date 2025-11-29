package com.fitness.servlets;

import java.io.IOException;

import com.fitness.Model.User;
import com.fitness.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Trim whitespace from input
        if (name != null) name = name.trim();
        if (email != null) email = email.trim();
        if (password != null) password = password.trim();

        // Validate input
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || 
            password == null || password.isEmpty()) {
            request.setAttribute("message", "All fields are required");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

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