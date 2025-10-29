package com.fitness.servlets;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitness.dao.WorkoutDAO;
import com.fitness.model.User;
import com.fitness.model.Workout;
import com.fitness.util.CalorieCalculator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProgressDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("ProgressDashboardServlet: Loading progress dashboard for user: " + user.getName());

        try {
            WorkoutDAO workoutDAO = new WorkoutDAO();
            
            // Calculate date ranges
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(7);
            LocalDate monthStart = today.minusDays(30);
            LocalDate quarterStart = today.minusDays(90);
            
            // Get workout data for different time periods
            Map<String, Object> weeklyProgress = workoutDAO.getWeeklyProgressSummary(
                user.getUserId(), Date.valueOf(weekStart), Date.valueOf(today)
            );
            
            Map<String, Object> monthlyProgress = workoutDAO.getMonthlyProgressSummary(
                user.getUserId(), Date.valueOf(monthStart), Date.valueOf(today)
            );
            
            Map<String, Object> quarterlyProgress = workoutDAO.getMonthlyProgressSummary(
                user.getUserId(), Date.valueOf(quarterStart), Date.valueOf(today)
            );
            
            // Get recent workouts for trend analysis
            List<Workout> recentWorkouts = workoutDAO.getWorkoutsByDateRange(
                user.getUserId(), Date.valueOf(monthStart), Date.valueOf(today)
            );
            
            // Exercise type distribution
            Map<String, Integer> exerciseDistribution = workoutDAO.getExerciseTypeDistribution(
                user.getUserId(), Date.valueOf(monthStart), Date.valueOf(today)
            );
            
            // Workout streak
            int workoutStreak = workoutDAO.getWorkoutStreak(user.getUserId());
            
            // Calculate calorie balance and recommendations
            boolean isMale = true; // TODO: Add gender field to User model
            double recommendedDailyIntake = CalorieCalculator.calculateRecommendedDailyIntake(user, isMale);
            double bmr = CalorieCalculator.calculateBMR(user, isMale);
            double tdee = CalorieCalculator.calculateTDEE(bmr, user.getFitnessLevel());
            
            // Weekly calorie balance analysis
            double weeklyCaloriesBurned = (Double) weeklyProgress.getOrDefault("totalCalories", 0.0);
            double weeklyCaloriesIntake = 0; // TODO: Integrate nutrition tracking
            CalorieCalculator.CalorieBalanceSummary weeklyBalance = 
                CalorieCalculator.calculateWeeklyBalance(
                    weeklyCaloriesBurned, weeklyCaloriesIntake, recommendedDailyIntake, 7
                );
            
            // Monthly calorie balance analysis
            double monthlyCaloriesBurned = (Double) monthlyProgress.getOrDefault("totalCalories", 0.0);
            double monthlyCaloriesIntake = 0; // TODO: Integrate nutrition tracking
            CalorieCalculator.CalorieBalanceSummary monthlyBalance = 
                CalorieCalculator.calculateWeeklyBalance(
                    monthlyCaloriesBurned, monthlyCaloriesIntake, recommendedDailyIntake, 30
                );
            
            // Calculate progress metrics
            Map<String, Object> progressMetrics = calculateProgressMetrics(
                weeklyProgress, monthlyProgress, quarterlyProgress, user
            );
            
            // Generate insights and recommendations
            Map<String, String> insights = generateInsights(
                weeklyProgress, monthlyProgress, exerciseDistribution, workoutStreak, user
            );
            
            // Set request attributes for JSP
            request.setAttribute("weeklyProgress", weeklyProgress);
            request.setAttribute("monthlyProgress", monthlyProgress);
            request.setAttribute("quarterlyProgress", quarterlyProgress);
            request.setAttribute("exerciseDistribution", exerciseDistribution);
            request.setAttribute("workoutStreak", workoutStreak);
            request.setAttribute("recentWorkouts", recentWorkouts);
            request.setAttribute("recommendedDailyIntake", recommendedDailyIntake);
            request.setAttribute("bmr", bmr);
            request.setAttribute("tdee", tdee);
            request.setAttribute("weeklyBalance", weeklyBalance);
            request.setAttribute("monthlyBalance", monthlyBalance);
            request.setAttribute("progressMetrics", progressMetrics);
            request.setAttribute("insights", insights);
            
            System.out.println("ProgressDashboardServlet: Weekly calories burned: " + weeklyCaloriesBurned);
            System.out.println("ProgressDashboardServlet: Monthly calories burned: " + monthlyCaloriesBurned);
            System.out.println("ProgressDashboardServlet: Recommended daily intake: " + recommendedDailyIntake);

            // Forward to the JSP
            request.getRequestDispatcher("/progress-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error loading progress dashboard: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error loading progress dashboard: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    /**
     * Calculate various progress metrics and trends
     */
    private Map<String, Object> calculateProgressMetrics(
            Map<String, Object> weekly, 
            Map<String, Object> monthly, 
            Map<String, Object> quarterly, 
            User user) {
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Workout frequency trends
        int weeklyWorkouts = (Integer) weekly.getOrDefault("totalWorkouts", 0);
        int monthlyWorkouts = (Integer) monthly.getOrDefault("totalWorkouts", 0);
        int quarterlyWorkouts = (Integer) quarterly.getOrDefault("totalWorkouts", 0);
        
        double weeklyFrequency = weeklyWorkouts / 7.0; // workouts per day
        double monthlyFrequency = monthlyWorkouts / 30.0;
        double quarterlyFrequency = quarterlyWorkouts / 90.0;
        
        metrics.put("weeklyFrequency", weeklyFrequency);
        metrics.put("monthlyFrequency", monthlyFrequency);
        metrics.put("quarterlyFrequency", quarterlyFrequency);
        
        // Duration trends
        int weeklyDuration = (Integer) weekly.getOrDefault("totalDuration", 0);
        int monthlyDuration = (Integer) monthly.getOrDefault("totalDuration", 0);
        
        double avgWeeklyWorkoutDuration = weeklyWorkouts > 0 ? (double) weeklyDuration / weeklyWorkouts : 0;
        double avgMonthlyWorkoutDuration = monthlyWorkouts > 0 ? (double) monthlyDuration / monthlyWorkouts : 0;
        
        metrics.put("avgWeeklyWorkoutDuration", avgWeeklyWorkoutDuration);
        metrics.put("avgMonthlyWorkoutDuration", avgMonthlyWorkoutDuration);
        
        // Calorie burn trends
        double weeklyCalories = (Double) weekly.getOrDefault("totalCalories", 0.0);
        double monthlyCalories = (Double) monthly.getOrDefault("totalCalories", 0.0);
        
        double avgDailyCalorieBurn = weeklyCalories / 7.0;
        double avgWeeklyCalorieBurn = monthlyCalories / 4.0; // approximate weeks in month
        
        metrics.put("avgDailyCalorieBurn", avgDailyCalorieBurn);
        metrics.put("avgWeeklyCalorieBurn", avgWeeklyCalorieBurn);
        
        // Goal alignment metrics
        String goal = user.getGoal();
        double goalCalorieAdjustment = CalorieCalculator.calculateCalorieGoalAdjustment(0, goal);
        boolean isOnTrackWithGoal = evaluateGoalProgress(avgDailyCalorieBurn, goalCalorieAdjustment, goal);
        
        metrics.put("goalCalorieAdjustment", goalCalorieAdjustment);
        metrics.put("isOnTrackWithGoal", isOnTrackWithGoal);
        
        return metrics;
    }
    
    /**
     * Generate personalized insights and recommendations
     */
    private Map<String, String> generateInsights(
            Map<String, Object> weekly,
            Map<String, Object> monthly,
            Map<String, Integer> exerciseDistribution,
            int workoutStreak,
            User user) {
        
        Map<String, String> insights = new HashMap<>();
        
        // Workout consistency insight
        int weeklyWorkouts = (Integer) weekly.getOrDefault("totalWorkouts", 0);
        int monthlyWorkouts = (Integer) monthly.getOrDefault("totalWorkouts", 0);
        
        if (workoutStreak >= 7) {
            insights.put("consistency", "Amazing! You've maintained a " + workoutStreak + " day workout streak. Keep it up!");
        } else if (weeklyWorkouts >= 3) {
            insights.put("consistency", "Great weekly consistency with " + weeklyWorkouts + " workouts this week.");
        } else if (weeklyWorkouts > 0) {
            insights.put("consistency", "Good start! Try to aim for at least 3 workouts per week for better results.");
        } else {
            insights.put("consistency", "Time to get moving! Even a 15-minute workout can make a difference.");
        }
        
        // Exercise variety insight
        int exerciseTypes = exerciseDistribution.size();
        if (exerciseTypes >= 3) {
            insights.put("variety", "Excellent exercise variety! You're working different muscle groups and energy systems.");
        } else if (exerciseTypes == 2) {
            insights.put("variety", "Good variety! Consider adding a third type of exercise for balanced fitness.");
        } else if (exerciseTypes == 1) {
            String dominantType = exerciseDistribution.keySet().iterator().next();
            insights.put("variety", "You're focused on " + dominantType + ". Consider adding some variety for balanced fitness.");
        } else {
            insights.put("variety", "Ready to start your fitness journey? Try mixing cardio, strength, and flexibility exercises.");
        }
        
        // Progress insight based on goal
        String goal = user.getGoal();
        double weeklyCalories = (Double) weekly.getOrDefault("totalCalories", 0.0);
        
        if ("lose".equals(goal)) {
            if (weeklyCalories >= 1500) {
                insights.put("progress", "Great calorie burn this week! You're on track for your weight loss goal.");
            } else if (weeklyCalories >= 800) {
                insights.put("progress", "Good progress! Try increasing workout intensity or duration for better results.");
            } else {
                insights.put("progress", "Let's boost that calorie burn! Aim for at least 300 calories per workout session.");
            }
        } else if ("gain".equals(goal)) {
            if (exerciseDistribution.getOrDefault("strength", 0) >= 2) {
                insights.put("progress", "Excellent focus on strength training! Perfect for muscle building goals.");
            } else {
                insights.put("progress", "For muscle gain, aim for at least 3 strength training sessions per week.");
            }
        } else if ("maintain".equals(goal)) {
            if (weeklyWorkouts >= 3) {
                insights.put("progress", "Perfect balance! You're maintaining great fitness habits.");
            } else {
                insights.put("progress", "Aim for 3-4 workouts per week to maintain your current fitness level.");
            }
        }
        
        // Calorie balance insight (placeholder for when nutrition tracking is added)
        if (weeklyCalories > 0) {
            double avgDailyBurn = weeklyCalories / 7.0;
            insights.put("calories", String.format("🔥 You're burning an average of %.0f calories per day through exercise!", avgDailyBurn));
        }
        
        return insights;
    }
    
    /**
     * Evaluate if user is on track with their fitness goal
     */
    private boolean evaluateGoalProgress(double avgDailyCalorieBurn, double goalCalorieAdjustment, String goal) {
        if (goal == null) return true;
        
        switch (goal.toLowerCase()) {
            case "lose":
                return avgDailyCalorieBurn >= 200; // At least 200 calories burned per day
            case "gain":
                return avgDailyCalorieBurn >= 150; // Moderate exercise for muscle building
            case "maintain":
                return avgDailyCalorieBurn >= 100; // Light exercise for maintenance
            default:
                return true;
        }
    }
}