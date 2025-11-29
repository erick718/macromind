package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("ProfileServlet Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileServletTest {

    private static UserDAO userDAO;
    private static User testUser;

    private ProfileServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        // Create a test user for profile updates
        testUser = new User("Profile Test User", "profile.test@example.com", "password123");
        userDAO.createUser(testUser);
        testUser = userDAO.getUserByEmail("profile.test@example.com");
    }

    @AfterAll
    static void tearDownClass() {
        if (testUser != null) {
            userDAO.deleteUser(testUser.getUserId());
        }
    }

    @BeforeEach
    void setUp() {
        servlet = new ProfileServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getContextPath()).thenReturn("");
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should redirect to login when user not in session")
        void shouldRedirectToLoginWhenNotAuthenticated() throws ServletException, IOException {
            when(mockSession.getAttribute("user")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/login.jsp");
        }

        @Test
        @DisplayName("Should not process profile when user not in session")
        void shouldNotProcessProfileWhenNotAuthenticated() throws ServletException, IOException {
            when(mockSession.getAttribute("user")).thenReturn(null);
            when(mockRequest.getParameter("age")).thenReturn("25");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession, never()).setAttribute(eq("user"), any());
        }
    }

    @Nested
    @DisplayName("Successful Profile Update Tests")
    class SuccessfulProfileUpdateTests {

        @Test
        @Order(1)
        @DisplayName("Should update all profile fields successfully")
        void shouldUpdateAllProfileFields() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("30");
            when(mockRequest.getParameter("height")).thenReturn("180");
            when(mockRequest.getParameter("weight")).thenReturn("75.5");
            when(mockRequest.getParameter("goal")).thenReturn("2000");
            when(mockRequest.getParameter("activity")).thenReturn("moderate");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
            verify(mockSession).setAttribute(eq("message"), eq("Profile updated successfully!"));
        }

        @Test
        @Order(2)
        @DisplayName("Should update session with new user data")
        void shouldUpdateSessionWithNewUserData() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("28");
            when(mockRequest.getParameter("height")).thenReturn("175");
            when(mockRequest.getParameter("weight")).thenReturn("70.0");
            when(mockRequest.getParameter("goal")).thenReturn("1800");
            when(mockRequest.getParameter("activity")).thenReturn("light");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute(eq("user"), eq(user));
        }

        @Test
        @Order(3)
        @DisplayName("Should redirect to dashboard after successful update")
        void shouldRedirectToDashboardAfterSuccess() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("25");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
        }

        @Test
        @Order(4)
        @DisplayName("Should set success message on successful update")
        void shouldSetSuccessMessage() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("26");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute("message", "Profile updated successfully!");
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should handle invalid age (non-numeric)")
        void shouldHandleInvalidAge() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("invalid");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute(eq("error"), anyString());
            verify(mockResponse).sendRedirect("/dashboard");
        }

        @Test
        @DisplayName("Should handle invalid height (non-numeric)")
        void shouldHandleInvalidHeight() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("height")).thenReturn("not a number");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute(eq("error"), anyString());
        }

        @Test
        @DisplayName("Should handle invalid weight (non-numeric)")
        void shouldHandleInvalidWeight() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("weight")).thenReturn("abc");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute(eq("error"), anyString());
        }

        @Test
        @DisplayName("Should accept goal as string")
        void shouldAcceptGoalAsString() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("goal")).thenReturn("lose weight");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute("message", "Profile updated successfully!");
        }

        @Test
        @DisplayName("Should handle empty string inputs")
        void shouldHandleEmptyStringInputs() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("");
            when(mockRequest.getParameter("height")).thenReturn("");
            when(mockRequest.getParameter("weight")).thenReturn("");
            when(mockRequest.getParameter("goal")).thenReturn("");
            when(mockRequest.getParameter("activity")).thenReturn("");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
            // Should not set error since empty strings are skipped
            verify(mockSession, never()).setAttribute(eq("error"), anyString());
        }

        @Test
        @DisplayName("Should handle whitespace-only inputs")
        void shouldHandleWhitespaceInputs() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("   ");
            when(mockRequest.getParameter("height")).thenReturn("  ");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
            // Should not set error since whitespace is trimmed to empty
            verify(mockSession, never()).setAttribute(eq("error"), anyString());
        }

        @Test
        @DisplayName("Should handle null parameters")
        void shouldHandleNullParameters() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn(null);
            when(mockRequest.getParameter("height")).thenReturn(null);
            when(mockRequest.getParameter("weight")).thenReturn(null);
            when(mockRequest.getParameter("goal")).thenReturn(null);
            when(mockRequest.getParameter("activity")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
            // Should not set error since nulls are skipped
            verify(mockSession, never()).setAttribute(eq("error"), anyString());
        }
    }

    @Nested
    @DisplayName("Partial Update Tests")
    class PartialUpdateTests {

        @Test
        @DisplayName("Should update only age when other fields are null")
        void shouldUpdateOnlyAge() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("35");
            when(mockRequest.getParameter("height")).thenReturn(null);
            when(mockRequest.getParameter("weight")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
            verify(mockSession).setAttribute("message", "Profile updated successfully!");
        }

        @Test
        @DisplayName("Should update only height when other fields are null")
        void shouldUpdateOnlyHeight() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn(null);
            when(mockRequest.getParameter("height")).thenReturn("165");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
        }

        @Test
        @DisplayName("Should update only weight when other fields are null")
        void shouldUpdateOnlyWeight() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("weight")).thenReturn("68.5");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
        }

        @Test
        @DisplayName("Should update only activity when other fields are null")
        void shouldUpdateOnlyActivity() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("activity")).thenReturn("sedentary");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("/dashboard");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @Order(1)
        @DisplayName("Should persist profile updates to database")
        void shouldPersistProfileUpdatesToDatabase() throws ServletException, IOException {
            User user = new User(testUser.getName(), testUser.getEmail(), "password");
            user.setUserId(testUser.getUserId());
            
            when(mockSession.getAttribute("user")).thenReturn(user);
            when(mockRequest.getParameter("age")).thenReturn("32");
            when(mockRequest.getParameter("height")).thenReturn("185");
            when(mockRequest.getParameter("weight")).thenReturn("82.0");
            when(mockRequest.getParameter("goal")).thenReturn("gain muscle");
            when(mockRequest.getParameter("activity")).thenReturn("active");

            servlet.doPost(mockRequest, mockResponse);

            // Verify the updates persisted
            User updatedUser = userDAO.getUserByEmail(testUser.getEmail());
            assertThat(updatedUser).isNotNull();
            assertThat(updatedUser.getAge()).isEqualTo(32);
            assertThat(updatedUser.getHeight()).isEqualTo(185);
            assertThat(updatedUser.getWeight()).isEqualTo(82.0f);
            assertThat(updatedUser.getGoal()).isEqualTo("gain muscle");
            assertThat(updatedUser.getFitnessLevel()).isEqualTo("active");
        }
    }
}
