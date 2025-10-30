<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-sm">
        <div class="page-header">
            <h1 class="page-title">Welcome Back</h1>
            <p class="page-subtitle">Sign in to continue your fitness journey</p>
        </div>

        <!-- Error Messages -->
        <% String message = (String) request.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-error">
                <strong>Login Failed:</strong> <%= message %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <form action="LoginServlet" method="post">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Enter your email address">
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required 
                               placeholder="Enter your password">
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Sign In
                        </button>
                    </div>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="text-muted">Don't have an account? 
                                        <a href="register.jsp" class="text-primary auth-link">
                        Create an account
                    </a>
                </div>
                <div class="text-center mt-3">
                    <a href="index.jsp" class="text-secondary auth-link-secondary">
                        ← Back to Home
                    </a>
            </div>
        </div>
    </div>
</body>
</html>