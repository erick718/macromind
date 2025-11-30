# MacroMind - Fitness Tracking Application

A comprehensive web-based fitness tracking application built with Java Servlets, JSP, and MySQL. MacroMind helps users monitor their workouts, track food intake, analyze fitness progress, and generate personalized workout plans.

## Features

### Core Functionality
- **User Management**: 
  - Secure registration with BCrypt password hashing
  - Login/logout with session management
  - Profile management with profile picture upload
  - Security questions for password recovery
  - Password reset functionality
  - Account deletion option

- **Workout Planning & Logging**: 
  - AI-powered workout plan generation based on fitness goals (lose/gain/maintain weight)
  - Customizable difficulty levels (beginner, intermediate, advanced)
  - Track cardio and strength training exercises
  - Automatic calorie burn calculations based on MET values
  - Edit and update existing workout entries
  - Workout history with filtering and search

- **Food & Nutrition Tracking**: 
  - Log daily food intake with detailed macronutrient breakdown
  - Track calories, protein, carbohydrates, and fat
  - Food entry history with date-based filtering
  - USDA FoodData Central API integration for accurate nutrition data

- **Progress Analytics**:
  - Comprehensive progress dashboard with interactive charts
  - Calorie balance monitoring (intake vs. burned)
  - Daily fitness summaries with aggregated metrics
  - Date range filtering (7/30/90 days or custom ranges)
  - Exercise distribution analysis (cardio vs. strength training)
  - Visual progress tracking over time

### Security Features
- BCrypt password hashing for secure credential storage
- Security questions for account recovery
- Session-based authentication and authorization
- Protected routes requiring authentication
- Secure password reset workflow

### User Experience
- Clean, responsive design with custom CSS framework
- Password visibility toggle on all password fields
- Intuitive dashboard with quick access to all features
- Real-time form validation
- Error handling with user-friendly messages

## Technology Stack

- **Backend**: Java 17, Jakarta Servlets 5.0
- **Frontend**: JSP, HTML5, Custom CSS (responsive design)
- **Database**: MySQL 8.0 with optimized schema and indexes
- **Build Tool**: Maven 3.x
- **Testing**: JUnit 5, Mockito 5, AssertJ (284 comprehensive tests)
- **Security**: BCrypt for password hashing
- **Server**: Apache Tomcat 10.1.44 (Jakarta EE 9+ compatible)
- **APIs**: USDA FoodData Central for nutrition data