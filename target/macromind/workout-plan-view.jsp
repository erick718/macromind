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
<html>
<head>
    <title><%= plan.getPlanName() %> - MacroMind</title>
    <link href="css/custom.css" rel="stylesheet">
    <style>
        .plan-container {
            max-width: 900px;
            margin: 50px auto;
            padding: 30px;
        }
        
        .plan-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .plan-title {
            font-size: 2.5em;
            margin: 0 0 10px 0;
            font-weight: bold;
        }
        
        .plan-subtitle {
            font-size: 1.2em;
            opacity: 0.9;
            margin: 0;
        }
        
        .plan-overview {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .overview-card {
            background-color: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        .overview-value {
            font-size: 2em;
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .overview-label {
            color: #666;
            font-size: 0.9em;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .exercises-section {
            background-color: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        
        .section-title {
            font-size: 1.8em;
            color: #333;
            margin-bottom: 20px;
            border-bottom: 3px solid #007bff;
            padding-bottom: 10px;
        }
        
        .exercises-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        
        .exercise-card {
            border: 1px solid #e9ecef;
            border-radius: 10px;
            padding: 20px;
            transition: all 0.3s ease;
        }
        
        .exercise-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 15px rgba(0,0,0,0.1);
        }
        
        .exercise-name {
            font-size: 1.3em;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }
        
        .exercise-description {
            color: #666;
            margin-bottom: 15px;
            line-height: 1.4;
        }
        
        .exercise-details {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 10px;
            font-size: 0.9em;
        }
        
        .exercise-detail {
            display: flex;
            justify-content: space-between;
            padding: 5px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        
        .exercise-detail:last-child {
            border-bottom: none;
        }
        
        .detail-label {
            font-weight: bold;
            color: #555;
        }
        
        .detail-value {
            color: #333;
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