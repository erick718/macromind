package com.fitness.servlets;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fitness.dao.WorkoutDAO;
import com.fitness.model.User;
import com.fitness.model.Workout;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class WorkoutHistoryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("WorkoutHistoryServlet: Loading workout history for user: " + user.getName());

        try {
            WorkoutDAO workoutDAO = new WorkoutDAO();
            
            // Get filter parameters
            String filterType = request.getParameter("filter");
            if (filterType == null || filterType.isEmpty()) {
                filterType = "all"; // Default to all workouts
            }

            List<Workout> workouts;
            Map<String, Object> progressSummary = null;

            // Filter workouts based on time period
            switch (filterType) {
                case "week":
                    LocalDate weekStart = LocalDate.now().minusDays(7);
                    LocalDate weekEnd = LocalDate.now();
                    workouts = workoutDAO.getWorkoutsByDateRange(
                        user.getUserId(), 
                        Date.valueOf(weekStart), 
                        Date.valueOf(weekEnd)
                    );
                    progressSummary = workoutDAO.getWeeklyProgressSummary(
                        user.getUserId(), 
                        Date.valueOf(weekStart), 
                        Date.valueOf(weekEnd)
                    );
                    break;
                    
                case "month":
                    LocalDate monthStart = LocalDate.now().minusDays(30);
                    LocalDate monthEnd = LocalDate.now();
                    workouts = workoutDAO.getWorkoutsByDateRange(
                        user.getUserId(), 
                        Date.valueOf(monthStart), 
                        Date.valueOf(monthEnd)
                    );
                    progressSummary = workoutDAO.getMonthlyProgressSummary(
                        user.getUserId(), 
                        Date.valueOf(monthStart), 
                        Date.valueOf(monthEnd)
                    );
                    break;
                    
                default: // "all"
                    workouts = workoutDAO.getWorkoutsByUserId(user.getUserId());
                    // Get summary for last 30 days for dashboard display
                    LocalDate lastMonth = LocalDate.now().minusDays(30);
                    progressSummary = workoutDAO.getMonthlyProgressSummary(
                        user.getUserId(), 
                        Date.valueOf(lastMonth), 
                        Date.valueOf(LocalDate.now())
                    );
                    break;
            }

            // Get additional analytics
            Map<String, Integer> exerciseDistribution = workoutDAO.getExerciseTypeDistribution(
                user.getUserId(), 
                Date.valueOf(LocalDate.now().minusDays(30)), 
                Date.valueOf(LocalDate.now())
            );
            
            int workoutStreak = workoutDAO.getWorkoutStreak(user.getUserId());

            // Set request attributes for JSP
            request.setAttribute("workouts", workouts);
            request.setAttribute("progressSummary", progressSummary);
            request.setAttribute("exerciseDistribution", exerciseDistribution);
            request.setAttribute("workoutStreak", workoutStreak);
            request.setAttribute("currentFilter", filterType);

            System.out.println("WorkoutHistoryServlet: Found " + workouts.size() + " workouts");
            if (progressSummary != null) {
                System.out.println("WorkoutHistoryServlet: Progress summary - Total workouts: " + 
                                 progressSummary.get("totalWorkouts"));
            }

            // Forward to the JSP
            request.getRequestDispatcher("/workout-history.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error loading workout history: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error loading workout history: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Handle workout deletion or editing
        String action = request.getParameter("action");
        String workoutIdStr = request.getParameter("workoutId");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (action != null && workoutIdStr != null) {
            try {
                int workoutId = Integer.parseInt(workoutIdStr);
                WorkoutDAO workoutDAO = new WorkoutDAO();
                
                if ("delete".equals(action)) {
                    // Verify workout belongs to user (security check)
                    Workout workout = workoutDAO.getWorkoutById(workoutId);
                    if (workout != null && workout.getUserId() == user.getUserId()) {
                        workoutDAO.deleteWorkout(workoutId);
                        
                        // Update daily summary after deletion
                        workoutDAO.updateDailyFitnessSummary(user.getUserId(), workout.getWorkoutDate());
                        
                        session.setAttribute("message", "Workout deleted successfully.");
                    } else {
                        session.setAttribute("error", "Workout not found or access denied.");
                    }
                }
                
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid workout ID.");
            } catch (Exception e) {
                System.err.println("Error processing workout action: " + e.getMessage());
                e.printStackTrace();
                session.setAttribute("error", "Error processing request: " + e.getMessage());
            }
        }
        
        // Redirect back to history page
        response.sendRedirect(request.getContextPath() + "/workout-history");
    }
}