<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
<div class="container mt-5">
    <h2>Login</h2>

    <c:if test="${not empty message}">
        <div class="alert alert-${messageType}">${message}</div>
    </c:if>

    <form action="LoginServlet" method="post">
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" class="form-control" name="email" id="email" required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" class="form-control" name="password" id="password" required>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Login</button>
    </form>

    <p class="mt-3">Don't have an account? <a href="register.jsp">Register</a></p>
</div>
<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>