package com.fitness.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.fitness.model.User;
import com.fitness.model.Workout;
import com.fitness.util.CalorieCalculator.CalorieBalanceSummary;

@DisplayName("CalorieCalculator Tests")
class CalorieCalculatorTest {

    private User testUser;
    private Workout testWorkout;

    @BeforeEach
    void setUp() {
        // Create a test user with typical values
        testUser = new User();
        testUser.setWeight(70.0f); // 70 kg
        testUser.setHeight(175);  // 175 cm
        testUser.setAge(25);      // 25 years old
        testUser.setFitnessLevel("moderate");
        testUser.setGoal("maintain");
        
        // Create a test workout
        testWorkout = new Workout();
        testWorkout.setDurationMinutes(30);
        testWorkout.setExerciseType("cardio");
    }

    @Nested
    @DisplayName("MET Calorie Calculations")
    class METCalculationTests {

        @Test
        @DisplayName("Should calculate calories correctly using MET formula")
        void shouldCalculateCaloriesCorrectlyUsingMET() {
            // Given: Running (MET = 8.0) for 30 minutes at 70kg
            double metValue = 8.0;
            double weightKg = 70.0;
            int durationMinutes = 30;
            
            // When
            double result = CalorieCalculator.calculateCaloriesByMET(metValue, weightKg, durationMinutes);
            
            // Then: 8.0 * 70 * 0.5 = 280 calories
            assertThat(result).isEqualTo(280.0);
        }

