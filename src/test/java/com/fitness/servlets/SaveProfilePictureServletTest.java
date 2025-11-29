package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("SaveProfilePictureServlet Tests")
class SaveProfilePictureServletTest {

    private SaveProfilePictureServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private StringWriter responseWriter;
    private User mockUser;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new SaveProfilePictureServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setName("Test User");

        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(mockUser);
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should reject upload when user not logged in")
        void shouldRejectUploadWhenNotLoggedIn() throws ServletException, IOException {
            when(mockRequest.getSession(false)).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(401);
            assertThat(responseWriter.toString()).contains("User not logged in");
        }

        @Test
        @DisplayName("Should reject upload when session has no user")
        void shouldRejectUploadWhenSessionHasNoUser() throws ServletException, IOException {
            when(mockSession.getAttribute("user")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(401);
            assertThat(responseWriter.toString()).contains("User not logged in");
        }
    }

    @Nested
    @DisplayName("File Validation Tests")
    class FileValidationTests {

        @Test
        @DisplayName("Should reject when no file uploaded")
        void shouldRejectWhenNoFileUploaded() throws ServletException, IOException {
            when(mockRequest.getPart("file")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(400);
            assertThat(responseWriter.toString()).contains("No file uploaded");
        }

        @Test
        @DisplayName("Should reject empty file")
        void shouldRejectEmptyFile() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            when(mockPart.getSize()).thenReturn(0L);
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(400);
            assertThat(responseWriter.toString()).contains("No file uploaded");
        }

        @Test
        @DisplayName("Should reject non-image file")
        void shouldRejectNonImageFile() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("application/pdf");
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(400);
            assertThat(responseWriter.toString()).contains("File must be an image");
        }

        @Test
        @DisplayName("Should reject file with null content type")
        void shouldRejectFileWithNullContentType() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn(null);
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(400);
            assertThat(responseWriter.toString()).contains("File must be an image");
        }

        @Test
        @DisplayName("Should reject non-JPEG/PNG image")
        void shouldRejectNonJpegPngImage() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("image/gif");
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(400);
            assertThat(responseWriter.toString()).contains("Only JPEG or PNG images are allowed");
        }

        @Test
        @DisplayName("Should reject file larger than 10MB")
        void shouldRejectFileLargerThan10MB() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            when(mockPart.getSize()).thenReturn(11L * 1024 * 1024); // 11 MB
            when(mockPart.getContentType()).thenReturn("image/jpeg");
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setStatus(400);
            assertThat(responseWriter.toString()).contains("Max file size is 10 MB");
        }

        @Test
        @DisplayName("Should accept file exactly 10MB")
        void shouldAcceptFileExactly10MB() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            byte[] imageData = new byte[10 * 1024 * 1024]; // Exactly 10 MB
            when(mockPart.getSize()).thenReturn(10L * 1024 * 1024);
            when(mockPart.getContentType()).thenReturn("image/jpeg");
            when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, never()).setStatus(400);
            assertThat(responseWriter.toString()).contains("Profile picture saved");
        }
    }

    @Nested
    @DisplayName("Image Type Tests")
    class ImageTypeTests {

        @Test
        @DisplayName("Should accept JPEG image")
        void shouldAcceptJpegImage() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            byte[] imageData = new byte[1000];
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("image/jpeg");
            when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, never()).setStatus(anyInt());
            assertThat(responseWriter.toString()).contains("Profile picture saved");
        }

        @Test
        @DisplayName("Should accept PNG image")
        void shouldAcceptPngImage() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            byte[] imageData = new byte[1000];
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("image/png");
            when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, never()).setStatus(anyInt());
            assertThat(responseWriter.toString()).contains("Profile picture saved");
        }

        @Test
        @DisplayName("Should accept JPEG with uppercase content type")
        void shouldAcceptJpegWithUppercaseContentType() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            byte[] imageData = new byte[1000];
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("image/JPEG");
            when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, never()).setStatus(anyInt());
            assertThat(responseWriter.toString()).contains("Profile picture saved");
        }
    }

    @Nested
    @DisplayName("Response Format Tests")
    class ResponseFormatTests {

        @Test
        @DisplayName("Should return JSON content type")
        void shouldReturnJsonContentType() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            byte[] imageData = new byte[1000];
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("image/jpeg");
            when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setContentType("application/json");
        }

        @Test
        @DisplayName("Should return valid JSON on success")
        void shouldReturnValidJsonOnSuccess() throws ServletException, IOException {
            Part mockPart = mock(Part.class);
            byte[] imageData = new byte[1000];
            when(mockPart.getSize()).thenReturn(1000L);
            when(mockPart.getContentType()).thenReturn("image/jpeg");
            when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
            when(mockRequest.getPart("file")).thenReturn(mockPart);

            servlet.doPost(mockRequest, mockResponse);

            String response = responseWriter.toString();
            assertThat(response).contains("\"message\"");
            assertThat(response).contains("Profile picture saved");
        }

        @Test
        @DisplayName("Should return valid JSON on error")
        void shouldReturnValidJsonOnError() throws ServletException, IOException {
            when(mockRequest.getPart("file")).thenReturn(null);

            servlet.doPost(mockRequest, mockResponse);

            String response = responseWriter.toString();
            assertThat(response).contains("\"message\"");
            assertThat(response).contains("No file uploaded");
        }
    }
}
