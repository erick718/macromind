package com.fitness.servlets;

import java.sql.Date;
import java.sql.Time;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WorkoutEditServlet Tests")
class WorkoutEditServletTest {

    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpServletResponse mockResponse;
    
    @Mock
    private HttpSession mockSession;
    
    @Mock
    private WorkoutDAO mockWorkoutDAO;
    
    @Mock
    private RequestDispatcher mockRequestDispatcher;
    
    private WorkoutEditServlet servlet;
    private User testUser;
    private Workout testWorkout;
    
    @BeforeEach
    void setUp() throws Exception {
        servlet = new WorkoutEditServlet(mockWorkoutDAO);
        
        // Setup test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setWeight(70.0f);
        
        // Setup test workout
        testWorkout = new Workout();
        testWorkout.setWorkoutId(100);
        testWorkout.setUserId(1);
        testWorkout.setExerciseName("Running");
        testWorkout.setExerciseType("cardio");
        testWorkout.setDurationMinutes(30);
        testWorkout.setCaloriesBurned(300.0);
        testWorkout.setWorkoutDate(Date.valueOf("2024-01-01"));
        testWorkout.setWorkoutTime(Time.valueOf("10:00:00"));
        testWorkout.setNotes("Morning run");
        
        // Basic mocking
        lenient().when(mockRequest.getSession(false)).thenReturn(mockSession);
        lenient().when(mockRequest.getSession()).thenReturn(mockSession);
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        lenient().when(mockSession.getAttribute("user")).thenReturn(testUser);
        lenient().when(mockRequest.getRequestDispatcher("/workout-edit.jsp")).thenReturn(mockRequestDispatcher);
    }
    
    @Nested
    @DisplayName("GET Request Tests - Load Workout for Editing")
    class GetRequestTests {
        
        @Test
        @DisplayName("Should load workout for editing when valid ID provided")
        void shouldLoadWorkoutForEditing() throws Exception {
            // Given
            when(mockRequest.getParameter("id")).thenReturn("100");
            when(mockWorkoutDAO.getWorkoutById(100)).thenReturn(testWorkout);
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockRequest).setAttribute("workout", testWorkout);
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
        }
        
