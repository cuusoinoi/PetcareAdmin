package com.petcare.repository;

import com.petcare.model.exception.PetcareException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Connection Manager
 * Handles database connection initialization and provides connection access
 * Uses singleton pattern for connection management
 */
public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/petcare";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Update with your password
    
    static {
        initializeConnection();
    }
    
    /**
     * Initialize database connection
     * Called automatically when class is loaded
     */
    private static void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Database connection established successfully");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", ex);
            throw new RuntimeException("Failed to load MySQL JDBC Driver", ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to establish database connection", ex);
            throw new RuntimeException("Failed to connect to database: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws PetcareException if connection is null or closed
     */
    public static Connection getConnection() throws PetcareException {
        try {
            if (connection == null || connection.isClosed()) {
                logger.warning("Connection is null or closed, reinitializing...");
                initializeConnection();
            }
            return connection;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error checking connection status", ex);
            throw new PetcareException("Database connection error", ex);
        }
    }
    
    /**
     * Check if connection is valid
     * 
     * @return true if connection is valid, false otherwise
     */
    public static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error validating connection", ex);
            return false;
        }
    }
    
    /**
     * Close database connection
     * Should be called when application shuts down
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed successfully");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to close database connection", ex);
        }
    }
    
    /**
     * Reinitialize connection
     * Useful for reconnection after connection loss
     * 
     * @throws PetcareException if reconnection fails
     */
    public static void reconnect() throws PetcareException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error closing old connection during reconnect", ex);
        }
        initializeConnection();
    }
}
