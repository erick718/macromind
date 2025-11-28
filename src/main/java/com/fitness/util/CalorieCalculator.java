package com.fitness.util;

import com.fitness.model.User;
import com.fitness.model.Workout;

public class CalorieCalculator {
    
    /**
     * Calculate calories burned using MET (Metabolic Equivalent of Task) formula
     * Formula: Calories = MET × weight (kg) × duration (hours)
     * 
     * @param metValue MET value for the exercise
     * @param weightKg User's weight in kilograms
     * @param durationMinutes Exercise duration in minutes
     * @return Calories burned
     */
    public static double calculateCaloriesByMET(double metValue, double weightKg, int durationMinutes) {
        double durationHours = durationMinutes / 60.0;
        return metValue * weightKg * durationHours;
    }
    
    /**
     * Calculate Basal Metabolic Rate using Mifflin-St Jeor Equation
     * BMR = 10 × weight(kg) + 6.25 × height(cm) - 5 × age(years) + gender_factor
     * Gender factor: +5 for men, -161 for women
     * 
     * @param user User object with weight, height, and age
     * @param isMale true if male, false if female
     * @return BMR in calories per day
     */
    public static double calculateBMR(User user, boolean isMale) {
        if (user.getWeight() <= 0 || user.getHeight() <= 0 || user.getAge() <= 0) {
            return 1800; // Default BMR if data is incomplete
        }
        
        double bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge();
        return isMale ? bmr + 5 : bmr - 161;
    }
    
    /**
     * Calculate Total Daily Energy Expenditure (TDEE)
     * TDEE = BMR × Activity Factor
     * 
     * @param bmr Basal Metabolic Rate
     * @param activityLevel User's activity level (low, moderate, high)
     * @return TDEE in calories per day
     */
    public static double calculateTDEE(double bmr, String activityLevel) {
        double activityFactor;
        
        if (activityLevel == null) {
            activityFactor = 1.4; // Default to lightly active
        } else {
            switch (activityLevel.toLowerCase()) {
                case "low":
                    activityFactor = 1.2; // Sedentary
                    break;
                case "moderate":
                    activityFactor = 1.55; // Moderately active
                    break;
                case "high":
                    activityFactor = 1.725; // Very active
                    break;
                default:
                    activityFactor = 1.4; // Lightly active
            }
        }
        
        return bmr * activityFactor;
    }
    
    /**
     * Enhanced calorie calculation that considers user's fitness level and body composition
     * 
     * @param user User object
     * @param workout Workout object
     * @param metValue MET value for the exercise
     * @param isMale Gender for BMR calculation
     * @return Enhanced calorie calculation
     */
    public static double calculateEnhancedCalories(User user, Workout workout, double metValue, boolean isMale) {
        // Base MET calculation
        double baseCalories = calculateCaloriesByMET(metValue, user.getWeight(), workout.getDurationMinutes());
        
        // Fitness level modifier
        double fitnessModifier = getFitnessLevelModifier(user.getFitnessLevel());
        
        // Exercise intensity modifier based on workout details
        double intensityModifier = getIntensityModifier(workout);
        
        // Age modifier (metabolism slows with age)
        double ageModifier = getAgeModifier(user.getAge());
        
        return baseCalories * fitnessModifier * intensityModifier * ageModifier;
    }
    
    /**
     * Get fitness level modifier for calorie calculation
     * More fit individuals burn calories more efficiently but may have lower resting burn
     */
    private static double getFitnessLevelModifier(String fitnessLevel) {
        if (fitnessLevel == null) return 1.0;
        
        switch (fitnessLevel.toLowerCase()) {
            case "low":
                return 0.95; // Less efficient, but higher baseline burn
            case "moderate":
                return 1.0; // Baseline
            case "high":
                return 1.1; // More efficient exercise, higher intensity capability
            default:
                return 1.0;
        }
    }
    
    /**
     * Get intensity modifier based on workout characteristics
     */
    private static double getIntensityModifier(Workout workout) {
        // Base modifier on duration and type
        double modifier = 1.0;
        
        // Duration modifier
        if (workout.getDurationMinutes() > 60) {
            modifier += 0.1; // Bonus for longer workouts
        } else if (workout.getDurationMinutes() < 15) {
            modifier -= 0.1; // Penalty for very short workouts
        }
        
        // Strength training volume modifier
        if ("strength".equalsIgnoreCase(workout.getExerciseType())) {
            double volume = workout.getTotalVolume();
            if (volume > 1000) { // High volume strength training
                modifier += 0.15;
            } else if (volume > 500) {
                modifier += 0.1;
            }
        }
        
        return Math.max(0.8, Math.min(1.3, modifier)); // Clamp between 0.8 and 1.3
    }
    
