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
    <title>Log Food - MacroMind</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }
        h1 {
            color: #333;
        }
        form {
            width: 300px;
        }
        label {
            display: block;
            margin-top: 10px;
        }
        input, select {
            width: 100%;
            padding: 6px;
            margin-top: 4px;
        }
        .btn {
            margin-top: 14px;
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<h1>Log Food for <%= user.getName() %></h1>

<form action="FoodEntryServlet" method="post">
    <label for="foodName">Food Name:</label>
    <input type="text" id="foodName" name="foodName" required>

    <label for="calories">Calories:</label>
    <input type="number" id="calories" name="calories" min="0" required>

    <label for="protein">Protein (g):</label>
    <input type="number" id="protein" name="protein" min="0">

    <label for="carbs">Carbs (g):</label>
    <input type="number" id="carbs" name="carbs" min="0">

    <label for="fat">Fat (g):</label>
    <input type="number" id="fat" name="fat" min="0">

    <button type="submit" class="btn">Save Entry</button>
</form>

<br>
<a href="calorieBalance">Back to Calorie Balance</a>

</body>
</html>