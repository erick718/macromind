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
<html>
<head>
    <title><%= plan.getPlanName() %> - MacroMind</title>
    <link href="css/custom.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            margin: 0;
            padding: 20px;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .plan-container {
            max-width: 1100px;
            margin: 30px auto;
            padding: 0;
        }
        
        .plan-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }
        
        .plan-title {
            font-size: 2.8em;
            margin: 0 0 15px 0;
            font-weight: bold;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }
        
        .plan-subtitle {
            font-size: 1.3em;
            opacity: 0.95;
            margin: 0;
            font-weight: 500;
        }
        
        .plan-overview {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .overview-card {
            background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
            text-align: center;
            transition: all 0.3s ease;
            border: 2px solid #e9ecef;
        }
        
        .overview-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
            border-color: #667eea;
        }
        
        .overview-value {
            font-size: 2.2em;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 8px;
        }
        
        .overview-label {
            color: #666;
            font-size: 0.95em;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 600;
        }
        
        .exercises-section {
            background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
            padding: 35px;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
            border: 2px solid #e9ecef;
        }
        
        .section-title {
            font-size: 2em;
            color: #2c3e50;
            margin-bottom: 25px;
            border-bottom: 4px solid #667eea;
            padding-bottom: 12px;
            font-weight: bold;
        }
        
        .exercises-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 25px;
        }
        
        .exercise-card {
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            padding: 25px;
            transition: all 0.3s ease;
            background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
        }
        
        .exercise-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
            border-color: #667eea;
        }
        
        .exercise-name {
            font-size: 1.4em;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 12px;
        }
        
        .exercise-description {
            color: #555;
            margin-bottom: 18px;
            line-height: 1.6;
            font-size: 0.95em;
        }
        
        .exercise-details {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
            font-size: 0.95em;
            margin-bottom: 15px;
        }
        
        .exercise-detail {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid #e9ecef;
        }
        
        .exercise-detail:last-child {
            border-bottom: none;
        }
        
        .detail-label {
            font-weight: bold;
            color: #34495e;
        }
        
        .detail-value {
            color: #2c3e50;
            font-weight: 600;
        }
        
        .muscle-group {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.85em;
            margin-top: 10px;
            font-weight: bold;
            letter-spacing: 0.5px;
            box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
        }
        
        .navigation {
            margin-bottom: 25px;
            text-align: center;
            background-color: rgba(255, 255, 255, 0.95);
            padding: 15px;
            border-radius: 10px;
            box-shadow: 0 3px 15px rgba(0,0,0,0.1);
        }
        
        .navigation a {
            color: #667eea;
            text-decoration: none;
            margin: 0 15px;
            font-weight: 600;
            font-size: 1em;
            transition: color 0.3s ease;
        }
        
        .navigation a:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        
        .tips-section {
            background: linear-gradient(135deg, #fff9e6 0%, #ffedcc 100%);
            padding: 30px;
            border-radius: 12px;
            margin-top: 30px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            border: 2px solid #ffe6b3;
        }
        
        .tips-title {
            font-size: 1.6em;
            color: #2c3e50;
            margin-bottom: 20px;
            font-weight: bold;
        }
        
        .tip {
            margin-bottom: 12px;
            padding-left: 35px;
            position: relative;
            color: #34495e;
            line-height: 1.6;
            font-size: 1em;
        }
        
        .tip:before {
            content: "💡";
            position: absolute;
            left: 0;
            font-size: 1.3em;
        }
    </style>
        }
        
        .muscle-group {
            display: inline-block;
            background-color: #f8f9fa;
            color: #495057;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            margin-top: 10px;
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
        
        .tips-section {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-top: 30px;
        }
        
        .tips-title {
            font-size: 1.4em;
            color: #333;
            margin-bottom: 15px;
        }
        
        .tip {
            margin-bottom: 10px;
            padding-left: 20px;
            position: relative;
        }
        
        .tip:before {
            content: "💡";
            position: absolute;
            left: 0;
        }
    </style>
</head>
<body>
    <div class="plan-container">
        <div class="navigation">
            <a href="dashboard.jsp">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=list">My Workout Plans</a>
            <a href="WorkoutPlanServlet?action=generate">Generate New Plan</a>
            <a href="LogoutServlet">Logout</a>
        </div>
        
        <div class="plan-header">
            <h1 class="plan-title"><%= plan.getPlanName() %></h1>
            <p class="plan-subtitle">
                Personalized for <%= plan.getGoal().equals("lose") ? "Weight Loss" : 
                                   plan.getGoal().equals("gain") ? "Strength Building" : "Endurance & Maintenance" %>
            </p>
        </div>
        
        <div class="plan-overview">
            <div class="overview-card">
                <div class="overview-value"><%= plan.getDurationWeeks() %></div>
                <div class="overview-label">Weeks</div>
            </div>
            <div class="overview-card">
                <div class="overview-value"><%= plan.getSessionsPerWeek() %></div>
                <div class="overview-label">Sessions/Week</div>
            </div>
            <div class="overview-card">
                <div class="overview-value"><%= plan.getExercises().size() %></div>
                <div class="overview-label">Exercises</div>
            </div>
            <div class="overview-card">
                <div class="overview-value"><%= plan.getTotalCaloriesBurned() %></div>
                <div class="overview-label">Calories/Session</div>
            </div>
            <div class="overview-card">
                <div class="overview-value"><%= plan.getDifficulty().substring(0, 1).toUpperCase() + plan.getDifficulty().substring(1) %></div>
                <div class="overview-label">Difficulty</div>
            </div>
        </div>
        
        <div class="exercises-section">
            <h2 class="section-title">Your Exercises</h2>
            
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
                        
                        <div class="muscle-group">
                            <%= exercise.getMuscleGroup().replace("_", " ").toUpperCase() %>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
        
        <div class="tips-section">
            <h3 class="tips-title">Workout Tips & Guidelines</h3>
            
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