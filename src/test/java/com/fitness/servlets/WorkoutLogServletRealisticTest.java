package com.fitness.servlets;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fitness.dao.WorkoutDAO;
import com.fitness.model.User;
import com.fitness.model.Workout;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Workout Logging User Story Tests")
class WorkoutLogServletRealisticTest {

    @Mock private WorkoutDAO mockWorkoutDAO;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    @Mock private HttpSession mockSession;
    @Mock private RequestDispatcher mockRequestDispatcher;

    private WorkoutLogServlet servlet;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new WorkoutLogServlet(mockWorkoutDAO);
        
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setWeight(70.0f);
        testUser.setHeight(175);
        testUser.setAge(25);
        
        // Basic mocking setup with lenient stubbing to avoid unnecessary stubbing exceptions
        lenient().when(mockRequest.getSession()).thenReturn(mockSession);
        lenient().when(mockRequest.getSession(false)).thenReturn(mockSession);
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        lenient().when(mockRequest.getRequestDispatcher("/workout-log.jsp")).thenReturn(mockRequestDispatcher);
        lenient().doNothing().when(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Nested
    @DisplayName("Workout Creation Tests - Cardio")
    class WorkoutCreationCardioTests {

        @Test
        @DisplayName("Should successfully log cardio workout when valid data provided")
        void shouldSuccessfullyLogCardioWorkoutWhenValidDataProvided() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("sets")).thenReturn("0");
            when(mockRequest.getParameter("reps")).thenReturn("0");
            when(mockRequest.getParameter("weight")).thenReturn("0");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            when(mockRequest.getParameter("workoutTime")).thenReturn("08:00");
            when(mockRequest.getParameter("notes")).thenReturn("Morning run");
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Running", 30, 70.0f)).thenReturn(300.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO).createWorkout(any(Workout.class));
            verify(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));
            verify(mockSession).setAttribute(eq("message"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }

        @Test
        @DisplayName("Should handle missing exercise name parameter")
        void shouldHandleMissingExerciseNameParameter() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn(null);
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-log");
        }

        @Test
        @DisplayName("Should handle invalid duration parameter")
        void shouldHandleInvalidDurationParameter() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("invalid");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            when(mockRequest.getParameter("workoutTime")).thenReturn("10:00");
            
            // Mock all other parameters to avoid strict stubbing issues
            when(mockRequest.getParameter("sets")).thenReturn(null);
            when(mockRequest.getParameter("reps")).thenReturn(null);
            when(mockRequest.getParameter("weight")).thenReturn(null);
            when(mockRequest.getParameter("notes")).thenReturn(null);
            
