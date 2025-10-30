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
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">Welcome back, <%= user.getName() %>!</h1>
            <p class="page-subtitle">Ready to crush your fitness goals today?</p>
        </div>
        
        <!-- Success/Error Messages -->
        <% String message = (String) session.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-success">
                <strong>Success!</strong> <%= message %>
            </div>
            <% session.removeAttribute("message"); %>
        <% } %>
        
        <% String error = (String) session.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-error">
                <strong>Error:</strong> <%= error %>
            </div>
            <% session.removeAttribute("error"); %>
        <% } %>

        <!-- Profile Overview Card -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Your Profile Overview</h2>
            </div>
            <div class="card-body">
                <div class="grid grid-auto">
                    <div class="card bg-light">
                        <div class="card-body text-center">
                            <h4 class="text-primary">Age</h4>
                            <p class="text-secondary">
                                <%= user.getAge() > 0 ? user.getAge() + " years" : "Not set" %>
                            </p>
                        </div>
                    </div>
                    
                    <div class="card bg-light">
                        <div class="card-body text-center">  
                            <h4 class="text-primary">Height</h4>
                            <p class="text-secondary">
                                <%= user.getHeight() > 0 ? user.getHeight() + " cm" : "Not set" %>
                            </p>
                        </div>
                    </div>
                    
                    <div class="card bg-light">
                        <div class="card-body text-center">
                            <h4 class="text-primary">Weight</h4>
                            <p class="text-secondary">
                                <%= user.getWeight() > 0 ? user.getWeight() + " kg" : "Not set" %>
                            </p>
                        </div>
                    </div>
                    
                    <div class="card bg-light">
                        <div class="card-body text-center">
                            <h4 class="text-primary">Fitness Level</h4>
                            <p class="text-secondary">
                                <% 
                                String fitnessDisplay = "Not set";
                                if (user.getFitnessLevel() != null) {
                                    switch(user.getFitnessLevel().toLowerCase()) {
                                        case "low": fitnessDisplay = "Low Activity"; break;
                                        case "moderate": fitnessDisplay = "Moderate Activity"; break;
                                        case "high": fitnessDisplay = "High Activity"; break;
                                        default: fitnessDisplay = user.getFitnessLevel();
                                    }
                                }
                                %>
                                <%= fitnessDisplay %>
                            </p>
                        </div>
                    </div>
                    
                    <div class="card bg-light">
                        <div class="card-body text-center">
                            <h4 class="text-primary">Goal</h4>
                            <p class="text-secondary">
                                <% 
                                String goalDisplay = "Not set";
                                if (user.getGoal() != null) {
                                    switch(user.getGoal().toLowerCase()) {
                                        case "lose": goalDisplay = "Lose Weight"; break;
                                        case "maintain": goalDisplay = "Maintain Weight"; break;
                                        case "gain": goalDisplay = "Gain Muscle"; break;
                                        default: goalDisplay = user.getGoal();
                                    }
                                }
                                %>
                                <%= goalDisplay %>
                            </p>
                        </div>
                    </div>
                </div>
                
                <% if (user.getAge() == 0 || user.getHeight() == 0 || user.getWeight() == 0 || 
                       user.getFitnessLevel() == null || user.getGoal() == null) { %>
                    <div class="alert alert-warning mt-4">
                        <strong>Complete Your Profile:</strong> Add your missing information to get personalized workout recommendations and accurate calorie calculations!
                    </div>
                <% } %>
            </div>
        </div>

        <!-- New Calorie Balance Features -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">🔥 New! Calorie Balance Tracking</h2>
                <p class="card-subtitle">Enhanced features from your teammate - track food intake, exercise, and calorie balance</p>
            </div>
            <div class="card-body">
                <div class="grid grid-auto">
                    <a href="calorieBalance" class="btn btn-primary btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none; border: 2px solid #3498db;">
                        
                        <div style="font-weight: 600;">🎯 Calorie Balance</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">Smart daily calorie tracking</div>
                    </a>
                    
                    <a href="daily_log.jsp" class="btn btn-info btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none;">
                        
                        <div style="font-weight: 600;">💪 Log Exercise</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">Track calories burned</div>
                    </a>
                </div>
            </div>
        </div>

        <!-- Original Fitness Tracking Features -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Fitness Tracking</h2>
                <p class="card-subtitle">Your original workout and nutrition tracking features</p>
            </div>
            <div class="card-body">
                <div class="grid grid-auto">
                    <a href="workout-log" class="btn btn-primary btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none;">
                        
                        <div style="font-weight: 600;">Log New Workout</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">Record your exercise session</div>
                    </a>
                    
                    <a href="workout-history" class="btn btn-success btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none;">
                        
                        <div style="font-weight: 600;">Workout History</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">View past workouts & stats</div>
                    </a>
                    
                    <a href="progress-dashboard" class="btn btn-info btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none;">
                        
                        <div style="font-weight: 600;">Progress Dashboard</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">Analytics & insights</div>
                    </a>

                    <a href="food_entry.jsp" class="btn btn-warning btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none;">
                        
                        <div style="font-weight: 600;">Log Food Intake</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">Track your meals & nutrition</div>
                    </a>
                    
                    <a href="food-history" class="btn btn-danger btn-lg text-center" style="padding: var(--spacing-xl); text-decoration: none;">
                        
                        <div style="font-weight: 600;">Food History</div>
                        <div style="font-size: var(--font-size-sm); opacity: 0.8;">View logged meals</div>
                    </a>
                </div>
            </div>
        </div>

        <!-- Account Actions -->
        <div class="nav-actions justify-center">
            <a href="profile.jsp" class="btn btn-warning">Edit Profile</a>
            <a href="LogoutServlet" class="btn btn-outline">Logout</a>
        </div>
    </div>
</div>
</body>
</html>