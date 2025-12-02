<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%@ page import="com.fitness.model.WorkoutPlan" %>
<%@ page import="com.fitness.model.Exercise" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    WorkoutPlan plan = (WorkoutPlan) request.getAttribute("workoutPlan");
    if (plan == null) {
        response.sendRedirect("WorkoutPlanServlet?action=list");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= plan.getPlanName() %> - MacroMind</title>
    <link href="css/custom.css" rel="stylesheet">
</head>
<body>
    <div class="container container-xl">
        <!-- Navigation Links -->
        <div class="card" style="margin-bottom: var(--spacing-lg);">
            <div class="card-body" style="text-align: center; padding: var(--spacing-md);">
                <a href="dashboard.jsp" class="btn btn-secondary" style="margin: 0 var(--spacing-sm);">← Dashboard</a>
                <a href="WorkoutPlanServlet?action=list" class="btn btn-secondary" style="margin: 0 var(--spacing-sm);">My Workout Plans</a>
                <a href="WorkoutPlanServlet?action=generate" class="btn btn-primary" style="margin: 0 var(--spacing-sm);">Generate New Plan</a>
                <a href="LogoutServlet" class="btn btn-secondary" style="margin: 0 var(--spacing-sm);">Logout</a>
            </div>
        </div>
        
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title"><%= plan.getPlanName() %></h1>
            <p class="page-subtitle">
                Personalized for <%= plan.getGoal().equals("lose") ? "Weight Loss" : 
                                   plan.getGoal().equals("gain") ? "Strength Building" : "Endurance & Maintenance" %>
            </p>
        </div>
        
        <!-- Plan Overview Stats -->
        <div class="grid grid-5">
            <div class="card bg-light">
                <div class="card-body text-center">
                    <h3 class="text-primary" style="font-size: 2em; margin-bottom: 0.5rem;"><%= plan.getDurationWeeks() %></h3>
                    <p class="text-secondary" style="text-transform: uppercase; font-size: 0.85em; font-weight: 600;">Weeks</p>
                </div>
            </div>
            <div class="card bg-light">
                <div class="card-body text-center">
                    <h3 class="text-primary" style="font-size: 2em; margin-bottom: 0.5rem;"><%= plan.getSessionsPerWeek() %></h3>
                    <p class="text-secondary" style="text-transform: uppercase; font-size: 0.85em; font-weight: 600;">Sessions/Week</p>
                </div>
            </div>
            <div class="card bg-light">
                <div class="card-body text-center">
                    <h3 class="text-primary" style="font-size: 2em; margin-bottom: 0.5rem;"><%= plan.getExercises().size() %></h3>
                    <p class="text-secondary" style="text-transform: uppercase; font-size: 0.85em; font-weight: 600;">Exercises</p>
                </div>
            </div>
            <div class="card bg-light">
                <div class="card-body text-center">
                    <h3 class="text-primary" style="font-size: 2em; margin-bottom: 0.5rem;"><%= plan.getTotalCaloriesBurned() %></h3>
                    <p class="text-secondary" style="text-transform: uppercase; font-size: 0.85em; font-weight: 600;">Cal/Session</p>
                </div>
            </div>
            <div class="card bg-light">
                <div class="card-body text-center">
                    <h3 class="text-primary" style="font-size: 2em; margin-bottom: 0.5rem;"><%= plan.getDifficulty().substring(0, 1).toUpperCase() + plan.getDifficulty().substring(1) %></h3>
                    <p class="text-secondary" style="text-transform: uppercase; font-size: 0.85em; font-weight: 600;">Difficulty</p>
                </div>
            </div>
        </div>
        
        <!-- Exercises Section -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Your Exercises</h2>
            </div>
            <div class="card-body">
                <div class="grid grid-auto">
                    <% for (Exercise exercise : plan.getExercises()) { %>
                        <div class="card bg-light">
                            <div class="card-body">
                                <h3 style="color: var(--text-primary); margin-bottom: var(--spacing-md); font-size: 1.25rem;"><%= exercise.getName() %></h3>
                                <p style="color: var(--text-secondary); margin-bottom: var(--spacing-md); line-height: 1.6;"><%= exercise.getDescription() %></p>
                                
                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: var(--spacing-sm); margin-bottom: var(--spacing-md); font-size: 0.9rem;">
                                    <div style="display: flex; justify-content: space-between; padding: var(--spacing-sm) 0; border-bottom: 1px solid var(--medium-gray);">
                                        <span style="font-weight: 600; color: var(--text-primary);">Duration:</span>
                                        <span style="color: var(--text-secondary);"><%= exercise.getDurationMinutes() %> min</span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between; padding: var(--spacing-sm) 0; border-bottom: 1px solid var(--medium-gray);">
                                        <span style="font-weight: 600; color: var(--text-primary);">Calories:</span>
                                        <span style="color: var(--text-secondary);"><%= exercise.getCaloriesBurned() %></span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between; padding: var(--spacing-sm) 0;">
                                        <span style="font-weight: 600; color: var(--text-primary);">Equipment:</span>
                                        <span style="color: var(--text-secondary);"><%= exercise.getEquipment() %></span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between; padding: var(--spacing-sm) 0;">
                                        <span style="font-weight: 600; color: var(--text-primary);">Difficulty:</span>
                                        <span style="color: var(--text-secondary);"><%= exercise.getDifficulty().substring(0, 1).toUpperCase() + exercise.getDifficulty().substring(1) %></span>
                                    </div>
                                </div>
                                
                                <span class="badge badge-primary" style="display: inline-block; margin-top: var(--spacing-sm);">
                                    <%= exercise.getMuscleGroup().replace("_", " ").toUpperCase() %>
                                </span>
                            </div>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
        
        <!-- Tips Section -->
        <div class="card" style="background: linear-gradient(135deg, #fff9e6 0%, #ffedcc 100%); border: 2px solid #ffe6b3;">
            <div class="card-header" style="background: transparent; border-bottom: 2px solid #ffe6b3;">
                <h3 class="card-title" style="color: var(--text-primary);">💡 Workout Tips & Guidelines</h3>
            </div>
            <div class="card-body">
                <% if ("lose".equals(plan.getGoal())) { %>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Perform exercises in circuit format with minimal rest for maximum calorie burn</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Combine with a healthy diet for optimal weight loss results</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Aim for <%= plan.getSessionsPerWeek() %> sessions per week, with at least one rest day between intense sessions</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Stay hydrated and monitor your heart rate during cardio exercises</p>
                <% } else if ("gain".equals(plan.getGoal())) { %>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Focus on proper form and controlled movements for each exercise</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Progressively increase weights or resistance as you get stronger</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Allow 48-72 hours rest between training the same muscle groups</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Ensure adequate protein intake to support muscle growth</p>
                <% } else { %>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Maintain steady, controlled pace throughout your workouts</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Focus on consistency rather than intensity</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Include flexibility and mobility work in your routine</p>
                    <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Listen to your body and adjust intensity as needed</p>
                <% } %>
                <p style="margin-bottom: var(--spacing-md); line-height: 1.7; color: var(--text-primary);">• Always warm up before exercising and cool down afterwards</p>
                <p style="margin-bottom: 0; line-height: 1.7; color: var(--text-primary);">• Consult with a healthcare provider before starting any new exercise program</p>
            </div>
        </div>
    </div>
</body>
</html>