<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Workout Plan Generator - MacroMind</title>
    <link href="css/custom.css" rel="stylesheet">
    <style>
        .generator-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        
        .goal-option {
            margin: 15px 0;
            padding: 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .goal-option:hover, .goal-option.selected {
            border-color: #007bff;
            background-color: #f8f9fa;
        }
        
        .goal-option input[type="radio"] {
            margin-right: 10px;
        }
        
        .goal-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .goal-description {
            color: #666;
            font-size: 0.9em;
        }
        
        .profile-info {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        
        .btn-generate {
            background-color: #28a745;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            width: 100%;
        }
        
        .btn-generate:hover {
            background-color: #218838;
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
    </style>
</head>
<body>
    <div class="generator-container">
        <div class="navigation">
            <a href="dashboard.jsp">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=list">My Workout Plans</a>
            <a href="LogoutServlet">Logout</a>
        </div>
        
        <h1>Generate Your Personalized Workout Plan</h1>
        
        <% if (error != null) { %>
            <div class="alert alert-danger">
                <%= error %>
            </div>
        <% } %>
        
        <div class="profile-info">
            <h3>Your Profile:</h3>
            <p><strong>Name:</strong> <%= user.getName() %></p>
            <p><strong>Age:</strong> <%= session.getAttribute("age") != null ? session.getAttribute("age") : "Not set" %></p>
            <p><strong>Activity Level:</strong> <%= session.getAttribute("activity") != null ? session.getAttribute("activity") : "Not set" %></p>
            <p><strong>Weight:</strong> <%= session.getAttribute("weight") != null ? session.getAttribute("weight") + " kg" : "Not set" %></p>
            
            <% if (session.getAttribute("age") == null || session.getAttribute("activity") == null || session.getAttribute("weight") == null) { %>
                <div style="color: #dc3545; margin-top: 10px;">
                    <strong>⚠️ Please complete your profile first:</strong>
                    <a href="profile.jsp">Update Profile</a>
                </div>
            <% } %>
        </div>
        
        <form action="WorkoutPlanServlet" method="post">
            <input type="hidden" name="action" value="generate">
            
            <h3>Select Your Fitness Goal:</h3>
            
            <label class="goal-option">
                <input type="radio" name="goal" value="lose" required>
                <div>
                    <div class="goal-title">Weight Loss</div>
                    <div class="goal-description">
                        High-intensity cardio workouts combined with strength training to maximize calorie burn and fat loss.
                        Focus on HIIT, circuit training, and metabolic exercises.
                    </div>
                </div>
            </label>
            
            <label class="goal-option">
                <input type="radio" name="goal" value="gain" required>
                <div>
                    <div class="goal-title">Strength & Muscle Building</div>
                    <div class="goal-description">
                        Progressive strength training with compound movements to build muscle mass and increase overall strength.
                        Focus on resistance training and progressive overload.
                    </div>
                </div>
            </label>
            
            <label class="goal-option">
                <input type="radio" name="goal" value="maintain" required>
                <div>
                    <div class="goal-title">Endurance & Maintenance</div>
                    <div class="goal-description">
                        Balanced cardio and flexibility training to improve cardiovascular health and maintain current fitness level.
                        Focus on steady-state cardio and functional movements.
                    </div>
                </div>
            </label>
            
            <button type="submit" class="btn-generate">
                🏋️‍♀️ Generate My Workout Plan
            </button>
        </form>
    </div>
    
    <script>
        // Add click handlers for goal options
        document.querySelectorAll('.goal-option').forEach(option => {
            option.addEventListener('click', function() {
                // Remove selected class from all options
                document.querySelectorAll('.goal-option').forEach(opt => opt.classList.remove('selected'));
                // Add selected class to clicked option
                this.classList.add('selected');
                // Check the radio button
                this.querySelector('input[type="radio"]').checked = true;
            });
        });
        
        // Add change handler for radio buttons
        document.querySelectorAll('input[type="radio"]').forEach(radio => {
            radio.addEventListener('change', function() {
                document.querySelectorAll('.goal-option').forEach(opt => opt.classList.remove('selected'));
                this.closest('.goal-option').classList.add('selected');
            });
        });
    </script>
</body>
</html>