        @ParameterizedTest
        @DisplayName("Should calculate calories for different MET values")
        @CsvSource({
            "3.0, 60, 30, 90.0",     // Walking (3 MET), 60kg, 30min = 90 calories
            "6.0, 80, 45, 360.0",    // Cycling (6 MET), 80kg, 45min = 360 calories
            "10.0, 70, 60, 700.0",   // Running (10 MET), 70kg, 60min = 700 calories
            "4.5, 65, 20, 97.5"      // Strength training (4.5 MET), 65kg, 20min = 97.5 calories
        })
        void shouldCalculateCaloriesForDifferentMETValues(double met, double weight, int duration, double expected) {
            double result = CalorieCalculator.calculateCaloriesByMET(met, weight, duration);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should handle zero duration")
        void shouldHandleZeroDuration() {
            double result = CalorieCalculator.calculateCaloriesByMET(8.0, 70.0, 0);
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Should handle negative inputs gracefully")
        void shouldHandleNegativeInputs() {
            // Negative MET should still calculate (could be used for cooling effect)
            double result = CalorieCalculator.calculateCaloriesByMET(-1.0, 70.0, 30);
            assertThat(result).isEqualTo(-35.0);
        }
    }

    @Nested
    @DisplayName("BMR Calculations")
    class BMRCalculationTests {

        @Test
        @DisplayName("Should calculate BMR for male correctly")
        void shouldCalculateBMRForMale() {
            // Given: Male, 70kg, 175cm, 25 years old
            // Expected: 10*70 + 6.25*175 - 5*25 + 5 = 700 + 1093.75 - 125 + 5 = 1673.75
            
            double result = CalorieCalculator.calculateBMR(testUser, true);
            
            assertThat(result).isEqualTo(1673.75);
        }

        @Test
        @DisplayName("Should calculate BMR for female correctly")
        void shouldCalculateBMRForFemale() {
            // Given: Female, 70kg, 175cm, 25 years old
            // Expected: 10*70 + 6.25*175 - 5*25 - 161 = 700 + 1093.75 - 125 - 161 = 1507.75
            
            double result = CalorieCalculator.calculateBMR(testUser, false);
            
            assertThat(result).isEqualTo(1507.75);
        }

        @Test
        @DisplayName("Should return default BMR for incomplete user data")
        void shouldReturnDefaultBMRForIncompleteData() {
            // Given: User with missing data
            User incompleteUser = new User();
            incompleteUser.setWeight(0);
            incompleteUser.setHeight(175);
            incompleteUser.setAge(25);
            
            double result = CalorieCalculator.calculateBMR(incompleteUser, true);
            
            assertThat(result).isEqualTo(1800.0);
        }

        @ParameterizedTest
        @DisplayName("Should calculate BMR for different ages")
        @CsvSource({
            "20, true, 1698.75",   // 20 year old male
            "30, true, 1648.75",   // 30 year old male
            "50, true, 1548.75",   // 50 year old male
            "20, false, 1532.75",  // 20 year old female
            "30, false, 1482.75",  // 30 year old female
            "50, false, 1382.75"   // 50 year old female
        })
        void shouldCalculateBMRForDifferentAges(int age, boolean isMale, double expected) {
            testUser.setAge(age);
            double result = CalorieCalculator.calculateBMR(testUser, isMale);
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("TDEE Calculations")
    class TDEECalculationTests {

        @Test
        @DisplayName("Should calculate TDEE for low activity level")
        void shouldCalculateTDEEForLowActivity() {
            double bmr = 1673.75;
            String activityLevel = "low";
            
            double result = CalorieCalculator.calculateTDEE(bmr, activityLevel);
            
            assertThat(result).isEqualTo(bmr * 1.2); // 2008.5
        }

        @Test
        @DisplayName("Should calculate TDEE for moderate activity level")
        void shouldCalculateTDEEForModerateActivity() {
            double bmr = 1673.75;
            String activityLevel = "moderate";
            
            double result = CalorieCalculator.calculateTDEE(bmr, activityLevel);
            
            assertThat(result).isEqualTo(bmr * 1.55); // 2594.3125
        }

        @Test
        @DisplayName("Should calculate TDEE for high activity level")
        void shouldCalculateTDEEForHighActivity() {
            double bmr = 1673.75;
            String activityLevel = "high";
            
            double result = CalorieCalculator.calculateTDEE(bmr, activityLevel);
            
            assertThat(result).isEqualTo(bmr * 1.725); // 2887.21875
        }

        @Test
        @DisplayName("Should use default activity factor for null activity level")
        void shouldUseDefaultActivityFactorForNull() {
            double bmr = 1673.75;
            
            double result = CalorieCalculator.calculateTDEE(bmr, null);
            
            assertThat(result).isEqualTo(bmr * 1.4); // 2343.25
        }

        @Test
        @DisplayName("Should use default activity factor for unknown activity level")
        void shouldUseDefaultActivityFactorForUnknown() {
            double bmr = 1673.75;
            
            double result = CalorieCalculator.calculateTDEE(bmr, "unknown");
            
            assertThat(result).isEqualTo(bmr * 1.4); // 2343.25
        }
    }

    @Nested
    @DisplayName("Enhanced Calorie Calculations")
    class EnhancedCalculationTests {

        @Test
        @DisplayName("Should calculate enhanced calories with all modifiers")
        void shouldCalculateEnhancedCaloriesWithModifiers() {
            // Given: Moderate fitness user doing 30-minute cardio
            double metValue = 6.0; // Moderate cycling
            testUser.setFitnessLevel("moderate");
            testUser.setAge(24); // Under 25 for the age modifier
            testWorkout.setDurationMinutes(30);
            testWorkout.setExerciseType("cardio");
            
            double result = CalorieCalculator.calculateEnhancedCalories(testUser, testWorkout, metValue, true);
            
            // Base calories: 6.0 * 70 * 0.5 = 210
            // Fitness modifier: 1.0 (moderate)
            // Age modifier: 1.05 (under 25)
            // Intensity modifier: 1.0 (normal duration, cardio)
            // Expected: 210 * 1.0 * 1.0 * 1.05 = 220.5
            assertThat(result).isEqualTo(220.5);
        }

        @Test
        @DisplayName("Should apply high fitness level modifier")
        void shouldApplyHighFitnessLevelModifier() {
            testUser.setFitnessLevel("high");
            double metValue = 6.0;
            
            double result = CalorieCalculator.calculateEnhancedCalories(testUser, testWorkout, metValue, true);
            
            // Should include 1.1 fitness modifier for high fitness level
            double baseCalories = 6.0 * 70 * 0.5; // 210
            assertThat(result).isGreaterThan(baseCalories * 1.05); // Should be > base * age modifier
        }

        @Test
        @DisplayName("Should apply age modifier for older adults")
        void shouldApplyAgeModifierForOlderAdults() {
            testUser.setAge(60);
            double metValue = 6.0;
            
            double result = CalorieCalculator.calculateEnhancedCalories(testUser, testWorkout, metValue, true);
            
            // Should apply 0.9 age modifier for 60+ years
            double baseCalories = 6.0 * 70 * 0.5; // 210
            double expectedWithAgeModifier = baseCalories * 1.0 * 1.0 * 0.9; // 189
            assertThat(result).isEqualTo(expectedWithAgeModifier);
        }
    }

    @Nested
    @DisplayName("Calorie Goal Adjustments")
    class CalorieGoalTests {

        @ParameterizedTest
        @DisplayName("Should calculate correct goal adjustments")
        @CsvSource({
            "lose, -500.0",
            "maintain, 0.0",
            "gain, 300.0",
            "unknown, 0.0"
        })
        void shouldCalculateCorrectGoalAdjustments(String goal, double expected) {
            double result = CalorieCalculator.calculateCalorieGoalAdjustment(2000, goal);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should handle null goal")
        void shouldHandleNullGoal() {
            double result = CalorieCalculator.calculateCalorieGoalAdjustment(2000, null);
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("Recommended Daily Intake")
    class RecommendedIntakeTests {

        @Test
        @DisplayName("Should calculate recommended daily intake for weight loss")
        void shouldCalculateRecommendedIntakeForWeightLoss() {
            testUser.setGoal("lose");
            
            double result = CalorieCalculator.calculateRecommendedDailyIntake(testUser, true);
            
            double expectedBMR = CalorieCalculator.calculateBMR(testUser, true);
            double expectedTDEE = CalorieCalculator.calculateTDEE(expectedBMR, "moderate");
            double expected = expectedTDEE - 500; // Deficit for weight loss
            
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should calculate recommended daily intake for muscle gain")
        void shouldCalculateRecommendedIntakeForMuscleGain() {
            testUser.setGoal("gain");
            
            double result = CalorieCalculator.calculateRecommendedDailyIntake(testUser, true);
            
            double expectedBMR = CalorieCalculator.calculateBMR(testUser, true);
            double expectedTDEE = CalorieCalculator.calculateTDEE(expectedBMR, "moderate");
            double expected = expectedTDEE + 300; // Surplus for muscle gain
            
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Weekly Balance Summary")
    class WeeklyBalanceTests {

        @Test
        @DisplayName("Should calculate weekly balance summary correctly")
        void shouldCalculateWeeklyBalanceSummary() {
            // Given: Weekly data
            double totalBurned = 2100; // 300 calories/day * 7 days
            double totalIntake = 14000; // 2000 calories/day * 7 days
            double recommendedDaily = 2000;
            int days = 7;
            
            CalorieBalanceSummary result = CalorieCalculator.calculateWeeklyBalance(
                totalBurned, totalIntake, recommendedDaily, days);
            
            assertThat(result.getTotalBurned()).isEqualTo(2100);
            assertThat(result.getTotalIntake()).isEqualTo(14000);
            assertThat(result.getActualBalance()).isEqualTo(11900); // 14000 - 2100
            assertThat(result.getRecommendedIntake()).isEqualTo(14000); // 2000 * 7
            assertThat(result.getRecommendedBalance()).isEqualTo(11900); // 14000 - 2100
            assertThat(result.getBalanceDifference()).isZero(); // Perfect balance
            assertThat(result.isOnTrack()).isTrue();
            assertThat(result.getBalanceStatus()).isEqualTo("On Track");
        }

        @Test
        @DisplayName("Should detect when above target")
        void shouldDetectWhenAboveTarget() {
            CalorieBalanceSummary result = CalorieCalculator.calculateWeeklyBalance(
                2000, 15000, 2000, 7); // 1000 extra calories consumed
            
            assertThat(result.getBalanceDifference()).isEqualTo(1000);
            assertThat(result.isOnTrack()).isFalse();
            assertThat(result.getBalanceStatus()).isEqualTo("Above Target");
        }

        @Test
        @DisplayName("Should detect when below target")
        void shouldDetectWhenBelowTarget() {
            CalorieBalanceSummary result = CalorieCalculator.calculateWeeklyBalance(
                2000, 13000, 2000, 7); // 1000 calories under consumed
            
            assertThat(result.getBalanceDifference()).isEqualTo(-1000);
            assertThat(result.isOnTrack()).isFalse();
            assertThat(result.getBalanceStatus()).isEqualTo("Below Target");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle extreme values gracefully")
        void shouldHandleExtremeValues() {
            User extremeUser = new User();
            extremeUser.setWeight(200.0f); // Very heavy
            extremeUser.setHeight(150);   // Short
            extremeUser.setAge(80);       // Elderly
            extremeUser.setFitnessLevel("low");
            extremeUser.setGoal("lose");
            
            assertThatCode(() -> {
                CalorieCalculator.calculateBMR(extremeUser, true);
                CalorieCalculator.calculateRecommendedDailyIntake(extremeUser, true);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle zero weight gracefully")
        void shouldHandleZeroWeight() {
            testUser.setWeight(0);
            
            double result = CalorieCalculator.calculateBMR(testUser, true);
            
            assertThat(result).isEqualTo(1800.0); // Default BMR
        }

        @Test
        @DisplayName("Should clamp intensity modifier within bounds")
        void shouldClampIntensityModifierWithinBounds() {
            Workout extremeWorkout = new Workout();
            extremeWorkout.setDurationMinutes(180); // Very long workout
            extremeWorkout.setExerciseType("strength");
            // Would normally create a very high modifier, but should be clamped
            
            double result = CalorieCalculator.calculateEnhancedCalories(testUser, extremeWorkout, 6.0, true);
            
            // Should not be more than 1.3 times the base calculation
            double baseCalories = CalorieCalculator.calculateCaloriesByMET(6.0, 70.0, 180);
            assertThat(result).isLessThanOrEqualTo(baseCalories * 1.3 * 1.05); // Max modifier * age modifier
        }
    }
}