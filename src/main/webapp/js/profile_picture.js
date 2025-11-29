(() => {
  const fileInput = document.getElementById('fileInput');
  const previewRow = document.getElementById('previewRow');
  const previewImg = document.getElementById('previewImg');
  const currentPic = document.getElementById('currentPic');
  const saveBtn = document.getElementById('saveBtn');
  const msg = document.getElementById('msg');
  const err = document.getElementById('err');

  let selectedFile = null;

  function clearMessages() {
    msg.textContent = '';
    err.textContent = '';
  }

  // When user selects a file → show preview (Scenario 2)
  fileInput.addEventListener('change', () => {
    clearMessages();
    const f = fileInput.files[0];
    if (!f) {
      previewRow.style.display = 'none';
      saveBtn.disabled = true;
      selectedFile = null;
      return;
    }

    // basic client-side validation to match backend rules
    if (!f.type.startsWith('image/')) {
      err.textContent = 'Please select an image file (JPEG or PNG).';
      fileInput.value = '';
      return;
    }
    if (f.size > 3 * 1024 * 1024) {
      err.textContent = 'Maximum file size is 3 MB.';
      fileInput.value = '';
      return;
    }

    previewImg.src = URL.createObjectURL(f);  // preview BEFORE saving
    previewRow.style.display = 'block';
    saveBtn.disabled = false;
    selectedFile = f;
  });

  // When user clicks save → POST to servlet (Scenarios 3 & 4)
  saveBtn.addEventListener('click', async () => {
    clearMessages();
    if (!selectedFile) return;

    const fd = new FormData();
    fd.append('file', selectedFile, selectedFile.name);

    try {
      const res = await fetch('api/profile/picture/save', {
        method: 'POST',
        body: fd
      });
      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        err.textContent = data.message || 'Upload failed';
        return;
      }

      msg.textContent = data.message || 'Profile picture saved';

      // Refresh display image (cache-busting param)
      currentPic.src = 'api/profile/picture?cb=' + Date.now();

      // Reset state
      fileInput.value = '';
      selectedFile = null;
      saveBtn.disabled = true;
      previewRow.style.display = 'none';
    } catch (e) {
      err.textContent = e.message || 'Network error';
    }
  });
})();
