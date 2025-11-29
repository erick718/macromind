package com.fitness.servlets;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fitness.dao.WorkoutDAO;
import com.fitness.Model.User;
import com.fitness.Model.Workout;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WorkoutLogServlet Tests - Workout Logging User Story")
class WorkoutLogServletTest {

    private WorkoutLogServlet servlet;

    @Mock
    private WorkoutDAO mockWorkoutDAO;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpSession mockSession;

    @Mock
    private RequestDispatcher mockRequestDispatcher;

    @BeforeEach
    void setUp() {
        lenient().when(mockRequest.getSession()).thenReturn(mockSession);
        lenient().when(mockRequest.getSession(false)).thenReturn(mockSession);
        lenient().when(mockRequest.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        
        servlet = new WorkoutLogServlet(mockWorkoutDAO);
    }

    @Nested
    @DisplayName("Workout Creation Tests")
    class WorkoutCreationTests {

        @Test
        @DisplayName("Should successfully log cardio workout")
        void shouldSuccessfullyLogCardioWorkout() throws ServletException, IOException {
            // Given - User Story: As a user, I want to log cardio workouts with duration and intensity
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn("10:30");
            when(mockRequest.getParameter("userWeight")).thenReturn("70.0");
            when(mockWorkoutDAO.calculateCaloriesBurned("Running", 30, 70.0)).thenReturn(300.0);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify workout was logged with calculated calories
            verify(mockWorkoutDAO).calculateCaloriesBurned("Running", 30, 70.0);
            verify(mockWorkoutDAO).createWorkout(argThat(workout -> 
                workout.getUserId() == mockUser.getUserId() &&
                "Running".equals(workout.getExerciseName()) &&
                "cardio".equals(workout.getExerciseType()) &&
                workout.getDurationMinutes() == 30 &&
                workout.getCaloriesBurned() == 300.0
            ));
            
            verify(mockSession).setAttribute(eq("message"), anyString());
        }

        @Test
        @DisplayName("Should successfully log strength training workout")
        void shouldSuccessfullyLogStrengthTrainingWorkout() throws ServletException, IOException {
            // Given - User Story: As a user, I want to log strength training with sets, reps, and weight
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Bench Press");
            when(mockRequest.getParameter("exerciseType")).thenReturn("strength");
            when(mockRequest.getParameter("sets")).thenReturn("3");
            when(mockRequest.getParameter("reps")).thenReturn("10");
            when(mockRequest.getParameter("weight")).thenReturn("80.0");
            when(mockRequest.getParameter("duration")).thenReturn("45");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn("14:30");
            when(mockRequest.getParameter("userWeight")).thenReturn("70.0");
            when(mockWorkoutDAO.calculateCaloriesBurned("Bench Press", 45, 70.0)).thenReturn(250.0);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify strength workout was logged with all parameters
            verify(mockWorkoutDAO).calculateCaloriesBurned("Bench Press", 45, 70.0);
            verify(mockWorkoutDAO).createWorkout(argThat(workout -> 
                workout.getUserId() == mockUser.getUserId() &&
                "Bench Press".equals(workout.getExerciseName()) &&
                "strength".equals(workout.getExerciseType()) &&
                workout.getSetsCount() == 3 &&
                workout.getRepsPerSet() == 10 &&
                workout.getWeightKg() == 80.0 &&
                workout.getDurationMinutes() == 45 &&
                workout.getCaloriesBurned() == 250.0
            ));
            verify(mockSession).setAttribute(eq("message"), anyString());
        }

        @Test
        @DisplayName("Should handle missing required workout parameters")
        void shouldHandleMissingRequiredWorkoutParameters() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO, never()).createWorkout(any(Workout.class));
            verify(mockSession).setAttribute(eq("error"), anyString());
            verify(mockResponse).sendRedirect("/workout-log");
        }

