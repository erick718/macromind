<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%@ page import="com.fitness.Model.WorkoutPlan" %>
<%@ page import="com.fitness.Model.Exercise" %>
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
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-xl">
        <div class="workout-plan-header">
            <h1 class="page-title"><%= plan.getPlanName() %></h1>
            <p class="page-subtitle">
                Personalized for <%= plan.getGoal().equals("lose") ? "Weight Loss" : 
                                   plan.getGoal().equals("gain") ? "Strength Building" : "Endurance & Maintenance" %>
            </p>
        </div>
        
        <div class="nav-actions justify-center mb-4">
            <a href="dashboard.jsp" class="btn btn-secondary">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=list" class="btn btn-outline">My Workout Plans</a>
            <a href="WorkoutPlanServlet?action=generate" class="btn btn-outline">Generate New Plan</a>
        </div>
        
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
        
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Your Exercises</h2>
            </div>
            <div class="card-body">
                <div class="exercises-grid">
                <% for (Exercise exercise : plan.getExercises()) { %>
                    <div class="exercise-card">
                        <div class="exercise-name"><%= exercise.getName() %></div>
                        <div class="exercise-description"><%= exercise.getDescription() %></div>
                        
                        <div class="exercise-details">
                            <div class="exercise-detail">
                                <span class="detail-label">Duration:</span>
                                <span class="detail-value"><%= exercise.getDurationMinutes() %> min</span>
                            </div>
                            <div class="exercise-detail">
                                <span class="detail-label">Calories:</span>
                                <span class="detail-value"><%= exercise.getCaloriesBurned() %></span>
                            </div>
                            <div class="exercise-detail">
                                <span class="detail-label">Equipment:</span>
                                <span class="detail-value"><%= exercise.getEquipment() %></span>
                            </div>
                            <div class="exercise-detail">
                                <span class="detail-label">Difficulty:</span>
                                <span class="detail-value"><%= exercise.getDifficulty().substring(0, 1).toUpperCase() + exercise.getDifficulty().substring(1) %></span>
                            </div>
                        </div>
                        
                        <div class="workout-muscle-group">
                            <%= exercise.getMuscleGroup().replace("_", " ").toUpperCase() %>
                        </div>
                    </div>
                <% } %>
                </div>
            </div>
        </div>
        
        <div class="workout-tips-section">
            <h3 class="card-title">Workout Tips & Guidelines</h3>
            
            <% if ("lose".equals(plan.getGoal())) { %>
                <div class="tip">Perform exercises in circuit format with minimal rest for maximum calorie burn</div>
                <div class="tip">Combine with a healthy diet for optimal weight loss results</div>
                <div class="tip">Aim for <%= plan.getSessionsPerWeek() %> sessions per week, with at least one rest day between intense sessions</div>
                <div class="tip">Stay hydrated and monitor your heart rate during cardio exercises</div>
            <% } else if ("gain".equals(plan.getGoal())) { %>
                <div class="tip">Focus on proper form and controlled movements for each exercise</div>
                <div class="tip">Progressively increase weights or resistance as you get stronger</div>
                <div class="tip">Allow 48-72 hours rest between training the same muscle groups</div>
                <div class="tip">Ensure adequate protein intake to support muscle growth</div>
            <% } else { %>
                <div class="tip">Maintain steady, controlled pace throughout your workouts</div>
                <div class="tip">Focus on consistency rather than intensity</div>
                <div class="tip">Include flexibility and mobility work in your routine</div>
                <div class="tip">Listen to your body and adjust intensity as needed</div>
            <% } %>
            
            <div class="tip">Always warm up before exercising and cool down afterwards</div>
            <div class="tip">Consult with a healthcare provider before starting any new exercise program</div>
        </div>
    </div>
</body>
</html>