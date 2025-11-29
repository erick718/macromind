package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.Model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("RegisterServlet Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterServletTest {

    private static UserDAO userDAO;
    private static final String TEST_EMAIL_PREFIX = "register.test";
    private static int testCounter = 0;

    private RegisterServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private RequestDispatcher mockDispatcher;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
    }

    @AfterAll
    static void tearDownClass() {
        // Clean up any test users created
        // In a real scenario, you might track created user IDs
    }

    @BeforeEach
    void setUp() throws IOException {
        servlet = new RegisterServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString())).thenReturn(mockDispatcher);
        
        testCounter++;
    }

    @Nested
    @DisplayName("Successful Registration Tests")
    class SuccessfulRegistrationTests {

        @Test
        @Order(1)
        @DisplayName("Should redirect to profile page on successful registration")
        void shouldRedirectToProfilePageOnSuccess() throws ServletException, IOException {
            String email = TEST_EMAIL_PREFIX + testCounter + "@example.com";
            when(mockRequest.getParameter("username")).thenReturn("Test User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("password123");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("profile.jsp");
            
            // Cleanup
            User createdUser = userDAO.getUserByEmail(email);
            if (createdUser != null) {
                userDAO.deleteUser(createdUser.getUserId());
            }
        }

        @Test
        @Order(2)
        @DisplayName("Should store user in session on successful registration")
        void shouldStoreUserInSessionOnSuccess() throws ServletException, IOException {
            String email = TEST_EMAIL_PREFIX + testCounter + "@example.com";
            when(mockRequest.getParameter("username")).thenReturn("Test User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("password123");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute(eq("user"), any(User.class));
            
            // Cleanup
            User createdUser = userDAO.getUserByEmail(email);
            if (createdUser != null) {
                userDAO.deleteUser(createdUser.getUserId());
            }
        }

        @Test
        @Order(3)
        @DisplayName("Should create new session on successful registration")
        void shouldCreateNewSessionOnSuccess() throws ServletException, IOException {
            String email = TEST_EMAIL_PREFIX + testCounter + "@example.com";
            when(mockRequest.getParameter("username")).thenReturn("Test User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("password123");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getSession();
            
            // Cleanup
            User createdUser = userDAO.getUserByEmail(email);
            if (createdUser != null) {
                userDAO.deleteUser(createdUser.getUserId());
            }
        }

        @Test
        @Order(4)
        @DisplayName("Should not forward to register page on success")
        void shouldNotForwardToRegisterPageOnSuccess() throws ServletException, IOException {
            String email = TEST_EMAIL_PREFIX + testCounter + "@example.com";
            when(mockRequest.getParameter("username")).thenReturn("Test User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("password123");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest, never()).getRequestDispatcher("register.jsp");
            
            // Cleanup
            User createdUser = userDAO.getUserByEmail(email);
            if (createdUser != null) {
                userDAO.deleteUser(createdUser.getUserId());
            }
        }
    }

    @Nested
    @DisplayName("Duplicate Email Tests")
    class DuplicateEmailTests {

        @Test
        @DisplayName("Should reject registration with existing email")
        void shouldRejectRegistrationWithExistingEmail() throws ServletException, IOException {
            // Create a user first
            String email = TEST_EMAIL_PREFIX + ".duplicate@example.com";
            User existingUser = new User("Existing User", email, "password123");
            userDAO.createUser(existingUser);
            existingUser = userDAO.getUserByEmail(email);

            // Try to register with same email
            when(mockRequest.getParameter("username")).thenReturn("New User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("newPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("register.jsp");
            verify(mockDispatcher).forward(mockRequest, mockResponse);
            
            // Cleanup
            userDAO.deleteUser(existingUser.getUserId());
        }

        @Test
        @DisplayName("Should set error message for duplicate email")
        void shouldSetErrorMessageForDuplicateEmail() throws ServletException, IOException {
            // Create a user first
            String email = TEST_EMAIL_PREFIX + ".duplicate2@example.com";
            User existingUser = new User("Existing User", email, "password123");
            userDAO.createUser(existingUser);
            existingUser = userDAO.getUserByEmail(email);

            // Try to register with same email
            when(mockRequest.getParameter("username")).thenReturn("New User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("newPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).setAttribute("message", "Email is already registered");
            
            // Cleanup
            userDAO.deleteUser(existingUser.getUserId());
        }

        @Test
        @DisplayName("Should not redirect on duplicate email")
        void shouldNotRedirectOnDuplicateEmail() throws ServletException, IOException {
            // Create a user first
            String email = TEST_EMAIL_PREFIX + ".duplicate3@example.com";
            User existingUser = new User("Existing User", email, "password123");
            userDAO.createUser(existingUser);
            existingUser = userDAO.getUserByEmail(email);

            // Try to register with same email
            when(mockRequest.getParameter("username")).thenReturn("New User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("newPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, never()).sendRedirect(anyString());
            
            // Cleanup
            userDAO.deleteUser(existingUser.getUserId());
        }

        @Test
        @DisplayName("Should not store user in session on duplicate email")
        void shouldNotStoreUserInSessionOnDuplicateEmail() throws ServletException, IOException {
            // Create a user first
            String email = TEST_EMAIL_PREFIX + ".duplicate4@example.com";
            User existingUser = new User("Existing User", email, "password123");
            userDAO.createUser(existingUser);
            existingUser = userDAO.getUserByEmail(email);

            // Try to register with same email
            when(mockRequest.getParameter("username")).thenReturn("New User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("newPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession, never()).setAttribute(eq("user"), any());
            
            // Cleanup
            userDAO.deleteUser(existingUser.getUserId());
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should handle registration with all valid fields")
        void shouldHandleRegistrationWithAllValidFields() throws ServletException, IOException {
            String email = TEST_EMAIL_PREFIX + ".allfields@example.com";
            when(mockRequest.getParameter("username")).thenReturn("Complete Name");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("strongPassword123");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("profile.jsp");
            
            // Cleanup
            User createdUser = userDAO.getUserByEmail(email);
            if (createdUser != null) {
                userDAO.deleteUser(createdUser.getUserId());
            }
        }

        @Test
        @DisplayName("Should accept various email formats")
        void shouldAcceptVariousEmailFormats() throws ServletException, IOException {
            String[] validEmails = {
                TEST_EMAIL_PREFIX + ".test1@example.com",
                TEST_EMAIL_PREFIX + ".test-2@example.com",
                TEST_EMAIL_PREFIX + ".test_3@example.co.uk"
            };

            for (String email : validEmails) {
                setUp(); // Reset mocks
                when(mockRequest.getParameter("username")).thenReturn("Test User");
                when(mockRequest.getParameter("email")).thenReturn(email);
                when(mockRequest.getParameter("password")).thenReturn("password123");

                servlet.doPost(mockRequest, mockResponse);

                verify(mockResponse).sendRedirect("profile.jsp");
                
                // Cleanup
                User createdUser = userDAO.getUserByEmail(email);
                if (createdUser != null) {
                    userDAO.deleteUser(createdUser.getUserId());
                }
            }
        }

        @Test
        @DisplayName("Should accept various username lengths")
        void shouldAcceptVariousUsernameLengths() throws ServletException, IOException {
            String[] usernames = {
                "A",
                "Short Name",
                "Very Long Name With Multiple Words And Characters"
            };

            for (int i = 0; i < usernames.length; i++) {
                setUp(); // Reset mocks
                String email = TEST_EMAIL_PREFIX + ".username" + i + "@example.com";
                when(mockRequest.getParameter("username")).thenReturn(usernames[i]);
                when(mockRequest.getParameter("email")).thenReturn(email);
                when(mockRequest.getParameter("password")).thenReturn("password123");

                servlet.doPost(mockRequest, mockResponse);

                verify(mockResponse).sendRedirect("profile.jsp");
                
                // Cleanup
                User createdUser = userDAO.getUserByEmail(email);
                if (createdUser != null) {
                    userDAO.deleteUser(createdUser.getUserId());
                }
            }
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should create user in database on registration")
        void shouldCreateUserInDatabaseOnRegistration() throws ServletException, IOException {
            String email = TEST_EMAIL_PREFIX + ".integration@example.com";
            when(mockRequest.getParameter("username")).thenReturn("Integration Test User");
            when(mockRequest.getParameter("email")).thenReturn(email);
            when(mockRequest.getParameter("password")).thenReturn("password123");

            servlet.doPost(mockRequest, mockResponse);

            // Verify user exists in database
            User createdUser = userDAO.getUserByEmail(email);
            org.junit.jupiter.api.Assertions.assertNotNull(createdUser);
            org.junit.jupiter.api.Assertions.assertEquals("Integration Test User", createdUser.getName());
            org.junit.jupiter.api.Assertions.assertEquals(email, createdUser.getEmail());
            
            // Cleanup
            userDAO.deleteUser(createdUser.getUserId());
        }
    }
}
