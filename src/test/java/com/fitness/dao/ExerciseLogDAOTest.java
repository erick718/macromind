package com.fitness.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fitness.util.DBConnection;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExerciseLogDAO Tests")
class ExerciseLogDAOTest {

    private ExerciseLogDAO exerciseLogDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock 
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Mock
    private DatabaseMetaData mockMetaData;
    
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        exerciseLogDAO = new ExerciseLogDAO();
        
        // Setup static mock for DBConnection
        mockedDBConnection = mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        
        // Setup common mock behavior with lenient stubbing
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        lenient().when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        // Mock the DatabaseMetaData to prevent any potential issues
        lenient().when(mockConnection.getMetaData()).thenReturn(mockMetaData);
        lenient().when(mockMetaData.getURL()).thenReturn("jdbc:mock://localhost/test");
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Nested
    @DisplayName("Daily Calories Burned Tests")
    class DailyCaloriesBurnedTests {

        @Test
        @DisplayName("Should return calories burned from daily fitness summary")
        void shouldReturnCaloriesBurnedFromDailyFitnessSummary() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.of(2023, 1, 15);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getDouble("total_calories_burned")).thenReturn(450.5);
            
            // When
            int totalCalories = exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(userId, date);
            
            // Then
            assertThat(totalCalories).isEqualTo(451); // Rounded
            verify(mockPreparedStatement).setInt(1, userId);
            verify(mockPreparedStatement).setDate(2, Date.valueOf(date));
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should return zero when no fitness summary found")
        void shouldReturnZeroWhenNoFitnessSummaryFound() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.of(2023, 1, 15);
            when(mockResultSet.next()).thenReturn(false);
            
            // When
            int totalCalories = exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(userId, date);
            
            // Then
            assertThat(totalCalories).isZero();
            verify(mockPreparedStatement).setInt(1, userId);
            verify(mockPreparedStatement).setDate(2, Date.valueOf(date));
        }

        @Test
        @DisplayName("Should handle SQL exception gracefully")
        void shouldHandleSQLExceptionGracefully() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.of(2023, 1, 15);
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Connection failed"));
            
            // When
            int totalCalories = exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(userId, date);
            
            // Then
            assertThat(totalCalories).isZero(); // Returns zero on exception
            verify(mockPreparedStatement).setInt(1, userId);
            verify(mockPreparedStatement).setDate(2, Date.valueOf(date));
        }

        @Test
        @DisplayName("Should round calories correctly")
        void shouldRoundCaloriesCorrectly() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.of(2023, 1, 15);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getDouble("total_calories_burned")).thenReturn(299.4);
            
            // When
            int totalCalories = exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(userId, date);
            
            // Then
            assertThat(totalCalories).isEqualTo(299); // Rounded down
        }

        @Test
        @DisplayName("Should handle zero calories")
        void shouldHandleZeroCalories() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.of(2023, 1, 15);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getDouble("total_calories_burned")).thenReturn(0.0);
            
            // When
            int totalCalories = exerciseLogDAO.getTotalCaloriesBurnedByUserAndDate(userId, date);
            
            // Then
            assertThat(totalCalories).isZero();
        }
    }
}