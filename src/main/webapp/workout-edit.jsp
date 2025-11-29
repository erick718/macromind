<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fitness.Model.User" %>
<%@ page import="com.fitness.Model.Workout" %>
<%@ page import="com.fitness.dao.WorkoutDAO" %>
<%@ page import="com.fitness.Model.ExerciseType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Get workout to edit
    Workout workout = (Workout) request.getAttribute("workout");
    if (workout == null) {
        response.sendRedirect("workout-history");
        return;
    }
    
    // Verify user owns this workout
    if (workout.getUserId() != user.getUserId()) {
        response.sendRedirect("workout-history");
        return;
    }
    
    // Get exercise types for dropdown
    WorkoutDAO workoutDAO = new WorkoutDAO();
    List<ExerciseType> exerciseTypes = workoutDAO.getAllExerciseTypes();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Workout - MacroMind</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <div class="container container-lg">
        <div class="page-header">
            <h1 class="page-title">Edit Workout</h1>
            <p class="page-subtitle">Update your workout details</p>
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
                <form action="workout-edit" method="post" id="workoutForm">
                    <!-- Hidden workout ID -->
                    <input type="hidden" name="workoutId" value="<%= workout.getWorkoutId() %>">
                    
                    <!-- Exercise Type Selection -->
                    <div class="exercise-type-tabs">
                        <button type="button" class="exercise-type-tab <%= "cardio".equals(workout.getExerciseType()) ? "active" : "" %>" 
                                onclick="showExerciseType('cardio')">Cardio</button>
                        <button type="button" class="exercise-type-tab <%= "strength".equals(workout.getExerciseType()) ? "active" : "" %>" 
                                onclick="showExerciseType('strength')">Strength</button>
                        <button type="button" class="exercise-type-tab <%= "flexibility".equals(workout.getExerciseType()) ? "active" : "" %>" 
                                onclick="showExerciseType('flexibility')">Flexibility</button>
                        <button type="button" class="exercise-type-tab <%= "sports".equals(workout.getExerciseType()) ? "active" : "" %>" 
                                onclick="showExerciseType('sports')">Sports</button>
                    </div>

                    <!-- Hidden exercise type field -->
                    <input type="hidden" id="exerciseType" name="exerciseType" value="<%= workout.getExerciseType() %>">

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
                                   value="<%= workout.getExerciseName() %>"
                                   placeholder="e.g., Running, Push-ups, Bench Press">
                        </div>
                        <div class="form-group">
                            <label for="duration" class="form-label">Duration (minutes) *</label>
                            <input type="number" id="duration" name="duration" class="form-input" min="1" max="300" required
                                   value="<%= workout.getDurationMinutes() %>">
                        </div>
                    </div>

                    <!-- Strength Training Fields -->
                    <div class="strength-fields <%= "strength".equals(workout.getExerciseType()) ? "" : "d-none" %>" id="strengthFields">
                        <h4>Strength Training Details</h4>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="sets" class="form-label">Sets</label>
                                <input type="number" id="sets" name="sets" class="form-input" min="0" max="20" 
                                       value="<%= workout.getSetsCount() %>" placeholder="0">
                            </div>
                            <div class="form-group">
                                <label for="reps" class="form-label">Reps per Set</label>
                                <input type="number" id="reps" name="reps" class="form-input" min="0" max="100" 
                                       value="<%= workout.getRepsPerSet() %>" placeholder="0">
                            </div>
                            <div class="form-group">
                                <label for="weight" class="form-label">Weight (kg)</label>
                                <input type="number" id="weight" name="weight" class="form-input" step="0.5" min="0" max="500" 
                                       value="<%= workout.getWeightKg() %>" placeholder="0">
                            </div>
                        </div>
                    </div>

                    <!-- Date and Time -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="workoutDate" class="form-label">Workout Date *</label>
                            <input type="date" id="workoutDate" name="workoutDate" class="form-input" required 
                                   value="<%= workout.getWorkoutDate() %>">
                        </div>
                        <div class="form-group">
                            <label for="workoutTime" class="form-label">Workout Time</label>
                            <input type="time" id="workoutTime" name="workoutTime" class="form-input"
                                   value="<%= workout.getWorkoutTime() != null ? workout.getWorkoutTime().toString() : "" %>">
                        </div>
                    </div>

                    <!-- Notes -->
                    <div class="form-group">
                        <label for="notes" class="form-label">Notes (optional)</label>
                        <textarea id="notes" name="notes" class="form-textarea" rows="3" 
                                 placeholder="How did the workout feel? Any observations?"><%= workout.getNotes() != null ? workout.getNotes() : "" %></textarea>
                    </div>

                    <!-- Submit Buttons -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            Update Workout
                        </button>
                        <a href="workout-history" class="btn btn-outline">
                            Cancel
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
                strengthFields.classList.remove('d-none');
            } else {
                strengthFields.classList.add('d-none');
                // Clear strength fields if switching away from strength
                if (type !== 'strength') {
                    document.getElementById('sets').value = '';
                    document.getElementById('reps').value = '';
                    document.getElementById('weight').value = '';
                }
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
                };
                container.appendChild(btn);
            });
        }

        // Initialize with current exercise type
        const currentType = '<%= workout.getExerciseType() %>';
        updateQuickExercises(currentType);

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
