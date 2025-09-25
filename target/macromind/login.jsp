<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Login Page</h1>

<% String message = (String) request.getAttribute("message"); %>
<% if (message != null) { %>
    <p style="color:red;"><%= message %></p>
<% } %>

<form action="LoginServlet" method="post">
    Email: <input type="email" name="email" required><br>
    Password: <input type="password" name="password" required><br>
    <input type="submit" value="Login">
</form>

<p>Don't have an account? <a href="register.jsp">Register here</a></p>
</body>
</html>