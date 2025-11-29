<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%@ page import="com.fitness.model.WorkoutPlan" %>
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
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            margin: 0;
            padding: 20px;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .plans-container {
            max-width: 1000px;
            margin: 30px auto;
            padding: 40px;
            background-color: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }
        
        h1 {
            color: #2c3e50;
            font-size: 2.2em;
            margin-bottom: 30px;
            text-align: center;
            font-weight: bold;
        }
        
        .plan-card {
            background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
            border-radius: 12px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
            padding: 25px;
            margin-bottom: 25px;
            transition: all 0.3s ease;
            border: 2px solid #e9ecef;
        }
        
        .plan-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            border-color: #667eea;
        }
        
        .plan-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px;
        }
        
        .plan-title {
            color: #2c3e50;
            font-size: 1.5em;
            font-weight: bold;
            margin: 0;
        }
        
        .plan-goal {
            display: inline-block;
            padding: 8px 18px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .goal-lose {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
            color: white;
            box-shadow: 0 3px 10px rgba(255, 107, 107, 0.3);
        }
        
        .goal-gain {
            background: linear-gradient(135deg, #4ecdc4 0%, #44a8a8 100%);
            color: white;
            box-shadow: 0 3px 10px rgba(78, 205, 196, 0.3);
        }
        
        .goal-maintain {
            background: linear-gradient(135deg, #45b7d1 0%, #3a9bb5 100%);
            color: white;
            box-shadow: 0 3px 10px rgba(69, 183, 209, 0.3);
        }
        
        .plan-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
            gap: 15px;
            margin: 20px 0;
        }
        
        .stat {
            text-align: center;
            padding: 15px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 10px;
            border: 2px solid #dee2e6;
            transition: all 0.3s ease;
        }
        
        .stat:hover {
            border-color: #667eea;
            transform: scale(1.05);
        }
        
        .stat-value {
            font-size: 1.5em;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        
        .stat-label {
            font-size: 0.85em;
            color: #666;
            text-transform: uppercase;
            font-weight: 600;
            letter-spacing: 0.5px;
        }
        
        .plan-actions {
            display: flex;
            gap: 12px;
            margin-top: 20px;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            font-size: 1em;
            font-weight: 600;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(0, 123, 255, 0.3);
        }
        
        .btn-primary:hover {
            background: linear-gradient(135deg, #0056b3 0%, #003d82 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0, 123, 255, 0.4);
        }
        
        .btn-danger {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
        }
        
        .btn-danger:hover {
            background: linear-gradient(135deg, #c82333 0%, #bd2130 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(220, 53, 69, 0.4);
        }
        
        .navigation {
            margin-bottom: 30px;
            text-align: center;
        }
        
        .navigation a {
            color: #667eea;
            text-decoration: none;
            margin: 0 15px;
            font-weight: 600;
            font-size: 1.05em;
            transition: color 0.3s ease;
        }
        
        .navigation a:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            color: #666;
        }
        
        .empty-state h3 {
            margin-bottom: 15px;
            color: #2c3e50;
            font-size: 1.8em;
        }
        
        .empty-state p {
            font-size: 1.1em;
            color: #555;
            margin-bottom: 30px;
        }
        
        .btn-generate {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 15px 35px;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
            box-shadow: 0 5px 20px rgba(40, 167, 69, 0.3);
        }
        
        .btn-generate:hover {
            background: linear-gradient(135deg, #218838 0%, #1ba87d 100%);
            text-decoration: none;
            color: white;
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(40, 167, 69, 0.4);
        }
        
        p strong {
            color: #2c3e50;
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