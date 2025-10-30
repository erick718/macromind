-- SQL Script to create workout plan tables for MacroMind fitness application

-- Create exercises table
CREATE TABLE IF NOT EXISTS exercises (
    exercise_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    muscle_group VARCHAR(50) NOT NULL,
    difficulty ENUM('beginner', 'intermediate', 'advanced') NOT NULL,
    equipment VARCHAR(100) DEFAULT 'none',
    duration_minutes INT NOT NULL,
    calories_burned INT NOT NULL,
    goal VARCHAR(50) DEFAULT 'general',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create workout_plans table
CREATE TABLE IF NOT EXISTS workout_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    goal ENUM('lose', 'gain', 'maintain') NOT NULL,
    difficulty ENUM('beginner', 'intermediate', 'advanced') NOT NULL,
    duration_weeks INT NOT NULL,
    sessions_per_week INT NOT NULL,
    total_calories_burned INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create workout_plan_exercises junction table
CREATE TABLE IF NOT EXISTS workout_plan_exercises (
    plan_id INT,
    exercise_id INT,
    PRIMARY KEY (plan_id, exercise_id),
    FOREIGN KEY (plan_id) REFERENCES workout_plans(plan_id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id) ON DELETE CASCADE
);

-- Insert sample exercises for different goals and difficulty levels

-- Cardio Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Running (Beginner)', 'Light jogging at comfortable pace', 'cardio', 'beginner', 'none', 20, 200, 'lose'),
('Running (Intermediate)', 'Moderate pace running', 'cardio', 'intermediate', 'none', 30, 350, 'lose'),
('Running (Advanced)', 'High-intensity running', 'cardio', 'advanced', 'none', 45, 500, 'lose'),
('Jumping Jacks', 'Full body cardio exercise', 'cardio', 'beginner', 'none', 10, 100, 'lose'),
('High Knees', 'Running in place with high knees', 'cardio', 'intermediate', 'none', 10, 120, 'lose'),
('Burpees', 'High-intensity full body exercise', 'cardio', 'advanced', 'none', 10, 200, 'lose'),
('Walking', 'Brisk walking exercise', 'cardio', 'beginner', 'none', 30, 150, 'maintain'),
('Cycling', 'Stationary or outdoor cycling', 'cardio', 'intermediate', 'bicycle', 30, 300, 'maintain'),
('Swimming', 'Full body swimming workout', 'cardio', 'advanced', 'pool', 30, 400, 'maintain');

-- Full Body Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Push-ups (Modified)', 'Knee push-ups for beginners', 'full_body', 'beginner', 'none', 10, 80, 'gain'),
('Push-ups (Standard)', 'Traditional push-ups', 'full_body', 'intermediate', 'none', 15, 120, 'gain'),
('Push-ups (Advanced)', 'Diamond or one-arm push-ups', 'full_body', 'advanced', 'none', 20, 180, 'gain'),
('Squats (Bodyweight)', 'Basic bodyweight squats', 'full_body', 'beginner', 'none', 15, 100, 'gain'),
('Squats (Weighted)', 'Squats with dumbbells or kettlebell', 'full_body', 'intermediate', 'dumbbells', 20, 150, 'gain'),
('Jump Squats', 'Explosive squat jumps', 'full_body', 'advanced', 'none', 15, 200, 'gain'),
('Mountain Climbers', 'Dynamic full body exercise', 'full_body', 'intermediate', 'none', 10, 150, 'lose'),
('Bear Crawls', 'Quadrupedal movement pattern', 'full_body', 'advanced', 'none', 10, 180, 'lose');

-- Core Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Plank (Modified)', 'Knee plank hold', 'core', 'beginner', 'none', 5, 40, 'gain'),
('Plank (Standard)', 'Traditional plank hold', 'core', 'intermediate', 'none', 8, 60, 'gain'),
('Plank (Advanced)', 'Single-arm or leg plank variations', 'core', 'advanced', 'none', 12, 100, 'gain'),
('Crunches', 'Basic abdominal crunches', 'core', 'beginner', 'none', 10, 60, 'lose'),
('Bicycle Crunches', 'Dynamic core rotation exercise', 'core', 'intermediate', 'none', 12, 90, 'lose'),
('Russian Twists', 'Seated core rotation exercise', 'core', 'advanced', 'none', 15, 120, 'lose'),
('Dead Bug', 'Core stability exercise', 'core', 'beginner', 'none', 8, 50, 'maintain'),
('Bird Dog', 'Core and back stability', 'core', 'intermediate', 'none', 10, 70, 'maintain');

-- Chest Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Wall Push-ups', 'Standing push-ups against wall', 'chest', 'beginner', 'none', 10, 60, 'gain'),
('Incline Push-ups', 'Push-ups with hands elevated', 'chest', 'beginner', 'bench', 12, 80, 'gain'),
('Chest Press (Light)', 'Dumbbell chest press with light weight', 'chest', 'intermediate', 'dumbbells', 15, 100, 'gain'),
('Chest Press (Heavy)', 'Heavy dumbbell or barbell chest press', 'chest', 'advanced', 'barbell', 20, 150, 'gain'),
('Chest Flyes', 'Dumbbell chest fly exercise', 'chest', 'intermediate', 'dumbbells', 15, 110, 'gain');

-- Back Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Resistance Band Rows', 'Seated rows with resistance band', 'back', 'beginner', 'resistance band', 12, 80, 'gain'),
('Bent-over Rows', 'Dumbbell bent-over rows', 'back', 'intermediate', 'dumbbells', 15, 120, 'gain'),
('Pull-ups (Assisted)', 'Pull-ups with assistance band', 'back', 'intermediate', 'pull-up bar', 10, 100, 'gain'),
('Pull-ups (Standard)', 'Unassisted pull-ups', 'back', 'advanced', 'pull-up bar', 15, 150, 'gain'),
('Superman', 'Back extension exercise', 'back', 'beginner', 'none', 10, 60, 'maintain');

-- Leg Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Wall Sit', 'Isometric squat hold against wall', 'legs', 'beginner', 'none', 5, 50, 'gain'),
('Lunges (Bodyweight)', 'Forward or reverse lunges', 'legs', 'beginner', 'none', 12, 90, 'gain'),
('Lunges (Weighted)', 'Lunges with dumbbells', 'legs', 'intermediate', 'dumbbells', 15, 130, 'gain'),
('Bulgarian Split Squats', 'Single-leg squat variation', 'legs', 'advanced', 'none', 15, 150, 'gain'),
('Calf Raises', 'Standing calf raises', 'legs', 'beginner', 'none', 10, 60, 'maintain'),
('Single-leg Glute Bridges', 'Hip bridge with one leg', 'legs', 'intermediate', 'none', 12, 80, 'gain');

-- Arms Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Arm Circles', 'Dynamic arm warm-up exercise', 'arms', 'beginner', 'none', 5, 30, 'maintain'),
('Tricep Dips (Chair)', 'Chair-assisted tricep dips', 'arms', 'beginner', 'chair', 10, 70, 'gain'),
('Tricep Dips (Bench)', 'Bench tricep dips', 'arms', 'intermediate', 'bench', 12, 90, 'gain'),
('Bicep Curls (Light)', 'Light dumbbell bicep curls', 'arms', 'beginner', 'dumbbells', 10, 60, 'gain'),
('Bicep Curls (Heavy)', 'Heavy dumbbell bicep curls', 'arms', 'intermediate', 'dumbbells', 15, 100, 'gain'),
('Hammer Curls', 'Neutral grip bicep curls', 'arms', 'intermediate', 'dumbbells', 15, 110, 'gain'),
('Tricep Extensions', 'Overhead tricep extensions', 'arms', 'advanced', 'dumbbells', 15, 120, 'gain');

-- Flexibility Exercises
INSERT INTO exercises (name, description, muscle_group, difficulty, equipment, duration_minutes, calories_burned, goal) VALUES
('Basic Stretching', 'General flexibility routine', 'flexibility', 'beginner', 'none', 15, 50, 'maintain'),
('Yoga Flow (Beginner)', 'Gentle yoga sequence', 'flexibility', 'beginner', 'yoga mat', 20, 80, 'maintain'),
('Yoga Flow (Intermediate)', 'Moderate yoga sequence', 'flexibility', 'intermediate', 'yoga mat', 30, 120, 'maintain'),
('Yoga Flow (Advanced)', 'Advanced yoga sequence', 'flexibility', 'advanced', 'yoga mat', 45, 200, 'maintain'),
('Foam Rolling', 'Self-myofascial release', 'flexibility', 'beginner', 'foam roller', 15, 60, 'maintain'),
('Dynamic Stretching', 'Movement-based stretching', 'flexibility', 'intermediate', 'none', 20, 80, 'maintain');