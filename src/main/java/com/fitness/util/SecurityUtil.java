package com.fitness.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for security-related operations, primarily password hashing and verification.
 * Uses Spring Security's BCrypt algorithm for secure password storage.
 */
public class SecurityUtil {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Hashes a plaintext password using BCrypt algorithm.
     * 
     * @param plainPassword The plaintext password to hash
     * @return The hashed password string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Verifies a plaintext password against a hashed password.
     * 
     * @param plainPassword The plaintext password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
