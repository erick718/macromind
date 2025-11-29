package com.fitness.servlets;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

import com.fitness.dao.WorkoutDAO;
import com.fitness.Model.User;
import com.fitness.Model.Workout;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/workout-edit")
public class WorkoutEditServlet extends HttpServlet {
    
    private WorkoutDAO workoutDAO;
    
    // Default constructor for container initialization
    public WorkoutEditServlet() {
        this(new WorkoutDAO());
    }
    
    // Constructor for dependency injection (testing)
    public WorkoutEditServlet(WorkoutDAO workoutDAO) {
        this.workoutDAO = workoutDAO;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("WorkoutEditServlet: doGet method called");
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            System.out.println("WorkoutEditServlet: No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get workout ID from parameter
        String workoutIdStr = request.getParameter("id");
        
        if (workoutIdStr == null || workoutIdStr.trim().isEmpty()) {
            System.out.println("WorkoutEditServlet: No workout ID provided");
            session.setAttribute("error", "Workout ID is required.");
            response.sendRedirect(request.getContextPath() + "/workout-history");
            return;
        }
        
        try {
            int workoutId = Integer.parseInt(workoutIdStr);
            System.out.println("WorkoutEditServlet: Loading workout with ID: " + workoutId);
            
            // Get workout from database
            Workout workout = workoutDAO.getWorkoutById(workoutId);
            
            if (workout == null) {
                System.out.println("WorkoutEditServlet: Workout not found");
                session.setAttribute("error", "Workout not found.");
                response.sendRedirect(request.getContextPath() + "/workout-history");
                return;
            }
            
            // Security check - ensure user owns this workout
            if (workout.getUserId() != user.getUserId()) {
                System.out.println("WorkoutEditServlet: User " + user.getUserId() + 
                                 " attempting to edit workout owned by " + workout.getUserId());
                session.setAttribute("error", "You can only edit your own workouts.");
                response.sendRedirect(request.getContextPath() + "/workout-history");
                return;
            }
            
            System.out.println("WorkoutEditServlet: Workout loaded successfully - " + workout.getExerciseName());
            
            // Set workout as request attribute and forward to edit page
            request.setAttribute("workout", workout);
            request.getRequestDispatcher("/workout-edit.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.err.println("WorkoutEditServlet: Invalid workout ID format: " + workoutIdStr);
            session.setAttribute("error", "Invalid workout ID.");
            response.sendRedirect(request.getContextPath() + "/workout-history");
        } catch (Exception e) {
            System.err.println("WorkoutEditServlet: Error loading workout: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error loading workout. Please try again.");
            response.sendRedirect(request.getContextPath() + "/workout-history");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("WorkoutEditServlet: doPost method called");
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            System.out.println("WorkoutEditServlet: No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // Get workout ID
            String workoutIdStr = request.getParameter("workoutId");
            if (workoutIdStr == null || workoutIdStr.trim().isEmpty()) {
                session.setAttribute("error", "Workout ID is required.");
                response.sendRedirect(request.getContextPath() + "/workout-history");
                return;
            }
            
            int workoutId = Integer.parseInt(workoutIdStr);
            
            // Verify workout exists and user owns it
            Workout existingWorkout = workoutDAO.getWorkoutById(workoutId);
            if (existingWorkout == null) {
                session.setAttribute("error", "Workout not found.");
                response.sendRedirect(request.getContextPath() + "/workout-history");
                return;
            }
            
            if (existingWorkout.getUserId() != user.getUserId()) {
                session.setAttribute("error", "You can only edit your own workouts.");
                response.sendRedirect(request.getContextPath() + "/workout-history");
                return;
            }
            
            // Get form parameters
            String exerciseName = request.getParameter("exerciseName");
            String exerciseType = request.getParameter("exerciseType");
            String durationStr = request.getParameter("duration");
            String workoutDateStr = request.getParameter("workoutDate");
            String workoutTimeStr = request.getParameter("workoutTime");
            String notes = request.getParameter("notes");
            
            // Validate required fields
            if (exerciseName == null || exerciseName.trim().isEmpty() ||
                exerciseType == null || exerciseType.trim().isEmpty() ||
                durationStr == null || workoutDateStr == null) {
                
                request.setAttribute("error", "Please fill in all required fields.");
                request.setAttribute("workout", existingWorkout);
                request.getRequestDispatcher("/workout-edit.jsp").forward(request, response);
                return;
            }
            
            System.out.println("WorkoutEditServlet: Updating workout:");
            System.out.println("- Exercise Name: " + exerciseName);
            System.out.println("- Exercise Type: " + exerciseType);
            System.out.println("- Duration: " + durationStr);
            System.out.println("- Date: " + workoutDateStr);
            System.out.println("- Time: " + workoutTimeStr);
            
            // Parse numeric values with defaults
            int duration = parseIntOrDefault(durationStr, 0);
            int sets = parseIntOrDefault(request.getParameter("sets"), 0);
            int reps = parseIntOrDefault(request.getParameter("reps"), 0);
            double weight = parseDoubleOrDefault(request.getParameter("weight"), 0.0);
            
            // Parse date and time
            Date workoutDate = Date.valueOf(workoutDateStr);
            Time workoutTime = null;
            if (workoutTimeStr != null && !workoutTimeStr.trim().isEmpty()) {
                workoutTime = Time.valueOf(LocalTime.parse(workoutTimeStr));
            }
            
            // Calculate calories burned
            double caloriesBurned = workoutDAO.calculateCaloriesBurned(
                exerciseName, 
                duration, 
                user.getWeight()
            );
            
            System.out.println("WorkoutEditServlet: Calculated calories burned: " + caloriesBurned);
            
            // Update workout object
            existingWorkout.setExerciseName(exerciseName);
            existingWorkout.setExerciseType(exerciseType);
            existingWorkout.setDurationMinutes(duration);
            existingWorkout.setSetsCount(sets);
            existingWorkout.setRepsPerSet(reps);
            existingWorkout.setWeightKg(weight);
            existingWorkout.setWorkoutDate(workoutDate);
            existingWorkout.setWorkoutTime(workoutTime);
            existingWorkout.setCaloriesBurned(caloriesBurned);
            existingWorkout.setNotes(notes);
            
            // Save to database
            System.out.println("WorkoutEditServlet: Saving workout to database");
            workoutDAO.updateWorkout(existingWorkout);
            
            // Update daily fitness summary
            workoutDAO.updateDailyFitnessSummary(user.getUserId(), workoutDate);
            
            System.out.println("WorkoutEditServlet: Workout updated successfully");
            
            session.setAttribute("message", 
                "Workout updated successfully! " + 
                (caloriesBurned > 0 ? "You burned approximately " + 
                Math.round(caloriesBurned) + " calories." : ""));
            
            response.sendRedirect(request.getContextPath() + "/workout-history");
            
        } catch (IllegalArgumentException e) {
            System.err.println("WorkoutEditServlet: Invalid date/time format: " + e.getMessage());
            session.setAttribute("error", "Invalid date or time format. Please check your input.");
            response.sendRedirect(request.getContextPath() + "/workout-history");
            
        } catch (Exception e) {
            System.err.println("WorkoutEditServlet: Error updating workout: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error updating workout. Please try again.");
            response.sendRedirect(request.getContextPath() + "/workout-history");
        }
    }
    
    // Helper method to parse integer with default value
    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    // Helper method to parse double with default value
    private double parseDoubleOrDefault(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
