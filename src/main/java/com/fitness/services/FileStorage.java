package com.fitness.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

/**
 * Simple file storage for profile pictures.
 * For Sprint 1, stores images in a folder on disk.
 */
public class FileStorage {

    // Default base dir; can be overridden in tests
    private static Path BASE_DIR = Paths.get(
            System.getProperty("user.home"),
            "fitness_uploads"
    );

    // For JUnit tests (same package)
    static void setBaseDirForTests(Path baseDir) {
        BASE_DIR = baseDir;
    }

    private static Path userDir(String userId) throws IOException {
        Path p = BASE_DIR.resolve(userId);
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
        return p;
    }

    private static Path userProfilePicPath(String userId) throws IOException {
        // We normalize everything to profile.jpg for now
        return userDir(userId).resolve("profile.jpg");
    }

    /**
     * Save/overwrite profile picture for user.
     */
    public static void saveProfilePic(String userId, InputStream in) throws IOException {
        Files.createDirectories(BASE_DIR);
        Path out = userProfilePicPath(userId);
        // REPLACE_EXISTING covers "update / change" behaviour
        Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Returns true if the user already has a profile picture.
     */
    public static boolean existsProfilePic(String userId) throws IOException {
        return Files.exists(userProfilePicPath(userId));
    }

    /**
     * Returns an InputStream for the profile picture or null if none exists.
     * Caller must close the stream.
     */
    public static InputStream readProfilePic(String userId) throws IOException {
        Path p = userProfilePicPath(userId);
        if (!Files.exists(p)) {
            return null;
        }
        return Files.newInputStream(p, StandardOpenOption.READ);
    }

    /**
     * Utility to copy an InputStream to an OutputStream.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int r;
        while ((r = in.read(buf)) != -1) {
            out.write(buf, 0, r);
        }
    }
}
