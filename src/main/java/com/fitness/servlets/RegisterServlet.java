package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); // plain text for now

        UserDAO userDAO = new UserDAO();

        try {
            if (userDAO.isEmailTaken(email)) {
                request.setAttribute("message", "Email is already registered");
                request.setAttribute("messageType", "danger");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            User user = new User(name, email, password);
            userDAO.createUser(user);

            // Set session for logged-in user
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            response.sendRedirect("profile.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Server error. Try again.");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}