<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
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
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        h1 { color: #333; }
        form { width: 340px; }
        label { display: block; margin-top: 10px; }
        input { width: 100%; padding: 6px; margin-top: 4px; }
        .row { display: flex; gap: 8px; }
        .row > div { flex: 1; }
        .btn { margin-top: 14px; background-color: #007bff; color: white; border: none; padding: 8px; border-radius: 5px; cursor: pointer; }
        .btn:hover { background-color: #0056b3; }
        .msg { margin-top: 8px; font-size: 12px; color: #666; }
    </style>
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

<form action="FoodEntryServlet" method="post" id="foodForm">
    <label for="foodName">Food Name:</label>
    <input type="text" id="foodName" name="foodName" list="food-suggestions" autocomplete="off" required>
    <datalist id="food-suggestions"></datalist>

    <div class="row">
        <div>
            <label for="consumedOunces">Consumed (oz):</label>
            <input type="number" id="consumedOunces" name="consumedOunces" min="0" step="0.1" placeholder="e.g., 4.0">
        </div>
        <div style="align-self: end;">
            <button type="button" class="btn" id="lookupBtn">Get Nutrition Info</button>
        </div>
    </div>

    <!-- Hidden base serving size returned by the servlet (grams). Default 100g -->
    <input type="hidden" id="servingSize" name="servingSize" value="100">

    <label for="calories">Calories:</label>
    <input type="number" id="calories" name="calories" readonly>

    <label for="protein">Protein (g):</label>
    <input type="number" id="protein" name="protein" readonly>

    <label for="carbs">Carbs (g):</label>
    <input type="number" id="carbs" name="carbs" readonly>

    <label for="fat">Fat (g):</label>
    <input type="number" id="fat" name="fat" readonly>

    <button type="submit" class="btn">Save Entry</button>
    <div class="msg" id="statusMsg"></div>
</form>

<br>
<a href="calorieBalance">Back to Calorie Balance</a>

<script>
const foodInput = document.getElementById("foodName");
const datalist = document.getElementById("food-suggestions");
const lookupBtn = document.getElementById("lookupBtn");
const statusMsg = document.getElementById("statusMsg");
const consumedOuncesEl = document.getElementById("consumedOunces");
const servingSizeEl = document.getElementById("servingSize");

// Keep unscaled USDA base macros so we can recalc client-side
let baseMacros = { calories: 0, protein: 0, carbs: 0, fat: 0 };
let baseServingSizeG = 100.0; // default per 100g
const OZ_TO_G = 28.3495;

// Debounce helper
function debounce(fn, delay) {
    let t; return (...args) => { clearTimeout(t); t = setTimeout(() => fn.apply(this, args), delay); };
}

// Fill scaled macros into fields
function fillMacrosScaled(cal, pro, carb, fat) {
    document.getElementById("calories").value = Math.round(Number(cal) || 0);
    document.getElementById("protein").value  = Number(pro || 0).toFixed(2);
    document.getElementById("carbs").value    = Number(carb || 0).toFixed(2);
    document.getElementById("fat").value      = Number(fat || 0).toFixed(2);
}

// Recalculate when weight changes
function recalcFromWeight() {
    const oz = Number(consumedOuncesEl.value || 0);
    const servingG = Number(servingSizeEl.value || baseServingSizeG);
    const grams = oz > 0 ? oz * OZ_TO_G : servingG; // fallback to base
    const multiplier = servingG > 0 ? (grams / servingG) : 1.0;

    const cal  = baseMacros.calories * multiplier;
    const pro  = baseMacros.protein  * multiplier;
    const carb = baseMacros.carbs    * multiplier;
    const fat  = baseMacros.fat      * multiplier;

    fillMacrosScaled(cal, pro, carb, fat);
}

// Fetch nutrition info for the current input
async function fetchNutrition() {
    const q = foodInput.value.trim();
    if (!q) return;

    statusMsg.textContent = "Looking up nutrition…";
    try {
        const res = await fetch("FoodEntryServlet?foodName=" + encodeURIComponent(q));
        if (!res.ok) throw new Error("HTTP " + res.status);
        const data = await res.json();

        // Store base macros (assumed per 100g unless servingSize provided)
        baseMacros.calories = Number(data.calories || 0);
        baseMacros.protein  = Number(data.protein  || 0);
        baseMacros.carbs    = Number(data.carbs    || 0);
        baseMacros.fat      = Number(data.fat      || 0);

        baseServingSizeG = Number(data.servingSize || 100.0);
        servingSizeEl.value = baseServingSizeG;

        statusMsg.textContent = data.name ? ("Loaded: " + data.name) : "No data found";

        // Initial calculation (respect current ounces input)
        recalcFromWeight();
    } catch (err) {
        statusMsg.textContent = "Error fetching nutrition: " + err.message;
    }
}

// Fetch autocomplete suggestions
const fetchSuggestions = debounce(async function() {
    const q = foodInput.value.trim();
    if (q.length < 2) { datalist.innerHTML = ""; return; }
    try {
        const res = await fetch("FoodEntryServlet?autocomplete=" + encodeURIComponent(q));
        if (!res.ok) throw new Error("HTTP " + res.status);
        const items = await res.json(); // array of descriptions
        datalist.innerHTML = "";
        items.forEach(desc => {
            const opt = document.createElement("option");
            opt.value = desc;
            datalist.appendChild(opt);
        });
    } catch (err) { /* ignore autocomplete errors */ }
}, 250);

// Events
foodInput.addEventListener("input", fetchSuggestions);
foodInput.addEventListener("change", fetchNutrition);
foodInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter") { e.preventDefault(); fetchNutrition(); }
});
lookupBtn.addEventListener("click", fetchNutrition);

// Live scaling when ounces change
consumedOuncesEl.addEventListener("input", recalcFromWeight);
</script>

</body>
</html>
