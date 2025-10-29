-- Create workouts table for MacroMind fitness tracking application
CREATE TABLE workouts (
    workout_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    exercise_type VARCHAR(50) NOT NULL, -- 'cardio', 'strength', 'flexibility', 'sports'
    sets_count INT DEFAULT 0,
    reps_per_set INT DEFAULT 0,
    weight_kg DECIMAL(5,2) DEFAULT 0.0,
    duration_minutes INT DEFAULT 0,
    calories_burned DECIMAL(6,2) DEFAULT 0.0,
    workout_date DATE NOT NULL,
    workout_time TIME DEFAULT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Index for faster queries
    INDEX idx_user_date (user_id, workout_date),
    INDEX idx_exercise_type (exercise_type)
);

-- Create table for exercise types and their calorie burn rates (MET values)
CREATE TABLE exercise_types (
    exercise_type_id INT AUTO_INCREMENT PRIMARY KEY,
    exercise_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL, -- 'cardio', 'strength', 'flexibility', 'sports'
    met_value DECIMAL(4,2) NOT NULL, -- Metabolic Equivalent of Task
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert common exercises with their MET values
INSERT INTO exercise_types (exercise_name, category, met_value, description) VALUES
-- Cardio exercises
('Running (6 mph)', 'cardio', 10.0, 'Running at 6 miles per hour pace'),
('Walking (3.5 mph)', 'cardio', 4.3, 'Brisk walking at 3.5 miles per hour'),
('Cycling (moderate)', 'cardio', 8.0, 'Cycling at moderate intensity, 12-14 mph'),
('Swimming (moderate)', 'cardio', 8.0, 'Swimming laps at moderate pace'),
('Elliptical', 'cardio', 7.0, 'Elliptical machine at moderate intensity'),
('Jump Rope', 'cardio', 12.3, 'Jumping rope at moderate pace'),
('Rowing Machine', 'cardio', 7.0, 'Rowing machine at moderate intensity'),

-- Strength training
('Weight Lifting (moderate)', 'strength', 6.0, 'General weight lifting, moderate intensity'),
('Weight Lifting (vigorous)', 'strength', 8.0, 'Vigorous weight lifting or bodybuilding'),
('Push-ups', 'strength', 3.8, 'Standard push-ups'),
('Pull-ups', 'strength', 8.0, 'Pull-ups or chin-ups'),
('Squats (bodyweight)', 'strength', 5.0, 'Bodyweight squats'),
('Deadlifts', 'strength', 6.0, 'Deadlifts with weights'),
('Bench Press', 'strength', 6.0, 'Bench press exercise'),

-- Flexibility and other
('Yoga (Hatha)', 'flexibility', 2.5, 'Hatha yoga, gentle stretching'),
('Yoga (Vinyasa)', 'flexibility', 3.0, 'Vinyasa or power yoga'),
('Stretching', 'flexibility', 2.3, 'General stretching exercises'),
('Pilates', 'flexibility', 3.0, 'Pilates exercises'),

-- Sports
('Basketball', 'sports', 8.0, 'Playing basketball, general'),
('Tennis', 'sports', 7.3, 'Playing tennis, singles'),
('Soccer', 'sports', 10.0, 'Playing soccer, competitive'),
('Martial Arts', 'sports', 10.3, 'Martial arts training');

-- Create daily fitness summary table for tracking progress
CREATE TABLE daily_fitness_summary (
    summary_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    summary_date DATE NOT NULL,
    total_workouts INT DEFAULT 0,
    total_duration_minutes INT DEFAULT 0,
    total_calories_burned DECIMAL(8,2) DEFAULT 0.0,
    calories_intake DECIMAL(8,2) DEFAULT 0.0, -- For future nutrition tracking
    calorie_balance DECIMAL(8,2) DEFAULT 0.0, -- calories_intake - calories_burned
    workout_streak_days INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, summary_date),
    
    -- Index for faster queries
    INDEX idx_user_summary_date (user_id, summary_date)
);