    /**
     * Get age modifier for metabolism
     */
    private static double getAgeModifier(int age) {
        if (age <= 0) return 1.0;
        
        if (age < 25) {
            return 1.05; // Higher metabolism when young
        } else if (age < 40) {
            return 1.0; // Baseline
        } else if (age < 55) {
            return 0.95; // Slightly slower metabolism
        } else {
            return 0.9; // Slower metabolism in older adults
        }
    }
    
    /**
     * Calculate calorie deficit/surplus for weight goals
     * 
     * @param currentCalorieBalance Current calorie balance (intake - burned)
     * @param goal User's fitness goal (lose, maintain, gain)
     * @return Recommended daily calorie adjustment
     */
    public static double calculateCalorieGoalAdjustment(double currentCalorieBalance, String goal) {
        if (goal == null) return 0;
        
        switch (goal.toLowerCase()) {
            case "lose":
                return -500; // 500 calorie deficit for ~1 lb/week weight loss
            case "maintain":
                return 0; // Maintain current balance
            case "gain":
                return 300; // 300 calorie surplus for lean muscle gain
            default:
                return 0;
        }
    }
    
    /**
     * Calculate recommended daily calorie intake based on goals
     * 
     * @param user User object
     * @param isMale Gender for BMR calculation
     * @return Recommended daily calorie intake
     */
    public static double calculateRecommendedDailyIntake(User user, boolean isMale) {
        double bmr = calculateBMR(user, isMale);
        double tdee = calculateTDEE(bmr, user.getFitnessLevel());
        double adjustment = calculateCalorieGoalAdjustment(0, user.getGoal());
        
        return tdee + adjustment;
    }
    
    /**
     * Calculate weekly calorie balance summary
     * 
     * @param totalCaloriesBurned Total calories burned this week
     * @param totalCaloriesIntake Total calories consumed this week (if tracked)
     * @param recommendedDailyIntake Recommended daily calorie intake
     * @param days Number of days in the period
     * @return Weekly calorie balance analysis
     */
    public static CalorieBalanceSummary calculateWeeklyBalance(
            double totalCaloriesBurned, 
            double totalCaloriesIntake, 
            double recommendedDailyIntake, 
            int days) {
        
        double recommendedTotalIntake = recommendedDailyIntake * days;
        double actualBalance = totalCaloriesIntake - totalCaloriesBurned;
        double recommendedBalance = recommendedTotalIntake - totalCaloriesBurned;
        double balanceDifference = actualBalance - recommendedBalance;
        
        return new CalorieBalanceSummary(
            totalCaloriesBurned,
            totalCaloriesIntake,
            actualBalance,
            recommendedBalance,
            balanceDifference,
            recommendedTotalIntake
        );
    }
    
    /**
     * Inner class to hold calorie balance summary data
     */
    public static class CalorieBalanceSummary {
        private final double totalBurned;
        private final double totalIntake;
        private final double actualBalance;
        private final double recommendedBalance;
        private final double balanceDifference;
        private final double recommendedIntake;
        
        public CalorieBalanceSummary(double totalBurned, double totalIntake, 
                                   double actualBalance, double recommendedBalance, 
                                   double balanceDifference, double recommendedIntake) {
            this.totalBurned = totalBurned;
            this.totalIntake = totalIntake;
            this.actualBalance = actualBalance;
            this.recommendedBalance = recommendedBalance;
            this.balanceDifference = balanceDifference;
            this.recommendedIntake = recommendedIntake;
        }
        
        // Getters
        public double getTotalBurned() { return totalBurned; }
        public double getTotalIntake() { return totalIntake; }
        public double getActualBalance() { return actualBalance; }
        public double getRecommendedBalance() { return recommendedBalance; }
        public double getBalanceDifference() { return balanceDifference; }
        public double getRecommendedIntake() { return recommendedIntake; }
        
        public boolean isOnTrack() {
            return Math.abs(balanceDifference) <= 100; // Within 100 calories is "on track"
        }
        
        public String getBalanceStatus() {
            if (isOnTrack()) {
                return "On Track";
            } else if (balanceDifference > 100) {
                return "Above Target";
            } else {
                return "Below Target";
            }
        }
    }
}