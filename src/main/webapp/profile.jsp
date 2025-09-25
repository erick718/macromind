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
    <title>Profile Setup</title>
</head>
<body>
<h1>Profile Setup for <%= user.getName() %></h1>

<form action="ProfileServlet" method="post">
    Age: <input type="number" name="age" required><br>
    Height (cm): <input type="number" name="height" required><br>
    Weight (kg): <input type="number" name="weight" required><br>
    Activity Level:
    <select name="activity" required>
        <option value="low">Low</option>
        <option value="moderate">Moderate</option>
        <option value="high">High</option>
    </select><br>
    Goal:
    <select name="goal" required>
        <option value="lose">Lose Weight</option>
        <option value="maintain">Maintain Weight</option>
        <option value="gain">Gain Muscle</option>
    </select><br>
    <input type="submit" value="Save Profile">
</form>

<a href="LogoutServlet">Logout</a>
</body>
</html>