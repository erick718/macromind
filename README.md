# MacroMind - Workout Plan Generator

A comprehensive fitness web application built with Java, JSP, and MySQL that provides personalized workout plan generation based on user goals and fitness levels.

## Features

### 🏋️‍♀️ Workout Plan Generator
- **Goal-Based Plans**: Generate workout plans tailored to specific fitness goals:
  - **Weight Loss**: High-intensity cardio and metabolic exercises
  - **Strength & Muscle Building**: Progressive resistance training and compound movements
  - **Endurance & Maintenance**: Balanced cardio and flexibility training

- **Intelligent Plan Generation**: 
  - Considers user's age, activity level, and fitness goals
  - Automatically determines appropriate difficulty level (beginner/intermediate/advanced)
  - Sets optimal duration and frequency based on goals
  - Calculates total calorie burn per session

- **Comprehensive Exercise Database**: 
  - 60+ pre-loaded exercises across multiple categories
  - Exercises categorized by muscle groups: cardio, full-body, core, chest, back, legs, arms, flexibility
  - Detailed exercise information including duration, calories burned, equipment needed

### 📊 User Profile System
- Complete fitness profile setup (age, height, weight, activity level, goals)
- Profile-based workout customization
- User authentication and session management

### 💻 User Interface
- Modern, responsive design with custom CSS
- Interactive workout plan generator with visual goal selection
- Comprehensive plan viewing with exercise details and tips
- Dashboard with profile overview and quick actions

## Technical Architecture

### Backend Components
- **Models**: `User`, `WorkoutPlan`, `Exercise`
- **DAOs**: `UserDAO`, `WorkoutPlanDAO`, `ExerciseDAO`
- **Services**: `WorkoutPlanService` (intelligent plan generation logic)
- **Servlets**: `WorkoutPlanServlet`, `LoginServlet`, `RegisterServlet`, `ProfileServlet`

### Database Schema
- **users**: User account information
- **exercises**: Exercise database with categorization
- **workout_plans**: Generated workout plans
- **workout_plan_exercises**: Many-to-many relationship between plans and exercises

### Frontend
- **JSP Pages**: Dynamic content rendering with embedded Java
- **CSS Styling**: Custom responsive design
- **JavaScript**: Interactive elements and form handling

## Workout Plan Generation Algorithm

The system uses intelligent algorithms to generate personalized workout plans:

1. **Difficulty Assessment**: Based on age and activity level
2. **Goal-Specific Exercise Selection**:
   - Weight Loss: 60% cardio, 30% strength, 10% flexibility
   - Strength Building: 70% strength training, 20% cardio, 10% flexibility
   - Endurance: 70% cardio, 20% functional movements, 10% flexibility
3. **Duration & Frequency Optimization**: Varies by goal and difficulty level
4. **Calorie Calculation**: Automatic calculation based on selected exercises

## Database Setup

Run the provided SQL script to set up the database:

```sql
-- Execute workout_plan_setup.sql to create:
-- - exercises table with 60+ sample exercises
-- - workout_plans table for storing generated plans
-- - workout_plan_exercises junction table
```

## Usage Flow

1. **Registration/Login**: Users create accounts and log in
2. **Profile Setup**: Complete fitness profile with goals and metrics
3. **Plan Generation**: Select fitness goal and generate personalized plan
4. **Plan Management**: View, manage, and delete workout plans
5. **Exercise Execution**: Follow detailed exercise instructions with tips

## Key Features of Generated Plans

- **Personalization**: Based on individual user profiles
- **Goal-Oriented**: Specifically designed for weight loss, strength, or endurance
- **Progressive**: Appropriate difficulty progression
- **Comprehensive**: Includes exercise details, tips, and guidelines
- **Flexible**: Multiple plans can be generated and managed

## Technologies Used

- **Backend**: Java 8, Jakarta Servlets, JSP
- **Database**: MySQL with JDBC
- **Build Tool**: Maven
- **Frontend**: HTML5, CSS3, JavaScript
- **Server**: Compatible with Tomcat 10+

## Installation & Deployment

1. Clone the repository
2. Set up MySQL database and run `workout_plan_setup.sql`
3. Update database connection settings in `DBConnection.java`
4. Build with Maven: `mvn clean package`
5. Deploy the generated WAR file to Tomcat server
6. Access the application at `http://localhost:8080/macromind/`

## Future Enhancements

- Exercise video/image integration
- Progress tracking and analytics
- Social features and sharing
- Mobile app development
- Nutrition plan integration
- Wearable device integration

## Contributing

This project demonstrates modern web application development with Java EE technologies, implementing a complete fitness solution with intelligent workout plan generation.