package com.fitness.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetupHelper {
    
    public static void addProfilePictureColumns() {
        String[] sqlStatements = {
            "ALTER TABLE users ADD COLUMN profile_picture MEDIUMBLOB",
            "ALTER TABLE users ADD COLUMN profile_picture_type VARCHAR(50)"
        };
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : sqlStatements) {
                try {
                    stmt.execute(sql);
                    System.out.println("Executed: " + sql);
                } catch (SQLException e) {
                    // Ignore if column already exists
                    if (e.getMessage().contains("Duplicate column")) {
                        System.out.println("Column already exists, skipping: " + sql);
                    } else {
                        System.err.println("Error executing: " + sql);
                        e.printStackTrace();
                    }
                }
            }
            
            System.out.println("Database setup complete!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        addProfilePictureColumns();
    }
}
