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
<html>
<head>
    <title>My Workout Plans - MacroMind</title>
    <link href="css/custom.css" rel="stylesheet">
    <style>
        .plans-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 30px;
        }
        
        .plan-card {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
            transition: transform 0.3s ease;
        }
        
        .plan-card:hover {
            transform: translateY(-2px);
        }
        
        .plan-header {
            display: flex;
            justify-content: between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .plan-title {
            color: #333;
            font-size: 1.3em;
            font-weight: bold;
            margin: 0;
        }
        
        .plan-goal {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 15px;
            font-size: 0.8em;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .goal-lose {
            background-color: #ff6b6b;
            color: white;
        }
        
        .goal-gain {
            background-color: #4ecdc4;
            color: white;
        }
        
        .goal-maintain {
            background-color: #45b7d1;
            color: white;
        }
        
        .plan-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
            gap: 15px;
            margin: 15px 0;
        }
        
        .stat {
            text-align: center;
            padding: 10px;
            background-color: #f8f9fa;
            border-radius: 8px;
        }
        
        .stat-value {
            font-size: 1.2em;
            font-weight: bold;
            color: #333;
        }
        
        .stat-label {
            font-size: 0.8em;
            color: #666;
            text-transform: uppercase;
        }
        
        .plan-actions {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }
        
        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-size: 0.9em;
        }
        
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
        }
        
        .btn:hover {
            opacity: 0.8;
        }
        
        .navigation {
            margin-bottom: 20px;
        }
        
        .navigation a {
            color: #007bff;
            text-decoration: none;
            margin-right: 15px;
        }
        
        .navigation a:hover {
            text-decoration: underline;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .empty-state h3 {
            margin-bottom: 10px;
            color: #333;
        }
        
        .btn-generate {
            background-color: #28a745;
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-generate:hover {
            background-color: #218838;
            text-decoration: none;
            color: white;
        }
    </style>
</head>
<body>
    <div class="plans-container">
        <div class="navigation">
            <a href="dashboard.jsp">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=generate">Generate New Plan</a>
            <a href="LogoutServlet">Logout</a>
        </div>
        
        <h1>My Workout Plans</h1>
        
        <!-- Debug Information -->
        <div style="background-color: #f8f9fa; padding: 10px; margin-bottom: 20px; border: 1px solid #dee2e6; border-radius: 5px;">
            <h4>Debug Information:</h4>
            <p><strong>User ID:</strong> <%= user != null ? user.getUserId() : "null" %></p>
            <p><strong>Plans Object:</strong> <%= plans != null ? "Not null" : "null" %></p>
            <p><strong>Plans Count:</strong> <%= plans != null ? plans.size() : "N/A" %></p>
            <% if (plans != null && !plans.isEmpty()) { %>
                <p><strong>First Plan:</strong> <%= plans.get(0).getPlanName() %></p>
            <% } %>
        </div>
        
        <% if (plans == null || plans.isEmpty()) { %>
            <div class="empty-state">
                <h3>No Workout Plans Yet</h3>
                <p>Create your first personalized workout plan based on your fitness goals!</p>
                <a href="WorkoutPlanServlet?action=generate" class="btn-generate">
                    🏋️‍♀️ Generate My First Plan
                </a>
            </div>
        <% } else { %>
            <% for (WorkoutPlan plan : plans) { %>
                <div class="plan-card">
                    <div class="plan-header">
                        <h2 class="plan-title"><%= plan.getPlanName() %></h2>
                        <span class="plan-goal goal-<%= plan.getGoal() %>">
                            <%= plan.getGoal().equals("lose") ? "Weight Loss" : 
                                plan.getGoal().equals("gain") ? "Strength" : "Endurance" %>
                        </span>
                    </div>
                    
                    <div class="plan-stats">
                        <div class="stat">
                            <div class="stat-value"><%= plan.getDurationWeeks() %></div>
                            <div class="stat-label">Weeks</div>
                        </div>
                        <div class="stat">
                            <div class="stat-value"><%= plan.getSessionsPerWeek() %></div>
                            <div class="stat-label">Sessions/Week</div>
                        </div>
                        <div class="stat">
                            <div class="stat-value"><%= plan.getExercises().size() %></div>
                            <div class="stat-label">Exercises</div>
                        </div>
                        <div class="stat">
                            <div class="stat-value"><%= plan.getTotalCaloriesBurned() %></div>
                            <div class="stat-label">Calories/Session</div>
                        </div>
                        <div class="stat">
                            <div class="stat-value"><%= plan.getDifficulty().substring(0, 1).toUpperCase() + plan.getDifficulty().substring(1) %></div>
                            <div class="stat-label">Difficulty</div>
                        </div>
                    </div>
                    
                    <p><strong>Created:</strong> <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(plan.getCreatedDate()) %></p>
                    
                    <div class="plan-actions">
                        <a href="WorkoutPlanServlet?action=view&planId=<%= plan.getPlanId() %>" class="btn btn-primary">
                            📋 View Details
                        </a>
                        <a href="WorkoutPlanServlet?action=delete&planId=<%= plan.getPlanId() %>" 
                           class="btn btn-danger"
                           onclick="return confirm('Are you sure you want to delete this workout plan?')">
                            🗑️ Delete
                        </a>
                    </div>
                </div>
            <% } %>
            
            <div style="text-align: center; margin-top: 30px;">
                <a href="WorkoutPlanServlet?action=generate" class="btn-generate">
                    ➕ Generate Another Plan
                </a>
            </div>
        <% } %>
    </div>
</body>
</html>