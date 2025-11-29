<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%@ page import="com.fitness.Model.Workout" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<Workout> workouts = (List<Workout>) request.getAttribute("workouts");
    @SuppressWarnings("unchecked")
    Map<String, Object> progressSummary = (Map<String, Object>) request.getAttribute("progressSummary");
    @SuppressWarnings("unchecked")
    Map<String, Integer> exerciseDistribution = (Map<String, Integer>) request.getAttribute("exerciseDistribution");
    Integer workoutStreak = (Integer) request.getAttribute("workoutStreak");
    String currentFilter = (String) request.getAttribute("currentFilter");
    
    if (workouts == null) workouts = new java.util.ArrayList<>();
    if (progressSummary == null) progressSummary = new java.util.HashMap<>();
    if (exerciseDistribution == null) exerciseDistribution = new java.util.HashMap<>();
    if (workoutStreak == null) workoutStreak = 0;
    if (currentFilter == null) currentFilter = "all";
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Workout History - MacroMind</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-xl">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">Workout History</h1>
            <p class="page-subtitle">Welcome back, <strong><%= user.getName() %></strong>! Track your fitness journey.</p>
            <div class="page-actions">
                <a href="workout-log" class="btn btn-primary">
                    
                    Log New Workout
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

        <!-- Summary Cards -->
        <div class="card-grid">
            <div class="stat-card">
                <div class="stat-value">Total Workouts: <%= progressSummary.get("totalWorkouts") != null ? progressSummary.get("totalWorkouts") : 0 %></div>
                <!-- <div class="stat-label">Total Workouts</div> -->
                
            </div>
            <div class="stat-card">
                <div class="stat-value">Total Duration: <%= progressSummary.get("totalDuration") != null ? progressSummary.get("totalDuration") + " min" : "0 min" %></div>
                <!-- <div class="stat-label">Total Duration</div> -->
                
            </div>
            <div class="stat-card">
                <div class="stat-value">Total Calories: <%= progressSummary.get("totalCalories") != null ? decimalFormat.format((Double)progressSummary.get("totalCalories")) : "0" %></div>
                <!-- <div class="stat-label">Calories Burned</div> -->
                
            </div>
            <div class="stat-card">
                <div class="stat-value">Workout Streak: <%= workoutStreak %> days</div>
                <!-- <div class="stat-label">Workout Streak</div> -->

            </div>
        </div>

        <!-- Progress Charts -->
        <div class="card-grid">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Exercise Type Distribution</h3>
                </div>
                <div class="card-body">
                    <% if (exerciseDistribution.isEmpty()) { %>
                        <div class="empty-state">
                            <p>No workout data available</p>
                        </div>
                    <% } else { %>
                        <% 
                        int maxCount = exerciseDistribution.values().stream().mapToInt(Integer::intValue).max().orElse(1);
                        for (Map.Entry<String, Integer> entry : exerciseDistribution.entrySet()) { 
                            double barWidth = (double) entry.getValue() / maxCount * 100;
                        %>
                            <div class="exercise-type-item">
                                <div>
                                    <div class="exercise-name"><%= entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1) %></div>
                                    <!-- <div class="exercise-type-bar" data-width="<%= String.format("%.1f", barWidth) %>" title="Count: <%= entry.getValue() %>, Width: <%= String.format("%.1f", barWidth) %>%"></div> -->
                                </div>
                                <span class="exercise-count"><%= entry.getValue() %></span>
                            </div>
                        <% } %>
                    <% } %>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Performance Overview</h3>
                </div>
                <div class="card-body">
                <div class="exercise-type-item">
                    <span>Average Workout Duration</span>
                    <span><%= progressSummary.get("avgDuration") != null ? decimalFormat.format((Double)progressSummary.get("avgDuration")) + " min" : "0 min" %></span>
                </div>
                <div class="exercise-type-item">
                    <span>Workout Days</span>
                    <span><%= progressSummary.get("workoutDays") != null ? progressSummary.get("workoutDays") : 0 %></span>
                </div>
                <div class="exercise-type-item">
                    <span>Avg Calories/Workout</span>
                    <span>
                        <% 
                        Object totalWorkouts = progressSummary.get("totalWorkouts");
                        Object totalCalories = progressSummary.get("totalCalories");
                        if (totalWorkouts != null && totalCalories != null && (Integer)totalWorkouts > 0) {
                            double avgCal = (Double)totalCalories / (Integer)totalWorkouts;
                            out.print(decimalFormat.format(avgCal));
                        } else {
                            out.print("0");
                        }
                        %>
                    </span>
                </div>
                </div>
            </div>
        </div>

        <!-- Filter Tabs -->
        <div class="filter-tabs">
            <a href="workout-history?filter=all" class="filter-tab <%= "all".equals(currentFilter) ? "active" : "" %>">All Time</a>
            <a href="workout-history?filter=week" class="filter-tab <%= "week".equals(currentFilter) ? "active" : "" %>">Last 7 Days</a>
            <a href="workout-history?filter=month" class="filter-tab <%= "month".equals(currentFilter) ? "active" : "" %>">Last 30 Days</a>
        </div>

        <!-- Workout List -->
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Recent Workouts</h3>
            </div>
            <div class="card-body">
                <% if (workouts.isEmpty()) { %>
                    <div class="empty-state">
                        <h3>No workouts found</h3>
                        <p>Start your fitness journey by logging your first workout!</p>
                        <a href="workout-log" class="btn btn-primary">
                            
                            Log Your First Workout
                        </a>
                    </div>
                <% } else { %>
                <% for (Workout workout : workouts) { %>
                    <div class="workout-item">
                        <div class="workout-info">
                            <div class="workout-name"><%= workout.getExerciseName() %></div>
                            <div class="workout-details">
                                <span class="exercise-type-badge"><%= workout.getExerciseType().substring(0,1).toUpperCase() + workout.getExerciseType().substring(1) %></span>
                                <span class="workout-date"><%= dateFormat.format(workout.getWorkoutDate()) %></span>
                                <% if (workout.getWorkoutTime() != null) { %>
                                    <span class="workout-time">at <%= timeFormat.format(workout.getWorkoutTime()) %></span>
                                <% } %>
                                <% if (workout.getNotes() != null && !workout.getNotes().trim().isEmpty()) { %>
                                    <div class="workout-notes">"<%= workout.getNotes() %>"</div>
                                <% } %>
                            </div>
                        </div>
                        
                        <div class="workout-stats">
                            <div class="workout-stat">
                                <div class="workout-stat-value"><%= workout.getDurationMinutes() %></div>
                                <div class="workout-stat-label">Minutes</div>
                            </div>
                            
                            <% if ("strength".equals(workout.getExerciseType()) && workout.getSetsCount() > 0) { %>
                                <div class="workout-stat">
                                    <div class="workout-stat-value"><%= workout.getSetsCount() %>×<%= workout.getRepsPerSet() %></div>
                                    <div class="workout-stat-label">Sets×Reps</div>
                                </div>
                                <% if (workout.getWeightKg() > 0) { %>
                                    <div class="workout-stat">
                                        <div class="workout-stat-value"><%= decimalFormat.format(workout.getWeightKg()) %></div>
                                        <div class="workout-stat-label">kg</div>
                                    </div>
                                <% } %>
                            <% } %>
                            
                            <div class="workout-stat">
                                <div class="workout-stat-value"><%= Math.round(workout.getCaloriesBurned()) %></div>
                                <div class="workout-stat-label">Calories</div>
                            </div>
                        </div>
                        
                        <div class="workout-actions">
                            <a href="workout-edit?id=<%= workout.getWorkoutId() %>" class="btn btn-outline btn-sm">
                                Edit
                            </a>
                            <form method="post" class="delete-form" 
                                  onsubmit="return confirm('Are you sure you want to delete this workout?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="workoutId" value="<%= workout.getWorkoutId() %>">
                                <button type="submit" class="btn btn-danger btn-sm">
                                    Delete
                                </button>
                            </form>
                        </div>
                    </div>
                <% } %>
            <% } %>
            </div>
        </div>
    </div>

    <script>
        // Set dynamic bar widths for exercise distribution
        document.addEventListener('DOMContentLoaded', function() {
            const bars = document.querySelectorAll('.exercise-type-bar[data-width]');
            bars.forEach(function(bar) {
                const width = bar.getAttribute('data-width');
                bar.style.width = width + '%';
            });
        });
    </script>
</body>
</html>