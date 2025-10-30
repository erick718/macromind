-- =====================================================
-- MacroMind Fitness Application - Complete Database Setup
-- Run this script in MySQL Workbench to set up the complete database
-- =====================================================

-- Create the database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS `macromind` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- Use the database
USE `macromind`;

-- =====================================================
-- 1. USERS TABLE (Core user authentication and profile)
-- =====================================================
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX `idx_email` (`email`),
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. USER PROFILES TABLE (Extended user fitness data)
-- =====================================================
CREATE TABLE IF NOT EXISTS `user_profiles` (
    `profile_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `age` INT,
    `height_cm` INT,
    `weight_kg` INT,
    `activity_level` ENUM('low', 'moderate', 'high') DEFAULT 'moderate',
    `fitness_goal` ENUM('lose', 'maintain', 'gain') DEFAULT 'maintain',
    `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_user_profile` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. EXERCISES TABLE (Exercise database)
-- =====================================================
CREATE TABLE IF NOT EXISTS `exercises` (
    `exercise_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `muscle_group` VARCHAR(50) NOT NULL,
    `difficulty` ENUM('beginner', 'intermediate', 'advanced') NOT NULL,
    `equipment` VARCHAR(100) DEFAULT 'none',
    `duration_minutes` INT NOT NULL,
    `calories_burned` INT NOT NULL,
    `goal` VARCHAR(50) DEFAULT 'general',
    `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX `idx_muscle_group` (`muscle_group`),
    INDEX `idx_difficulty` (`difficulty`),
    INDEX `idx_goal` (`goal`),
    INDEX `idx_muscle_difficulty` (`muscle_group`, `difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. WORKOUT PLANS TABLE (Generated workout plans)
-- =====================================================
CREATE TABLE IF NOT EXISTS `workout_plans` (
    `plan_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `plan_name` VARCHAR(100) NOT NULL,
    `goal` ENUM('lose', 'gain', 'maintain') NOT NULL,
    `difficulty` ENUM('beginner', 'intermediate', 'advanced') NOT NULL,
    `duration_weeks` INT NOT NULL,
    `sessions_per_week` INT NOT NULL,
    `total_calories_burned` INT NOT NULL DEFAULT 0,
    `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_goal` (`goal`),
    INDEX `idx_created_date` (`created_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. WORKOUT PLAN EXERCISES TABLE (Many-to-many relationship)
-- =====================================================
CREATE TABLE IF NOT EXISTS `workout_plan_exercises` (
    `plan_id` INT NOT NULL,
    `exercise_id` INT NOT NULL,
    `order_in_plan` INT DEFAULT 0,
    `sets` INT DEFAULT 1,
    `reps` INT DEFAULT NULL,
    `rest_seconds` INT DEFAULT 60,
    
    PRIMARY KEY (`plan_id`, `exercise_id`),
    FOREIGN KEY (`plan_id`) REFERENCES `workout_plans`(`plan_id`) ON DELETE CASCADE,
    FOREIGN KEY (`exercise_id`) REFERENCES `exercises`(`exercise_id`) ON DELETE CASCADE,
    INDEX `idx_plan_id` (`plan_id`),
    INDEX `idx_exercise_id` (`exercise_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. POPULATE EXERCISES TABLE WITH SAMPLE DATA
-- =====================================================

-- Clear existing exercises (if any)
DELETE FROM `exercises`;

-- Reset auto increment
ALTER TABLE `exercises` AUTO_INCREMENT = 1;

-- Insert Cardio Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Running (Beginner)', 'Light jogging at comfortable pace to build cardiovascular endurance', 'cardio', 'beginner', 'none', 20, 200, 'lose'),
('Running (Intermediate)', 'Moderate pace running for improved fitness', 'cardio', 'intermediate', 'none', 30, 350, 'lose'),
('Running (Advanced)', 'High-intensity running for maximum calorie burn', 'cardio', 'advanced', 'none', 45, 500, 'lose'),
('Jumping Jacks', 'Full body cardio exercise to elevate heart rate', 'cardio', 'beginner', 'none', 10, 100, 'lose'),
('High Knees', 'Running in place with high knees for cardio conditioning', 'cardio', 'intermediate', 'none', 10, 120, 'lose'),
('Burpees', 'High-intensity full body exercise combining squat, plank, and jump', 'cardio', 'advanced', 'none', 10, 200, 'lose'),
('Walking', 'Brisk walking exercise for low-impact cardio', 'cardio', 'beginner', 'none', 30, 150, 'maintain'),
('Cycling', 'Stationary or outdoor cycling for cardiovascular fitness', 'cardio', 'intermediate', 'bicycle', 30, 300, 'maintain'),
('Swimming', 'Full body swimming workout for complete fitness', 'cardio', 'advanced', 'pool', 30, 400, 'maintain'),
('Jump Rope', 'Classic cardio exercise using a jump rope', 'cardio', 'intermediate', 'jump rope', 15, 180, 'lose');

-- Insert Full Body Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Push-ups (Modified)', 'Knee push-ups for beginners to build upper body strength', 'full_body', 'beginner', 'none', 10, 80, 'gain'),
('Push-ups (Standard)', 'Traditional push-ups for chest, shoulders, and triceps', 'full_body', 'intermediate', 'none', 15, 120, 'gain'),
('Push-ups (Advanced)', 'Diamond, one-arm, or decline push-ups for advanced strength', 'full_body', 'advanced', 'none', 20, 180, 'gain'),
('Squats (Bodyweight)', 'Basic bodyweight squats for leg and glute strength', 'full_body', 'beginner', 'none', 15, 100, 'gain'),
('Squats (Weighted)', 'Squats with dumbbells or kettlebell for added resistance', 'full_body', 'intermediate', 'dumbbells', 20, 150, 'gain'),
('Jump Squats', 'Explosive squat jumps for power and cardio', 'full_body', 'advanced', 'none', 15, 200, 'gain'),
('Mountain Climbers', 'Dynamic full body exercise targeting core and cardio', 'full_body', 'intermediate', 'none', 10, 150, 'lose'),
('Bear Crawls', 'Quadrupedal movement pattern for full body strength', 'full_body', 'advanced', 'none', 10, 180, 'lose'),
('Thrusters', 'Squat to overhead press combination movement', 'full_body', 'advanced', 'dumbbells', 15, 220, 'gain');

-- Insert Core Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Plank (Modified)', 'Knee plank hold for building core stability', 'core', 'beginner', 'none', 5, 40, 'gain'),
('Plank (Standard)', 'Traditional plank hold for core strength', 'core', 'intermediate', 'none', 8, 60, 'gain'),
('Plank (Advanced)', 'Single-arm, leg, or side plank variations', 'core', 'advanced', 'none', 12, 100, 'gain'),
('Crunches', 'Basic abdominal crunches for core strength', 'core', 'beginner', 'none', 10, 60, 'lose'),
('Bicycle Crunches', 'Dynamic core rotation exercise for obliques', 'core', 'intermediate', 'none', 12, 90, 'lose'),
('Russian Twists', 'Seated core rotation exercise with optional weight', 'core', 'advanced', 'medicine ball', 15, 120, 'lose'),
('Dead Bug', 'Core stability exercise for deep abdominal muscles', 'core', 'beginner', 'none', 8, 50, 'maintain'),
('Bird Dog', 'Core and back stability exercise', 'core', 'intermediate', 'none', 10, 70, 'maintain'),
('Hollow Body Hold', 'Advanced core isometric exercise', 'core', 'advanced', 'none', 8, 80, 'gain');

-- Insert Chest Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Wall Push-ups', 'Standing push-ups against wall for beginners', 'chest', 'beginner', 'none', 10, 60, 'gain'),
('Incline Push-ups', 'Push-ups with hands elevated on bench or step', 'chest', 'beginner', 'bench', 12, 80, 'gain'),
('Chest Press (Light)', 'Dumbbell chest press with light weight', 'chest', 'intermediate', 'dumbbells', 15, 100, 'gain'),
('Chest Press (Heavy)', 'Heavy dumbbell or barbell chest press', 'chest', 'advanced', 'barbell', 20, 150, 'gain'),
('Chest Flyes', 'Dumbbell chest fly exercise for pec isolation', 'chest', 'intermediate', 'dumbbells', 15, 110, 'gain'),
('Dips', 'Parallel bar or bench dips for chest and triceps', 'chest', 'advanced', 'dip bars', 12, 120, 'gain');

-- Insert Back Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Resistance Band Rows', 'Seated rows using resistance band', 'back', 'beginner', 'resistance band', 12, 80, 'gain'),
('Bent-over Rows', 'Dumbbell bent-over rows for lat development', 'back', 'intermediate', 'dumbbells', 15, 120, 'gain'),
('Pull-ups (Assisted)', 'Pull-ups with assistance band or machine', 'back', 'intermediate', 'pull-up bar', 10, 100, 'gain'),
('Pull-ups (Standard)', 'Unassisted pull-ups for advanced strength', 'back', 'advanced', 'pull-up bar', 15, 150, 'gain'),
('Superman', 'Back extension exercise lying prone', 'back', 'beginner', 'none', 10, 60, 'maintain'),
('Lat Pulldowns', 'Cable lat pulldown exercise', 'back', 'intermediate', 'cable machine', 15, 110, 'gain'),
('Reverse Flyes', 'Rear deltoid and upper back exercise', 'back', 'beginner', 'dumbbells', 12, 90, 'gain');

-- Insert Leg Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Wall Sit', 'Isometric squat hold against wall', 'legs', 'beginner', 'none', 5, 50, 'gain'),
('Lunges (Bodyweight)', 'Forward, reverse, or lateral lunges', 'legs', 'beginner', 'none', 12, 90, 'gain'),
('Lunges (Weighted)', 'Lunges with dumbbells or barbell', 'legs', 'intermediate', 'dumbbells', 15, 130, 'gain'),
('Bulgarian Split Squats', 'Single-leg squat with rear foot elevated', 'legs', 'advanced', 'none', 15, 150, 'gain'),
('Calf Raises', 'Standing calf raises for lower leg strength', 'legs', 'beginner', 'none', 10, 60, 'maintain'),
('Single-leg Glute Bridges', 'Hip bridge exercise with one leg', 'legs', 'intermediate', 'none', 12, 80, 'gain'),
('Step-ups', 'Step up onto bench or box', 'legs', 'intermediate', 'bench', 15, 120, 'gain'),
('Deadlifts', 'Hip hinge movement with weight', 'legs', 'advanced', 'barbell', 20, 180, 'gain');

-- Insert Arms Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Arm Circles', 'Dynamic arm warm-up exercise', 'arms', 'beginner', 'none', 5, 30, 'maintain'),
('Tricep Dips (Chair)', 'Chair-assisted tricep dips', 'arms', 'beginner', 'chair', 10, 70, 'gain'),
('Tricep Dips (Bench)', 'Bench tricep dips for advanced difficulty', 'arms', 'intermediate', 'bench', 12, 90, 'gain'),
('Bicep Curls (Light)', 'Light dumbbell bicep curls', 'arms', 'beginner', 'dumbbells', 10, 60, 'gain'),
('Bicep Curls (Heavy)', 'Heavy dumbbell bicep curls', 'arms', 'intermediate', 'dumbbells', 15, 100, 'gain'),
('Hammer Curls', 'Neutral grip bicep curls', 'arms', 'intermediate', 'dumbbells', 15, 110, 'gain'),
('Tricep Extensions', 'Overhead tricep extensions', 'arms', 'advanced', 'dumbbells', 15, 120, 'gain'),
('Close-grip Push-ups', 'Push-ups with hands close together for triceps', 'arms', 'intermediate', 'none', 12, 100, 'gain');

-- Insert Flexibility Exercises
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Basic Stretching', 'General flexibility routine for all muscle groups', 'flexibility', 'beginner', 'none', 15, 50, 'maintain'),
('Yoga Flow (Beginner)', 'Gentle yoga sequence for flexibility and relaxation', 'flexibility', 'beginner', 'yoga mat', 20, 80, 'maintain'),
('Yoga Flow (Intermediate)', 'Moderate yoga sequence with more challenging poses', 'flexibility', 'intermediate', 'yoga mat', 30, 120, 'maintain'),
('Yoga Flow (Advanced)', 'Advanced yoga sequence with complex poses', 'flexibility', 'advanced', 'yoga mat', 45, 200, 'maintain'),
('Foam Rolling', 'Self-myofascial release using foam roller', 'flexibility', 'beginner', 'foam roller', 15, 60, 'maintain'),
('Dynamic Stretching', 'Movement-based stretching routine', 'flexibility', 'intermediate', 'none', 20, 80, 'maintain'),
('Pilates Core', 'Pilates exercises focusing on core strength', 'flexibility', 'intermediate', 'pilates mat', 25, 100, 'maintain');

-- =====================================================
-- 7. CREATE SAMPLE USER (Optional - for testing)
-- =====================================================

-- Insert a sample user for testing (password is hashed version of "password123")
INSERT IGNORE INTO `users` (`username`, `email`, `password`) VALUES 
('testuser', 'test@example.com', 'password123');

-- Get the user ID and create a profile
SET @sample_user_id = (SELECT `user_id` FROM `users` WHERE `email` = 'test@example.com' LIMIT 1);

INSERT IGNORE INTO `user_profiles` (`user_id`, `age`, `height_cm`, `weight_kg`, `activity_level`, `fitness_goal`) 
VALUES (@sample_user_id, 28, 175, 70, 'moderate', 'lose');

-- =====================================================
-- 8. VERIFY DATABASE SETUP
-- =====================================================

-- Show table information
SELECT 'Database Setup Complete!' as Status;
SELECT COUNT(*) as 'Total Exercises' FROM `exercises`;
SELECT COUNT(*) as 'Total Users' FROM `users`;
SELECT COUNT(*) as 'User Profiles' FROM `user_profiles`;

-- Show exercise distribution by category
SELECT 
    `muscle_group` as 'Muscle Group',
    `difficulty` as 'Difficulty',
    COUNT(*) as 'Exercise Count'
FROM `exercises` 
GROUP BY `muscle_group`, `difficulty`
ORDER BY `muscle_group`, `difficulty`;

-- =====================================================
-- NOTES FOR MYSQL WORKBENCH USAGE:
-- =====================================================
-- 1. Copy and paste this entire script into MySQL Workbench
-- 2. Execute the script (it will create database, tables, and sample data)
-- 3. The script is safe to run multiple times (uses IF NOT EXISTS)
-- 4. Update the database connection details in your Java application:
--    - Database: macromind
--    - Username: root (or your MySQL username)
--    - Password: (your MySQL password)
--    - URL: jdbc:mysql://localhost:3306/macromind
-- =====================================================