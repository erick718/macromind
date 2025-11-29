-- ========================================================
-- Database Migration for Will Branch Merge
-- ========================================================
-- This script updates your database to match the merged code
-- Run this in your MacroMind database
-- ========================================================

USE MacroMind;

-- Step 1: Check if password column exists and rename it
-- (Skip if already renamed to hashed_password)
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'MacroMind' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'password'
);

SET @query = IF(@col_exists > 0, 
    'ALTER TABLE users CHANGE COLUMN password hashed_password VARCHAR(255) NOT NULL', 
    'SELECT "Column already renamed to hashed_password" AS message'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 2: Add security_question column if it doesn't exist
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'MacroMind' 
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

-- Step 3: Add security_answer_hash column if it doesn't exist
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'MacroMind' 
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

-- Step 4: Verify the changes
SELECT '✅ Migration Complete!' AS Status;
SELECT 'Current users table structure:' AS Info;
DESCRIBE users;

-- Step 5: Check if existing users need password reset
SELECT 
    COUNT(*) as total_users,
    SUM(CASE WHEN hashed_password NOT LIKE '$2a$%' THEN 1 ELSE 0 END) as users_needing_password_reset,
    SUM(CASE WHEN security_question IS NOT NULL THEN 1 ELSE 0 END) as users_with_security_question
FROM users;

-- NOTE: If users have plaintext passwords (not starting with $2a$),
-- they will need to re-register or you need to hash them manually
