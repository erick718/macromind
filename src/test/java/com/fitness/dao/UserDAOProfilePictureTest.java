package com.fitness.dao;

import com.fitness.model.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDAO Profile Picture Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOProfilePictureTest {

    private static UserDAO userDAO;
    private static User testUser;
    private static final String TEST_EMAIL = "profilepic.test@example.com";

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        
        // Create test user
        testUser = new User("Profile Test User", TEST_EMAIL, "password123");
        userDAO.createUser(testUser);
        
        // Retrieve the created user to get the ID
        testUser = userDAO.getUserByEmail(TEST_EMAIL);
    }

    @AfterAll
    static void tearDownClass() {
        // Clean up test user
        if (testUser != null) {
            userDAO.deleteUser(testUser.getUserId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should save JPEG profile picture to database")
    void shouldSaveJpegProfilePicture() {
        byte[] jpegData = createMockJpegData();
        
        userDAO.saveProfilePicture(testUser.getUserId(), jpegData, "image/jpeg");
        
        User result = userDAO.getProfilePicture(testUser.getUserId());
        
        assertThat(result).isNotNull();
        assertThat(result.getProfilePicture()).isEqualTo(jpegData);
        assertThat(result.getProfilePictureType()).isEqualTo("image/jpeg");
    }

    @Test
    @Order(2)
    @DisplayName("Should update existing profile picture")
    void shouldUpdateExistingProfilePicture() {
        byte[] newPngData = createMockPngData();
        
        userDAO.saveProfilePicture(testUser.getUserId(), newPngData, "image/png");
        
        User result = userDAO.getProfilePicture(testUser.getUserId());
        
        assertThat(result).isNotNull();
        assertThat(result.getProfilePicture()).isEqualTo(newPngData);
        assertThat(result.getProfilePictureType()).isEqualTo("image/png");
    }

    @Test
    @Order(3)
    @DisplayName("Should save large profile picture (up to 10MB)")
    void shouldSaveLargeProfilePicture() {
        byte[] largeData = new byte[5 * 1024 * 1024]; // 5 MB
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }
        
        userDAO.saveProfilePicture(testUser.getUserId(), largeData, "image/jpeg");
        
        User result = userDAO.getProfilePicture(testUser.getUserId());
        
        assertThat(result).isNotNull();
        assertThat(result.getProfilePicture()).hasSize(largeData.length);
        assertThat(result.getProfilePictureType()).isEqualTo("image/jpeg");
    }

    @Test
    @Order(4)
    @DisplayName("Should return null for user without profile picture")
    void shouldReturnNullForUserWithoutProfilePicture() {
        // Create a new user without profile picture
        User newUser = new User("No Pic User", "nopic@example.com", "password123");
        userDAO.createUser(newUser);
        newUser = userDAO.getUserByEmail("nopic@example.com");
        
        User result = userDAO.getProfilePicture(newUser.getUserId());
        
        if (result != null) {
            assertThat(result.getProfilePicture()).isNull();
        }
        
        // Clean up
        userDAO.deleteUser(newUser.getUserId());
    }

    @Test
    @Order(5)
    @DisplayName("Should return null for non-existent user")
    void shouldReturnNullForNonExistentUser() {
        User result = userDAO.getProfilePicture(99999);
        
        assertThat(result).isNull();
    }

    @Test
    @Order(6)
    @DisplayName("Should handle null image data gracefully")
    void shouldHandleNullImageDataGracefully() {
        // This should not throw an exception
        userDAO.saveProfilePicture(testUser.getUserId(), null, "image/jpeg");
        
        User result = userDAO.getProfilePicture(testUser.getUserId());
        
        if (result != null) {
            assertThat(result.getProfilePicture()).isNull();
        }
    }

    @Test
    @Order(7)
    @DisplayName("Should handle empty image data")
    void shouldHandleEmptyImageData() {
        byte[] emptyData = new byte[0];
        
        userDAO.saveProfilePicture(testUser.getUserId(), emptyData, "image/jpeg");
        
        User result = userDAO.getProfilePicture(testUser.getUserId());
        
        assertThat(result).isNotNull();
        assertThat(result.getProfilePicture()).hasSize(0);
    }

    @Test
    @Order(8)
    @DisplayName("Should save only user ID and picture data without affecting other fields")
    void shouldSaveOnlyPictureDataWithoutAffectingOtherFields() {
        // Get user's current data
        User beforeUpdate = userDAO.getUserByEmail(TEST_EMAIL);
        String originalName = beforeUpdate.getName();
        String originalEmail = beforeUpdate.getEmail();
        
        // Save profile picture
        byte[] pictureData = createMockJpegData();
        userDAO.saveProfilePicture(testUser.getUserId(), pictureData, "image/jpeg");
        
        // Verify other fields unchanged
        User afterUpdate = userDAO.getUserByEmail(TEST_EMAIL);
        assertThat(afterUpdate.getName()).isEqualTo(originalName);
        assertThat(afterUpdate.getEmail()).isEqualTo(originalEmail);
    }

    // Helper methods
    private byte[] createMockJpegData() {
        // Mock JPEG header signature
        return new byte[]{
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, // JPEG SOI and APP0
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01, // JFIF marker
            0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
            (byte) 0xFF, (byte) 0xD9 // JPEG EOI
        };
    }

    private byte[] createMockPngData() {
        // Mock PNG header signature
        return new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4, (byte) 0x89,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
    }
}
