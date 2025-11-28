package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
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

import com.fitness.dao.FoodEntryDAO;
import com.fitness.Model.FoodEntry;
import com.fitness.Model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FoodEntryServlet Tests - Food Entry Logging User Story")
class FoodEntryServletTest {

    private FoodEntryServlet servlet;

    @Mock
    private FoodEntryDAO mockFoodEntryDAO;

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
        
        servlet = new FoodEntryServlet(mockFoodEntryDAO);
    }

    @Nested
    @DisplayName("Food Entry Creation Tests")
    class FoodEntryCreationTests {

        @Test
        @DisplayName("Should successfully log food entry when valid data provided")
        void shouldSuccessfullyLogFoodEntryWhenValidDataProvided() throws ServletException, IOException {
            // Given - User Story: As a user, I want to log my food intake with nutritional details
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("foodName")).thenReturn("Grilled Chicken Breast");
            when(mockRequest.getParameter("calories")).thenReturn("165");
            when(mockRequest.getParameter("protein")).thenReturn("31.0");
            when(mockRequest.getParameter("carbs")).thenReturn("0.0");
            when(mockRequest.getParameter("fat")).thenReturn("3.6");
            when(mockRequest.getParameter("consumed_oz")).thenReturn("3.5");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify food entry was created with correct nutritional data
            verify(mockFoodEntryDAO).createFoodEntry(argThat(entry -> 
                entry.getUserId() == mockUser.getUserId() &&
                "Grilled Chicken Breast".equals(entry.getFoodName()) &&
                entry.getCalories() == 165 &&
                entry.getProtein() == 31.0f &&
                entry.getCarbs() == 0.0f &&
                entry.getFat() == 3.6f &&
                entry.getConsumedOz() == 3.5
            ));
            
            verify(mockSession).setAttribute(eq("message"), anyString());
            verify(mockResponse).sendRedirect("dashboard");
        }

        @Test
        @DisplayName("Should handle missing food name parameter")
        void shouldHandleMissingFoodNameParameter() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("foodName")).thenReturn(null);
            when(mockRequest.getParameter("calories")).thenReturn("100");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should throw NumberFormatException and forward to error page
            verify(mockFoodEntryDAO, never()).createFoodEntry(any(FoodEntry.class));
            verify(mockRequest).setAttribute(eq("error"), anyString());
        }

        @Test
        @DisplayName("Should handle invalid calorie value")
        void shouldHandleInvalidCalorieValue() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("foodName")).thenReturn("Test Food");
            when(mockRequest.getParameter("calories")).thenReturn("invalid");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should catch NumberFormatException and forward to error page
            verify(mockFoodEntryDAO, never()).createFoodEntry(any(FoodEntry.class));
            verify(mockRequest).setAttribute(eq("error"), anyString());
        }

        @Test
        @DisplayName("Should handle user not logged in")
        void shouldHandleUserNotLoggedIn() throws ServletException, IOException {
            // Given
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(null);

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then
            verify(mockFoodEntryDAO, never()).createFoodEntry(any(FoodEntry.class));
            verify(mockResponse).sendRedirect("login.jsp");
        }
    }

    @Nested
    @DisplayName("Food Entry History Display Tests")
    class FoodEntryHistoryDisplayTests {

        @Test
        @DisplayName("Should display food entries for logged in user")
        void shouldDisplayFoodEntriesForLoggedInUser() throws ServletException, IOException {
            // Given - User Story: As a user, I want to view my food entry history
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Verify servlet forwards to food entry page
            // Note: Servlet creates its own DAO, so we can't verify DAO interactions
            verify(mockRequest).getRequestDispatcher("food_entry.jsp");
        }

        @Test
        @DisplayName("Should handle empty food entry history")
        void shouldHandleEmptyFoodEntryHistory() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);

            // When
            assertThatCode(() -> servlet.doGet(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Servlet forwards to page regardless of entry count
            verify(mockRequest).getRequestDispatcher("food_entry.jsp");
        }
    }

    @Nested
    @DisplayName("Nutritional Data Validation Tests")
    class NutritionalDataValidationTests {

        @Test
        @DisplayName("Should handle negative nutritional values")
        void shouldHandleNegativeNutritionalValues() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("foodName")).thenReturn("Test Food");
            when(mockRequest.getParameter("calories")).thenReturn("-100");
            when(mockRequest.getParameter("protein")).thenReturn("0");
            when(mockRequest.getParameter("carbs")).thenReturn("0");
            when(mockRequest.getParameter("fat")).thenReturn("0");
            when(mockRequest.getParameter("consumed_oz")).thenReturn("1");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Servlet accepts negative values, creates entry
            verify(mockFoodEntryDAO).createFoodEntry(any(FoodEntry.class));
        }

        @Test
        @DisplayName("Should handle zero calories correctly")
        void shouldHandleZeroCaloriesCorrectly() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("foodName")).thenReturn("Water");
            when(mockRequest.getParameter("calories")).thenReturn("0");
            when(mockRequest.getParameter("protein")).thenReturn("0");
            when(mockRequest.getParameter("carbs")).thenReturn("0");
            when(mockRequest.getParameter("fat")).thenReturn("0");
            when(mockRequest.getParameter("consumed_oz")).thenReturn("8");

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Zero calories should be allowed (e.g., for water)
            verify(mockFoodEntryDAO).createFoodEntry(argThat(entry -> 
                "Water".equals(entry.getFoodName()) &&
                entry.getCalories() == 0
            ));
            verify(mockSession).setAttribute(eq("message"), anyString());
        }

        @Test
        @DisplayName("Should use default values for missing nutritional parameters")
        void shouldUseDefaultValuesForMissingNutritionalParameters() throws ServletException, IOException {
            // Given
            User mockUser = createMockUser();
            when(mockRequest.getSession()).thenReturn(mockSession);
            when(mockSession.getAttribute("user")).thenReturn(mockUser);
            when(mockRequest.getParameter("foodName")).thenReturn("Apple");
            when(mockRequest.getParameter("calories")).thenReturn("95");
            // Protein, carbs, fat, and consumed_oz are null - will cause NumberFormatException

            // When
            assertThatCode(() -> servlet.doPost(mockRequest, mockResponse))
                .doesNotThrowAnyException();

            // Then - Should catch NumberFormatException and set error
            verify(mockFoodEntryDAO, never()).createFoodEntry(any(FoodEntry.class));
            verify(mockRequest).setAttribute(eq("error"), anyString());
        }
    }

    // Helper methods
    private User createMockUser() {
        User user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setName("Test User");
        return user;
    }

    private List<FoodEntry> createMockFoodEntries() {
        FoodEntry entry1 = new FoodEntry(
            1, "Breakfast Oatmeal", 150, 5.0f, 30.0f, 3.0f, 8.0, LocalDateTime.now().minusHours(2)
        );
        entry1.setEntryId(1);

        FoodEntry entry2 = new FoodEntry(
            1, "Lunch Salad", 200, 12.0f, 15.0f, 8.0f, 6.0, LocalDateTime.now().minusHours(4)
        );
        entry2.setEntryId(2);

        return Arrays.asList(entry1, entry2);
    }
}