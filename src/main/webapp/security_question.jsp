<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%
    User user = (User) session.getAttribute("reset_user");
    if (user == null || user.getSecurityQuestion() == null) {
        response.sendRedirect("forgot_password.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Security Question - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-sm">
        <div class="page-header">
            <h1 class="page-title">Answer Security Question</h1>
            <p class="page-subtitle">Please answer your security question to verify your identity</p>
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
                <div class="alert alert-info mb-4">
                    <strong>Your Security Question:</strong><br>
                    <%= user.getSecurityQuestion() %>
                </div>
                
                <form action="VerifySecurityAnswerServlet" method="post">
                    <div class="form-group">
                        <label for="security_answer">Your Answer</label>
                        <input type="text" id="security_answer" name="security_answer" required 
                               placeholder="Enter your answer" autocomplete="off">
                        <small class="text-muted">Answer is not case-sensitive</small>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Verify Answer
                        </button>
                    </div>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="text-muted">
                    <a href="forgot_password.jsp" class="text-secondary auth-link-secondary">
                        ← Try a different email
                    </a>
                </p>
            </div>
        </div>
    </div>
</body>
</html>
