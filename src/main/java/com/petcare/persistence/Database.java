package com.petcare.persistence;

import com.petcare.model.exception.PetcareException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper thực thi query/update qua connection từ DatabaseConnection.
 * Cấu hình từ src/main/resources/database.properties.
 */
public class Database {
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    public static ResultSet executeQuery(String query, Object... params) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (PetcareException ex) {
            logger.log(Level.SEVERE, "Database connection error: " + query, ex);
            return null;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Query execution failed: " + query, ex);
            return null;
        }
    }

    public static int executeUpdate(String query, Object... params) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (PetcareException ex) {
            logger.log(Level.SEVERE, "Database connection error: " + query, ex);
            return 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Update execution failed: " + query, ex);
            return 0;
        }
    }

    public static Connection getConnection() {
        try {
            return DatabaseConnection.getConnection();
        } catch (PetcareException ex) {
            logger.log(Level.SEVERE, "Failed to get connection", ex);
            return null;
        }
    }

    public static void close() {
        DatabaseConnection.close();
    }
}
