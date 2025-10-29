package com.fitness.servlets;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

import com.fitness.dao.WorkoutDAO;
import com.fitness.model.User;
import com.fitness.model.Workout;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class WorkoutLogServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Forward to the workout logging form
        request.getRequestDispatcher("/workout-log.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("WorkoutLogServlet: doPost method called");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("WorkoutLogServlet: No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("WorkoutLogServlet: Processing workout for user: " + user.getName());

        try {
            // Get workout data from form
            String exerciseName = request.getParameter("exerciseName");
            String exerciseType = request.getParameter("exerciseType");
            String setsStr = request.getParameter("sets");
            String repsStr = request.getParameter("reps");
            String weightStr = request.getParameter("weight");
            String durationStr = request.getParameter("duration");
            String workoutDateStr = request.getParameter("workoutDate");
            String workoutTimeStr = request.getParameter("workoutTime");
            String notes = request.getParameter("notes");

            System.out.println("WorkoutLogServlet: Received form data:");
            System.out.println("- Exercise Name: " + exerciseName);
            System.out.println("- Exercise Type: " + exerciseType);
            System.out.println("- Sets: " + setsStr);
            System.out.println("- Reps: " + repsStr);
            System.out.println("- Weight: " + weightStr);
            System.out.println("- Duration: " + durationStr);
            System.out.println("- Date: " + workoutDateStr);
            System.out.println("- Time: " + workoutTimeStr);

            // Validate required fields
            if (isEmpty(exerciseName) || isEmpty(exerciseType) || isEmpty(durationStr) || isEmpty(workoutDateStr)) {
                session.setAttribute("error", "Please fill in all required fields (Exercise Name, Type, Duration, and Date).");
                response.sendRedirect(request.getContextPath() + "/workout-log");
                return;
            }

            // Create new workout object
            Workout workout = new Workout();
            workout.setUserId(user.getUserId());
            workout.setExerciseName(exerciseName.trim());
            workout.setExerciseType(exerciseType.trim());
            
            // Parse numeric fields (with defaults for optional fields)
            workout.setSetsCount(parseIntOrDefault(setsStr, 0));
            workout.setRepsPerSet(parseIntOrDefault(repsStr, 0));
            workout.setWeightKg(parseDoubleOrDefault(weightStr, 0.0));
            workout.setDurationMinutes(parseIntOrDefault(durationStr, 0));
            
            // Parse date and time
            Date workoutDate = Date.valueOf(workoutDateStr);
            workout.setWorkoutDate(workoutDate);
            
            if (!isEmpty(workoutTimeStr)) {
                Time workoutTime = Time.valueOf(workoutTimeStr + ":00"); // Add seconds if not provided
                workout.setWorkoutTime(workoutTime);
            } else {
                // Set current time if not provided
                workout.setWorkoutTime(Time.valueOf(LocalTime.now()));
            }
            
            if (!isEmpty(notes)) {
                workout.setNotes(notes.trim());
            }

            // Calculate calories burned
            WorkoutDAO workoutDAO = new WorkoutDAO();
            double caloriesBurned = workoutDAO.calculateCaloriesBurned(
                exerciseName, 
                workout.getDurationMinutes(), 
                user.getWeight()
            );
            workout.setCaloriesBurned(caloriesBurned);

            System.out.println("WorkoutLogServlet: Calculated calories burned: " + caloriesBurned);

            // Save to database
            System.out.println("WorkoutLogServlet: Saving workout to database for user ID: " + user.getUserId());
            workoutDAO.createWorkout(workout);
            
            // Update daily fitness summary
            workoutDAO.updateDailyFitnessSummary(user.getUserId(), workoutDate);
            
            System.out.println("WorkoutLogServlet: Workout saved successfully");

            // Store success message
            session.setAttribute("message", "Workout logged successfully! You burned approximately " + 
                                Math.round(caloriesBurned) + " calories.");

            // Redirect to workout history or dashboard
            response.sendRedirect(request.getContextPath() + "/workout-history");

        } catch (NumberFormatException e) {
            session.setAttribute("error", "Please enter valid numbers for sets, reps, weight, and duration.");
            response.sendRedirect(request.getContextPath() + "/workout-log");
        } catch (Exception e) {
            session.setAttribute("error", "Error logging workout: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/workout-log");
        }
    }

    // Helper methods
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private int parseIntOrDefault(String str, int defaultValue) {
        if (isEmpty(str)) return defaultValue;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDoubleOrDefault(String str, double defaultValue) {
        if (isEmpty(str)) return defaultValue;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}