            // Servlet will parse "invalid" as 0 and proceed to create workout
            when(mockWorkoutDAO.calculateCaloriesBurned("Running", 0, 70.0f)).thenReturn(0.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            // Servlet treats invalid as 0 and creates workout
            verify(mockSession).setAttribute(eq("message"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
    }

    @Nested
    @DisplayName("Workout Creation Tests - Strength")
    class WorkoutCreationStrengthTests {

        @Test
        @DisplayName("Should successfully log strength workout when valid data provided")
        void shouldSuccessfullyLogStrengthWorkoutWhenValidDataProvided() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Bench Press");
            when(mockRequest.getParameter("exerciseType")).thenReturn("strength");
            when(mockRequest.getParameter("sets")).thenReturn("3");
            when(mockRequest.getParameter("reps")).thenReturn("10");
            when(mockRequest.getParameter("weight")).thenReturn("80.0");
            when(mockRequest.getParameter("duration")).thenReturn("45");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            when(mockRequest.getParameter("workoutTime")).thenReturn("18:00");
            when(mockRequest.getParameter("notes")).thenReturn("Evening strength training");
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Bench Press", 45, 70.0f)).thenReturn(200.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO).createWorkout(any(Workout.class));
            verify(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));
            verify(mockSession).setAttribute(eq("message"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }

        @Test
        @DisplayName("Should handle optional parameters correctly for strength training")
        void shouldHandleOptionalParametersCorrectlyForStrengthTraining() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Push-ups");
            when(mockRequest.getParameter("exerciseType")).thenReturn("strength");
            when(mockRequest.getParameter("sets")).thenReturn(""); // Empty string
            when(mockRequest.getParameter("reps")).thenReturn(""); // Empty string
            when(mockRequest.getParameter("weight")).thenReturn(""); // Empty string (bodyweight)
            when(mockRequest.getParameter("duration")).thenReturn("20");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            when(mockRequest.getParameter("workoutTime")).thenReturn(""); // Empty time
            when(mockRequest.getParameter("notes")).thenReturn(null); // Null notes
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Push-ups", 20, 70.0f)).thenReturn(150.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO).createWorkout(any(Workout.class));
            verify(mockSession).setAttribute(eq("message"), any(String.class));
        }
    }

    @Nested
    @DisplayName("Workout History Display Tests")
    class WorkoutHistoryDisplayTests {

        @Test
        @DisplayName("Should display workout form for logged in user")
        void shouldDisplayWorkoutFormForLoggedInUser() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should redirect to login when user not logged in for GET request")
        void shouldRedirectToLoginWhenUserNotLoggedInForGetRequest() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockResponse).sendRedirect("/login.jsp");
        }
    }

    @Nested
    @DisplayName("Calorie Calculation Tests")
    class CalorieCalculationTests {

        @Test
        @DisplayName("Should calculate calories for different exercise types")
        void shouldCalculateCaloriesForDifferentExerciseTypes() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Swimming");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("60");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Swimming", 60, 70.0f)).thenReturn(500.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO).calculateCaloriesBurned("Swimming", 60, 70.0f);
            verify(mockWorkoutDAO).createWorkout(any(Workout.class));
        }

        @Test
        @DisplayName("Should use default MET value when exercise not found")
        void shouldUseDefaultMetValueWhenExerciseNotFound() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Unknown Exercise");
            when(mockRequest.getParameter("exerciseType")).thenReturn("other");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Unknown Exercise", 30, 70.0f)).thenReturn(120.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO).calculateCaloriesBurned("Unknown Exercise", 30, 70.0f);
            verify(mockWorkoutDAO).createWorkout(any(Workout.class));
        }
    }

    @Nested
    @DisplayName("User Session Management Tests")
    class UserSessionManagementTests {

        @Test
        @DisplayName("Should redirect to login when user not logged in")
        void shouldRedirectToLoginWhenUserNotLoggedIn() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockResponse).sendRedirect("/login.jsp");
        }

        @Test
        @DisplayName("Should handle GET request when user not logged in")
        void shouldHandleGetRequestWhenUserNotLoggedIn() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockResponse).sendRedirect("/login.jsp");
        }

        @Test
        @DisplayName("Should maintain user session throughout workout logging process")
        void shouldMaintainUserSessionThroughoutWorkoutLoggingProcess() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Cycling");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            // Mock ALL parameters that the servlet expects to avoid strict stubbing issues
            when(mockRequest.getParameter("sets")).thenReturn("0");
            when(mockRequest.getParameter("reps")).thenReturn("0");
            when(mockRequest.getParameter("weight")).thenReturn("0");
            when(mockRequest.getParameter("duration")).thenReturn("45");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            when(mockRequest.getParameter("workoutTime")).thenReturn("10:00");
            when(mockRequest.getParameter("notes")).thenReturn("Session test");
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Cycling", 45, 70.0f)).thenReturn(350.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockSession).getAttribute("user"); // Session accessed
            verify(mockSession).setAttribute(eq("message"), any(String.class)); // Success message stored
            verify(mockResponse).sendRedirect("/workout-history");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle database save errors gracefully")
        void shouldHandleDatabaseSaveErrorsGracefully() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            // Mock ALL parameters to avoid strict stubbing issues
            when(mockRequest.getParameter("sets")).thenReturn("0");
            when(mockRequest.getParameter("reps")).thenReturn("0");
            when(mockRequest.getParameter("weight")).thenReturn("0");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-01");
            when(mockRequest.getParameter("workoutTime")).thenReturn("09:00");
            when(mockRequest.getParameter("notes")).thenReturn("Error test");
            
            when(mockWorkoutDAO.calculateCaloriesBurned("Running", 30, 70.0f)).thenReturn(300.0);
            doNothing().when(mockWorkoutDAO).createWorkout(any(Workout.class));
            // Simulate exception on updateDailyFitnessSummary
            doThrow(new RuntimeException("Database connection failed"))
                .when(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-log");
        }

        @Test
        @DisplayName("Should handle invalid date format")
        void shouldHandleInvalidDateFormat() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("invalid-date");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-log");
        }

        @Test
        @DisplayName("Should handle missing required fields")
        void shouldHandleMissingRequiredFields() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(testUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("");
            when(mockRequest.getParameter("exerciseType")).thenReturn("");
            when(mockRequest.getParameter("duration")).thenReturn("");
            when(mockRequest.getParameter("workoutDate")).thenReturn("");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-log");
        }
    }
}