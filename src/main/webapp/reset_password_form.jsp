<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            <h1 class="page-title">Reset Your Password</h1>
            <p class="page-subtitle">Enter your new password below.</p>
        </div>

        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-error">
                <strong>Error:</strong> <%= error %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <form action="resetPassword" method="post">
                    
                    <input type="hidden" name="token" value="<%= request.getAttribute("token") %>">

                    <div class="form-group">
                        <label for="newPassword">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" required 
                               placeholder="Enter new password">
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required 
                               placeholder="Confirm new password">
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Change Password
                        </button>
                    </div>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <a href="login.jsp" class="text-secondary auth-link-secondary">
                    ← Back to Sign In
                </a>
            </div>
        </div>
    </div>
</body>
</html>