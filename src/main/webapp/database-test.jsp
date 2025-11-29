<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.fitness.util.DBConnection" %>
<%@ page import="com.fitness.Model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Database Test - MacroMind</title>
    <style>
        .container { max-width: 800px; margin: 50px auto; padding: 20px; }
        .test-section { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
        .info { background-color: #d1ecf1; color: #0c5460; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Database Connection Test</h1>
        <p><a href="dashboard.jsp">← Back to Dashboard</a></p>
        
        <div class="test-section info">
            <h3>Current User Information:</h3>
            <p><strong>User ID:</strong> <%= user.getUserId() %></p>
            <p><strong>Username:</strong> <%= user.getName() %></p>
            <p><strong>Email:</strong> <%= user.getEmail() %></p>
        </div>
        
        <%
        Connection conn = null;
        try {
            // Test database connection
            conn = DBConnection.getConnection();
        %>
            <div class="test-section success">
                <h3>✅ Database Connection: SUCCESS</h3>
                <p>Successfully connected to the database.</p>
            </div>
        <%
            // Check if tables exist
            String[] tables = {"users", "exercises", "workout_plans", "workout_plan_exercises"};
            for (String tableName : tables) {
                try {
                    PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
        %>
                    <div class="test-section success">
                        <h4>✅ Table '<%= tableName %>':</h4>
                        <p>Exists with <%= count %> records</p>
                    </div>
        <%
                    rs.close();
                    ps.close();
                } catch (SQLException e) {
        %>
                    <div class="test-section error">
                        <h4>❌ Table '<%= tableName %>':</h4>
                        <p>Error: <%= e.getMessage() %></p>
                    </div>
        <%
                }
            }
            
            // Check workout plans for current user
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM workout_plans WHERE user_id = ?");
                ps.setInt(1, user.getUserId());
                ResultSet rs = ps.executeQuery();
                
                int planCount = 0;
        %>
                <div class="test-section info">
                    <h3>Workout Plans for Current User:</h3>
        <%
                while (rs.next()) {
                    planCount++;
        %>
                    <p><strong>Plan <%= planCount %>:</strong> 
                       ID: <%= rs.getInt("plan_id") %>, 
                       Name: <%= rs.getString("plan_name") %>, 
                       Goal: <%= rs.getString("goal") %>
                    </p>
        <%
                }
                if (planCount == 0) {
        %>
                    <p><strong>No workout plans found for user ID <%= user.getUserId() %></strong></p>
        <%
                } else {
        %>
                    <p><strong>Total Plans Found: <%= planCount %></strong></p>
        <%
                }
        %>
                </div>
        <%
                rs.close();
                ps.close();
            } catch (SQLException e) {
        %>
                <div class="test-section error">
                    <h4>❌ Error checking workout plans:</h4>
                    <p><%= e.getMessage() %></p>
                </div>
        <%
            }
            
            // Check exercises
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as count FROM exercises");
                ResultSet rs = ps.executeQuery();
                rs.next();
                int exerciseCount = rs.getInt("count");
        %>
                <div class="test-section <%= exerciseCount > 0 ? "success" : "error" %>">
                    <h3><%= exerciseCount > 0 ? "✅" : "❌" %> Exercise Database:</h3>
                    <p><%= exerciseCount %> exercises available</p>
                    <% if (exerciseCount == 0) { %>
                        <p><strong>Note:</strong> You need to run the database setup script to populate exercises!</p>
                    <% } %>
                </div>
        <%
                rs.close();
                ps.close();
            } catch (SQLException e) {
        %>
                <div class="test-section error">
                    <h4>❌ Error checking exercises:</h4>
                    <p><%= e.getMessage() %></p>
                </div>
        <%
            }
            
        } catch (SQLException e) {
        %>
            <div class="test-section error">
                <h3>❌ Database Connection: FAILED</h3>
                <p>Error: <%= e.getMessage() %></p>
                <p><strong>Possible causes:</strong></p>
                <ul>
                    <li>MySQL server is not running</li>
                    <li>Database 'macromind' doesn't exist</li>
                    <li>Wrong username/password in DBConnection.java</li>
                    <li>MySQL JDBC driver not found</li>
                </ul>
            </div>
        <%
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Ignore close errors
                }
            }
        }
        %>
        
        <div class="test-section info">
            <h3>Next Steps:</h3>
            <ul>
                <li>If database connection failed: Check MySQL server and credentials</li>
                <li>If tables don't exist: Run the database setup script in MySQL Workbench</li>
                <li>If no exercises exist: Run the database setup script to populate sample data</li>
                <li>If no workout plans exist: That's normal - generate your first plan!</li>
            </ul>
        </div>
        
        <p><a href="WorkoutPlanServlet?action=generate">Generate a Workout Plan</a> | 
           <a href="WorkoutPlanServlet">View My Workout Plans</a></p>
    </div>
</body>
</html>