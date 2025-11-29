<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.fitness.util.DBConnection" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Database Users - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
    <style>
        .user-table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
            background: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .user-table th {
            background: #2196F3;
            color: white;
            padding: 12px;
            text-align: left;
            font-weight: 600;
        }
        .user-table td {
            padding: 10px 12px;
            border-bottom: 1px solid #e0e0e0;
        }
        .user-table tr:hover {
            background: #f5f5f5;
        }
        .info-box {
            background: #e3f2fd;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin: 15px 0;
        }
        .warning-box {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin: 15px 0;
        }
        .error-box {
            background: #f8d7da;
            border-left: 4px solid #dc3545;
            padding: 15px;
            margin: 15px 0;
        }
        code {
            background: #f4f4f4;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="page-header">
            <h1 class="page-title">👥 Database Users</h1>
            <p class="page-subtitle">View all registered accounts</p>
        </div>

        <%
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userCount = 0;
        boolean hasError = false;
        String errorMessage = "";
        
        try {
            conn = DBConnection.getConnection();
            String query = "SELECT user_id, username, email, password, age, weight, height, goal, created_date FROM users ORDER BY user_id DESC";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        %>
            
            <div class="info-box">
                <strong>🔍 What to look for:</strong>
                <ul style="margin: 10px 0 0 20px;">
                    <li>Find your email address in the list below</li>
                    <li>Check if the email spelling matches exactly what you're using to login</li>
                    <li>Note your password (passwords are currently stored as plain text)</li>
                </ul>
            </div>

            <div class="card">
                <div class="card-body">
                    <h3>Registered Users</h3>
                    
                    <%
                    // Count users first
                    while (rs.next()) {
                        userCount++;
                    }
                    
                    if (userCount == 0) {
                    %>
                        <div class="warning-box">
                            <strong>⚠️ No Users Found!</strong>
                            <p>The database is empty. You need to register an account first.</p>
                            <p><a href="register.jsp" class="btn btn-primary">Register New Account</a></p>
                        </div>
                    <%
                    } else {
                        // Re-query to display
                        rs.close();
                        ps.close();
                        ps = conn.prepareStatement(query);
                        rs = ps.executeQuery();
                    %>
                        <p><strong>Total Users:</strong> <%= userCount %></p>
                        
                        <table class="user-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Username</th>
                                    <th>Email</th>
                                    <th>Password</th>
                                    <th>Profile Set</th>
                                    <th>Created</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                while (rs.next()) {
                                    int userId = rs.getInt("user_id");
                                    String username = rs.getString("username");
                                    String email = rs.getString("email");
                                    String password = rs.getString("password");
                                    Integer age = rs.getObject("age") != null ? rs.getInt("age") : null;
                                    Timestamp created = rs.getTimestamp("created_date");
                                    
                                    boolean hasProfile = age != null && age > 0;
                                %>
                                <tr>
                                    <td><strong>#<%= userId %></strong></td>
                                    <td><%= username != null ? username : "<em>null</em>" %></td>
                                    <td><code><%= email != null ? email : "<em>null</em>" %></code></td>
                                    <td><code><%= password != null ? password : "<span style='color:red;'>NULL</span>" %></code></td>
                                    <td><%= hasProfile ? "✅ Yes" : "❌ No" %></td>
                                    <td><%= created != null ? created.toString() : "N/A" %></td>
                                </tr>
                                <%
                                }
                                %>
                            </tbody>
                        </table>
                        
                        <div class="info-box" style="margin-top: 20px;">
                            <strong>💡 Next Steps:</strong>
                            <ol style="margin: 10px 0 0 20px;">
                                <li>Find your email in the table above</li>
                                <li>Copy the exact email and password (including any spaces or special characters)</li>
                                <li>Try logging in with those exact credentials</li>
                                <li>Use the <a href="debug-login.jsp">Debug Login Page</a> for detailed testing</li>
                            </ol>
                        </div>
                    <%
                    }
                    %>
                </div>
            </div>
            
        <%
        } catch (SQLException e) {
            hasError = true;
            errorMessage = e.getMessage();
            e.printStackTrace();
        %>
            <div class="error-box">
                <strong>❌ Database Error</strong>
                <p>Could not connect to or query the database.</p>
                <p><strong>Error:</strong> <code><%= errorMessage %></code></p>
                <br>
                <strong>Possible Solutions:</strong>
                <ul style="margin: 10px 0 0 20px;">
                    <li>Check that MySQL is running</li>
                    <li>Verify database credentials in <code>DBConnection.java</code></li>
                    <li>Make sure the <code>macromind</code> database exists</li>
                    <li>Run the database setup script: <code>macromind_complete_database_setup.sql</code></li>
                </ul>
            </div>
        <%
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        %>
        
        <div class="text-center mt-3">
            <a href="debug-login.jsp" class="btn btn-primary">🔍 Test Login</a>
            <a href="register.jsp" class="btn btn-secondary">📝 Register New Account</a>
            <a href="login.jsp" class="btn btn-secondary">🔐 Go to Login</a>
        </div>
    </div>
</body>
</html>
