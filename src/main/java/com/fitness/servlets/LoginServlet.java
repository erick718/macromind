package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(email.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.jsp?error=empty");
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.getUserByEmail(email);
            if(user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("dashboard.jsp");
            } else {
                response.sendRedirect("login.jsp?error=invalid");
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=db");
        }
    }
    
}
