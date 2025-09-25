<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
<h1>Welcome, <%= user.getName() %>!</h1>

<ul>
    <li>Age: <%= session.getAttribute("age") != null ? session.getAttribute("age") : "N/A" %></li>
    <li>Height: <%= session.getAttribute("height") != null ? session.getAttribute("height") : "N/A" %></li>
    <li>Weight: <%= session.getAttribute("weight") != null ? session.getAttribute("weight") : "N/A" %></li>
    <li>Activity Level: <%= session.getAttribute("activity") != null ? session.getAttribute("activity") : "N/A" %></li>
    <li>Goal: <%= session.getAttribute("goal") != null ? session.getAttribute("goal") : "N/A" %></li>
</ul>

<a href="LogoutServlet">Logout</a>
</body>
</html>