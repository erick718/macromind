<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar">
    <div class="nav-left">
        <img src="images/logo.png" alt="Logo" class="logo">
        <span class="brand-name">Macromind</span>
    </div>
    <ul class="nav-links">
        <li><a href="dashboard.jsp">Dashboard</a></li>
        <li><a href="meals.jsp">Meals</a></li>
        <li><a href="workouts.jsp">Workouts</a></li>
        <li class="dropdown">
            <a href="#" class="dropbtn">User ▾</a>
            <div class="dropdown-content">
                <a href="profile.jsp">Profile</a>
                <a href="settings.jsp">Settings</a>
                <a href="logout.jsp">Logout</a>
            </div>
        </li>
    </ul>
</nav>
