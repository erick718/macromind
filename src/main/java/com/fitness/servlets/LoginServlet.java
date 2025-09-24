package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();

        try {
            User user = userDAO.getUserByEmail(email);
            if (user != null && password.equals(user.getPassword())) { // plain text for now
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // Redirect to profile or dashboard
                response.sendRedirect("profile.jsp");

            } else {
                request.setAttribute("message", "Invalid email or password");
                request.setAttribute("messageType", "danger");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Server error. Try again.");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}