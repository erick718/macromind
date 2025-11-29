package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("LoginServlet Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginServletTest {

    private static UserDAO userDAO;
    private static User testUser;
    private static final String TEST_EMAIL = "login.test@example.com";
    private static final String TEST_PASSWORD = "testPassword123";

    private LoginServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private RequestDispatcher mockDispatcher;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        
        // Create test user for login tests
        testUser = new User("Login Test User", TEST_EMAIL, TEST_PASSWORD);
        userDAO.createUser(testUser);
        testUser = userDAO.getUserByEmail(TEST_EMAIL);
    }

    @AfterAll
    static void tearDownClass() {
        // Clean up test user
        if (testUser != null) {
            userDAO.deleteUser(testUser.getUserId());
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        servlet = new LoginServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString())).thenReturn(mockDispatcher);
    }

    @Nested
    @DisplayName("Successful Login Tests")
    class SuccessfulLoginTests {

        @Test
        @Order(1)
        @DisplayName("Should redirect to dashboard on successful login")
        void shouldRedirectToDashboardOnSuccessfulLogin() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).sendRedirect("dashboard");
        }

        @Test
        @Order(2)
        @DisplayName("Should store user in session on successful login")
        void shouldStoreUserInSessionOnSuccessfulLogin() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).setAttribute(eq("user"), any(User.class));
        }

        @Test
        @Order(3)
        @DisplayName("Should create new session on successful login")
        void shouldCreateNewSessionOnSuccessfulLogin() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getSession();
        }

        @Test
        @Order(4)
        @DisplayName("Should not forward to login page on success")
        void shouldNotForwardToLoginPageOnSuccess() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest, never()).getRequestDispatcher("login.jsp");
        }
    }

    @Nested
    @DisplayName("Failed Login Tests")
    class FailedLoginTests {

        @Test
        @DisplayName("Should forward to login page on invalid password")
        void shouldForwardToLoginPageOnInvalidPassword() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn("wrongPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should set error message for invalid password")
        void shouldSetErrorMessageForInvalidPassword() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn("wrongPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }

        @Test
        @DisplayName("Should forward to login page for non-existent user")
        void shouldForwardToLoginPageForNonExistentUser() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn("nonexistent@example.com");
            when(mockRequest.getParameter("password")).thenReturn("anyPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockDispatcher).forward(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Should set error message for non-existent user")
        void shouldSetErrorMessageForNonExistentUser() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn("nonexistent@example.com");
            when(mockRequest.getParameter("password")).thenReturn("anyPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }

        @Test
        @DisplayName("Should not redirect to dashboard on failed login")
        void shouldNotRedirectToDashboardOnFailedLogin() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn("wrongPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, never()).sendRedirect(anyString());
        }

        @Test
        @DisplayName("Should not store user in session on failed login")
        void shouldNotStoreUserInSessionOnFailedLogin() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn("wrongPassword");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession, never()).setAttribute(eq("user"), any());
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should handle null email")
        void shouldHandleNullEmail() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(null);
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }

        @Test
        @DisplayName("Should handle null password")
        void shouldHandleNullPassword() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }

        @Test
        @DisplayName("Should handle empty email")
        void shouldHandleEmptyEmail() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn("");
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }

        @Test
        @DisplayName("Should handle empty password")
        void shouldHandleEmptyPassword() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn("");

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }

        @Test
        @DisplayName("Should handle whitespace in credentials")
        void shouldHandleWhitespaceInCredentials() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn("  " + TEST_EMAIL + "  ");
            when(mockRequest.getParameter("password")).thenReturn("  " + TEST_PASSWORD + "  ");

            servlet.doPost(mockRequest, mockResponse);

            // Spaces are not trimmed, so this should fail
            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }
    }

    @Nested
    @DisplayName("Case Sensitivity Tests")
    class CaseSensitivityTests {

        @Test
        @DisplayName("Should be case-sensitive for password")
        void shouldBeCaseSensitiveForPassword() throws ServletException, IOException {
            when(mockRequest.getParameter("email")).thenReturn(TEST_EMAIL);
            when(mockRequest.getParameter("password")).thenReturn(TEST_PASSWORD.toUpperCase());

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("login.jsp");
            verify(mockRequest).setAttribute("message", "Invalid email or password");
        }
    }
}
