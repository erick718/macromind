<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.fitness.util.CalorieCalculator" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    Map<String, Object> weeklyProgress = (Map<String, Object>) request.getAttribute("weeklyProgress");
    @SuppressWarnings("unchecked")
    Map<String, Object> monthlyProgress = (Map<String, Object>) request.getAttribute("monthlyProgress");
    @SuppressWarnings("unchecked")
    Map<String, Object> progressMetrics = (Map<String, Object>) request.getAttribute("progressMetrics");
    @SuppressWarnings("unchecked")
    Map<String, String> insights = (Map<String, String>) request.getAttribute("insights");
    
    Double recommendedDailyIntake = (Double) request.getAttribute("recommendedDailyIntake");
    Double bmr = (Double) request.getAttribute("bmr");
    Double tdee = (Double) request.getAttribute("tdee");
    CalorieCalculator.CalorieBalanceSummary weeklyBalance = 
        (CalorieCalculator.CalorieBalanceSummary) request.getAttribute("weeklyBalance");
    CalorieCalculator.CalorieBalanceSummary monthlyBalance = 
        (CalorieCalculator.CalorieBalanceSummary) request.getAttribute("monthlyBalance");
    
    Integer workoutStreak = (Integer) request.getAttribute("workoutStreak");
    
    if (weeklyProgress == null) weeklyProgress = new java.util.HashMap<>();
    if (monthlyProgress == null) monthlyProgress = new java.util.HashMap<>();
    if (progressMetrics == null) progressMetrics = new java.util.HashMap<>();
    if (insights == null) insights = new java.util.HashMap<>();
    if (recommendedDailyIntake == null) recommendedDailyIntake = 2000.0;
    if (bmr == null) bmr = 1800.0;
    if (tdee == null) tdee = 2200.0;
    if (workoutStreak == null) workoutStreak = 0;
    
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    DecimalFormat integerFormat = new DecimalFormat("#");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Progress Dashboard - MacroMind</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-xl">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">Progress Dashboard</h1>
            <p class="page-subtitle">Welcome back, <strong><%= user.getName() %></strong>! Track your fitness analytics.</p>
            <div class="page-actions">
                <a href="workout-log" class="btn btn-primary">
                    
                    Log Workout
                </a>
                <a href="workout-history" class="btn btn-secondary">
                    
                    History
                </a>
                <a href="dashboard" class="btn btn-outline">
                    
                    Dashboard
                </a>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <% String message = (String) session.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-success">
                <%= message %>
            </div>
            <% session.removeAttribute("message"); %>
        <% } %>
        
        <% String error = (String) session.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-error">
                <%= error %>
            </div>
            <% session.removeAttribute("error"); %>
        <% } %>

        <!-- Key Metrics -->
        <div class="card-grid">
            <div class="stat-card">
                <div class="stat-value"><%= workoutStreak %></div>
                <div class="stat-label">Day Workout Streak</div>
                
                <div class="stat-description">Keep it burning!</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-value"><%= weeklyProgress.get("totalWorkouts") != null ? weeklyProgress.get("totalWorkouts") : 0 %></div>
                <div class="stat-label">Workouts This Week</div>
                
                <div class="stat-description"><%= weeklyProgress.get("totalDuration") != null ? weeklyProgress.get("totalDuration") + " total minutes" : "0 minutes" %></div>
            </div>
            
            <div class="stat-card">
                <div class="stat-value"><%= weeklyProgress.get("totalCalories") != null ? integerFormat.format((Double)weeklyProgress.get("totalCalories")) : "0" %></div>
                <div class="stat-label">Weekly Calories Burned</div>
                
                <div class="stat-description">
                    <%
                    Double weeklyCalories = (Double) weeklyProgress.get("totalCalories");
                    if (weeklyCalories != null && weeklyCalories > 0) {
                        double dailyAvg = weeklyCalories / 7.0;
                        out.print(integerFormat.format(dailyAvg) + " per day average");
                    } else {
                        out.print("Start working out!");
                    }
                    %>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-value">
                    <%
                    String goal = user.getGoal();
                    if (goal != null) {
                        switch(goal.toLowerCase()) {
                            case "lose": out.print("Lose Weight"); break;
                            case "maintain": out.print("Maintain"); break;
                            case "gain": out.print("Build Muscle"); break;
                            default: out.print("Get Fit");
                        }
                    } else {
                        out.print("Get Fit");
                    }
                    %>
                </div>
                <div class="stat-label">Your Goal</div>
                
                <div class="stat-description">
                    <% Boolean onTrack = (Boolean) progressMetrics.get("isOnTrackWithGoal"); %>
                    <%= (onTrack != null && onTrack) ? "On track!" : "Let's push harder!" %>
                </div>
            </div>
        </div>

        <!-- Progress Comparison Charts -->
        <div class="card-grid">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Weekly vs Monthly Average</h3>
                </div>
                <div class="card-body">
                
                <div class="comparison-row">
                    <span class="comparison-label">Workouts per Week</span>
                    <span class="comparison-value">
                        <%= weeklyProgress.get("totalWorkouts") != null ? weeklyProgress.get("totalWorkouts") : 0 %> 
                        vs 
                        <%= monthlyProgress.get("totalWorkouts") != null ? Math.round((Integer)monthlyProgress.get("totalWorkouts") / 4.0) : 0 %>
                    </span>
                </div>
                
                <div class="comparison-row">
                    <span class="comparison-label">Avg Workout Duration</span>
                    <span class="comparison-value">
                        <%= progressMetrics.get("avgWeeklyWorkoutDuration") != null ? integerFormat.format((Double)progressMetrics.get("avgWeeklyWorkoutDuration")) + " min" : "0 min" %>
                    </span>
                </div>
                
                <div class="comparison-row">
                    <span class="comparison-label">Weekly Frequency</span>
                    <span class="comparison-value">
                        <%= progressMetrics.get("weeklyFrequency") != null ? decimalFormat.format((Double)progressMetrics.get("weeklyFrequency")) + " per day" : "0 per day" %>
                    </span>
                </div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Calorie Analysis</h3>
                </div>
                <div class="card-body">
                
                <div class="comparison-row">
                    <span class="comparison-label">Your BMR</span>
                    <span class="comparison-value"><%= integerFormat.format(bmr) %> cal/day</span>
                </div>
                
                <div class="comparison-row">
                    <span class="comparison-label">Your TDEE</span>
                    <span class="comparison-value"><%= integerFormat.format(tdee) %> cal/day</span>
                </div>
                
                <div class="comparison-row">
                    <span class="comparison-label">Recommended Intake</span>
                    <span class="comparison-value"><%= integerFormat.format(recommendedDailyIntake) %> cal/day</span>
                </div>
                
                <div class="comparison-row">
                    <span class="comparison-label">Daily Burn (Exercise)</span>
                    <span class="comparison-value">
                        <%= progressMetrics.get("avgDailyCalorieBurn") != null ? integerFormat.format((Double)progressMetrics.get("avgDailyCalorieBurn")) + " cal" : "0 cal" %>
                    </span>
                </div>
            </div>
        </div>

        <!-- Calorie Balance Summary -->
        <% if (weeklyBalance != null) { %>
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Weekly Calorie Balance</h3>
            </div>
            <div class="card-body">
                <div class="calorie-balance <%= weeklyBalance.getActualBalance() > 100 ? "positive" : (weeklyBalance.getActualBalance() < -100 ? "negative" : "neutral") %>">
                    <strong>Balance Status: <%= weeklyBalance.getBalanceStatus() %></strong><br>
                    Exercise Calories Burned: <%= integerFormat.format(weeklyBalance.getTotalBurned()) %><br>
                    <% if (weeklyBalance.getTotalIntake() > 0) { %>
                        Calories Consumed: <%= integerFormat.format(weeklyBalance.getTotalIntake()) %><br>
                        Net Balance: <%= integerFormat.format(weeklyBalance.getActualBalance()) %> calories<br>
                    <% } %>
                    <small>Note: Add nutrition tracking to see complete calorie balance</small>
                </div>
            </div>
        </div>
        <% } %>

        <!-- Personalized Insights -->
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Your Personalized Insights</h3>
            </div>
            <div class="card-body">
            
                            <% if (insights.get("consistency") != null) { %>
                <div class="insight-item">
                    <div class="insight-category">Workout Consistency</div>
                    <div class="insight-message"><%= insights.get("consistency") %></div>
                </div>
                <% } %>
                
                <% if (insights.get("variety") != null) { %>
                <div class="insight-item">
                    <div class="insight-category">Exercise Variety</div>
                    <div class="insight-message"><%= insights.get("variety") %></div>
                </div>
                <% } %>
                
                <% if (insights.get("intensity") != null) { %>
                <div class="insight-item">
                    <div class="insight-category">Workout Intensity</div>
                    <div class="insight-message"><%= insights.get("intensity") %></div>
                </div>
                <% } %>
                
                <% if (insights.get("goal_progress") != null) { %>
                <div class="insight-item">
                    <div class="insight-category">Goal Progress</div>
                    <div class="insight-message"><%= insights.get("goal_progress") %></div>
                </div>
                <% } %>
                
                <% if (insights.get("recommendation") != null) { %>
                <div class="insight-item">
                    <div class="insight-category">Recommendation</div>
                    <div class="insight-message"><%= insights.get("recommendation") %></div>
                </div>
                <% } %>
            </div>
        </div>
        </div>
    </div>
</body>
</html>