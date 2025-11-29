<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.dao.UserDAO" %>
<%@ page import="com.fitness.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Debugger - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
    <style>
        .debug-info {
            background: #f4f4f4;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin: 15px 0;
            font-family: monospace;
            white-space: pre-wrap;
        }
        .success { border-left-color: #4CAF50; background: #f1f8f4; }
        .error { border-left-color: #f44336; background: #fef1f1; }
        .warning { border-left-color: #ff9800; background: #fff8f1; }
    </style>
</head>
<body>
    <div class="container">
        <div class="page-header">
            <h1 class="page-title">🔍 Login Debugger</h1>
            <p class="page-subtitle">Test your account credentials</p>
        </div>

        <div class="card">
            <div class="card-body">
                <form method="post">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" required 
                               value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>"
                               placeholder="Enter your email address">
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="text" id="password" name="password" required 
                               value="<%= request.getParameter("password") != null ? request.getParameter("password") : "" %>"
                               placeholder="Enter your password">
                        <small style="color: #666;">Note: Password is visible for debugging purposes</small>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block">
                            🔍 Test Login
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <% 
        if (request.getMethod().equals("POST")) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            
            if (email != null) email = email.trim();
            if (password != null) password = password.trim();
        %>
            <div class="card">
                <div class="card-body">
                    <h3>Debug Results</h3>
                    
                    <div class="debug-info">
                        <strong>📧 Email Entered:</strong> "<%= email %>"
                        <strong>Length:</strong> <%= email != null ? email.length() : 0 %> characters
                    </div>
                    
                    <div class="debug-info">
                        <strong>🔐 Password Entered:</strong> "<%= password %>"
                        <strong>Length:</strong> <%= password != null ? password.length() : 0 %> characters
                    </div>
                    
                    <%
                    UserDAO dao = new UserDAO();
                    User user = dao.getUserByEmail(email);
                    
                    if (user == null) {
                    %>
                        <div class="debug-info error">
                            <strong>❌ User Not Found</strong>
                            No account exists with email: <%= email %>
                            
                            <strong>Suggestion:</strong>
                            - Check if the email is spelled correctly
                            - Try registering a new account
                            - Check the database directly
                        </div>
                    <%
                    } else {
                        String storedPassword = user.getPassword();
                        boolean passwordMatch = storedPassword != null && storedPassword.equals(password);
                    %>
                        <div class="debug-info success">
                            <strong>✅ User Found</strong>
                            User ID: <%= user.getUserId() %>
                            Username: <%= user.getName() %>
                            Email: <%= user.getEmail() %>
                        </div>
                        
                        <div class="debug-info <%= storedPassword != null ? "" : "warning" %>">
                            <strong>🔑 Stored Password:</strong> "<%= storedPassword != null ? storedPassword : "NULL" %>"
                            <strong>Length:</strong> <%= storedPassword != null ? storedPassword.length() : 0 %> characters
                            <% if (storedPassword == null) { %>
                            
                            <strong>⚠️ WARNING:</strong> Password is NULL in database!
                            This account was not created properly.
                            <% } %>
                        </div>
                        
                        <div class="debug-info <%= passwordMatch ? "success" : "error" %>">
                            <strong>Password Comparison:</strong>
                            Match: <%= passwordMatch ? "✅ YES" : "❌ NO" %>
                            
                            <% if (!passwordMatch && storedPassword != null) { %>
                            
                            <strong>🔍 Detailed Analysis:</strong>
                            Entered: "<%= password %>"
                            Stored:  "<%= storedPassword %>"
                            
                            <% 
                            if (!password.equals(storedPassword)) {
                                out.println("\n<strong>Differences detected:</strong>");
                                out.println("- Length mismatch: " + password.length() + " vs " + storedPassword.length());
                                
                                // Show character-by-character comparison
                                int maxLen = Math.max(password.length(), storedPassword.length());
                                if (maxLen <= 50) {
                                    out.println("\nCharacter comparison:");
                                    for (int i = 0; i < maxLen; i++) {
                                        char c1 = i < password.length() ? password.charAt(i) : ' ';
                                        char c2 = i < storedPassword.length() ? storedPassword.charAt(i) : ' ';
                                        if (c1 != c2) {
                                            out.println("  Position " + i + ": entered='" + c1 + "' (" + (int)c1 + ") vs stored='" + c2 + "' (" + (int)c2 + ")");
                                        }
                                    }
                                }
                            }
                            %>
                            <% } %>
                        </div>
                        
                        <% if (passwordMatch) { %>
                        <div class="debug-info success">
                            <strong>🎉 Login Would Succeed!</strong>
                            You can now use these credentials in the regular login page.
                        </div>
                        <% } %>
                    <%
                    }
                    %>
                </div>
            </div>
        <% } %>
        
        <div class="text-center mt-3">
            <a href="login.jsp" class="btn btn-secondary">Go to Login Page</a>
            <a href="register.jsp" class="btn btn-secondary">Go to Register Page</a>
        </div>
    </div>
</body>
</html>
