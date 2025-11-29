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
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
            <strong>Profile Error:</strong> <%= error %>
        </div>
        <% session.removeAttribute("error"); %>
    <% } %>

    <!-- Profile Picture Card -->
    <div class="card mb-4">
        <div class="card-header">
            <h3 class="card-title">Profile Picture</h3>
            <p class="card-subtitle">Upload or change your profile picture</p>
        </div>
        <div class="card-body">
            <div class="profile-picture-upload-container">
                <div class="current-picture-section">
                    <img src="api/profile/picture?cb=<%= System.currentTimeMillis() %>" 
                         alt="Profile Picture" 
                         id="currentProfilePic"
                         class="profile-picture-large">
                </div>
                
                <div class="upload-section">
                    <h4>Upload New Picture</h4>
                    <input type="file" id="profilePicInput" accept="image/jpeg,image/png" class="form-input mb-3">
                    <div id="uploadStatus" class="mb-3"></div>
                    <button type="button" class="btn btn-primary" onclick="uploadProfilePicture()">Upload Picture</button>
                    <p class="text-muted mt-2">JPEG or PNG, max 10MB</p>
                </div>
            </div>
        </div>
    </div>

    <div class="card">
            <div class="card-header">
                <h3 class="card-title">Personal Information</h3>
                <p class="card-subtitle">This information helps us calculate accurate calorie burns and provide personalized recommendations</p>
            </div>
            <div class="card-body">
                <form action="ProfileServlet" method="post">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="age">Age (years)</label>
                            <input type="number" id="age" name="age" min="1" max="120" 
                                   value="<%= user.getAge() > 0 ? user.getAge() : "" %>" required
                                   placeholder="Enter your age">
                        </div>
                        
                        <div class="form-group">
                            <label for="height">Height (cm)</label>
                            <input type="number" id="height" name="height" min="50" max="300" 
                                   value="<%= user.getHeight() > 0 ? user.getHeight() : "" %>" required
                                   placeholder="Enter your height">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="weight">Weight (kg)</label>
                        <input type="number" id="weight" name="weight" step="0.1" min="20" max="500" 
                               value="<%= user.getWeight() > 0 ? user.getWeight() : "" %>" required
                               placeholder="Enter your current weight">
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="activity">Activity Level</label>
                            <select id="activity" name="activity" required>
                                <option value="">-- Select Activity Level --</option>
                                <option value="low" <%= "low".equals(user.getFitnessLevel()) ? "selected" : "" %>>
                                    Low - Sedentary lifestyle
                                </option>
                                <option value="moderate" <%= "moderate".equals(user.getFitnessLevel()) ? "selected" : "" %>>
                                    Moderate - Regular exercise
                                </option>
                                <option value="high" <%= "high".equals(user.getFitnessLevel()) ? "selected" : "" %>>
                                    High - Very active lifestyle
                                </option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="goal">Fitness Goal</label>
                            <select id="goal" name="goal" required>
                                <option value="">-- Select Your Goal --</option>
                                <option value="lose" <%= "lose".equals(user.getGoal()) ? "selected" : "" %>>
                                    Lose Weight
                                </option>
                                <option value="maintain" <%= "maintain".equals(user.getGoal()) ? "selected" : "" %>>
                                    Maintain Weight
                                </option>
                                <option value="gain" <%= "gain".equals(user.getGoal()) ? "selected" : "" %>>
                                    Gain Muscle
                                </option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            Save Profile
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="nav-actions justify-center">
            <a href="dashboard" class="btn btn-secondary">Back to Dashboard</a>
            <a href="LogoutServlet" class="btn btn-outline">Logout</a>
            <button class="btn btn-danger" onclick="openDeleteModal()">Delete Account</button>
        </div>
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

function uploadProfilePicture() {
    const fileInput = document.getElementById('profilePicInput');
    const statusDiv = document.getElementById('uploadStatus');
    const file = fileInput.files[0];
    
    if (!file) {
        statusDiv.innerHTML = '<div class="alert alert-error">Please select a file first</div>';
        return;
    }
    
    // Validate file type
    if (!file.type.match(/image\/(jpeg|png)/)) {
        statusDiv.innerHTML = '<div class="alert alert-error">Only JPEG or PNG images are allowed</div>';
        return;
    }
    
    // Validate file size (10MB)
    if (file.size > 10 * 1024 * 1024) {
        statusDiv.innerHTML = '<div class="alert alert-error">File size must be less than 10MB</div>';
        return;
    }
    
    const formData = new FormData();
    formData.append('file', file);
    
    statusDiv.innerHTML = '<div class="alert alert-info">Uploading...</div>';
    
    fetch('api/profile/picture/save', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.message === 'Profile picture saved') {
            statusDiv.innerHTML = '<div class="alert alert-success">Profile picture uploaded successfully!</div>';
            // Refresh the profile picture with cache busting
            document.getElementById('currentProfilePic').src = 'api/profile/picture?cb=' + new Date().getTime();
            fileInput.value = ''; // Clear the file input
        } else {
            statusDiv.innerHTML = '<div class="alert alert-error">' + data.message + '</div>';
        }
    })
    .catch(error => {
        statusDiv.innerHTML = '<div class="alert alert-error">Upload failed: ' + error.message + '</div>';
    });
}
</script>

</body>
</html>