package com.fitness.servlets;

import com.fitness.util.DBConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class RegisterServlet extends Httpservlet {
String username = request.getParameter("username");
String email = request.getParameter("email");
String password = request.getParameter("password"); // plain text

try (Connection conn = DBConnection.getConnection()) {
    String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, username);
    stmt.setString(2, email);
    stmt.setString(3, password);
    stmt.executeUpdate();
    response.sendRedirect("success.jsp");
} catch (Exception e) {
    e.printStackTrace();
    response.sendRedirect("error.jsp");
}
    
}
