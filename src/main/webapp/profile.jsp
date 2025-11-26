<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>Profile Setup - MacroMind</title>
    <link rel="stylesheet" href="css/custom.css">
</head>

<body>
<div class="container">
    <h2>Profile Setup for <%= user.getName() %></h2>

    <!-- Success/Error Messages -->
    <% String error = (String) session.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="alert alert-error">
            <%= error %>
        </div>
        <% session.removeAttribute("error"); %>
    <% } %>

    <!-- Profile form -->
    <form action="ProfileServlet" method="post">
        <div class="form-group">
            <label for="age">Age:</label>
            <input type="number" id="age" name="age" min="1" max="120"
                   value="<%= user.getAge() > 0 ? user.getAge() : "" %>" required>
        </div>

        <div class="form-group">
            <label for="height">Height (cm):</label>
            <input type="number" id="height" name="height" min="50" max="300"
                   value="<%= user.getHeight() > 0 ? user.getHeight() : "" %>" required>
        </div>

        <div class="form-group">
            <label for="weight">Weight (kg):</label>
            <input type="number" id="weight" name="weight" step="0.1" min="20" max="500"
                   value="<%= user.getWeight() > 0 ? user.getWeight() : "" %>" required>
        </div>

        <div class="form-group">
            <label for="activity">Activity Level:</label>
            <select id="activity" name="activity" required>
                <option value="">-- Select Activity Level --</option>
                <option value="low" <%= "low".equals(user.getFitnessLevel()) ? "selected" : "" %>>Low</option>
                <option value="moderate" <%= "moderate".equals(user.getFitnessLevel()) ? "selected" : "" %>>Moderate</option>
                <option value="high" <%= "high".equals(user.getFitnessLevel()) ? "selected" : "" %>>High</option>
            </select>
        </div>

        <div class="form-group">
            <label for="goal">Goal:</label>
            <select id="goal" name="goal" required>
                <option value="">-- Select Your Goal --</option>
                <option value="lose" <%= "lose".equals(user.getGoal()) ? "selected" : "" %>>Lose Weight</option>
                <option value="maintain" <%= "maintain".equals(user.getGoal()) ? "selected" : "" %>>Maintain Weight</option>
                <option value="gain" <%= "gain".equals(user.getGoal()) ? "selected" : "" %>>Gain Muscle</option>
            </select>
        </div>

        <div class="form-group">
            <input type="submit" value="Save Profile" class="btn btn-primary" style="width: 100%;">
        </div>
    </form>

    <div class="actions">
        <a href="dashboard" class="btn btn-secondary">Back to Dashboard</a>
        <a href="LogoutServlet" class="btn btn-outline">Logout</a>
    </div>

    <!-- Delete account button -->
    <button class="btn btn-danger" onclick="openDeleteModal()">Delete Account</button>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteModal" class="modal">
    <div class="modal-content">
        <h3>Are you sure?</h3>
        <p>This will permanently delete your account and all your saved data.</p>

        <button class="btn btn-secondary" onclick="closeDeleteModal()">Cancel</button>

        <form action="DeleteAccountServlet" method="POST" style="display:inline;">
            <input type="text" name="confirm" placeholder="Type DELETE to confirm" required>
            <button type="submit" class="btn btn-danger">Delete Account</button>
        </form>
    </div>
</div>

<style>
.modal {
    display: none;
    position: fixed;
    left: 0; top: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.7);
}
.modal-content {
    margin: 15% auto;
    padding: 20px;
    width: 30%;
    background: white;
    border-radius: 10px;
}
</style>

<script>
function openDeleteModal() {
    document.getElementById("deleteModal").style.display = "block";
}
function closeDeleteModal() {
    document.getElementById("deleteModal").style.display = "none";
}
</script>

</body>
</html>