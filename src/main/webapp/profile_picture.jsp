<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Profile Picture</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <style>
    body { font-family: system-ui, Arial, sans-serif; margin: 24px; }
    .wrap { max-width: 480px; }
    .preview {
      width: 160px; height: 160px;
      border-radius: 50%;
      object-fit: cover;
      border: 1px solid #ddd;
    }
    .row { margin: 12px 0; }
    button { padding: 8px 12px; cursor: pointer; }
    #msg { color: #0a0; }
    #err { color: #b00; }
  </style>
</head>
<body>
<div class="wrap">
  <h2>Profile Picture</h2>

  <!-- current image -->
  <div class="row">
    <img id="currentPic"
         class="preview"
         src="api/profile/picture?cb=<%= System.currentTimeMillis() %>"
         alt="Current profile picture" />
  </div>

  <!-- file input -->
  <div class="row">
    <input type="file" id="fileInput" accept="image/*" />
  </div>

  <!-- preview before saving -->
  <div class="row" id="previewRow" style="display:none;">
    <label>Preview before saving:</label><br/>
    <img id="previewImg" class="preview" alt="Preview" />
  </div>

  <div class="row">
    <button id="saveBtn" disabled>Save New Picture</button>
  </div>

  <div id="msg"></div>
  <div id="err"></div>
</div>

<script src="js/profile_picture.js"></script>
</body>
</html>
