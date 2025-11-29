<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.fitness.util.DBConnection" %>
<%@ page import="com.fitness.dao.WorkoutPlanDAO" %>
<%@ page import="com.fitness.model.WorkoutPlan" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <title>Database Connection Test</title>
</head>
<body>
<h1>Database Connection Test</h1>

<%
try {
    Connection conn = DBConnection.getConnection();
    out.println("<p style='color: green;'>✅ Database connection successful!</p>");
    
    // Test the workout plans table
    String query = "SELECT COUNT(*) as count FROM workout_plans";
    PreparedStatement ps = conn.prepareStatement(query);
    ResultSet rs = ps.executeQuery();
    
    if (rs.next()) {
        int count = rs.getInt("count");
        out.println("<p>Total workout plans in database: " + count + "</p>");
    }
    
    // Test if there are any workout plans for user ID 1 (or any existing user)
    query = "SELECT * FROM workout_plans LIMIT 5";
    ps = conn.prepareStatement(query);
    rs = ps.executeQuery();
    
    out.println("<h3>Sample workout plans in database:</h3>");
    out.println("<ul>");
    while (rs.next()) {
        out.println("<li>Plan ID: " + rs.getInt("plan_id") + 
                   ", User ID: " + rs.getInt("user_id") + 
                   ", Name: " + rs.getString("plan_name") + 
                   ", Goal: " + rs.getString("goal") + "</li>");
    }
    out.println("</ul>");
    
    // Test WorkoutPlanDAO directly
    WorkoutPlanDAO dao = new WorkoutPlanDAO();
    out.println("<h3>Testing WorkoutPlanDAO:</h3>");
    
    // Try to get plans for user 1
    List<WorkoutPlan> plans = dao.getWorkoutPlansByUserId(1);
    out.println("<p>Plans for user 1: " + (plans != null ? plans.size() : "null") + "</p>");
    
    conn.close();
    
} catch (Exception e) {
    out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
    e.printStackTrace();
}
%>

</body>
</html>