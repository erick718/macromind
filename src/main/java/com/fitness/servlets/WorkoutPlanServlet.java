package com.fitness.servlets;

import java.io.IOException;
import java.util.List;

import com.fitness.Model.User;
import com.fitness.Model.WorkoutPlan;
import com.fitness.dao.UserDAO;
import com.fitness.service.WorkoutPlanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class WorkoutPlanServlet extends HttpServlet {
    
    private WorkoutPlanService workoutPlanService;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        workoutPlanService = new WorkoutPlanService();
        userDAO = new UserDAO();
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
            // Reload user from database to get latest profile data
            User freshUser = userDAO.getUserByEmail(user.getEmail());
            if (freshUser != null) {
                session.setAttribute("user", freshUser);
            }
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
                
                // Reload user from database to get latest profile data
                User freshUser = userDAO.getUserByEmail(user.getEmail());
                if (freshUser == null) {
                    request.setAttribute("error", "Unable to load user profile.");
                    request.getRequestDispatcher("workout-generator.jsp").forward(request, response);
                    return;
                }
                
                // Validate that user has completed their profile
                if (freshUser.getAge() <= 0 || freshUser.getWeight() <= 0 || 
                    freshUser.getFitnessLevel() == null || freshUser.getFitnessLevel().isEmpty()) {
                    request.setAttribute("error", "Please complete your profile first.");
                    request.getRequestDispatcher("profile.jsp").forward(request, response);
                    return;
                }
                
                // Validate goal parameter
                if (goal == null || goal.isEmpty()) {
                    request.setAttribute("error", "Please select a fitness goal.");
                    request.getRequestDispatcher("workout-generator.jsp").forward(request, response);
                    return;
                }
                
                // Generate workout plan using data from database
                WorkoutPlan plan = workoutPlanService.generateWorkoutPlan(
                    freshUser.getUserId(), 
                    goal, 
                    freshUser.getFitnessLevel(), 
                    freshUser.getAge(), 
                    (int) freshUser.getWeight()
                );
                
                // Save the plan
                workoutPlanService.saveWorkoutPlan(plan);
                
                // Update session with fresh user data
                session.setAttribute("user", freshUser);
                
                // Redirect to view the generated plan
                response.sendRedirect("WorkoutPlanServlet?action=view&planId=" + plan.getPlanId());
                
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Error generating workout plan: " + e.getMessage());
                request.getRequestDispatcher("workout-generator.jsp").forward(request, response);
            }
        } else {
            // Default to GET handling
            doGet(request, response);
        }
    }
}