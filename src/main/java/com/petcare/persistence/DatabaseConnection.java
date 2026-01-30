package com.petcare.persistence;

import com.petcare.config.DatabaseConfig;
import com.petcare.model.exception.PetcareException;
import com.petcare.persistence.strategy.DatabaseInitStrategy;
import com.petcare.persistence.strategy.DatabaseInitStrategyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static Connection connection;
    private static boolean schemaChecked;
    private static final Object initLock = new Object();

    static {
        initializeConnection();
    }

    private static void initializeConnection() {
        synchronized (initLock) {
            try {
                Class.forName(DatabaseConfig.getDriver());
                connection = DriverManager.getConnection(
                        DatabaseConfig.getUrl(),
                        DatabaseConfig.getUser(),
                        DatabaseConfig.getPassword()
                );
                logger.info("Database connection established successfully (" + DatabaseConfig.getDriver() + ")");
                if (!schemaChecked) {
                    schemaChecked = true;
                    DatabaseInitStrategy strategy = DatabaseInitStrategyFactory.getStrategy();
                    try {
                        strategy.afterConnect(connection);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Database init strategy failed", e);
                    }
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(
                                DatabaseConfig.getUrl(),
                                DatabaseConfig.getUser(),
                                DatabaseConfig.getPassword()
                        );
                        logger.info("New connection opened after init.");
                    }
                }
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "JDBC Driver not found: " + DatabaseConfig.getDriver(), ex);
                throw new RuntimeException("Failed to load JDBC Driver", ex);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Failed to establish database connection", ex);
                throw new RuntimeException("Failed to connect to database: " + ex.getMessage(), ex);
            }
        }
    }

    public static Connection getConnection() throws PetcareException {
        try {
            if (connection == null || connection.isClosed()) {
                logger.warning("Connection is null or closed, reinitializing...");
                initializeConnection();
            }
            Connection real = connection;
            return (Connection) Proxy.newProxyInstance(
                    DatabaseConnection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if ("close".equals(method.getName())) {
                                return null; // no-op: không đóng connection thật
                            }
                            return method.invoke(real, args);
                        }
                    });
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error checking connection status", ex);
            throw new PetcareException("Database connection error", ex);
        }
    }

    public static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error validating connection", ex);
            return false;
        }
    }

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
