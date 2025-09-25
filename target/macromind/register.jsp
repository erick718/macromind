<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fitness Registration</title>
</head>
<body>
    <h1>Register</h1>

    <!-- Error messages -->
    <%
        String error = request.getParameter("error");
        if ("empty".equals(error)) {
    %>
        <p style="color:red;">Please fill in all fields.</p>
    <%
        } else if ("exists".equals(error)) {
    %>
        <p style="color:red;">Username or email already exists.</p>
    <%
        } else if ("db".equals(error)) {
    %>
        <p style="color:red;">Database error. Please try again.</p>
    <%
        }
    %>

    <!-- Registration form -->
    <form action="register" method="post">
        Username: <input type="text" name="username" required><br>
        Email: <input type="email" name="email" required><br>
        Password: <input type="password" name="password" required><br>
        <input type="submit" value="Register">
    </form>
</body>
</html>