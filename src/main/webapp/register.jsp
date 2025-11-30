<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-sm">
        <div class="page-header">
            <h1 class="page-title">Join MacroMind</h1>
            <p class="page-subtitle">Create your account and start your fitness journey today</p>
        </div>

        <!-- Error Messages -->
        <% String message = (String) request.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-error">
                <strong>Registration Error:</strong> <%= message %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <form action="RegisterServlet" method="post">
                    <div class="form-group">
                        <label for="username">Full Name</label>
                        <input type="text" id="username" name="username" required 
                               placeholder="Enter your full name">
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Enter your email address">
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="password-wrapper">
                            <input type="password" id="password" name="password" required 
                                   placeholder="Create a secure password" minlength="6" style="padding-right: 80px;">
                            <button type="button" class="password-toggle" onclick="togglePassword()">Show</button>
                        </div>
                        <small class="text-muted">Password must be at least 6 characters long</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="security_question">Security Question</label>
                        <select id="security_question" name="security_question" required>
                            <option value="">Select a security question...</option>
                            <option value="What was the name of your first pet?">What was the name of your first pet?</option>
                            <option value="What city were you born in?">What city were you born in?</option>
                            <option value="What is your mother's maiden name?">What is your mother's maiden name?</option>
                            <option value="What was the name of your elementary school?">What was the name of your elementary school?</option>
                            <option value="What is your favorite book?">What is your favorite book?</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="security_answer">Security Answer</label>
                        <input type="text" id="security_answer" name="security_answer" required 
                               placeholder="Enter your answer">
                        <small class="text-muted">This will help you reset your password if you forget it</small>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Create Account
                        </button>
                    </div>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="text-muted">Already have an account? 
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
        
        <div class="card mt-4">
            <div class="card-body text-center">
                <h4 class="text-primary mb-3">What you'll get with MacroMind:</h4>
                <div class="grid grid-auto">
                    <div>
                        <h5 class="text-success">Workout Tracking</h5>
                        <p class="text-muted">Log exercises, sets, reps, and track calories burned</p>
                    </div>
                    <div>
                        <h5 class="text-info">Progress Analytics</h5>
                        <p class="text-muted">View detailed insights and performance trends</p>
                    </div>
                    <div>
                        <h5 class="text-warning">Goal Setting</h5>
                        <p class="text-muted">Set personalized fitness goals and track achievements</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        function togglePassword() {
            const passwordField = document.getElementById('password');
            const toggleButton = document.querySelector('.password-toggle');
            
            if (passwordField.type === 'password') {
                passwordField.type = 'text';
                toggleButton.textContent = 'Hide';
            } else {
                passwordField.type = 'password';
                toggleButton.textContent = 'Show';
            }
        }
    </script>
</body>
</html>