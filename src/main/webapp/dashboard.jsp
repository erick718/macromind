<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
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
                            <% if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) { %>
                                <img src="data:<%= user.getProfilePictureType() %>;base64,<%= java.util.Base64.getEncoder().encodeToString(user.getProfilePicture()) %>" 
                                     alt="Profile Picture" 
                                     class="profile-picture">
                            <% } else { %>
                                <div class="profile-picture" style="background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; color: white; font-size: 3rem; font-weight: bold; margin: 0 auto;">
                                    <%= user.getName().substring(0, 1).toUpperCase() %>
                                </div>
                            <% } %>
                        </div>
                    </div>
                    
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

        <!-- Fitness & Calorie Tracking -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Fitness & Calorie Tracking</h2>
                <p class="card-subtitle">Complete workout logging, nutrition tracking, and calorie balance</p>
            </div>
            <div class="card-body">
                <div class="grid grid-auto">
                    <a href="workout-log" class="btn btn-primary btn-lg text-center dashboard-nav-card">
                        <div class="dashboard-nav-title">Log Workout</div>
                        <div class="dashboard-nav-subtitle">Track exercises & calories burned</div>
                    </a>

                    <a href="food_entry.jsp" class="btn btn-warning btn-lg text-center dashboard-nav-card">
                        <div class="dashboard-nav-title">Log Food</div>
                        <div class="dashboard-nav-subtitle">Track meals & nutrition</div>
                    </a>
                    
                    <a href="calorieBalance" class="btn btn-success btn-lg text-center dashboard-nav-card">
                        <div class="dashboard-nav-title">Calorie Balance</div>
                        <div class="dashboard-nav-subtitle">Daily calorie tracking & goals</div>
                    </a>
                    
                    <a href="workout-history" class="btn btn-info btn-lg text-center dashboard-nav-card">
                        <div class="dashboard-nav-title">Workout History</div>
                        <div class="dashboard-nav-subtitle">View past workouts & progress</div>
                    </a>
                    
                    <a href="food-history" class="btn btn-danger btn-lg text-center dashboard-nav-card">
                        <div class="dashboard-nav-title">Food History</div>
                        <div class="dashboard-nav-subtitle">View logged meals</div>
                    </a>
                    
                    <a href="progress-dashboard" class="btn btn-outline btn-lg text-center dashboard-nav-card">
                        <div class="dashboard-nav-title">Progress Dashboard</div>
                        <div class="dashboard-nav-subtitle">Analytics & insights</div>
                    </a>
                    
                    <a href="workout-generator.jsp" class="btn btn-primary btn-lg text-center dashboard-nav-card" style="grid-column: 1 / -1;">
                        <div class="dashboard-nav-title">Generate Workout Plan</div>
                        <div class="dashboard-nav-subtitle">AI-powered personalized plans</div>
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