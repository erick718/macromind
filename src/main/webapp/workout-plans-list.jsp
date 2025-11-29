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
    <div class="container container-xl">
        <div class="nav-actions justify-center mb-4">
            <a href="dashboard.jsp" class="btn btn-secondary">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=generate" class="btn btn-outline">Generate New Plan</a>
            <a href="LogoutServlet" class="btn btn-outline">Logout</a>
        </div>
        
        <div class="page-header mb-xl">
            <h1 class="page-title">My Workout Plans</h1>
            <p class="page-subtitle">Track and manage your personalized fitness programs</p>
        </div>
        
        <% if (plans == null || plans.isEmpty()) { %>
            <div class="card text-center">
                <div class="card-body">
                    <h3 class="mb-md">No Workout Plans Yet</h3>
                    <p class="text-secondary mb-lg">Create your first personalized workout plan based on your fitness goals!</p>
                    <a href="WorkoutPlanServlet?action=generate" class="btn btn-success btn-lg">
                        🏋️‍♀️ Generate My First Plan
                    </a>
                </div>
            </div>
        <% } else { %>
            <% for (WorkoutPlan plan : plans) { %>
                <div class="card mb-lg">
                    <div class="card-header">
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                            <h2 class="card-title" style="margin: 0;"><%= plan.getPlanName() %></h2>
                            <span class="badge badge-<%= plan.getGoal().equals("lose") ? "danger" : plan.getGoal().equals("gain") ? "success" : "info" %>">
                                <%= plan.getGoal().equals("lose") ? "Weight Loss" : 
                                    plan.getGoal().equals("gain") ? "Strength" : "Endurance" %>
                            </span>
                        </div>
                    </div>
                    
                    <div class="card-body">
                        <div class="workout-plan-overview">
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
                        
                        <p class="text-secondary mb-md"><strong>Created:</strong> <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(plan.getCreatedDate()) %></p>
                        
                        <div class="btn-group">
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
                </div>
            <% } %>
            
            <div class="text-center mt-xl">
                <a href="WorkoutPlanServlet?action=generate" class="btn btn-success btn-lg">
                    Generate Another Plan
                </a>
            </div>
        <% } %>
    </div>
</body>
</html>