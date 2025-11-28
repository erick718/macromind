package com.fitness.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.fitness.model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@DisplayName("ProgressDashboardServlet Tests")
public class ProgressDashboardServletTest {

    private ProgressDashboardServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new ProgressDashboardServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        // Setup test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@test.com");
        testUser.setAge(30);
        testUser.setWeight(70.0f);
        testUser.setHeight(175);
        testUser.setGoal("maintain");
        testUser.setFitnessLevel("moderate");

        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getContextPath()).thenReturn("");
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should redirect to login when user is not logged in")
        public void testRedirectToLoginWhenNotLoggedIn() throws ServletException, IOException {
            when(request.getSession(false)).thenReturn(null);

            servlet.doGet(request, response);

            verify(response).sendRedirect("/login.jsp");
            verify(request, never()).getRequestDispatcher(anyString());
        }

        @Test
        @DisplayName("Should redirect to login when session is null")
        public void testRedirectToLoginWhenSessionIsNull() throws ServletException, IOException {
            when(session.getAttribute("user")).thenReturn(null);

            servlet.doGet(request, response);

            verify(response).sendRedirect("/login.jsp");
        }

        @Test
        @DisplayName("Should load dashboard for authenticated user")
        public void testLoadDashboardForAuthenticatedUser() throws ServletException, IOException {
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).getRequestDispatcher("/progress-dashboard.jsp");
            verify(dispatcher).forward(request, response);
        }
    }

    @Nested
    @DisplayName("Date Range Filter Tests")
    class DateRangeFilterTests {

        @Test
        @DisplayName("Should use default 7 days when no range parameter provided")
        public void testDefaultWeekRange() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn(null);
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).setAttribute(eq("dateRange"), eq("week"));
            
            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> endDateCaptor = ArgumentCaptor.forClass(String.class);
            
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            verify(request).setAttribute(eq("endDate"), endDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate end = LocalDate.parse(endDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(7);
            
            assertEquals(expectedStart, start);
            assertEquals(LocalDate.now(), end);
        }

        @Test
        @DisplayName("Should filter by 7 days when range is 'week'")
        public void testWeekRangeFilter() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("week");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(7);
            
            assertEquals(expectedStart, start);
        }

        @Test
        @DisplayName("Should filter by 30 days when range is 'month'")
        public void testMonthRangeFilter() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("month");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(30);
            
            assertEquals(expectedStart, start);
        }

        @Test
        @DisplayName("Should filter by 90 days when range is 'quarter'")
        public void testQuarterRangeFilter() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("quarter");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(90);
            
            assertEquals(expectedStart, start);
        }

        @Test
        @DisplayName("Should use far past date when range is 'all'")
        public void testAllTimeRangeFilter() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("all");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.of(2000, 1, 1);
            
            assertEquals(expectedStart, start);
        }

        @Test
        @DisplayName("Should use custom date range when provided")
        public void testCustomDateRange() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("custom");
            when(request.getParameter("startDate")).thenReturn("2024-01-01");
            when(request.getParameter("endDate")).thenReturn("2024-01-31");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> endDateCaptor = ArgumentCaptor.forClass(String.class);
            
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            verify(request).setAttribute(eq("endDate"), endDateCaptor.capture());
            
            assertEquals("2024-01-01", startDateCaptor.getValue());
            assertEquals("2024-01-31", endDateCaptor.getValue());
        }

        @Test
        @DisplayName("Should swap dates if start is after end")
        public void testSwapDatesIfReversed() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("custom");
            when(request.getParameter("startDate")).thenReturn("2024-01-31");
            when(request.getParameter("endDate")).thenReturn("2024-01-01");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> endDateCaptor = ArgumentCaptor.forClass(String.class);
            
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            verify(request).setAttribute(eq("endDate"), endDateCaptor.capture());
            
            assertEquals("2024-01-01", startDateCaptor.getValue());
            assertEquals("2024-01-31", endDateCaptor.getValue());
        }

        @Test
        @DisplayName("Should cap end date to today if future date provided")
        public void testCapEndDateToToday() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("custom");
            when(request.getParameter("startDate")).thenReturn("2024-01-01");
            when(request.getParameter("endDate")).thenReturn(LocalDate.now().plusDays(10).toString());
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> endDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("endDate"), endDateCaptor.capture());
            
            LocalDate endDate = LocalDate.parse(endDateCaptor.getValue());
            assertEquals(LocalDate.now(), endDate);
        }

        @Test
        @DisplayName("Should default to week range when custom dates are invalid")
        public void testDefaultToWeekWhenInvalidCustomDates() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("custom");
            when(request.getParameter("startDate")).thenReturn("invalid-date");
            when(request.getParameter("endDate")).thenReturn("2024-01-31");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(7);
            
            assertEquals(expectedStart, start);
        }

        @Test
        @DisplayName("Should default to week range when custom dates are empty")
        public void testDefaultToWeekWhenCustomDatesEmpty() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("custom");
            when(request.getParameter("startDate")).thenReturn("");
            when(request.getParameter("endDate")).thenReturn("");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(7);
            
            assertEquals(expectedStart, start);
        }

        @Test
        @DisplayName("Should default to week range when custom dates are null")
        public void testDefaultToWeekWhenCustomDatesNull() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("custom");
            when(request.getParameter("startDate")).thenReturn(null);
            when(request.getParameter("endDate")).thenReturn(null);
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
            verify(request).setAttribute(eq("startDate"), startDateCaptor.capture());
            
            LocalDate start = LocalDate.parse(startDateCaptor.getValue());
            LocalDate expectedStart = LocalDate.now().minusDays(7);
            
            assertEquals(expectedStart, start);
        }
    }

    @Nested
    @DisplayName("Filtered Data Tests")
    class FilteredDataTests {

        @Test
        @DisplayName("Should set filteredProgress attribute")
        public void testSetFilteredProgressAttribute() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("week");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).setAttribute(eq("filteredProgress"), any(Map.class));
        }

        @Test
        @DisplayName("Should set progressMetrics with filtered data")
        public void testSetProgressMetricsWithFilteredData() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("month");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> metricsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(request).setAttribute(eq("progressMetrics"), metricsCaptor.capture());
            
            Map<String, Object> metrics = metricsCaptor.getValue();
            assertNotNull(metrics);
            assertTrue(metrics.containsKey("avgFilteredDailyCalorieBurn"));
            assertTrue(metrics.containsKey("avgFilteredWorkoutDuration"));
            assertTrue(metrics.containsKey("filteredFrequency"));
        }

        @Test
        @DisplayName("Should calculate correct filtered frequency for different ranges")
        public void testCalculateFilteredFrequency() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("quarter");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> metricsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(request).setAttribute(eq("progressMetrics"), metricsCaptor.capture());
            
            Map<String, Object> metrics = metricsCaptor.getValue();
            assertNotNull(metrics.get("filteredFrequency"));
            assertTrue(metrics.get("filteredFrequency") instanceof Double);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should redirect to dashboard on error")
        public void testRedirectOnError() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("week");
            when(request.getRequestDispatcher("/progress-dashboard.jsp"))
                .thenThrow(new RuntimeException("Database error"));

            servlet.doGet(request, response);

            verify(session).setAttribute(eq("error"), anyString());
            verify(response).sendRedirect("/dashboard");
        }

        @Test
        @DisplayName("Should handle null user gracefully")
        public void testHandleNullUserGracefully() throws ServletException, IOException {
            when(session.getAttribute("user")).thenReturn(null);

            servlet.doGet(request, response);

            verify(response).sendRedirect("/login.jsp");
            verify(request, never()).getRequestDispatcher(anyString());
        }
    }

    @Nested
    @DisplayName("Data Accuracy Tests")
    class DataAccuracyTests {

        @Test
        @DisplayName("Should request workout data for correct date range")
        public void testRequestCorrectDateRange() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("week");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            ArgumentCaptor<String> startCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> endCaptor = ArgumentCaptor.forClass(String.class);
            
            verify(request).setAttribute(eq("startDate"), startCaptor.capture());
            verify(request).setAttribute(eq("endDate"), endCaptor.capture());
            
            LocalDate start = LocalDate.parse(startCaptor.getValue());
            LocalDate end = LocalDate.parse(endCaptor.getValue());
            
            // Verify the date range is correct
            assertEquals(7, java.time.temporal.ChronoUnit.DAYS.between(start, end));
        }

        @Test
        @DisplayName("Should maintain weekly and monthly data alongside filtered data")
        public void testMaintainBaselineData() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("quarter");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            // Should still set weekly and monthly progress for comparison
            verify(request).setAttribute(eq("weeklyProgress"), any(Map.class));
            verify(request).setAttribute(eq("monthlyProgress"), any(Map.class));
            verify(request).setAttribute(eq("filteredProgress"), any(Map.class));
        }

        @Test
        @DisplayName("Should set all required attributes")
        public void testSetAllRequiredAttributes() throws ServletException, IOException {
            when(request.getParameter("range")).thenReturn("month");
            when(request.getRequestDispatcher("/progress-dashboard.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).setAttribute(eq("weeklyProgress"), any());
            verify(request).setAttribute(eq("monthlyProgress"), any());
            verify(request).setAttribute(eq("filteredProgress"), any());
            verify(request).setAttribute(eq("exerciseDistribution"), any());
            verify(request).setAttribute(eq("workoutStreak"), any());
            verify(request).setAttribute(eq("recentWorkouts"), any());
            verify(request).setAttribute(eq("progressMetrics"), any());
            verify(request).setAttribute(eq("insights"), any());
            verify(request).setAttribute(eq("startDate"), any());
            verify(request).setAttribute(eq("endDate"), any());
            verify(request).setAttribute(eq("dateRange"), any());
        }
    }
}
