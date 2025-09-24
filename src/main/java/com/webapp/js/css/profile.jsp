<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <title>Profile Setup</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
<div class="container mt-5">
    <h2>Profile Setup</h2>

    <form action="ProfileServlet" method="post">
        <div class="form-group">
            <label for="age">Age</label>
            <input type="number" class="form-control" name="age" id="age" required>
        </div>

        <div class="form-group">
            <label for="height">Height (cm)</label>
            <input type="number" class="form-control" name="height" id="height" required>
        </div>

        <div class="form-group">
            <label for="weight">Weight (kg)</label>
            <input type="number" class="form-control" name="weight" id="weight" required>
        </div>

        <div class="form-group">
            <label for="activity">Activity Level</label>
            <select class="form-control" name="activity" id="activity" required>
                <option value="low">Low</option>
                <option value="moderate">Moderate</option>
                <option value="high">High</option>
            </select>
        </div>

        <div class="form-group">
            <label for="goal">Fitness Goal</label>
            <select class="form-control" name="goal" id="goal" required>
                <option value="lose">Lose Weight</option>
                <option value="maintain">Maintain Weight</option>
                <option value="gain">Gain Muscle</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Save Profile</button>
    </form>

    <a href="LogoutServlet" class="btn btn-secondary mt-3">Logout</a>
</div>
<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>