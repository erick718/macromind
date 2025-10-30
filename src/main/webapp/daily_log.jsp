<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User, java.util.List, com.fitness.model.FoodEntry" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<html>
<head>
    <title>Daily Log</title>
</head>
<body>
<h1>Daily Log for <%= user.getName() %></h1>
<p><a href="calorieBalance">Back to Calorie Summary</a></p>
</body>
</html>