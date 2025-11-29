-- Add profile_picture column to users table
-- This will store the image data as a BLOB (Binary Large Object)

ALTER TABLE users 
ADD COLUMN profile_picture MEDIUMBLOB;

-- Optional: Add a column to store the content type (JPEG or PNG)
ALTER TABLE users 
ADD COLUMN profile_picture_type VARCHAR(50);
