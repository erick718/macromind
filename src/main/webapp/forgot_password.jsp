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
            <h1 class="page-title">Reset Your Password</h1>
            <p class="page-subtitle">Enter your email to begin the password reset process</p>
        </div>

        <!-- Error/Success Messages -->
        <% String message = (String) request.getAttribute("message"); %>
        <% String errorType = (String) request.getAttribute("errorType"); %>
        <% if (message != null) { %>
            <div class="alert <%= "success".equals(errorType) ? "alert-success" : "alert-error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <p class="text-muted mb-4">We'll ask you to answer your security question to verify your identity.</p>
                
                <form action="ForgotPasswordServlet" method="post">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Enter your registered email address">
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Continue
                        </button>
                    </div>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="text-muted">Remember your password? 
                    <a href="login.jsp" class="text-primary auth-link">
                        Sign in here
                    </a>
                </p>
                <p class="text-muted">
                    <a href="index.jsp" class="text-secondary auth-link-secondary">
                        ← Back to Home
                    </a>
                </p>
            </div>
        </div>
    </div>
</body>
</html>
