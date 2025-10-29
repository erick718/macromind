<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.model.User" %>
<%@ page import="com.fitness.dao.WorkoutDAO" %>
<%@ page import="com.fitness.model.ExerciseType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Get exercise types for dropdown
    WorkoutDAO workoutDAO = new WorkoutDAO();
    List<ExerciseType> exerciseTypes = workoutDAO.getAllExerciseTypes();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Log Workout - MacroMind</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">Log Your Workout</h1>
            <p class="page-subtitle">Welcome back, <strong><%= user.getName() %></strong>! Track your fitness progress.</p>
        </div>
        
        <!-- Success/Error Messages -->
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
                <form action="workout-log" method="post" id="workoutForm">
            <!-- Exercise Type Selection -->
            <div class="exercise-type-tabs">
                <button type="button" class="exercise-type-tab active" onclick="showExerciseType('cardio')">Cardio</button>
                <button type="button" class="exercise-type-tab" onclick="showExerciseType('strength')">Strength</button>
                <button type="button" class="exercise-type-tab" onclick="showExerciseType('flexibility')">Flexibility</button>
                <button type="button" class="exercise-type-tab" onclick="showExerciseType('sports')">Sports</button>
            </div>

            <!-- Hidden exercise type field -->
            <input type="hidden" id="exerciseType" name="exerciseType" value="cardio">

                    <!-- Quick Exercise Selection (dynamically populated) -->
                    <div class="form-section">
                        <label class="section-title">Quick Select:</label>
                        <div id="quickExerciseButtons"></div>
                    </div>

                    <!-- Exercise Details -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="exerciseName" class="form-label">Exercise Name *</label>
                            <input type="text" id="exerciseName" name="exerciseName" class="form-input" required 
                                   placeholder="e.g., Running, Push-ups, Bench Press">
                        </div>
                        <div class="form-group">
                            <label for="duration" class="form-label">Duration (minutes) *</label>
                            <input type="number" id="duration" name="duration" class="form-input" min="1" max="300" required>
                        </div>
                    </div>

                    <!-- Strength Training Fields -->
                    <div class="strength-fields" id="strengthFields">
                        <h4>Strength Training Details</h4>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="sets" class="form-label">Sets</label>
                                <input type="number" id="sets" name="sets" class="form-input" min="0" max="20" placeholder="0">
                            </div>
                            <div class="form-group">
                                <label for="reps" class="form-label">Reps per Set</label>
                                <input type="number" id="reps" name="reps" class="form-input" min="0" max="100" placeholder="0">
                            </div>
                            <div class="form-group">
                                <label for="weight" class="form-label">Weight (kg)</label>
                                <input type="number" id="weight" name="weight" class="form-input" step="0.5" min="0" max="500" placeholder="0">
                            </div>
                        </div>
                    </div>

                    <!-- Date and Time -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="workoutDate" class="form-label">Workout Date *</label>
                            <input type="date" id="workoutDate" name="workoutDate" class="form-input" required 
                                   value="<%= LocalDate.now() %>">
                        </div>
                        <div class="form-group">
                            <label for="workoutTime" class="form-label">Workout Time</label>
                            <input type="time" id="workoutTime" name="workoutTime" class="form-input">
                        </div>
                    </div>

                    <!-- Notes -->
                    <div class="form-group">
                        <label for="notes" class="form-label">Notes (optional)</label>
                        <textarea id="notes" name="notes" class="form-textarea" rows="3" 
                                 placeholder="How did the workout feel? Any observations?"></textarea>
                    </div>

                    <!-- Submit Buttons -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            
                            Log Workout
                        </button>
                        <a href="workout-history" class="btn btn-secondary">
                            
                            View History
                        </a>
                        <a href="dashboard" class="btn btn-outline">
                            
                            Dashboard
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Exercise data by category
        const exercisesByCategory = {
            cardio: [
                'Running (6 mph)', 'Walking (3.5 mph)', 'Cycling (moderate)', 
                'Swimming (moderate)', 'Elliptical', 'Jump Rope', 'Rowing Machine'
            ],
            strength: [
                'Weight Lifting (moderate)', 'Weight Lifting (vigorous)', 'Push-ups', 
                'Pull-ups', 'Squats (bodyweight)', 'Deadlifts', 'Bench Press'
            ],
            flexibility: [
                'Yoga (Hatha)', 'Yoga (Vinyasa)', 'Stretching', 'Pilates'
            ],
            sports: [
                'Basketball', 'Tennis', 'Soccer', 'Martial Arts'
            ]
        };

        function showExerciseType(type) {
            // Update active tab
            document.querySelectorAll('.exercise-type-tab').forEach(tab => {
                tab.classList.remove('active');
            });
            event.target.classList.add('active');
            
            // Set hidden field
            document.getElementById('exerciseType').value = type;
            
            // Show/hide strength fields
            const strengthFields = document.getElementById('strengthFields');
            if (type === 'strength') {
                strengthFields.style.display = 'block';
            } else {
                strengthFields.style.display = 'none';
                // Clear strength fields
                document.getElementById('sets').value = '';
                document.getElementById('reps').value = '';
                document.getElementById('weight').value = '';
            }
            
            // Update quick exercise buttons
            updateQuickExercises(type);
        }

        function updateQuickExercises(category) {
            const container = document.getElementById('quickExerciseButtons');
            container.innerHTML = '';
            
            exercisesByCategory[category].forEach(exercise => {
                const btn = document.createElement('span');
                btn.className = 'quick-exercise-btn';
                btn.textContent = exercise;
                btn.onclick = () => {
                    document.getElementById('exerciseName').value = exercise;
                    
                    // Set default duration based on exercise type
                    const durationField = document.getElementById('duration');
                    if (category === 'cardio') {
                        durationField.value = '30';
                    } else if (category === 'strength') {
                        durationField.value = '45';
                        document.getElementById('sets').value = '3';
                        document.getElementById('reps').value = '10';
                    } else {
                        durationField.value = '20';
                    }
                };
                container.appendChild(btn);
            });
        }

        // Initialize with cardio exercises
        updateQuickExercises('cardio');

        // Set current time
        const now = new Date();
        const timeString = now.getHours().toString().padStart(2, '0') + ':' + 
                          now.getMinutes().toString().padStart(2, '0');
        document.getElementById('workoutTime').value = timeString;

        // Form validation
        document.getElementById('workoutForm').addEventListener('submit', function(e) {
            const exerciseName = document.getElementById('exerciseName').value.trim();
            const duration = document.getElementById('duration').value;
            const workoutDate = document.getElementById('workoutDate').value;
            
            if (!exerciseName || !duration || !workoutDate) {
                e.preventDefault();
                alert('Please fill in all required fields: Exercise Name, Duration, and Date.');
                return false;
            }
            
            if (parseInt(duration) <= 0) {
                e.preventDefault();
                alert('Duration must be greater than 0 minutes.');
                return false;
            }
        });
    </script>
</body>
</html>