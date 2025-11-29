<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%@ page import="com.fitness.Model.WorkoutPlan" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<WorkoutPlan> plans = (List<WorkoutPlan>) request.getAttribute("workoutPlans");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Workout Plans - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">My Workout Plans</h1>
            <p class="page-subtitle">Manage your personalized workout programs</p>
        </div>
        
        <div class="nav-actions justify-center mb-4">
            <a href="dashboard.jsp" class="btn btn-secondary">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=generate" class="btn btn-success">Generate New Plan</a>
        </div>
        
        <% if (plans == null || plans.isEmpty()) { %>
            <div class="empty-state">
                <h3>No Workout Plans Yet</h3>
                <p>Create your first personalized workout plan based on your fitness goals!</p>
                <a href="WorkoutPlanServlet?action=generate" class="btn btn-success btn-lg">
                    🏋️‍♀️ Generate My First Plan
                </a>
            </div>
        <% } else { %>
            <% for (WorkoutPlan plan : plans) { %>
                <div class="workout-plan-card">
                    <div class="workout-plan-card-header">
                        <h2 class="workout-plan-card-title"><%= plan.getPlanName() %></h2>
                        <span class="workout-plan-goal workout-plan-goal-<%= plan.getGoal() %>">
                            <%= plan.getGoal().equals("lose") ? "Weight Loss" : 
                                plan.getGoal().equals("gain") ? "Strength" : "Endurance" %>
                        </span>
                    </div>
                    
                    <div class="workout-plan-stats">
                        <div class="stat-card">
                            <div class="stat-value"><%= plan.getDurationWeeks() %></div>
                            <div class="stat-label">Weeks</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value"><%= plan.getSessionsPerWeek() %></div>
                            <div class="stat-label">Sessions/Week</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value"><%= plan.getExercises().size() %></div>
                            <div class="stat-label">Exercises</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value"><%= plan.getTotalCaloriesBurned() %></div>
                            <div class="stat-label">Calories/Session</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value"><%= plan.getDifficulty().substring(0, 1).toUpperCase() + plan.getDifficulty().substring(1) %></div>
                            <div class="stat-label">Level</div>
                        </div>
                    </div>
                    
                    <p class="mt-3"><strong>Created:</strong> <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(plan.getCreatedDate()) %></p>
                    
                    <div class="nav-actions mt-3">
                        <a href="WorkoutPlanServlet?action=view&planId=<%= plan.getPlanId() %>" class="btn btn-primary">
                            View Details
                        </a>
                        <a href="WorkoutPlanServlet?action=delete&planId=<%= plan.getPlanId() %>" 
                           class="btn btn-danger"
                           onclick="return confirm('Are you sure you want to delete this workout plan?')">
                            Delete
                        </a>
                    </div>
                </div>
            <% } %>
            
            <div class="text-center mt-5">
                <a href="WorkoutPlanServlet?action=generate" class="btn btn-success btn-lg">
                    Generate Another Plan
                </a>
            </div>
        <% } %>
    </div>
</body>
</html>