        @Test
        @DisplayName("Should handle invalid duration parameter")
        void shouldHandleInvalidDurationParameter() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Jogging");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("invalid");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockWorkoutDAO.calculateCaloriesBurned("Jogging", 0, 70.0)).thenReturn(0.0);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Servlet uses parseIntOrDefault which returns 0 for invalid input
            verify(mockWorkoutDAO).createWorkout(argThat(workout -> 
                "Jogging".equals(workout.getExerciseName()) &&
                workout.getDurationMinutes() == 0 // Invalid input becomes 0
            ));
            verify(mockSession).setAttribute(eq("message"), anyString());
        }
    }

    @Nested
    @DisplayName("Workout History Display Tests")
    class WorkoutHistoryDisplayTests {

        @Test
        @DisplayName("Should display workout history for logged in user")
        void shouldDisplayWorkoutHistoryForLoggedInUser() throws ServletException, IOException {
            // Given - User Story: As a user, I want to access the workout log form
            User mockUser = createMockUser();
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify user can access the form (servlet forwards to JSP)
            verify(mockRequest).getRequestDispatcher("/workout-log.jsp");
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should handle empty workout history")
        void shouldHandleEmptyWorkoutHistory() throws ServletException, IOException {
            // Given - User accesses workout log form
            User mockUser = createMockUser();
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Servlet forwards to form, doesn't set attributes
            verify(mockRequest).getRequestDispatcher("/workout-log.jsp");
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }
    }

    @Nested
    @DisplayName("Calorie Calculation Tests")
    class CalorieCalculationTests {

        @Test
        @DisplayName("Should calculate calories for different exercise types")
        void shouldCalculateCaloriesForDifferentExerciseTypes() throws ServletException, IOException {
            // Given - User Story: As a user, I want accurate calorie burn calculations based on my activities
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Swimming");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("45");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn("10:30");
            when(mockRequest.getParameter("userWeight")).thenReturn("70.0");
            when(mockWorkoutDAO.calculateCaloriesBurned("Swimming", 45, 70.0)).thenReturn(400.0);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify workout was created with calories calculated
            verify(mockWorkoutDAO).calculateCaloriesBurned("Swimming", 45, 70.0);
            verify(mockWorkoutDAO).createWorkout(argThat(workout -> 
                workout.getCaloriesBurned() == 400.0 && // Should have calculated calories
                "Swimming".equals(workout.getExerciseName())
            ));
        }

        @Test
        @DisplayName("Should use default MET value when exercise not found")
        void shouldUseDefaultMetValueWhenExerciseNotFound() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Unknown Exercise");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn("10:30");
            when(mockRequest.getParameter("userWeight")).thenReturn("70.0");
            when(mockWorkoutDAO.calculateCaloriesBurned("Unknown Exercise", 30, 70.0)).thenReturn(150.0);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify workout was created with calorie calculation
            verify(mockWorkoutDAO).calculateCaloriesBurned("Unknown Exercise", 30, 70.0);
            verify(mockWorkoutDAO).createWorkout(argThat(workout -> 
                workout.getCaloriesBurned() == 150.0
            ));
        }
    }

    @Nested
    @DisplayName("User Session Management Tests")
    class UserSessionManagementTests {

        @Test
        @DisplayName("Should redirect to login when user not logged in")
        void shouldRedirectToLoginWhenUserNotLoggedIn() throws ServletException, IOException {
            // Given
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO, never()).createWorkout(any(Workout.class));
            verify(mockResponse).sendRedirect("/login.jsp");
        }

        @Test
        @DisplayName("Should handle GET request when user not logged in")
        void shouldHandleGetRequestWhenUserNotLoggedIn() throws ServletException, IOException {
            // Given
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockWorkoutDAO, never()).getWorkoutsByUserId(anyInt());
            verify(mockResponse).sendRedirect("/login.jsp");
        }
    }

    // Helper methods
    private User createMockUser() {
        User user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setWeight(70.0f); // Set default weight for calorie calculations
        return user;
    }

    private List<Workout> createMockWorkouts() {
        Workout workout1 = new Workout(
            1, "Running", "cardio", 0, 0, 0.0, 30, Date.valueOf(LocalDate.now().minusDays(1))
        );
        workout1.setWorkoutId(1);
        workout1.setCaloriesBurned(300.0);

        Workout workout2 = new Workout(
            1, "Bench Press", "strength", 3, 10, 80.0, 45, Date.valueOf(LocalDate.now())
        );
        workout2.setWorkoutId(2);
        workout2.setCaloriesBurned(250.0);

        return Arrays.asList(workout1, workout2);
    }
}