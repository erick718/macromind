<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
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
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            margin: 0;
            padding: 20px;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .generator-container {
            max-width: 700px;
            margin: 50px auto;
            padding: 40px;
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }
        
        h1 {
            color: #2c3e50;
            font-size: 2em;
            margin-bottom: 25px;
            text-align: center;
        }
        
        h3 {
            color: #34495e;
            font-size: 1.3em;
            margin: 25px 0 15px 0;
        }
        
        .goal-option {
            margin: 15px 0;
            padding: 20px;
            border: 3px solid #e0e0e0;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
            background-color: #fafafa;
        }
        
        .goal-option:hover {
            border-color: #667eea;
            background-color: #f0f4ff;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.2);
        }
        
        .goal-option.selected {
            border-color: #667eea;
            background: linear-gradient(135deg, #f0f4ff 0%, #e8ecff 100%);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
        }
        
        .goal-option input[type="radio"] {
            margin-right: 12px;
            width: 18px;
            height: 18px;
            cursor: pointer;
        }
        
        .goal-title {
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 8px;
            font-size: 1.15em;
        }
        
        .goal-description {
            color: #555;
            font-size: 0.95em;
            line-height: 1.6;
        }
        
        .profile-info {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 25px;
            border-left: 5px solid #667eea;
        }
        
        .profile-info h3 {
            margin-top: 0;
            color: #2c3e50;
        }
        
        .profile-info p {
            margin: 10px 0;
            color: #2c3e50;
            font-size: 1em;
        }
        
        .profile-info strong {
            color: #34495e;
            min-width: 120px;
            display: inline-block;
        }
        
        .btn-generate {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            width: 100%;
            margin-top: 20px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
        }
        
        .btn-generate:hover {
            background: linear-gradient(135deg, #218838 0%, #1ba87d 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(40, 167, 69, 0.4);
        }
        
        .navigation {
            margin-bottom: 25px;
            text-align: center;
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
        
        .alert {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-weight: 500;
        }
        
        .alert-danger {
            background-color: #f8d7da;
            border: 2px solid #dc3545;
            color: #721c24;
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
            <p><strong>Age:</strong> <%= user.getAge() > 0 ? user.getAge() : "Not set" %></p>
            <p><strong>Fitness Level:</strong> <%= user.getFitnessLevel() != null && !user.getFitnessLevel().isEmpty() ? user.getFitnessLevel() : "Not set" %></p>
            <p><strong>Weight:</strong> <%= user.getWeight() > 0 ? user.getWeight() + " kg" : "Not set" %></p>
            <p><strong>Height:</strong> <%= user.getHeight() > 0 ? user.getHeight() + " cm" : "Not set" %></p>
            <p><strong>Goal:</strong> <%= user.getGoal() != null && !user.getGoal().isEmpty() ? user.getGoal() : "Not set" %></p>
            
            <% if (user.getAge() <= 0 || user.getWeight() <= 0 || user.getFitnessLevel() == null || user.getFitnessLevel().isEmpty()) { %>
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