<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-sm">
        <div class="page-header">
            <h1 class="page-title">Forgot Password</h1>
            <p class="page-subtitle">Enter your email to receive a reset link.</p>
        </div>

        <% String message = (String) request.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-success">
                <strong>Success:</strong> <%= message %>
            </div>
        <% } %>
        
        <div class="card">
            <div class="card-body">
                <form action="forgotPasswordServlet" method="post">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Enter your email address">
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Request Reset Link
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