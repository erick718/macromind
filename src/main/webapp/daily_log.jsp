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
    <title>Log Exercise - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">Log Exercise</h1>
            <p class="page-subtitle">Track your workouts and calories burned</p>
        </div>

        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Add Exercise</h2>
            </div>
            <div class="card-body">
                <form action="LogExerciseServlet" method="post" class="form">
                    <div class="form-group">
                        <label for="exercise_name" class="form-label">Exercise Name:</label>
                        <input type="text" id="exercise_name" name="exercise_name" 
                               class="form-input" required 
                               placeholder="e.g., Morning Run, Gym Session">
                    </div>

                    <div class="form-group">
                        <label for="exercise_type" class="form-label">Exercise Type:</label>
                        <select id="exercise_type" name="exercise_type" class="form-input" required>
                            <option value="">Select exercise type</option>
                            <option value="cardio">Cardio (Running, Cycling)</option>
                            <option value="weightlifting">Weight Lifting</option>
                            <option value="hiit">HIIT (High Intensity)</option>
                            <option value="general">General Exercise</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="duration" class="form-label">Duration (minutes):</label>
                        <input type="number" id="duration" name="duration" 
                               class="form-input" required min="1" max="600"
                               placeholder="e.g., 30">
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn btn-primary">Log Exercise</button>
                        <a href="calorieBalance" class="btn btn-outline">Cancel</a>
                    </div>
                </form>
            </div>
        </div>

        <div class="nav-actions justify-center">
            <a href="calorieBalance" class="btn btn-success">View Calorie Balance</a>
            <a href="dashboard" class="btn btn-outline">Back to Dashboard</a>
        </div>
    </div>
</body>
</html>