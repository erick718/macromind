<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fitness.servlets.CalorieBalanceServlet.Meal" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    int recommendedCalories = (int) request.getAttribute("recommendedCalories");
    int totalIntake = (int) request.getAttribute("totalIntake");
    int totalBurned = (int) request.getAttribute("totalBurned");
    int netCalories = (int) request.getAttribute("netCalories");
    int remainingCalories = (int) request.getAttribute("remainingCalories");
    List<Meal> meals = (List<Meal>) request.getAttribute("meals");
%>

<html>
<head>
    <title>Daily Calorie Balance</title>
</head>
<body>
<h1>Calorie Balance for <%= user.getName() %></h1>

<h2>Recommended Daily Calories: <%= recommendedCalories %> kcal</h2>

<h2>Meals Consumed Today</h2>
<% if (meals.isEmpty()) { %>
    <p>No meals logged yet.</p>
<% } else { %>
<table border="1">
<tr><th>Food</th><th>Calories</th></tr>
<% for(Meal meal : meals) { %>
<tr>
    <td><%= meal.getName() %></td>
    <td><%= meal.getCalories() %></td>
</tr>
<% } %>
</table>
<% } %>

<p><strong>Total Calories Consumed:</strong> <%= totalIntake %> kcal</p>
<p><strong>Remaining Calories:</strong> <%= remainingCalories %> kcal</p>
<p><strong>Total Calories Burned:</strong> <%= totalBurned %> kcal</p>
<p><strong>Net Calories:</strong> <%= netCalories %> kcal</p>

<% if(netCalories > recommendedCalories){ %>
    <p style="color:red;">You are above your recommended calories.</p>
<% } else if (netCalories < recommendedCalories){ %>
    <p style="color:green;">You are below your recommended calories.</p>
<% } else { %>
    <p>You are right on track!</p>
<% } %>

<a href="dashboard">Back to Dashboard</a>
</body>
</html>