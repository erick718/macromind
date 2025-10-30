<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User, java.util.List, com.fitness.model.FoodEntry" %>
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
    List<FoodEntry> entries = (List<FoodEntry>) request.getAttribute("entries");

    // Calculate percentage of goal reached
    double progress = ((double) netCalories / recommendedCalories) * 100;
    if (progress > 100) progress = 100;
    if (progress < 0) progress = 0;

    // Determine progress bar color
    String progressColor = (netCalories > recommendedCalories) ? "#e74c3c" : "#2ecc71";
%>

<html>
<head>
    <title>Daily Calorie Balance</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 40px; 
            background-color: #fdfdfd;
        }
        h1, h2 { color: #333; }
        .summary {
            background-color: #fff;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            width: 600px;
        }
        .progress-container {
            background-color: #ddd;
            border-radius: 20px;
            overflow: hidden;
            width: 100%;
            height: 25px;
            margin-top: 10px;
        }
        .progress-bar {
            height: 100%;
            text-align: center;
            line-height: 25px;
            color: white;
            font-weight: bold;
            width: <%= progress %>%;
            background-color: <%= progressColor %>;
            transition: width 0.5s ease-in-out;
        }
        table { 
            border-collapse: collapse; 
            width: 600px; 
            margin-top: 20px; 
        }
        th, td { 
            border: 1px solid #ccc; 
            padding: 8px; 
            text-align: left; 
        }
        th { background-color: #f4f4f4; }
        a { color: #3498db; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>

<div class="summary">
    <h1>Calorie Balance for <%= user.getName() %></h1>
    <h2>Recommended: <%= recommendedCalories %> kcal</h2>
    <p><strong>Consumed:</strong> <%= totalIntake %> kcal</p>
    <p><strong>Burned:</strong> <%= totalBurned %> kcal</p>
    <p><strong>Net:</strong> <%= netCalories %> kcal</p>
    <p><strong>Remaining:</strong> <%= remainingCalories %> kcal</p>

    <div class="progress-container">
        <div class="progress-bar"><%= String.format("%.0f", progress) %>%</div>
    </div>

    <% if (netCalories > recommendedCalories) { %>
        <p style="color:red; margin-top:10px;">You are above your daily goal!</p>
    <% } else if (remainingCalories > 0) { %>
        <p style="color:green; margin-top:10px;">You’re within your goal range!</p>
    <% } else { %>
        <p style="color:orange; margin-top:10px;">Perfectly balanced today!</p>
    <% } %>
</div>

<h3>Today's Food Entries</h3>
<% if (entries.isEmpty()) { %>
    <p>No food logged yet.</p>
<% } else { %>
<table>
    <tr><th>Food</th><th>Calories</th><th>Protein (g)</th><th>Carbs (g)</th><th>Fat (g)</th></tr>
    <% for (FoodEntry entry : entries) { %>
    <tr>
        <td><%= entry.getFoodName() %></td>
        <td><%= entry.getCalories() %></td>
        <td><%= entry.getProtein() %></td>
        <td><%= entry.getCarbs() %></td>
        <td><%= entry.getFat() %></td>
    </tr>
    <% } %>
</table>
<% } %>

<br>
<a href="food_entry.jsp">Log Food</a> |
<a href="dashboard">Back to Dashboard</a>

</body>
</html>