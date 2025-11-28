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
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile Setup - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-md">
        <div class="page-header">
            <h1 class="page-title">Profile Setup</h1>
            <p class="page-subtitle">Complete your profile for <%= user.getName() %> to get personalized recommendations</p>
        </div>
        
        <!-- Success/Error Messages -->
        <% String error = (String) session.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-error">
                <strong>Profile Error:</strong> <%= error %>
            </div>
            <% session.removeAttribute("error"); %>
        <% } %>

        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Personal Information</h3>
                <p class="card-subtitle">This information helps us calculate accurate calorie burns and provide personalized recommendations</p>
            </div>
            <div class="card-body">
                <form action="ProfileServlet" method="post">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="age">Age (years)</label>
                            <input type="number" id="age" name="age" min="1" max="120" 
                                   value="<%= user.getAge() > 0 ? user.getAge() : "" %>" required
                                   placeholder="Enter your age">
                        </div>
                        
                        <div class="form-group">
                            <label for="height">Height (cm)</label>
                            <input type="number" id="height" name="height" min="50" max="300" 
                                   value="<%= user.getHeight() > 0 ? user.getHeight() : "" %>" required
                                   placeholder="Enter your height">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="weight">Weight (kg)</label>
                        <input type="number" id="weight" name="weight" step="0.1" min="20" max="500" 
                               value="<%= user.getWeight() > 0 ? user.getWeight() : "" %>" required
                               placeholder="Enter your current weight">
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="activity">Activity Level</label>
                            <select id="activity" name="activity" required>
                                <option value="">-- Select Activity Level --</option>
                                <option value="low" <%= "low".equals(user.getFitnessLevel()) ? "selected" : "" %>>
                                    Low - Sedentary lifestyle
                                </option>
                                <option value="moderate" <%= "moderate".equals(user.getFitnessLevel()) ? "selected" : "" %>>
                                    Moderate - Regular exercise
                                </option>
                                <option value="high" <%= "high".equals(user.getFitnessLevel()) ? "selected" : "" %>>
                                    High - Very active lifestyle
                                </option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="goal">Fitness Goal</label>
                            <select id="goal" name="goal" required>
                                <option value="">-- Select Your Goal --</option>
                                <option value="lose" <%= "lose".equals(user.getGoal()) ? "selected" : "" %>>
                                    Lose Weight
                                </option>
                                <option value="maintain" <%= "maintain".equals(user.getGoal()) ? "selected" : "" %>>
                                    Maintain Weight
                                </option>
                                <option value="gain" <%= "gain".equals(user.getGoal()) ? "selected" : "" %>>
                                    Gain Muscle
                                </option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Save Profile
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="nav-actions justify-center">
            <a href="dashboard" class="btn btn-secondary">Back to Dashboard</a>
            <a href="LogoutServlet" class="btn btn-outline">Logout</a>
        </div>
    </div>
</div>
</body>
</html>