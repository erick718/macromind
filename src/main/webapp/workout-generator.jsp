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
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workout Plan Generator - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container">
        <div class="page-header">
            <h1 class="page-title">Generate Your Personalized Workout Plan</h1>
            <p class="page-subtitle">AI-powered fitness plan based on your profile and goals</p>
        </div>
        
        <div class="nav-actions justify-center mb-4">
            <a href="dashboard.jsp" class="btn btn-secondary">← Back to Dashboard</a>
            <a href="WorkoutPlanServlet?action=list" class="btn btn-outline">My Workout Plans</a>
        </div>
        
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
                <div class="profile-info-warning">
                    <strong>Please complete your profile first:</strong>
                    <a href="profile.jsp">Update Profile</a>
                </div>
            <% } %>
        </div>
        
        <form action="WorkoutPlanServlet" method="post">
            <input type="hidden" name="action" value="generate">
            
            <h3 class="mb-3">Select Your Fitness Goal:</h3>
            
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
            
            <button type="submit" class="btn btn-success btn-lg btn-block mt-4">
                Generate My Workout Plan
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