package com.fitness.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fitness.Model.Workout;
import com.fitness.util.DBConnection;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutDAO Tests")
class WorkoutDAOTest {

    private WorkoutDAO workoutDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock 
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        workoutDAO = new WorkoutDAO();
        
        // Setup static mock for DBConnection
        mockedDBConnection = mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        
        // Setup common mock behavior with lenient stubbing
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        lenient().when(mockConnection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        lenient().when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        lenient().when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        lenient().when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Nested
    @DisplayName("Workout Creation Tests")
    class WorkoutCreationTests {

        @Test
        @DisplayName("Should successfully create new workout")
        void shouldSuccessfullyCreateNewWorkout() throws SQLException {
            // Given
            Workout workout = createTestWorkout();
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(123);
            
            // When
            assertThatCode(() -> workoutDAO.createWorkout(workout))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockPreparedStatement).setInt(1, workout.getUserId());
            verify(mockPreparedStatement).setString(2, workout.getExerciseName());
            verify(mockPreparedStatement).setString(3, workout.getExerciseType());
            verify(mockPreparedStatement).setInt(4, workout.getSetsCount());
            verify(mockPreparedStatement).setInt(5, workout.getRepsPerSet());
            verify(mockPreparedStatement).setDouble(6, workout.getWeightKg());
            verify(mockPreparedStatement).setInt(7, workout.getDurationMinutes());
            verify(mockPreparedStatement).setDouble(8, workout.getCaloriesBurned());
            verify(mockPreparedStatement).executeUpdate();
        }

        @Test
        @DisplayName("Should handle SQL exception during workout creation")
        void shouldHandleSQLExceptionDuringWorkoutCreation() throws SQLException {
            // Given
            Workout workout = createTestWorkout();
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Insert failed"));
            
            // When & Then
            assertThatCode(() -> workoutDAO.createWorkout(workout))
                .doesNotThrowAnyException(); // DAO swallows exceptions
        }
    }

    @Nested
    @DisplayName("Workout Retrieval Tests")  
    class WorkoutRetrievalTests {

        @Test
        @DisplayName("Should retrieve workouts by user ID")
        void shouldRetrieveWorkoutsByUserId() throws SQLException {
            // Given
            int userId = 1;
            setupMockResultSetForWorkouts();
            
            // When
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(userId);
            
            // Then
            assertThat(workouts).isNotEmpty();
            verify(mockPreparedStatement).setInt(1, userId);
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should return empty list when no workouts found")
        void shouldReturnEmptyListWhenNoWorkoutsFound() throws SQLException {
            // Given
            int userId = 1;
            when(mockResultSet.next()).thenReturn(false);
            
            // When
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(userId);
            
            // Then
            assertThat(workouts).isEmpty();
        }

        @Test
        @DisplayName("Should handle SQL exception during retrieval")
        void shouldHandleSQLExceptionDuringRetrieval() throws SQLException {
            // Given
            int userId = 1;
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));
            
            // When
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(userId);
            
            // Then
            assertThat(workouts).isEmpty(); // Returns empty list on exception
        }

