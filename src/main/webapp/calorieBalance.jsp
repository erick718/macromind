<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User, java.util.List, com.fitness.model.FoodEntry" %>
<%-- VS Code: Ignore CSS parsing warnings for JSP expressions --%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    int recommendedCalories = (int) request.getAttribute("recommendedCalories");
    int totalIntake = (int) request.getAttribute("totalIntake");
    int totalBurned = (int) request.getAttribute("totalBurned");
    int netCalories = (int) request.getAttribute("netCalories");
    int remainingCalories = (int) request.getAttribute("remainingCalories");
    List<FoodEntry> entries = (List<FoodEntry>) request.getAttribute("entries");

    // Calculate percentage of goal reached
    double progress = ((double) netCalories / recommendedCalories) * 100;
    if (progress > 100) progress = 100;
    if (progress < 0) progress = 0;

    // Determine colors
    String progressColor = (netCalories > recommendedCalories) ? "#e74c3c" : "#2ecc71";
    String netCaloriesColor = (netCalories > recommendedCalories) ? "#e74c3c" : "#2ecc71";
    String remainingCaloriesColor = (remainingCalories < 0) ? "#e74c3c" : "#2ecc71";
%>

<html>
<head>
    <title>Daily Calorie Balance</title>
    <link rel="stylesheet" href="css/custom.css">
    <script>
        // Set dynamic styles from JSP values
        document.addEventListener('DOMContentLoaded', function() {
            var netCaloriesEl = document.querySelector('.net-calories-value');
            var remainingCaloriesEl = document.querySelector('.remaining-calories-value');
            var progressBarEl = document.querySelector('.custom-progress-bar');
            
            if (netCaloriesEl) {
                netCaloriesEl.style.color = '<%= netCaloriesColor %>';
            }
            if (remainingCaloriesEl) {
                remainingCaloriesEl.style.color = '<%= remainingCaloriesColor %>';
            }
            if (progressBarEl) {
                progressBarEl.style.width = '<%= progress %>%';
                progressBarEl.style.backgroundColor = '<%= progressColor %>';
            }
        });
    </script>
</head>
<body>

<div class="container container-lg">
    <div class="page-header">
        <h1 class="page-title">Daily Calorie Balance</h1>
        <p class="page-subtitle">Track your daily calorie intake and burn for <%= user.getName() %></p>
    </div>

    <div class="nav-actions justify-center mb-4">
        <a href="food_entry.jsp" class="btn btn-primary">Log Food</a>
        <a href="dashboard" class="btn btn-outline">Back to Dashboard</a>
    </div>

    <div class="calorie-summary">
        <h2>Recommended: <%= recommendedCalories %> kcal</h2>
        <div class="grid grid-2 mb-4">
            <div class="stat-card">
                <div class="stat-value"><%= totalIntake %></div>
                <div class="stat-label">Consumed</div>
                <div class="stat-description">Total calories eaten today</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= totalBurned %></div>
                <div class="stat-label">Burned</div>
                <div class="stat-description">Calories burned through exercise</div>
            </div>
        </div>
        
        <div class="grid grid-2 mb-4">
            <div class="stat-card">
                <div class="stat-value net-calories-value"><%= netCalories %></div>
                <div class="stat-label">Net Calories</div>
                <div class="stat-description">Consumed minus burned</div>
            </div>
            <div class="stat-card">
                <div class="stat-value remaining-calories-value"><%= remainingCalories %></div>
                <div class="stat-label">Remaining</div>
                <div class="stat-description">Calories left for the day</div>
            </div>
        </div>

        <div class="progress-container">
            <div class="progress-bar custom-progress-bar"><%= String.format("%.0f", progress) %>%</div>
        </div>

        <% if (netCalories > recommendedCalories) { %>
            <div class="calorie-status over-goal">You are above your daily goal!</div>
        <% } else if (remainingCalories > 0) { %>
            <div class="calorie-status within-goal">You're within your goal range!</div>
        <% } else { %>
            <div class="calorie-status balanced">Perfectly balanced today!</div>
        <% } %>
    </div>

    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Today's Food Entries</h3>
        </div>
        <div class="card-body">
            <% if (entries.isEmpty()) { %>
                <div class="empty-state">
                    <h3>No food logged yet</h3>
                    <p>Start tracking your nutrition by logging your first meal!</p>
                </div>
            <% } else { %>
            <div class="table-responsive">
                <table class="table calorie-entries-table">
                    <thead>
                        <tr><th>Food</th><th>Calories</th><th>Protein (g)</th><th>Carbs (g)</th><th>Fat (g)</th></tr>
                    </thead>
                    <tbody>
                        <% for (FoodEntry entry : entries) { %>
                        <tr>
                            <td><%= entry.getFoodName() %></td>
                            <td><%= entry.getCalories() %></td>
                            <td><%= entry.getProtein() %></td>
                            <td><%= entry.getCarbs() %></td>
                            <td><%= entry.getFat() %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>
    </div>
</div>

</body>
</html>