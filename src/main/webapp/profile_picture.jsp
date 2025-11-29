<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Profile Picture - MacroMind</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link rel="stylesheet" href="css/custom.css">
  <style>
    .upload-container { max-width: 600px; margin: 0 auto; }
    .preview-img { margin: 1rem 0; }
    #msg { color: var(--success-color); margin-top: 1rem; font-weight: 600; }
    #err { color: var(--danger-color); margin-top: 1rem; font-weight: 600; }
  </style>
</head>
<body>
<div class="container">
  <div class="page-header">
    <h1 class="page-title">Profile Picture</h1>
    <p class="page-subtitle">Upload and manage your profile picture</p>
  </div>

  <div class="upload-container">
    <div class="card">
      <div class="card-header">
        <h3 class="card-title">Current Picture</h3>
      </div>
      <div class="card-body text-center">
        <img id="currentPic"
             class="profile-picture-large"
             src="api/profile/picture?cb=<%= System.currentTimeMillis() %>"
             alt="Current profile picture" />
      </div>
    </div>

    <div class="card mt-4">
      <div class="card-header">
        <h3 class="card-title">Upload New Picture</h3>
        <p class="card-subtitle">JPEG or PNG, max 3 MB</p>
      </div>
      <div class="card-body">
        <div class="form-group">
          <label for="fileInput">Choose Image</label>
          <input type="file" id="fileInput" accept="image/*" class="form-input" />
        </div>

        <div id="previewRow" style="display:none;" class="text-center preview-img">
          <label class="form-label">Preview before saving:</label><br/>
          <img id="previewImg" class="profile-picture-large" alt="Preview" />
        </div>

        <div class="form-actions">
          <button id="saveBtn" class="btn btn-primary" disabled>Save New Picture</button>
          <a href="profile.jsp" class="btn btn-outline">Back to Profile</a>
        </div>

        <div id="msg"></div>
        <div id="err"></div>
      </div>
    </div>
  </div>
</div>

<script src="js/profile_picture.js"></script>
</body>
</html>
