<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset Success - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-sm">
        <div class="page-header">
            <h1 class="page-title">Password Reset Successful!</h1>
            <p class="page-subtitle">Your password has been changed</p>
        </div>

        <div class="card">
            <div class="card-body text-center">
                <div class="alert alert-success mb-4">
                    <h3>✓ Success!</h3>
                    <p class="mb-0">Your password has been successfully reset. You can now log in with your new password.</p>
                </div>
                
                <a href="login.jsp" class="btn btn-primary btn-lg">
                    Go to Login
                </a>
            </div>
            
            <div class="card-footer text-center">
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
