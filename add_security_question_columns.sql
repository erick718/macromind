-- Add security question fields to users table
-- This allows users to reset their password using a security question

-- Add security_question column if it doesn't exist
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'security_question'
);

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN security_question VARCHAR(255) DEFAULT NULL AFTER hashed_password', 
    'SELECT "Column security_question already exists" AS message'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add security_answer_hash column if it doesn't exist
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'security_answer_hash'
);

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN security_answer_hash VARCHAR(255) DEFAULT NULL AFTER security_question', 
    'SELECT "Column security_answer_hash already exists" AS message'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verify the changes
SELECT 'Security question columns added successfully!' AS Status;
DESCRIBE users;
