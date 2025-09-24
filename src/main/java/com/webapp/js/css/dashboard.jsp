<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
<div class="container mt-5">
    <h2>Welcome, <%= user.getName() %>!</h2>

    <p>Your profile:</p>
    <ul>
        <li>Age: <%= session.getAttribute("age") %></li>
        <li>Height: <%= session.getAttribute("height") %> cm</li>
        <li>Weight: <%= session.getAttribute("weight") %> kg</li>
        <li>Activity Level: <%= session.getAttribute("activity") %></li>
        <li>Goal: <%= session.getAttribute("goal") %></li>
    </ul>

    <a href="LogoutServlet" class="btn btn-danger">Logout</a>
</div>
<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>