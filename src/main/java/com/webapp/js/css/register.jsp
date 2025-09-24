<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
<div class="container mt-5">
    <h2>Register</h2>

    <c:if test="${not empty message}">
        <div class="alert alert-${messageType}">${message}</div>
    </c:if>

    <form action="RegisterServlet" method="post">
        <div class="form-group">
            <label for="username">Name</label>
            <input type="text" class="form-control" name="username" id="username" required>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" class="form-control" name="email" id="email" required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" class="form-control" name="password" id="password" required>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Register</button>
    </form>

    <p class="mt-3">Already have an account? <a href="login.jsp">Log in</a></p>
</div>
<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>