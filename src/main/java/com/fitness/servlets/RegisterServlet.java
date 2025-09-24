package com.fitness.servlets;

import com.fitness.util.DBConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.sendRedirect("register.jsp?error=empty");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check duplicates
            String checkSql = "SELECT * FROM users WHERE username=? OR email=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();
            if(rs.next()) {
                response.sendRedirect("register.jsp?error=exists");
                return;
            }

            // Insert user
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();

            response.sendRedirect("success.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=db");
        }
    }
    
}
