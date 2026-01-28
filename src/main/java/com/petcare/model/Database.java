package com.petcare.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection manager with PreparedStatement support
 * Prevents SQL Injection attacks
 */
public class Database {
    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/petcare";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Update with your password
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Database connection established successfully");
        } catch (ClassNotFoundException | SQLException ex) {
            logger.log(Level.SEVERE, "Failed to establish database connection", ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * Execute SELECT query with parameters
     * @param query SQL query with ? placeholders
     * @param params Parameters to fill placeholders
     * @return ResultSet
     */
    public static ResultSet executeQuery(String query, Object... params) {
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Query execution failed: " + query, ex);
            return null;
        }
    }
    
    /**
     * Execute UPDATE/INSERT/DELETE query with parameters
     * @param query SQL query with ? placeholders
     * @param params Parameters to fill placeholders
     * @return Number of affected rows
     */
    public static int executeUpdate(String query, Object... params) {
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Update execution failed: " + query, ex);
            return 0;
        }
    }
    
    /**
     * Get database connection (for transactions)
     * @return Connection
     */
    public static Connection getConnection() {
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to close database connection", ex);
        }
    }
}
