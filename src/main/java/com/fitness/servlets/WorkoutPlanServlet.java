package com.fitness.servlets;

import java.io.IOException;
import java.util.List;

import com.fitness.Model.User;
import com.fitness.Model.WorkoutPlan;
import com.fitness.service.WorkoutPlanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class WorkoutPlanServlet extends HttpServlet {
    
    private WorkoutPlanService workoutPlanService;
    
    @Override
    public void init() throws ServletException {
        workoutPlanService = new WorkoutPlanService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("generate".equals(action)) {
            // Show the workout plan generator form
            request.getRequestDispatcher("workout-generator.jsp").forward(request, response);
        } else if ("view".equals(action)) {
            // View a specific workout plan
            String planIdStr = request.getParameter("planId");
            if (planIdStr != null) {
                try {
                    int planId = Integer.parseInt(planIdStr);
                    WorkoutPlan plan = workoutPlanService.getWorkoutPlanById(planId);
                    if (plan != null && plan.getUserId() == user.getUserId()) {
                        request.setAttribute("workoutPlan", plan);
                        request.getRequestDispatcher("workout-plan-view.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("WorkoutPlanServlet?action=list");
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect("WorkoutPlanServlet?action=list");
                }
            } else {
                response.sendRedirect("WorkoutPlanServlet?action=list");
            }
        } else if ("delete".equals(action)) {
            // Delete a workout plan
            String planIdStr = request.getParameter("planId");
            if (planIdStr != null) {
                try {
                    int planId = Integer.parseInt(planIdStr);
                    WorkoutPlan plan = workoutPlanService.getWorkoutPlanById(planId);
                    if (plan != null && plan.getUserId() == user.getUserId()) {
                        workoutPlanService.deleteWorkoutPlan(planId);
                    }
                } catch (NumberFormatException e) {
                    // Invalid plan ID, ignore
                }
            }
            response.sendRedirect("WorkoutPlanServlet?action=list");
        } else {
            // Default action: list all workout plans
            List<WorkoutPlan> plans = workoutPlanService.getUserWorkoutPlans(user.getUserId());
            System.out.println("DEBUG: Retrieved " + (plans != null ? plans.size() : "null") + " plans for user " + user.getUserId());
            if (plans != null && !plans.isEmpty()) {
                System.out.println("DEBUG: First plan name: " + plans.get(0).getPlanName());
            }
            request.setAttribute("workoutPlans", plans);
            request.getRequestDispatcher("workout-plans-list.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("generate".equals(action)) {
            // Generate new workout plan
            try {
                String goal = request.getParameter("goal");
                String activityLevel = (String) session.getAttribute("activity");
                String ageStr = (String) session.getAttribute("age");
                String weightStr = (String) session.getAttribute("weight");
                
                // Validate inputs
                if (goal == null || activityLevel == null || ageStr == null || weightStr == null) {
                    request.setAttribute("error", "Please complete your profile first.");
                    request.getRequestDispatcher("profile.jsp").forward(request, response);
                    return;
                }
                
                int age = Integer.parseInt(ageStr);
                int weight = Integer.parseInt(weightStr);
                
                // Generate workout plan
                WorkoutPlan plan = workoutPlanService.generateWorkoutPlan(
                    user.getUserId(), goal, activityLevel, age, weight
                );
                
                // Save the plan
                workoutPlanService.saveWorkoutPlan(plan);
                
                // Redirect to view the generated plan
                response.sendRedirect("WorkoutPlanServlet?action=view&planId=" + plan.getPlanId());
                
            } catch (Exception e) {
                request.setAttribute("error", "Error generating workout plan: " + e.getMessage());
                request.getRequestDispatcher("workout-generator.jsp").forward(request, response);
            }
        } else {
            // Default to GET handling
            doGet(request, response);
        }
    }
}