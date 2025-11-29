<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%
    User user = (User) session.getAttribute("reset_user");
    Boolean verified = (Boolean) session.getAttribute("security_verified");
    if (user == null || verified == null || !verified) {
        response.sendRedirect("forgot_password.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-sm">
        <div class="page-header">
            <h1 class="page-title">Create New Password</h1>
            <p class="page-subtitle">Enter your new password below</p>
        </div>

        <!-- Error Messages -->
        <% String message = (String) request.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-error">
                <strong>Error:</strong> <%= message %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <div class="alert alert-success mb-4">
                    <strong>Identity Verified!</strong><br>
                    You can now create a new password for <%= user.getEmail() %>
                </div>
                
                <form action="ResetPasswordServlet" method="post">
                    <div class="form-group">
                        <label for="new_password">New Password</label>
                        <input type="password" id="new_password" name="new_password" required 
                               placeholder="Enter new password" minlength="6">
                        <small class="text-muted">Password must be at least 6 characters long</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirm_password">Confirm Password</label>
                        <input type="password" id="confirm_password" name="confirm_password" required 
                               placeholder="Re-enter new password" minlength="6">
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Reset Password
                        </button>
                    </div>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="text-muted">
                    <a href="login.jsp" class="text-secondary auth-link-secondary">
                        ← Back to Login
                    </a>
                </p>
            </div>
        </div>
    </div>
</body>
</html>
