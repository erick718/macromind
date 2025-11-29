package com.fitness.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fitness.Model.User;
import com.fitness.util.DBConnection;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDAO Tests")
class UserDAOTest {

    private UserDAO userDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock 
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = new UserDAO();
        
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
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should return true when email exists in database")
        void shouldReturnTrueWhenEmailExists() throws SQLException {
            // Given
            String email = "test@example.com";
            when(mockResultSet.next()).thenReturn(true); // Email exists
            
            // When
            boolean result = userDAO.isEmailTaken(email);
            
            // Then
            assertThat(result).isTrue();
            verify(mockPreparedStatement).setString(1, email);
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should return false when email does not exist in database")
        void shouldReturnFalseWhenEmailDoesNotExist() throws SQLException {
            // Given
            String email = "nonexistent@example.com";
            when(mockResultSet.next()).thenReturn(false); // Email doesn't exist
            
            // When
            boolean result = userDAO.isEmailTaken(email);
            
            // Then
            assertThat(result).isFalse();
            verify(mockPreparedStatement).setString(1, email);
        }

        @Test
        @DisplayName("Should handle database connection error gracefully")
        void shouldHandleDatabaseConnectionError() throws SQLException {
            // Given
            mockedDBConnection.when(DBConnection::getConnection)
                .thenThrow(new SQLException("Database connection failed"));
            
            // When
            boolean result = userDAO.isEmailTaken("test@example.com");
            
            // Then
            assertThat(result).isFalse(); // Should return false as fallback
        }

        @Test
        @DisplayName("Should handle SQL exception during query execution")
        void shouldHandleSQLExceptionDuringQuery() throws SQLException {
            // Given
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));
            
            // When
            boolean result = userDAO.isEmailTaken("test@example.com");
            
            // Then
            assertThat(result).isFalse(); // Should return false as fallback
        }
    }

    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {

        @Test
        @DisplayName("Should successfully create a new user")
        void shouldSuccessfullyCreateNewUser() throws SQLException {
            // Given
            User newUser = createTestUser();
            ResultSet mockGeneratedKeys = mock(ResultSet.class);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
            when(mockGeneratedKeys.next()).thenReturn(true);
            when(mockGeneratedKeys.getInt(1)).thenReturn(123);
            
            // When
            userDAO.createUser(newUser);
            
            // Then
            verify(mockPreparedStatement).setString(1, newUser.getName());
            verify(mockPreparedStatement).setString(2, newUser.getEmail());
            verify(mockPreparedStatement).setString(3, newUser.getPassword());
            verify(mockPreparedStatement).executeUpdate();
            assertThat(newUser.getUserId()).isEqualTo(123);
        }

        @Test
        @DisplayName("Should handle SQL exception during user creation")
        void shouldHandleSQLExceptionDuringUserCreation() throws SQLException {
            // Given
            User newUser = createTestUser();
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Insert failed"));
            
            // When & Then
            assertThatCode(() -> userDAO.createUser(newUser))
                .doesNotThrowAnyException(); // Method doesn't throw, just prints stack trace
        }
    }

    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {

        @Test
        @DisplayName("Should successfully retrieve user by email")
        void shouldSuccessfullyRetrieveUserByEmail() throws SQLException {
            // Given
            String email = "test@example.com";
            User expectedUser = createTestUser();
            expectedUser.setUserId(1);
            
            when(mockResultSet.next()).thenReturn(true);
            setupMockResultSetForUser(mockResultSet, expectedUser);
            
            // When
            User result = userDAO.getUserByEmail(email);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getName()).isEqualTo(expectedUser.getName());
            verify(mockPreparedStatement).setString(1, email);
        }

        @Test
        @DisplayName("Should return null when user not found by email")
        void shouldReturnNullWhenUserNotFoundByEmail() throws SQLException {
            // Given
            String email = "nonexistent@example.com";
            when(mockResultSet.next()).thenReturn(false);
            
            // When
            User result = userDAO.getUserByEmail(email);
            
            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle SQL exception during user retrieval")
        void shouldHandleSQLExceptionDuringUserRetrieval() throws SQLException {
            // Given
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Select failed"));
            
            // When
            User result = userDAO.getUserByEmail("test@example.com");
            
            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("User Profile Update Tests")
    class UserProfileUpdateTests {

        @Test
        @DisplayName("Should successfully update user profile")
        void shouldSuccessfullyUpdateUserProfile() throws SQLException {
            // Given
            User user = createTestUser();
            user.setUserId(1);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row updated
            
            // When & Then
            assertThatCode(() -> userDAO.updateUserProfile(user))
                .doesNotThrowAnyException();
            
            verify(mockPreparedStatement).setInt(1, user.getAge());
            verify(mockPreparedStatement).setFloat(2, user.getWeight());
            verify(mockPreparedStatement).setInt(3, user.getHeight());
            verify(mockPreparedStatement).setString(4, user.getGoal());
            verify(mockPreparedStatement).setString(5, user.getDietaryPreference());
            verify(mockPreparedStatement).setString(6, user.getFitnessLevel());
            verify(mockPreparedStatement).setInt(7, user.getAvailability());
            verify(mockPreparedStatement).setInt(8, user.getUserId());
        }

        @Test
        @DisplayName("Should handle SQL exception during profile update")
        void shouldHandleSQLExceptionDuringProfileUpdate() throws SQLException {
            // Given
            User user = createTestUser();
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Update failed"));
            
            // When & Then
            assertThatCode(() -> userDAO.updateUserProfile(user))
                .doesNotThrowAnyException(); // Method handles exception internally
        }
    }

    // Helper methods
    private User createTestUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword123");
        user.setAge(25);
        user.setWeight(70.0f);
        user.setHeight(175);
        user.setGoal("maintain");
        user.setFitnessLevel("moderate");
        return user;
    }

    private void setupMockResultSetForUser(ResultSet rs, User user) throws SQLException {
        when(rs.getInt("user_id")).thenReturn(user.getUserId());
        when(rs.getString("username")).thenReturn(user.getName()); // Fixed: DAO uses "username", not "name"
        when(rs.getString("email")).thenReturn(user.getEmail());
        when(rs.getString("password")).thenReturn(user.getPassword());
        when(rs.getInt("age")).thenReturn(user.getAge());
        when(rs.getFloat("weight")).thenReturn(user.getWeight());
        when(rs.getInt("height")).thenReturn(user.getHeight());
        when(rs.getString("goal")).thenReturn(user.getGoal());
        when(rs.getString("dietary_preference")).thenReturn(user.getDietaryPreference());
        when(rs.getString("fitness_level")).thenReturn(user.getFitnessLevel());
        when(rs.getInt("availability")).thenReturn(user.getAvailability());
    }
}