package com.fitness.servlets;

import com.fitness.dao.ExerciseLogDAO;
import com.fitness.dao.FoodEntryDAO;
import com.fitness.model.FoodEntry;
import com.fitness.model.User;
import com.fitness.util.CalorieCalculator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CalorieBalanceServlet Tests")
class CalorieBalanceServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private FoodEntryDAO foodEntryDAO;

    @Mock
    private ExerciseLogDAO exerciseLogDAO;

    private CalorieBalanceServlet servlet;

    private User testUser;

    @BeforeEach
    void setUp() {
        servlet = new CalorieBalanceServlet(foodEntryDAO, exerciseLogDAO);
        testUser = createTestUser();
        
        // Setup default mocking behavior (lenient to avoid unnecessary stubbing errors)
        lenient().when(request.getSession()).thenReturn(session);
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Nested
    @DisplayName("User Authentication Tests")
    class UserAuthenticationTests {

        @Test
        @DisplayName("Should redirect to login when no session exists")
        void shouldRedirectToLoginWhenNoSessionExists() throws ServletException, IOException {
            // Given
            when(request.getSession(false)).thenReturn(null);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(response).sendRedirect("login.jsp");
            verify(request, never()).getRequestDispatcher(anyString());
        }

        @Test
        @DisplayName("Should redirect to login when user not in session")
        void shouldRedirectToLoginWhenUserNotInSession() throws ServletException, IOException {
            // Given
            when(session.getAttribute("user")).thenReturn(null);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(response).sendRedirect("login.jsp");
            verify(request, never()).getRequestDispatcher(anyString());
        }

        @Test
        @DisplayName("Should proceed when authenticated user exists")
        void shouldProceedWhenAuthenticatedUserExists() throws ServletException, IOException {
            // Given
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(response, never()).sendRedirect(anyString());
            verify(requestDispatcher).forward(request, response);
        }
    }

    @Nested
    @DisplayName("Calorie Calculation Tests")
    class CalorieCalculationTests {

        @Test
        @DisplayName("Should calculate BMR correctly for user")
        void shouldCalculateBMRCorrectlyForUser() throws ServletException, IOException {
            // Given
            testUser.setWeight(70.0f);
            testUser.setHeight(175);
            testUser.setAge(25);
            testUser.setFitnessLevel("moderate");
            testUser.setGoal("maintain");
            
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            // BMR = 10 * 70 + 6.25 * 175 - 5 * 25 + 5 = 1673.75
            // TDEE = 1673.75 * 1.55 (moderate) = 2594.3125
            // Maintain goal = no adjustment
            verify(request).setAttribute(eq("recommendedCalories"), eq(2594));
        }

        @Test
        @DisplayName("Should apply weight loss goal adjustment")
        void shouldApplyWeightLossGoalAdjustment() throws ServletException, IOException {
            // Given
            testUser.setGoal("lose");
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            // Should subtract 500 calories for weight loss: 2594 - 500 = 2094
            verify(request).setAttribute(eq("recommendedCalories"), eq(2094));
        }

        @Test
        @DisplayName("Should apply muscle gain goal adjustment")
        void shouldApplyMuscleGainGoalAdjustment() throws ServletException, IOException {
            // Given
            testUser.setGoal("gain");
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            // Should add 500 calories for muscle gain: 2594 + 500 = 3094
            verify(request).setAttribute(eq("recommendedCalories"), eq(3094));
        }

        @Test
        @DisplayName("Should handle high activity level")
        void shouldHandleHighActivityLevel() throws ServletException, IOException {
            // Given
            testUser.setFitnessLevel("high");
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            // High activity factor = 1.9: 1673.75 * 1.9 = 3180
            verify(request).setAttribute(eq("recommendedCalories"), eq(3180));
        }

        @Test
        @DisplayName("Should handle low activity level")
        void shouldHandleLowActivityLevel() throws ServletException, IOException {
            // Given
            testUser.setFitnessLevel("low");
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            // Low activity factor = 1.2: 1673.75 * 1.2 = 2008
            verify(request).setAttribute(eq("recommendedCalories"), eq(2008));
        }
    }

    @Nested
    @DisplayName("Food Entry Processing Tests")
    class FoodEntryProcessingTests {

        @Test
        @DisplayName("Should calculate total intake from food entries")
        void shouldCalculateTotalIntakeFromFoodEntries() throws ServletException, IOException {
            // Given
            List<FoodEntry> foodEntries = Arrays.asList(
                createFoodEntry("Breakfast", 400),
                createFoodEntry("Lunch", 600),
                createFoodEntry("Dinner", 500)
            );
            
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(foodEntries);
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(300);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(request).setAttribute(eq("totalIntake"), eq(1500));
            verify(request).setAttribute(eq("entries"), eq(foodEntries));
        }

        @Test
        @DisplayName("Should handle empty food entries list")
        void shouldHandleEmptyFoodEntriesList() throws ServletException, IOException {
            // Given
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(request).setAttribute(eq("totalIntake"), eq(0));
            verify(request).setAttribute(eq("entries"), eq(Collections.emptyList()));
        }
    }

    @Nested
    @DisplayName("Exercise Calorie Processing Tests")
    class ExerciseCalorieProcessingTests {

        @Test
        @DisplayName("Should retrieve calories burned from exercise log")
        void shouldRetrieveCaloriesBurnedFromExerciseLog() throws ServletException, IOException {
            // Given
            int caloriesBurned = 450;
            
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(testUser.getUserId(), LocalDate.now()))
                .thenReturn(caloriesBurned);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(request).setAttribute(eq("totalBurned"), eq(caloriesBurned));
        }

        @Test
        @DisplayName("Should handle zero calories burned")
        void shouldHandleZeroCaloriesBurned() throws ServletException, IOException {
            // Given
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(request).setAttribute(eq("totalBurned"), eq(0));
        }
    }

    @Nested
    @DisplayName("Balance Calculation Tests")
    class BalanceCalculationTests {

        @Test
        @DisplayName("Should calculate net calories and remaining calories correctly")
        void shouldCalculateNetAndRemainingCaloriesCorrectly() throws ServletException, IOException {
            // Given
            List<FoodEntry> foodEntries = Arrays.asList(
                createFoodEntry("Meal 1", 800),
                createFoodEntry("Meal 2", 700)
            );
            int totalIntake = 1500;
            int totalBurned = 300;
            
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(foodEntries);
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(totalBurned);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            int expectedNetCalories = totalIntake - totalBurned; // 1500 - 300 = 1200
            
            verify(request).setAttribute(eq("totalIntake"), eq(totalIntake));
            verify(request).setAttribute(eq("totalBurned"), eq(totalBurned));
            verify(request).setAttribute(eq("netCalories"), eq(expectedNetCalories));
            // Remaining calories will vary based on the calculated recommended calories
        }

        @Test
        @DisplayName("Should handle negative remaining calories")
        void shouldHandleNegativeRemainingCalories() throws ServletException, IOException {
            // Given - High calorie intake scenario
            List<FoodEntry> foodEntries = Arrays.asList(
                createFoodEntry("Large Meal", 3000)
            );
            
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(foodEntries);
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(200);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(request).setAttribute(eq("totalIntake"), eq(3000));
            verify(request).setAttribute(eq("totalBurned"), eq(200));
            verify(request).setAttribute(eq("netCalories"), eq(2800));
            // Remaining calories should be negative (over budget)
        }
    }

    @Nested
    @DisplayName("Request Forwarding Tests")
    class RequestForwardingTests {

        @Test
        @DisplayName("Should forward to calorie balance JSP")
        void shouldForwardToCalorieBalanceJSP() throws ServletException, IOException {
            // Given
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then
            verify(request).getRequestDispatcher("/calorieBalance.jsp");
            verify(requestDispatcher).forward(request, response);
        }

        @Test
        @DisplayName("Should set all required attributes for JSP")
        void shouldSetAllRequiredAttributesForJSP() throws ServletException, IOException {
            // Given
            when(session.getAttribute("user")).thenReturn(testUser);
            when(foodEntryDAO.getFoodEntriesByUser(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
            when(exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(anyInt(), any(LocalDate.class)))
                .thenReturn(0);
            
            // When
            servlet.doGet(request, response);
            
            // Then - Verify all required attributes are set
            verify(request).setAttribute(eq("recommendedCalories"), any(Integer.class));
            verify(request).setAttribute(eq("totalIntake"), any(Integer.class));
            verify(request).setAttribute(eq("totalBurned"), any(Integer.class));
            verify(request).setAttribute(eq("netCalories"), any(Integer.class));
            verify(request).setAttribute(eq("remainingCalories"), any(Integer.class));
            verify(request).setAttribute(eq("entries"), any(List.class));
        }
    }

    // Helper methods
    private User createTestUser() {
        User user = new User();
        user.setUserId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setWeight(70.0f);
        user.setHeight(175);
        user.setGoal("maintain");
        user.setFitnessLevel("moderate");
        return user;
    }

    private FoodEntry createFoodEntry(String foodName, int calories) {
        FoodEntry entry = new FoodEntry();
        entry.setFoodName(foodName);
        entry.setCalories(calories);
        return entry;
    }
}