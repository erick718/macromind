package com.fitness.dao;

import com.fitness.model.FoodEntry;
import com.fitness.util.DBConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FoodEntryDAO Tests")
class FoodEntryDAOTest {

    private FoodEntryDAO foodEntryDAO;
    
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
        foodEntryDAO = new FoodEntryDAO();
        
        // Setup static mock for DBConnection
        mockedDBConnection = mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        
        // Setup common mock behavior with lenient stubbing
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        lenient().when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        lenient().when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Mock the DatabaseMetaData to prevent NullPointerException
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
    @DisplayName("Food Entry Creation Tests")
    class FoodEntryCreationTests {

        @Test
        @DisplayName("Should successfully create new food entry")
        void shouldSuccessfullyCreateNewFoodEntry() throws SQLException {
            // Given
            FoodEntry entry = createTestFoodEntry();
            
            // When
            assertThatCode(() -> foodEntryDAO.createFoodEntry(entry))
                .doesNotThrowAnyException();
            
            // Then
            verify(mockPreparedStatement).setInt(1, entry.getUserId());
            verify(mockPreparedStatement).setString(2, entry.getFoodName());
            verify(mockPreparedStatement).setInt(3, entry.getCalories());
            verify(mockPreparedStatement).setFloat(4, (float) entry.getProtein());
            verify(mockPreparedStatement).setFloat(5, (float) entry.getCarbs());
            verify(mockPreparedStatement).setFloat(6, (float) entry.getFat());
            verify(mockPreparedStatement).setDouble(7, (float) entry.getConsumedOz());
            verify(mockPreparedStatement).executeUpdate();
        }

        @Test
        @DisplayName("Should handle SQL exception during food entry creation")
        void shouldHandleSQLExceptionDuringFoodEntryCreation() throws SQLException {
            // Given
            FoodEntry entry = createTestFoodEntry();
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Insert failed"));
            
            // When & Then
            assertThatCode(() -> foodEntryDAO.createFoodEntry(entry))
                .doesNotThrowAnyException(); // DAO swallows exceptions
        }
    }

    @Nested
    @DisplayName("Food Entry Retrieval Tests")  
    class FoodEntryRetrievalTests {

        @Test
        @DisplayName("Should retrieve food entries for user")
        void shouldRetrieveFoodEntriesForUser() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.now();
            setupMockResultSetForFoodEntries();
            
            // When
            List<FoodEntry> entries = foodEntryDAO.getFoodEntriesByUser(userId, date);
            
            // Then
            assertThat(entries).hasSize(1);
            verify(mockPreparedStatement).setInt(1, userId);
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should return empty list when no entries found")
        void shouldReturnEmptyListWhenNoEntriesFound() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.now();
            when(mockResultSet.next()).thenReturn(false);
            
            // When
            List<FoodEntry> entries = foodEntryDAO.getFoodEntriesByUser(userId, date);
            
            // Then
            assertThat(entries).isEmpty();
        }

        @Test
        @DisplayName("Should handle SQL exception during retrieval")
        void shouldHandleSQLExceptionDuringRetrieval() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.now();
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));
            
            // When
            List<FoodEntry> entries = foodEntryDAO.getFoodEntriesByUser(userId, date);
            
            // Then
            assertThat(entries).isEmpty(); // Returns empty list on exception
        }

        @Test
        @DisplayName("Should map result set to food entry correctly")
        void shouldMapResultSetToFoodEntryCorrectly() throws SQLException {
            // Given
            int userId = 1;
            LocalDate date = LocalDate.now();
            setupMockResultSetForSingleEntry();
            
            // When
            List<FoodEntry> entries = foodEntryDAO.getFoodEntriesByUser(userId, date);
            
            // Then
            assertThat(entries).hasSize(1);
            FoodEntry entry = entries.get(0);
            assertThat(entry.getEntryId()).isEqualTo(1);
            assertThat(entry.getUserId()).isEqualTo(userId);
            assertThat(entry.getFoodName()).isEqualTo("Test Food");
            assertThat(entry.getCalories()).isEqualTo(250);
            assertThat(entry.getProtein()).isEqualTo(20.0f);
            assertThat(entry.getCarbs()).isEqualTo(30.0f);
            assertThat(entry.getFat()).isEqualTo(10.0f);
            assertThat(entry.getConsumedOz()).isEqualTo(8.0);
        }
    }

    // Helper methods
    private FoodEntry createTestFoodEntry() {
        return new FoodEntry(
            1, // userId
            "Test Food",
            250, // calories  
            20.0f, // protein
            30.0f, // carbs
            10.0f, // fat
            8.0, // consumed oz
            LocalDateTime.now()
        );
    }

    private void setupMockResultSetForFoodEntries() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false); // One entry, then done
        setupMockResultSetForSingleEntry();
    }

    private void setupMockResultSetForSingleEntry() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getString("food_name")).thenReturn("Test Food");
        when(mockResultSet.getInt("calories")).thenReturn(250);
        when(mockResultSet.getFloat("protein")).thenReturn(20.0f);
        when(mockResultSet.getFloat("carbs")).thenReturn(30.0f);
        when(mockResultSet.getFloat("fat")).thenReturn(10.0f);
        when(mockResultSet.getFloat("consumed_oz")).thenReturn(8.0f);
        when(mockResultSet.getTimestamp("date_time")).thenReturn(Timestamp.valueOf("2023-01-15 10:30:00"));
    }
}