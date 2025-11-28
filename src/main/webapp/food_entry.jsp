<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Log Food - MacroMind</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">Log Your Food</h1>
            <p class="page-subtitle">Welcome back, <strong><%= user.getName() %></strong>! Track your daily nutrition.</p>
        </div>

        <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <form action="FoodEntryServlet" method="post" id="foodEntryForm">

                    <div class="form-row">
                        <div class="form-group">
                            <label for="foodName" class="form-label">Food Name *</label>
                            <input type="text" id="foodName" name="foodName" class="form-input" required placeholder="e.g., Hamburger">
                        </div>
                        <div class="form-group">
                            <label for="calories" class="form-label">Calories *</label>
                            <input type="number" id="calories" name="calories" class="form-input" min="0" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="protein" class="form-label">Protein (g) *</label>
                            <input type="number" id="protein" name="protein" class="form-input" step="0.1" min="0" required>
                        </div>
                        <div class="form-group">
                            <label for="carbs" class="form-label">Carbs (g) *</label>
                            <input type="number" id="carbs" name="carbs" class="form-input" step="0.1" min="0" required>
                        </div>
                        <div class="form-group">
                            <label for="fat" class="form-label">Fat (g) *</label>
                            <input type="number" id="fat" name="fat" class="form-input" step="0.1" min="0" required>
                        </div>
                        <div class="form-group">
                            <label for="consumed_oz" class="form-label">Serving Size (oz) *</label>
                            <input type="number" id="consumed_oz" name="consumed_oz" class="form-input" step="0.1" min="0" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="entryDate" class="form-label">Date *</label>
                            <input type="date" id="entryDate" name="entryDate" class="form-input" value="<%= java.time.LocalDate.now() %>" required>
                        </div>
                        <div class="form-group">
                            <label for="entryTime" class="form-label">Time</label>
                            <input type="time" id="entryTime" name="entryTime" class="form-input">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="notes" class="form-label">Notes</label>
                        <textarea id="notes" name="notes" class="form-textarea" rows="3" placeholder="Optional observations"></textarea>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Log Food</button>
                        <a href="dashboard" class="btn btn-outline">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        document.getElementById('foodEntryForm').addEventListener('submit', function(e) {
            const requiredFields = ['foodName','calories','protein','carbs','fat','consumed_oz','entryDate'];
            for (const field of requiredFields) {
                if (!document.getElementById(field).value) {
                    e.preventDefault();
                    alert('Please fill all required fields.');
                    return false;
                }
            }
        });
    </script>
</body>
</html>
