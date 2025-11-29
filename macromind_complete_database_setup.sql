-- =====================================================
-- MacroMind Fitness Application - Complete Database Setup
-- This script creates all necessary tables and columns for the application
-- Run this script in MySQL Workbench to set up the complete database
-- Last Updated: November 28, 2025
-- =====================================================

-- Create the database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS `macromind` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- Use the database
USE `macromind`;

-- Drop existing tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS `workout_plan_exercises`;
DROP TABLE IF EXISTS `workout_plans`;
DROP TABLE IF EXISTS `daily_fitness_summary`;
DROP TABLE IF EXISTS `workouts`;
DROP TABLE IF EXISTS `food_entries`;
DROP TABLE IF EXISTS `exercise_types`;
DROP TABLE IF EXISTS `exercises`;
DROP TABLE IF EXISTS `users`;

-- =====================================================
-- 1. USERS TABLE (Core user authentication and profile)
-- =====================================================
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `age` INT DEFAULT NULL,
    `weight` FLOAT DEFAULT NULL,
    `height` INT DEFAULT NULL,
    `goal` VARCHAR(50) DEFAULT NULL,
    `dietary_preference` VARCHAR(100) DEFAULT NULL,
    `fitness_level` VARCHAR(50) DEFAULT NULL,
    `availability` INT DEFAULT NULL,
    `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX `idx_email` (`email`),
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. EXERCISES TABLE (Exercise database for workout plans)
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
-- 3. WORKOUT PLANS TABLE (Generated workout plans)
-- =====================================================
CREATE TABLE IF NOT EXISTS `workout_plans` (
    `plan_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `plan_name` VARCHAR(100) NOT NULL,
    `goal` VARCHAR(50) NOT NULL,
    `difficulty` VARCHAR(50) NOT NULL,
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
-- 4. WORKOUT PLAN EXERCISES TABLE (Many-to-many relationship)
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
-- 5. WORKOUTS TABLE (User workout logs)
-- =====================================================
CREATE TABLE IF NOT EXISTS `workouts` (
    `workout_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `exercise_name` VARCHAR(100) NOT NULL,
    `exercise_type` VARCHAR(50) NOT NULL,
    `sets_count` INT DEFAULT 0,
    `reps_per_set` INT DEFAULT 0,
    `weight_kg` DOUBLE DEFAULT 0.0,
    `duration_minutes` INT NOT NULL,
    `calories_burned` DOUBLE DEFAULT 0.0,
    `workout_date` DATE NOT NULL,
    `workout_time` TIME DEFAULT NULL,
    `notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_workout_date` (`workout_date`),
    INDEX `idx_user_date` (`user_id`, `workout_date`),
    INDEX `idx_exercise_type` (`exercise_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. EXERCISE TYPES TABLE (Exercise MET values for calorie calculation)
-- =====================================================
CREATE TABLE IF NOT EXISTS `exercise_types` (
    `exercise_type_id` INT AUTO_INCREMENT PRIMARY KEY,
    `exercise_name` VARCHAR(100) NOT NULL UNIQUE,
    `category` VARCHAR(50) NOT NULL,
    `met_value` DOUBLE NOT NULL,
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX `idx_exercise_name` (`exercise_name`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. DAILY FITNESS SUMMARY TABLE (Aggregated daily workout data)
-- =====================================================
CREATE TABLE IF NOT EXISTS `daily_fitness_summary` (
    `summary_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `summary_date` DATE NOT NULL,
    `total_workouts` INT DEFAULT 0,
    `total_duration_minutes` INT DEFAULT 0,
    `total_calories_burned` DOUBLE DEFAULT 0.0,
    `calories_intake` DOUBLE DEFAULT 0.0,
    `calorie_balance` DOUBLE DEFAULT 0.0,
    `workout_streak_days` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_user_date` (`user_id`, `summary_date`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_summary_date` (`summary_date`),
    INDEX `idx_user_date` (`user_id`, `summary_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. FOOD ENTRIES TABLE (User food intake logs)
-- =====================================================
CREATE TABLE IF NOT EXISTS `food_entries` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `food_name` VARCHAR(100) NOT NULL,
    `calories` INT NOT NULL,
    `protein` FLOAT NOT NULL,
    `carbs` FLOAT NOT NULL,
    `fat` FLOAT NOT NULL,
    `consumed_oz` DOUBLE NOT NULL,
    `date_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_date_time` (`date_time`),
    INDEX `idx_user_date` (`user_id`, `date_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SAMPLE DATA - EXERCISE TYPES (MET Values)
-- =====================================================
INSERT INTO `exercise_types` (`exercise_name`, `category`, `met_value`, `description`) VALUES
('Running (8 mph)', 'Cardio', 11.5, 'Running at a vigorous pace'),
('Running (6 mph)', 'Cardio', 10.0, 'Running at a moderate pace'),
('Running (5 mph)', 'Cardio', 8.3, 'Running at a light pace'),
('Cycling (16-19 mph)', 'Cardio', 12.0, 'Vigorous stationary cycling'),
('Cycling (12-14 mph)', 'Cardio', 8.0, 'Moderate cycling'),
('Swimming (vigorous)', 'Cardio', 10.0, 'Vigorous swimming laps'),
('Swimming (moderate)', 'Cardio', 8.0, 'Moderate swimming'),
('Walking (4 mph)', 'Cardio', 5.0, 'Brisk walking'),
('Walking (3 mph)', 'Cardio', 3.5, 'Moderate walking'),
('Walking (2 mph)', 'Cardio', 2.5, 'Slow walking'),
('Elliptical (moderate)', 'Cardio', 5.0, 'Moderate elliptical training'),
('Elliptical (vigorous)', 'Cardio', 7.0, 'Vigorous elliptical training'),
('Jump Rope', 'Cardio', 12.3, 'Jumping rope'),
('Rowing Machine (vigorous)', 'Cardio', 12.0, 'Vigorous rowing'),
('Rowing Machine (moderate)', 'Cardio', 7.0, 'Moderate rowing'),
('Stair Climbing', 'Cardio', 8.8, 'Climbing stairs'),
('Weight Lifting (vigorous)', 'Strength', 6.0, 'Vigorous weight lifting'),
('Weight Lifting (moderate)', 'Strength', 3.5, 'Moderate weight lifting'),
('Bodyweight Exercises', 'Strength', 8.0, 'Calisthenics, vigorous effort'),
('Push-ups, Sit-ups', 'Strength', 3.8, 'Light calisthenics'),
('Yoga (Hatha)', 'Flexibility', 2.5, 'General yoga practice'),
('Yoga (Power)', 'Flexibility', 4.0, 'Vigorous power yoga'),
('Pilates', 'Flexibility', 3.0, 'Pilates exercises'),
('Stretching', 'Flexibility', 2.3, 'General stretching'),
('HIIT Training', 'Cardio', 12.0, 'High-intensity interval training'),
('Boxing', 'Cardio', 9.0, 'Boxing training'),
('Basketball', 'Sports', 6.5, 'Playing basketball'),
('Soccer', 'Sports', 10.0, 'Playing soccer'),
('Tennis', 'Sports', 7.3, 'Playing tennis')
ON DUPLICATE KEY UPDATE 
    `category` = VALUES(`category`),
    `met_value` = VALUES(`met_value`),
    `description` = VALUES(`description`);

-- =====================================================
-- SAMPLE DATA - EXERCISES FOR WORKOUT PLANS
-- =====================================================

-- Beginner exercises for weight loss
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Brisk Walking', 'Walk at a steady, brisk pace', 'Full Body', 'beginner', 'none', 30, 150, 'lose'),
('Bodyweight Squats', 'Basic squats without weights', 'Legs', 'beginner', 'none', 15, 80, 'lose'),
('Wall Push-ups', 'Push-ups against a wall', 'Chest', 'beginner', 'none', 10, 50, 'lose'),
('Seated Leg Raises', 'Leg raises while seated', 'Abs', 'beginner', 'chair', 10, 40, 'lose'),
('Arm Circles', 'Large arm circles for shoulder mobility', 'Shoulders', 'beginner', 'none', 10, 30, 'lose')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- Intermediate exercises for weight loss
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Jogging', 'Light to moderate jogging', 'Full Body', 'intermediate', 'none', 30, 300, 'lose'),
('Jump Squats', 'Explosive squat jumps', 'Legs', 'intermediate', 'none', 15, 150, 'lose'),
('Push-ups', 'Standard push-ups', 'Chest', 'intermediate', 'none', 15, 100, 'lose'),
('Mountain Climbers', 'Running motion in plank position', 'Abs', 'intermediate', 'none', 15, 120, 'lose'),
('Burpees', 'Full body explosive movement', 'Full Body', 'intermediate', 'none', 15, 180, 'lose'),
('Jumping Jacks', 'Classic jumping jacks', 'Full Body', 'intermediate', 'none', 15, 100, 'lose')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- Advanced exercises for weight loss
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Running', 'Moderate to fast running', 'Full Body', 'advanced', 'none', 30, 400, 'lose'),
('Jump Rope', 'High-intensity jump rope', 'Full Body', 'advanced', 'jump rope', 20, 300, 'lose'),
('High Knees', 'Running in place with high knees', 'Legs', 'advanced', 'none', 15, 180, 'lose'),
('Plyometric Push-ups', 'Explosive push-ups', 'Chest', 'advanced', 'none', 15, 150, 'lose'),
('Box Jumps', 'Jumping onto elevated platform', 'Legs', 'advanced', 'box', 15, 200, 'lose')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- Beginner exercises for strength/muscle gain
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Dumbbell Curls', 'Basic bicep curls', 'Arms', 'beginner', 'dumbbells', 15, 60, 'gain'),
('Dumbbell Press', 'Shoulder press with dumbbells', 'Shoulders', 'beginner', 'dumbbells', 15, 80, 'gain'),
('Goblet Squats', 'Squats holding a weight', 'Legs', 'beginner', 'dumbbell', 15, 90, 'gain'),
('Dumbbell Rows', 'Bent-over rows', 'Back', 'beginner', 'dumbbells', 15, 85, 'gain'),
('Bench Press', 'Basic bench press', 'Chest', 'beginner', 'barbell', 20, 120, 'gain')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- Intermediate exercises for strength/muscle gain
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Barbell Squats', 'Back squats with barbell', 'Legs', 'intermediate', 'barbell', 20, 150, 'gain'),
('Deadlifts', 'Conventional deadlifts', 'Back', 'intermediate', 'barbell', 20, 160, 'gain'),
('Incline Bench Press', 'Upper chest press', 'Chest', 'intermediate', 'barbell', 20, 140, 'gain'),
('Pull-ups', 'Standard pull-ups', 'Back', 'intermediate', 'pull-up bar', 15, 120, 'gain'),
('Dips', 'Parallel bar dips', 'Chest', 'intermediate', 'dip bars', 15, 110, 'gain'),
('Overhead Press', 'Military press', 'Shoulders', 'intermediate', 'barbell', 15, 130, 'gain')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- Advanced exercises for strength/muscle gain
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Heavy Squats', 'High-weight back squats', 'Legs', 'advanced', 'barbell', 25, 200, 'gain'),
('Heavy Deadlifts', 'Heavy conventional deadlifts', 'Back', 'advanced', 'barbell', 25, 220, 'gain'),
('Weighted Pull-ups', 'Pull-ups with added weight', 'Back', 'advanced', 'weight belt', 20, 180, 'gain'),
('Heavy Bench Press', 'High-weight bench press', 'Chest', 'advanced', 'barbell', 25, 190, 'gain'),
('Front Squats', 'Squats with bar in front', 'Legs', 'advanced', 'barbell', 20, 180, 'gain')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- General/Maintenance exercises (suitable for all goals)
INSERT INTO `exercises` (`name`, `description`, `muscle_group`, `difficulty`, `equipment`, `duration_minutes`, `calories_burned`, `goal`) VALUES
('Plank', 'Core stabilization exercise', 'Abs', 'beginner', 'none', 10, 50, 'general'),
('Side Plank', 'Lateral core stability', 'Abs', 'intermediate', 'none', 10, 60, 'general'),
('Lunges', 'Forward lunges', 'Legs', 'beginner', 'none', 15, 90, 'general'),
('Bicycle Crunches', 'Alternating elbow to knee crunches', 'Abs', 'beginner', 'none', 10, 60, 'general'),
('Superman Exercise', 'Back extension exercise', 'Back', 'beginner', 'none', 10, 45, 'general'),
('Yoga Flow', 'Basic yoga sequence', 'Full Body', 'beginner', 'yoga mat', 30, 120, 'general'),
('Swimming', 'Freestyle swimming', 'Full Body', 'intermediate', 'pool', 30, 250, 'general')
ON DUPLICATE KEY UPDATE 
    `description` = VALUES(`description`),
    `calories_burned` = VALUES(`calories_burned`);

-- =====================================================
-- DATABASE SETUP COMPLETE
-- =====================================================

-- Display table summary
SELECT 'Database setup complete!' AS Status;
SELECT COUNT(*) AS 'Total Exercises' FROM exercises;
SELECT COUNT(*) AS 'Total Exercise Types' FROM exercise_types;

-- Show table structure
SHOW TABLES;
