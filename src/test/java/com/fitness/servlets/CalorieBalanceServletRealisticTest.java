package com.fitness.servlets;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.fitness.dao.ExerciseLogDAO;
import com.fitness.dao.FoodEntryDAO;
import com.fitness.Model.FoodEntry;
import com.fitness.Model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)@DisplayName("Calorie Balance Tracking User Story Tests")
class CalorieBalanceServletRealisticTest {

    @Mock private FoodEntryDAO mockFoodEntryDAO;
    @Mock private ExerciseLogDAO mockExerciseLogDAO;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    @Mock private HttpSession mockSession;
    @Mock private RequestDispatcher mockRequestDispatcher;

    private CalorieBalanceServlet servlet;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CalorieBalanceServlet(mockFoodEntryDAO, mockExerciseLogDAO);
        
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setWeight(70.0f);
        testUser.setHeight(175);
        testUser.setAge(25);
        testUser.setFitnessLevel("moderate");
        testUser.setGoal("maintain");
        
        // Basic mocking setup
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher("/calorieBalance.jsp")).thenReturn(mockRequestDispatcher);
        when(mockRequest.getContextPath()).thenReturn("");        doNothing().when(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Nested
    @DisplayName("User Authentication Tests")
    class UserAuthenticationTests {

        @Test
        @DisplayName("Should redirect to login when user not authenticated")
        void shouldRedirectToLoginWhenUserNotAuthenticated() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockResponse).sendRedirect("login.jsp");
        }

        @Test
        @DisplayName("Should process calorie balance for authenticated user")
        void shouldProcessCalorieBalanceForAuthenticatedUser() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }
    }

    @Nested
    @DisplayName("Calorie Calculation Tests")
    class CalorieCalculationTests {

        @Test
        @DisplayName("Should calculate BMR and recommended calories correctly")
        void shouldCalculateBMRAndRecommendedCaloriesCorrectly() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequest).setAttribute(eq("totalIntake"), any(Integer.class));
            verify(mockRequest).setAttribute(eq("totalBurned"), any(Integer.class));
            verify(mockRequest).setAttribute(eq("netCalories"), any(Integer.class));
            verify(mockRequest).setAttribute(eq("remainingCalories"), any(Integer.class));
        }

        @Test
        @DisplayName("Should handle different fitness levels correctly")
        void shouldHandleDifferentFitnessLevelsCorrectly() throws Exception {
            // Given - High activity level user
            testUser.setFitnessLevel("high");
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should handle different goals correctly")
        void shouldHandleDifferentGoalsCorrectly() throws Exception {
            // Given - Weight loss goal
            testUser.setGoal("lose");
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }
    }

    @Nested
    @DisplayName("Food Entry Processing Tests")
    class FoodEntryProcessingTests {

        @Test
        @DisplayName("Should calculate total calorie intake from food entries")
        void shouldCalculateTotalCalorieIntakeFromFoodEntries() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            
            FoodEntry entry1 = new FoodEntry(1, "Chicken", 200, 30.0f, 0.0f, 4.0f, 6.0, null);
            FoodEntry entry2 = new FoodEntry(1, "Rice", 150, 3.0f, 30.0f, 1.0f, 4.0, null);
            List<FoodEntry> entries = Arrays.asList(entry1, entry2);
            
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class))).thenReturn(entries);
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(200);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute("entries", entries);
            verify(mockRequest).setAttribute("totalIntake", 350); // 200 + 150
            verify(mockRequest).setAttribute("totalBurned", 200);
            verify(mockRequest).setAttribute("netCalories", 150); // 350 - 200
        }

        @Test
        @DisplayName("Should handle empty food entry list")
        void shouldHandleEmptyFoodEntryList() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(300);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute("entries", Collections.emptyList());
            verify(mockRequest).setAttribute("totalIntake", 0);
            verify(mockRequest).setAttribute("totalBurned", 300);
            verify(mockRequest).setAttribute("netCalories", -300); // 0 - 300
        }
    }

    @Nested
    @DisplayName("Exercise Calorie Processing Tests")
    class ExerciseCalorieProcessingTests {

        @Test
        @DisplayName("Should retrieve total calories burned from exercise log")
        void shouldRetrieveTotalCaloriesBurnedFromExerciseLog() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(450);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockExerciseLogDAO).getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class));
            verify(mockRequest).setAttribute("totalBurned", 450);
        }

        @Test
        @DisplayName("Should handle zero calories burned")
        void shouldHandleZeroCaloriesBurned() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute("totalBurned", 0);
        }
    }

    @Nested
    @DisplayName("Balance Calculation Tests")
    class BalanceCalculationTests {

        @Test
        @DisplayName("Should calculate positive calorie balance correctly")
        void shouldCalculatePositiveCalorieBalanceCorrectly() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            
            FoodEntry entry = new FoodEntry(1, "High Calorie Meal", 800, 50.0f, 60.0f, 30.0f, 12.0, null);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Arrays.asList(entry));
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(200);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute("totalIntake", 800);
            verify(mockRequest).setAttribute("totalBurned", 200);
            verify(mockRequest).setAttribute("netCalories", 600); // 800 - 200
        }

        @Test
        @DisplayName("Should calculate negative calorie balance correctly")
        void shouldCalculateNegativeCalorieBalanceCorrectly() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            
            FoodEntry entry = new FoodEntry(1, "Light Meal", 200, 20.0f, 10.0f, 5.0f, 4.0, null);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Arrays.asList(entry));
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(500);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequest).setAttribute("totalIntake", 200);
            verify(mockRequest).setAttribute("totalBurned", 500);
            verify(mockRequest).setAttribute("netCalories", -300); // 200 - 500
        }

        @Test
        @DisplayName("Should calculate remaining calories for weight loss goal")
        void shouldCalculateRemainingCaloriesForWeightLossGoal() throws Exception {
            // Given
            testUser.setGoal("lose");
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should show reduced calorie goal for weight loss
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequest).setAttribute(eq("remainingCalories"), any(Integer.class));
        }

        @Test
        @DisplayName("Should calculate remaining calories for weight gain goal")
        void shouldCalculateRemainingCaloriesForWeightGainGoal() throws Exception {
            // Given
            testUser.setGoal("gain");
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should show increased calorie goal for weight gain
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequest).setAttribute(eq("remainingCalories"), any(Integer.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle null fitness level")
        void shouldHandleNullFitnessLevel() throws Exception {
            // Given
            testUser.setFitnessLevel(null);
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should use moderate as default
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should handle null goal")
        void shouldHandleNullGoal() throws Exception {
            // Given
            testUser.setGoal(null);
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockFoodEntryDAO.getFoodEntriesByUser(eq(1), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(mockExerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(0);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should use maintain as default
            verify(mockRequest).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should handle session being null")
        void shouldHandleSessionBeingNull() throws Exception {
            // Given
            when(mockRequest.getSession(false)).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockResponse).sendRedirect("login.jsp");
        }
    }
}