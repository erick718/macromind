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
    <title>Dashboard - MacroMind</title>
</head>
<body>
<div class="container">
    <h1>Welcome back, <%= user.getName() %>!</h1>
    
    <!-- Success/Error Messages -->
    <% String message = (String) session.getAttribute("message"); %>
    <% if (message != null) { %>
        <div class="alert alert-success">
            <%= message %>
        </div>
        <% session.removeAttribute("message"); %>
    <% } %>
    
    <% String error = (String) session.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="alert alert-error">
            <%= error %>
        </div>
        <% session.removeAttribute("error"); %>
    <% } %>

    <div class="profile-section">
        <h2>Your Profile</h2>
        <div class="profile-info">
            <div class="profile-item">
                <strong>Age:</strong> 
                <%= user.getAge() > 0 ? user.getAge() + " years" : "Not set" %>
            </div>
            <div class="profile-item">
                <strong>Height:</strong> 
                <%= user.getHeight() > 0 ? user.getHeight() + " cm" : "Not set" %>
            </div>
            <div class="profile-item">
                <strong>Weight:</strong> 
                <%= user.getWeight() > 0 ? user.getWeight() + " kg" : "Not set" %>
            </div>
            <div class="profile-item">
                <strong>Fitness Level:</strong> 
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
            </div>
            <div class="profile-item">
                <strong>Goal:</strong> 
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
            </div>
        </div>
        
        <% if (user.getAge() == 0 || user.getHeight() == 0 || user.getWeight() == 0 || 
               user.getFitnessLevel() == null || user.getGoal() == null) { %>
            <div class="incomplete-profile">
                <p><strong>Complete your profile to get personalized recommendations!</strong></p>
                <!--<a href="profile.jsp" class="btn btn-primary">Complete Profile</a>-->
            </div>
        <% } else { %>
            <div class="complete-profile">
                <p>Your profile is complete!</p>
                <!--<a href="profile.jsp" class="btn btn-secondary">Update Profile</a>-->
            </div>
        <% } %>
    </div>

    <div class="actions">
        <a href="profile.jsp" class="btn btn-primary" >Edit Profile</a>
        <a href="LogoutServlet" class="btn btn-outline">Logout</a>
    </div>
</div>
</body>
</html>