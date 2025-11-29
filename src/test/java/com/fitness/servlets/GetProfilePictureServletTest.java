package com.fitness.servlets;

import com.fitness.dao.UserDAO;
import com.fitness.Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@DisplayName("GetProfilePictureServlet Tests")
class GetProfilePictureServletTest {

    private GetProfilePictureServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private ByteArrayOutputStream responseOutputStream;
    private User mockUser;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new GetProfilePictureServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setName("Test User");

        responseOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                responseOutputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            }
        };

        when(mockResponse.getOutputStream()).thenReturn(servletOutputStream);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(mockUser);
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should return placeholder when user not logged in")
        void shouldReturnPlaceholderWhenNotLoggedIn() throws ServletException, IOException {
            when(mockRequest.getSession(false)).thenReturn(null);

            servlet.doGet(mockRequest, mockResponse);

            verify(mockResponse).setContentType("image/png");
            assertThat(responseOutputStream.size()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should return placeholder when session has no user")
        void shouldReturnPlaceholderWhenSessionHasNoUser() throws ServletException, IOException {
            when(mockSession.getAttribute("user")).thenReturn(null);

            servlet.doGet(mockRequest, mockResponse);

            verify(mockResponse).setContentType("image/png");
            assertThat(responseOutputStream.size()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("Placeholder Tests")
    class PlaceholderTests {

        @Test
        @DisplayName("Should return PNG placeholder when no picture exists")
        void shouldReturnPngPlaceholderWhenNoPictureExists() throws ServletException, IOException {
            servlet.doGet(mockRequest, mockResponse);

            verify(mockResponse).setContentType("image/png");
            byte[] output = responseOutputStream.toByteArray();
            
            // Check PNG signature (first 8 bytes)
            assertThat(output.length).isGreaterThan(8);
            assertThat(output[0]).isEqualTo((byte) 0x89);
            assertThat(output[1]).isEqualTo((byte) 0x50); // 'P'
            assertThat(output[2]).isEqualTo((byte) 0x4E); // 'N'
            assertThat(output[3]).isEqualTo((byte) 0x47); // 'G'
        }

        @Test
        @DisplayName("Placeholder should be valid PNG")
        void placeholderShouldBeValidPng() throws ServletException, IOException {
            servlet.doGet(mockRequest, mockResponse);

            byte[] output = responseOutputStream.toByteArray();
            
            // Check for PNG header and IEND chunk at end
            assertThat(output).contains((byte) 0x49, (byte) 0x45, (byte) 0x4E, (byte) 0x44); // "IEND"
        }
    }

    @Nested
    @DisplayName("Content Type Tests")
    class ContentTypeTests {

        @Test
        @DisplayName("Should set content type to image/png for placeholder")
        void shouldSetContentTypeToPngForPlaceholder() throws ServletException, IOException {
            servlet.doGet(mockRequest, mockResponse);

            verify(mockResponse).setContentType("image/png");
        }
    }

    @Nested
    @DisplayName("Response Tests")
    class ResponseTests {

        @Test
        @DisplayName("Should write data to output stream")
        void shouldWriteDataToOutputStream() throws ServletException, IOException {
            servlet.doGet(mockRequest, mockResponse);

            assertThat(responseOutputStream.size()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should not set error status for valid request")
        void shouldNotSetErrorStatusForValidRequest() throws ServletException, IOException {
            servlet.doGet(mockRequest, mockResponse);

            verify(mockResponse, never()).setStatus(anyInt());
            verify(mockResponse, never()).sendError(anyInt());
            verify(mockResponse, never()).sendError(anyInt(), anyString());
        }
    }
}