        @Test
        @DisplayName("Should redirect to login when user not authenticated")
        void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(null);
            when(mockRequest.getParameter("id")).thenReturn("100");
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockResponse).sendRedirect("/login.jsp");
            verify(mockWorkoutDAO, never()).getWorkoutById(anyInt());
        }
        
        @Test
        @DisplayName("Should redirect when workout ID is missing")
        void shouldRedirectWhenWorkoutIdMissing() throws Exception {
            // Given
            when(mockRequest.getParameter("id")).thenReturn(null);
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should redirect when workout ID is empty")
        void shouldRedirectWhenWorkoutIdEmpty() throws Exception {
            // Given
            when(mockRequest.getParameter("id")).thenReturn("   ");
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should redirect when workout ID is invalid")
        void shouldRedirectWhenWorkoutIdInvalid() throws Exception {
            // Given
            when(mockRequest.getParameter("id")).thenReturn("invalid");
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should redirect when workout not found")
        void shouldRedirectWhenWorkoutNotFound() throws Exception {
            // Given
            when(mockRequest.getParameter("id")).thenReturn("999");
            when(mockWorkoutDAO.getWorkoutById(999)).thenReturn(null);
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), eq("Workout not found."));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should prevent editing workout owned by another user")
        void shouldPreventEditingOtherUsersWorkout() throws Exception {
            // Given
            testWorkout.setUserId(999); // Different user
            when(mockRequest.getParameter("id")).thenReturn("100");
            when(mockWorkoutDAO.getWorkoutById(100)).thenReturn(testWorkout);
            
            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), eq("You can only edit your own workouts."));
            verify(mockResponse).sendRedirect("/workout-history");
            verify(mockRequest, never()).setAttribute(eq("workout"), any());
        }
    }
    
    @Nested
    @DisplayName("POST Request Tests - Update Workout")
    class PostRequestTests {
        
        @BeforeEach
        void setUp() {
            // Setup for POST requests
            lenient().when(mockRequest.getParameter("workoutId")).thenReturn("100");
            lenient().when(mockWorkoutDAO.getWorkoutById(100)).thenReturn(testWorkout);
            lenient().when(mockWorkoutDAO.calculateCaloriesBurned(any(String.class), anyInt(), any(Float.class)))
                     .thenReturn(350.0);
            lenient().doNothing().when(mockWorkoutDAO).updateWorkout(any(Workout.class));
            lenient().doNothing().when(mockWorkoutDAO).updateDailyFitnessSummary(anyInt(), any(Date.class));
        }
        
        @Test
        @DisplayName("Should successfully update cardio workout")
        void shouldSuccessfullyUpdateCardioWorkout() throws Exception {
            // Given
            when(mockRequest.getParameter("exerciseName")).thenReturn("Swimming");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("45");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn("14:30");
            when(mockRequest.getParameter("notes")).thenReturn("Pool workout");
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockWorkoutDAO).updateWorkout(any(Workout.class));
            verify(mockWorkoutDAO).updateDailyFitnessSummary(eq(1), any(Date.class));
            verify(mockSession).setAttribute(eq("message"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should successfully update strength workout")
        void shouldSuccessfullyUpdateStrengthWorkout() throws Exception {
            // Given
            when(mockRequest.getParameter("exerciseName")).thenReturn("Bench Press");
            when(mockRequest.getParameter("exerciseType")).thenReturn("strength");
            when(mockRequest.getParameter("duration")).thenReturn("60");
            when(mockRequest.getParameter("sets")).thenReturn("4");
            when(mockRequest.getParameter("reps")).thenReturn("8");
            when(mockRequest.getParameter("weight")).thenReturn("80.5");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn("18:00");
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockWorkoutDAO).updateWorkout(any(Workout.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should redirect to login when user not authenticated")
        void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
            // Given
            when(mockSession.getAttribute("user")).thenReturn(null);
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockResponse).sendRedirect("/login.jsp");
            verify(mockWorkoutDAO, never()).updateWorkout(any());
        }
        
        @Test
        @DisplayName("Should redirect when workout ID is missing")
        void shouldRedirectWhenWorkoutIdMissing() throws Exception {
            // Given
            when(mockRequest.getParameter("workoutId")).thenReturn(null);
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
            verify(mockWorkoutDAO, never()).updateWorkout(any());
        }
        
        @Test
        @DisplayName("Should redirect when workout not found")
        void shouldRedirectWhenWorkoutNotFound() throws Exception {
            // Given
            when(mockWorkoutDAO.getWorkoutById(100)).thenReturn(null);
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), eq("Workout not found."));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should prevent updating workout owned by another user")
        void shouldPreventUpdatingOtherUsersWorkout() throws Exception {
            // Given
            testWorkout.setUserId(999);
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), eq("You can only edit your own workouts."));
            verify(mockResponse).sendRedirect("/workout-history");
            verify(mockWorkoutDAO, never()).updateWorkout(any());
        }
        
        @Test
        @DisplayName("Should show error when required fields are missing")
        void shouldShowErrorWhenRequiredFieldsMissing() throws Exception {
            // Given
            when(mockRequest.getParameter("exerciseName")).thenReturn("");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockRequest).setAttribute(eq("error"), any(String.class));
            verify(mockRequest).setAttribute("workout", testWorkout);
            verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
            verify(mockWorkoutDAO, never()).updateWorkout(any());
        }
        
        @Test
        @DisplayName("Should handle invalid date format gracefully")
        void shouldHandleInvalidDateFormat() throws Exception {
            // Given
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("invalid-date");
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockSession).setAttribute(eq("error"), any(String.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should handle missing optional fields")
        void shouldHandleMissingOptionalFields() throws Exception {
            // Given
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("workoutTime")).thenReturn(null);
            when(mockRequest.getParameter("notes")).thenReturn(null);
            when(mockRequest.getParameter("sets")).thenReturn(null);
            when(mockRequest.getParameter("reps")).thenReturn(null);
            when(mockRequest.getParameter("weight")).thenReturn(null);
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockWorkoutDAO).updateWorkout(any(Workout.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
        
        @Test
        @DisplayName("Should handle invalid numeric values with defaults")
        void shouldHandleInvalidNumericValues() throws Exception {
            // Given
            when(mockRequest.getParameter("exerciseName")).thenReturn("Running");
            when(mockRequest.getParameter("exerciseType")).thenReturn("cardio");
            when(mockRequest.getParameter("duration")).thenReturn("30");
            when(mockRequest.getParameter("workoutDate")).thenReturn("2024-01-15");
            when(mockRequest.getParameter("sets")).thenReturn("invalid");
            when(mockRequest.getParameter("reps")).thenReturn("invalid");
            when(mockRequest.getParameter("weight")).thenReturn("invalid");
            
            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockWorkoutDAO).updateWorkout(any(Workout.class));
            verify(mockResponse).sendRedirect("/workout-history");
        }
    }
}
