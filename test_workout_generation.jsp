<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.service.WorkoutPlanService" %>
<%@ page import="com.fitness.Model.WorkoutPlan" %>
<%@ page import="com.fitness.Model.Exercise" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <title>Test Workout Plan Generation</title>
</head>
<body>
<h1>Test Workout Plan Generation</h1>

<%
try {
    // Create a WorkoutPlanService instance
    WorkoutPlanService service = new WorkoutPlanService();
    
    out.println("<h3>Testing workout plan generation...</h3>");
    
    // Generate a test workout plan
    WorkoutPlan plan = service.generateWorkoutPlan(1, "lose", "moderate", 30, 70);
    
    if (plan != null) {
        out.println("<p style='color: green;'>✅ Workout plan generated successfully!</p>");
        out.println("<p><strong>Plan Name:</strong> " + plan.getPlanName() + "</p>");
        out.println("<p><strong>Goal:</strong> " + plan.getGoal() + "</p>");
        out.println("<p><strong>Difficulty:</strong> " + plan.getDifficulty() + "</p>");
        out.println("<p><strong>Duration:</strong> " + plan.getDurationWeeks() + " weeks</p>");
        out.println("<p><strong>Sessions per week:</strong> " + plan.getSessionsPerWeek() + "</p>");
        out.println("<p><strong>Total calories:</strong> " + plan.getTotalCaloriesBurned() + "</p>");
        out.println("<p><strong>Number of exercises:</strong> " + plan.getExercises().size() + "</p>");
        
        out.println("<h4>Exercises:</h4>");
        out.println("<ul>");
        for (Exercise exercise : plan.getExercises()) {
            out.println("<li>" + exercise.getName() + " (ID: " + exercise.getExerciseId() + ") - " + 
                       exercise.getMuscleGroup() + " - " + exercise.getCaloriesBurned() + " calories</li>");
        }
        out.println("</ul>");
        
        // Try to save the plan
        out.println("<h3>Saving the plan...</h3>");
        service.saveWorkoutPlan(plan);
        out.println("<p style='color: green;'>✅ Plan saved with ID: " + plan.getPlanId() + "</p>");
        
        // Try to retrieve plans for this user
        out.println("<h3>Retrieving plans for user 1...</h3>");
        List<WorkoutPlan> userPlans = service.getUserWorkoutPlans(1);
        if (userPlans != null) {
            out.println("<p><strong>Number of plans found:</strong> " + userPlans.size() + "</p>");
            for (WorkoutPlan userPlan : userPlans) {
                out.println("<p>Plan: " + userPlan.getPlanName() + " (ID: " + userPlan.getPlanId() + 
                           ") - " + userPlan.getExercises().size() + " exercises</p>");
            }
        } else {
            out.println("<p style='color: red;'>❌ No plans found!</p>");
        }
        
    } else {
        out.println("<p style='color: red;'>❌ Failed to generate workout plan!</p>");
    }
    
} catch (Exception e) {
    out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
    e.printStackTrace();
}
%>

</body>
</html>