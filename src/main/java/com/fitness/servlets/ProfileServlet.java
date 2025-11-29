package com.fitness.servlets;

import java.io.IOException;

import com.fitness.dao.UserDAO;
import com.fitness.Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("ProfileServlet: doPost method called");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("ProfileServlet: No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("ProfileServlet: Processing profile for user: " + user.getName());

        try {
            // Get profile data from form and update the user object
            String ageStr = request.getParameter("age");
            String heightStr = request.getParameter("height");
            String weightStr = request.getParameter("weight");
            String goalStr = request.getParameter("goal");
            String activity = request.getParameter("activity");

            System.out.println("ProfileServlet: Received form data:");
            System.out.println("- Age: " + ageStr);
            System.out.println("- Height: " + heightStr);
            System.out.println("- Weight: " + weightStr);
            System.out.println("- Goal: " + goalStr);
            System.out.println("- Activity: " + activity);

            // Parse and set the values
            if (ageStr != null && !ageStr.trim().isEmpty()) {
                user.setAge(Integer.parseInt(ageStr));
            }
            if (heightStr != null && !heightStr.trim().isEmpty()) {
                user.setHeight(Integer.parseInt(heightStr));
            }
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                user.setWeight(Float.parseFloat(weightStr));
            }
            if (goalStr != null && !goalStr.trim().isEmpty()) {
                user.setGoal(goalStr);
            }
            if (activity != null && !activity.trim().isEmpty()) {
                user.setFitnessLevel(activity);
            }

            // Save to database
            System.out.println("ProfileServlet: Saving profile to database for user ID: " + user.getUserId());
            UserDAO dao = new UserDAO();
            dao.updateUserProfile(user);
            System.out.println("ProfileServlet: Profile saved successfully");

            // Update the user in session with the new profile data
            session.setAttribute("user", user);

            // Store success message
            session.setAttribute("message", "Profile updated successfully!");

        } catch (NumberFormatException e) {
            // Handle invalid number inputs
            session.setAttribute("error", "Please enter valid numbers for age, height, weight, and goal.");
        } catch (Exception e) {
            // Handle other errors
            session.setAttribute("error", "Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }

        // Redirect to dashboard through the servlet (not directly to JSP)
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}