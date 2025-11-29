<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%@ page import="com.fitness.Model.FoodEntry" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Collections" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Retrieve the list of entries from the request attribute set by the Servlet
    @SuppressWarnings("unchecked")
    List<FoodEntry> foodHistory = (List<FoodEntry>) request.getAttribute("foodHistory");
    if (foodHistory == null) {
        foodHistory = Collections.emptyList();
    }
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy @ HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Food History - MacroMind</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">Your Food History</h1>
            <p class="page-subtitle">Review and track your past meals, <strong><%= user.getName() %></strong>.</p>
        </div>

        <div class="nav-actions justify-center mb-4">
            <a href="food_entry.jsp" class="btn btn-warning">Log New Food</a>
            <a href="dashboard" class="btn btn-outline">Dashboard</a>
        </div>

        <% String message = (String) session.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-success">
                <strong>Success!</strong> <%= message %>
            </div>
            <% session.removeAttribute("message"); %>
        <% } %>

        <div class="card">
            <div class="card-body">
                <% if (foodHistory.isEmpty()) { %>
                    <div class="alert alert-warning">
                        You haven't logged any food yet for today!
                    </div>
                <% } else { %>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Date/Time</th>
                                    <th>Food</th>
                                    <th>Calories</th>
                                    <th>Protein (g)</th>
                                    <th>Carbs (g)</th>
                                    <th>Fat (g)</th>
                                    <th>Serving (oz)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (FoodEntry entry : foodHistory) { %>
                                <tr>
                                    <td><%= entry.getEntryDate().format(formatter) %></td>
                                    <td><%= entry.getFoodName() %></td>
                                    <td><%= entry.getCalories() %></td>
                                    <td><%= entry.getProtein() %></td>
                                    <td><%= entry.getCarbs() %></td>
                                    <td><%= entry.getFat() %></td>
                                    <td><%= String.format("%.1f", entry.getConsumedOz()) %></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>