        @Test
        @DisplayName("Should map result set to workout correctly")
        void shouldMapResultSetToWorkoutCorrectly() throws SQLException {
            // Given
            int userId = 1;
            setupMockResultSetForSingleWorkout();
            
            // When
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(userId);
            
            // Then
            assertThat(workouts).hasSize(1);
            Workout workout = workouts.get(0);
            assertThat(workout.getWorkoutId()).isEqualTo(1);
            assertThat(workout.getUserId()).isEqualTo(userId);
            assertThat(workout.getExerciseName()).isEqualTo("Running");
            assertThat(workout.getExerciseType()).isEqualTo("cardio");
            assertThat(workout.getDurationMinutes()).isEqualTo(30);
            assertThat(workout.getCaloriesBurned()).isEqualTo(300.0);
        }
    }

    @Nested
    @DisplayName("Workout Date Range Tests")
    class WorkoutDateRangeTests {

        @Test
        @DisplayName("Should retrieve workouts by date range")
        void shouldRetrieveWorkoutsByDateRange() throws SQLException {
            // Given
            int userId = 1;
            Date startDate = Date.valueOf("2023-01-01");
            Date endDate = Date.valueOf("2023-01-31");
            setupMockResultSetForWorkouts();
            
            // When
            List<Workout> workouts = workoutDAO.getWorkoutsByDateRange(userId, startDate, endDate);
            
            // Then
            assertThat(workouts).isNotEmpty();
            verify(mockPreparedStatement).setInt(1, userId);
            verify(mockPreparedStatement).setDate(2, startDate);
            verify(mockPreparedStatement).setDate(3, endDate);
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should handle SQL exception during date range retrieval")
        void shouldHandleSQLExceptionDuringDateRangeRetrieval() throws SQLException {
            // Given
            int userId = 1;
            Date startDate = Date.valueOf("2023-01-01");
            Date endDate = Date.valueOf("2023-01-31");
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));
            
            // When
            List<Workout> workouts = workoutDAO.getWorkoutsByDateRange(userId, startDate, endDate);
            
            // Then
            assertThat(workouts).isEmpty();
        }
    }

    @Nested
    @DisplayName("Analytics Tests")
    class AnalyticsTests {

        @Test
        @DisplayName("Should calculate MET value for exercise")
        void shouldCalculateMetValueForExercise() throws SQLException {
            // Given
            String exerciseName = "Running";
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getDouble("met_value")).thenReturn(10.0);
            
            // When
            double metValue = workoutDAO.getMetValueForExercise(exerciseName);
            
            // Then
            assertThat(metValue).isEqualTo(10.0);
            verify(mockPreparedStatement).setString(1, exerciseName);
        }

        @Test
        @DisplayName("Should return default MET value when exercise not found")
        void shouldReturnDefaultMetValueWhenExerciseNotFound() throws SQLException {
            // Given
            String exerciseName = "Unknown Exercise";
            when(mockResultSet.next()).thenReturn(false);
            
            // When
            double metValue = workoutDAO.getMetValueForExercise(exerciseName);
            
            // Then
            assertThat(metValue).isEqualTo(5.0); // Default value
        }

        @Test
        @DisplayName("Should calculate calories burned")
        void shouldCalculateCaloriesBurned() throws SQLException {
            // Given
            String exerciseName = "Running";
            int durationMinutes = 30;
            double userWeight = 70.0;
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getDouble("met_value")).thenReturn(10.0);
            
            // When
            double calories = workoutDAO.calculateCaloriesBurned(exerciseName, durationMinutes, userWeight);
            
            // Then
            assertThat(calories).isEqualTo(350.0); // 10 * 70 * 0.5 = 350
        }
    }

    // Helper methods
    private Workout createTestWorkout() {
        return new Workout(
            1, // userId
            "Running", // exerciseName
            "cardio", // exerciseType
            0, // setsCount (not applicable for cardio)
            0, // repsPerSet (not applicable for cardio)
            0.0, // weightKg (not applicable for cardio)
            30, // durationMinutes
            Date.valueOf("2023-01-15") // workoutDate
        );
    }

    private void setupMockResultSetForWorkouts() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false); // One workout, then done
        setupMockResultSetForSingleWorkout();
    }

    private void setupMockResultSetForSingleWorkout() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("workout_id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getString("exercise_name")).thenReturn("Running");
        when(mockResultSet.getString("exercise_type")).thenReturn("cardio");
        when(mockResultSet.getInt("sets_count")).thenReturn(0);
        when(mockResultSet.getInt("reps_per_set")).thenReturn(0);
        when(mockResultSet.getDouble("weight_kg")).thenReturn(0.0);
        when(mockResultSet.getInt("duration_minutes")).thenReturn(30);
        when(mockResultSet.getDouble("calories_burned")).thenReturn(300.0);
        when(mockResultSet.getDate("workout_date")).thenReturn(Date.valueOf("2023-01-15"));
        when(mockResultSet.getTime("workout_time")).thenReturn(Time.valueOf("10:00:00"));
        when(mockResultSet.getString("notes")).thenReturn("Good workout");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(mockResultSet.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
    }
}