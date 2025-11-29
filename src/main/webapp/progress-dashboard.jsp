<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
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
    Map<String, Object> filteredProgress = (Map<String, Object>) request.getAttribute("filteredProgress");
    @SuppressWarnings("unchecked")
    Map<String, Object> progressMetrics = (Map<String, Object>) request.getAttribute("progressMetrics");
    @SuppressWarnings("unchecked")
    Map<String, String> insights = (Map<String, String>) request.getAttribute("insights");
    
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
    String dateRange = (String) request.getAttribute("dateRange");
    
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
    if (filteredProgress == null) filteredProgress = new java.util.HashMap<>();
    if (progressMetrics == null) progressMetrics = new java.util.HashMap<>();
    if (insights == null) insights = new java.util.HashMap<>();
    if (recommendedDailyIntake == null) recommendedDailyIntake = 2000.0;
    if (bmr == null) bmr = 1800.0;
    if (tdee == null) tdee = 2200.0;
    if (workoutStreak == null) workoutStreak = 0;
    if (dateRange == null) dateRange = "week";

    // Date formatter for MM/DD/YYYY display
    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    String formattedStartDate = "";
    String formattedEndDate = "";
    
    if (startDate != null && !startDate.isEmpty()) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            formattedStartDate = start.format(displayFormatter);
        } catch (Exception e) {
            formattedStartDate = startDate;
        }
    }
    
    if (endDate != null && !endDate.isEmpty()) {
        try {
            LocalDate end = LocalDate.parse(endDate);
            formattedEndDate = end.format(displayFormatter);
        } catch (Exception e) {
            formattedEndDate = endDate;
        }
    }
    if (dateRange == null) dateRange = "week";
    
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

        <!-- Date Range Filter -->
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Filter by Date Range</h3>
            </div>
            <div class="card-body">
                <form method="get" action="progress-dashboard" id="dateFilterForm">
                    <!-- Quick Filter Buttons -->
                    <div class="filter-tabs">
                        <button type="button" class="filter-tab <%= request.getParameter("range") == null || "week".equals(request.getParameter("range")) ? "active" : "" %>" 
                                onclick="setDateRange('week')">Last 7 Days</button>
                        <button type="button" class="filter-tab <%= "month".equals(request.getParameter("range")) ? "active" : "" %>" 
                                onclick="setDateRange('month')">Last 30 Days</button>
                        <button type="button" class="filter-tab <%= "quarter".equals(request.getParameter("range")) ? "active" : "" %>" 
                                onclick="setDateRange('quarter')">Last 90 Days</button>
                        <button type="button" class="filter-tab <%= "all".equals(request.getParameter("range")) ? "active" : "" %>" 
                                onclick="setDateRange('all')">All Time</button>
                        <button type="button" class="filter-tab <%= "custom".equals(request.getParameter("range")) ? "active" : "" %>" 
                                onclick="setDateRange('custom')">Custom Range</button>
                    </div>
                    
                    <!-- Custom Date Range Inputs -->
                    <div id="customDateRange" class="<%= "custom".equals(request.getParameter("range")) ? "" : "d-none" %>" style="margin-top: 1rem;">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="startDate">Start Date</label>
                                <input type="date" id="startDate" name="startDate" class="form-input"
                                       max="<%= java.time.LocalDate.now() %>"
                                       value="<%= request.getParameter("startDate") != null ? request.getParameter("startDate") : "" %>"
                                       required>
                            </div>
                            <div class="form-group">
                                <label for="endDate">End Date</label>
                                <input type="date" id="endDate" name="endDate" class="form-input"
                                       max="<%= java.time.LocalDate.now() %>"
                                       value="<%= request.getParameter("endDate") != null ? request.getParameter("endDate") : "" %>"
                                       required>
                            </div>
                            <div class="form-group" style="display: flex; align-items: flex-end;">
                                <button type="submit" class="btn btn-primary" onclick="return validateDateRange()">Apply Filter</button>
                            </div>
                        </div>
                    </div>
                    
                    <input type="hidden" id="range" name="range" value="<%= request.getParameter("range") != null ? request.getParameter("range") : "week" %>">
                </form>
            </div>
        </div>

        <script>
            function setDateRange(range) {
                document.getElementById('range').value = range;
                
                if (range === 'custom') {
                    document.getElementById('customDateRange').style.display = 'block';
                } else {
                    document.getElementById('customDateRange').style.display = 'none';
                    document.getElementById('dateFilterForm').submit();
                }
            }
            
            function validateDateRange() {
                var startDate = document.getElementById('startDate').value;
                var endDate = document.getElementById('endDate').value;
                var today = new Date().toISOString().split('T')[0];
                
                // Check if dates are provided
                if (!startDate || !endDate) {
                    alert('Please select both start and end dates.');
                    return false;
                }
                
                // Check if dates are not in the future
                if (startDate > today || endDate > today) {
                    alert('Cannot select future dates. Please select dates up to today.');
                    return false;
                }
                
                // Check if start date is before end date
                if (startDate > endDate) {
                    alert('Start date must be before or equal to end date.');
                    return false;
                }
                
                return true;
            }
        </script>

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

        <!-- Filtered Date Range Summary -->
        <% if (startDate != null && endDate != null) { %>
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Showing Results for: 
                    <%
                    if ("week".equals(dateRange)) {
                        out.print("Last 7 Days");
                    } else if ("month".equals(dateRange)) {
                        out.print("Last 30 Days");
                    } else if ("quarter".equals(dateRange)) {
                        out.print("Last 90 Days");
                    } else if ("all".equals(dateRange)) {
                        out.print("All Time");
                    } else if ("custom".equals(dateRange)) {
                        out.print(formattedStartDate + " to " + formattedEndDate);
                    }
                    %>
                </h3>
            </div>
            <div class="card-body">
                <div class="card-grid">
                    <div class="stat-card" style="border-left-color: var(--success-color);">
                        <div class="stat-value"><%= filteredProgress.get("totalWorkouts") != null ? filteredProgress.get("totalWorkouts") : 0 %></div>
                        <div class="stat-label">Total Workouts</div>
                        <div class="stat-description">in selected range</div>
                    </div>
                    
                    <div class="stat-card" style="border-left-color: var(--info-color);">
                        <div class="stat-value"><%= filteredProgress.get("totalDuration") != null ? filteredProgress.get("totalDuration") : 0 %></div>
                        <div class="stat-label">Total Minutes</div>
                        <div class="stat-description">
                            <%
                            Integer filteredWorkouts = (Integer) filteredProgress.get("totalWorkouts");
                            Integer filteredDuration = (Integer) filteredProgress.get("totalDuration");
                            if (filteredWorkouts != null && filteredWorkouts > 0 && filteredDuration != null) {
                                double avgDuration = (double) filteredDuration / filteredWorkouts;
                                out.print(integerFormat.format(avgDuration) + " min average");
                            } else {
                                out.print("No workouts yet");
                            }
                            %>
                        </div>
                    </div>
                    
                    <div class="stat-card" style="border-left-color: var(--danger-color);">
                        <div class="stat-value"><%= filteredProgress.get("totalCalories") != null ? integerFormat.format((Double)filteredProgress.get("totalCalories")) : "0" %></div>
                        <div class="stat-label">Calories Burned</div>
                        <div class="stat-description">
                            <%
                            Double filteredCalories = (Double) filteredProgress.get("totalCalories");
                            Double avgFilteredDaily = (Double) progressMetrics.get("avgFilteredDailyCalorieBurn");
                            if (avgFilteredDaily != null && avgFilteredDaily > 0) {
                                out.print(integerFormat.format(avgFilteredDaily) + " per day");
                            } else {
                                out.print("Start burning!");
                            }
                            %>
                        </div>
                    </div>
                    
                    <div class="stat-card" style="border-left-color: var(--warning-color);">
                        <div class="stat-value">
                            <%
                            Double avgFilteredWorkoutDuration = (Double) progressMetrics.get("avgFilteredWorkoutDuration");
                            out.print(avgFilteredWorkoutDuration != null ? integerFormat.format(avgFilteredWorkoutDuration) : "0");
                            %>
                        </div>
                        <div class="stat-label">Avg Workout Duration</div>
                        <div class="stat-description">minutes per session</div>
                    </div>
                </div>
            </div>
        </div>
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