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
<html>
<head>
    <title>Dashboard - MacroMind</title>
    <link href="css/custom.css" rel="stylesheet">
    <style>
        .dashboard-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 30px;
        }
        
        .welcome-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 15px;
            text-align: center;
            margin-bottom: 30px;
        }
        
        .welcome-title {
            font-size: 2.5em;
            margin: 0;
            font-weight: bold;
        }
        
        .profile-info {
            background-color: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .profile-title {
            font-size: 1.5em;
            color: #333;
            margin-bottom: 20px;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        
        .profile-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
        }
        
        .stat-card {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            text-align: center;
        }
        
        .stat-value {
            font-size: 1.3em;
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .stat-label {
            color: #666;
            font-size: 0.9em;
        }
        
        .actions-section {
            background-color: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .actions-title {
            font-size: 1.5em;
            color: #333;
            margin-bottom: 20px;
            border-bottom: 2px solid #28a745;
            padding-bottom: 10px;
        }
        
        .action-buttons {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        
        .action-card {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s ease;
            text-decoration: none;
            color: inherit;
        }
        
        .action-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 15px rgba(0,0,0,0.1);
            text-decoration: none;
            color: inherit;
        }
        
        .action-card.primary {
            border-color: #007bff;
            background-color: #f8f9fa;
        }
        
        .action-card.success {
            border-color: #28a745;
            background-color: #f8fff9;
        }
        
        .action-card.warning {
            border-color: #ffc107;
            background-color: #fffdf7;
        }
        
        .action-icon {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        
        .action-title {
            font-size: 1.2em;
            font-weight: bold;
            margin-bottom: 8px;
        }
        
        .action-description {
            color: #666;
            font-size: 0.9em;
        }
        
        .navigation {
            text-align: center;
            margin-top: 30px;
        }
        
        .navigation a {
            color: #dc3545;
            text-decoration: none;
            font-weight: bold;
        }
        
        .navigation a:hover {
            text-decoration: underline;
        }
        
        .incomplete-profile {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 20px;
            color: #856404;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <div class="welcome-header">
            <h1 class="welcome-title">Welcome, <%= user.getName() %>! 🏋️‍♀️</h1>
        </div>
        
        <% 
            boolean hasCompleteProfile = session.getAttribute("age") != null && 
                                       session.getAttribute("height") != null && 
                                       session.getAttribute("weight") != null && 
                                       session.getAttribute("activity") != null && 
                                       session.getAttribute("goal") != null;
            
            if (!hasCompleteProfile) {
        %>
            <div class="incomplete-profile">
                <strong>⚠️ Complete Your Profile</strong><br>
                Please complete your profile to get personalized workout plans and recommendations.
                <a href="profile.jsp" style="color: #007bff; font-weight: bold;">Complete Profile →</a>
            </div>
        <% } %>
        
        <div class="profile-info">
            <h2 class="profile-title">Your Profile</h2>
            <div class="profile-stats">
                <div class="stat-card">
                    <div class="stat-value"><%= session.getAttribute("age") != null ? session.getAttribute("age") : "N/A" %></div>
                    <div class="stat-label">Age</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><%= session.getAttribute("height") != null ? session.getAttribute("height") + " cm" : "N/A" %></div>
                    <div class="stat-label">Height</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><%= session.getAttribute("weight") != null ? session.getAttribute("weight") + " kg" : "N/A" %></div>
                    <div class="stat-label">Weight</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><%= session.getAttribute("activity") != null ? 
                        ((String)session.getAttribute("activity")).substring(0, 1).toUpperCase() + 
                        ((String)session.getAttribute("activity")).substring(1) : "N/A" %></div>
                    <div class="stat-label">Activity Level</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><%= session.getAttribute("goal") != null ? 
                        session.getAttribute("goal").equals("lose") ? "Weight Loss" :
                        session.getAttribute("goal").equals("gain") ? "Muscle Gain" :
                        session.getAttribute("goal").equals("maintain") ? "Maintenance" : "N/A" : "N/A" %></div>
                    <div class="stat-label">Current Goal</div>
                </div>
            </div>
        </div>
        
        <div class="actions-section">
            <h2 class="actions-title">Fitness Actions</h2>
            <div class="action-buttons">
                <a href="WorkoutPlanServlet?action=generate" class="action-card success">
                    <div class="action-icon">🎯</div>
                    <div class="action-title">Generate Workout Plan</div>
                    <div class="action-description">Create a personalized workout plan based on your goals</div>
                </a>
                
                <a href="WorkoutPlanServlet?action=list" class="action-card primary">
                    <div class="action-icon">📋</div>
                    <div class="action-title">My Workout Plans</div>
                    <div class="action-description">View and manage your existing workout plans</div>
                </a>
                
                <a href="profile.jsp" class="action-card warning">
                    <div class="action-icon">👤</div>
                    <div class="action-title">Update Profile</div>
                    <div class="action-description">Modify your fitness profile and preferences</div>
                </a>
                
                <a href="database-test.jsp" class="action-card" style="border-color: #6c757d; background-color: #f8f9fa;">
                    <div class="action-icon">🔧</div>
                    <div class="action-title">Database Test</div>
                    <div class="action-description">Check database connection and troubleshoot issues</div>
                </a>
            </div>
        </div>
        
        <div class="navigation">
            <a href="LogoutServlet">🚪 Logout</a>
        </div>
    </div>
</body>
</html>