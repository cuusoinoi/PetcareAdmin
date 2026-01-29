package com.petcare.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load database configuration from src/main/resources/database.properties.
 * Không hard-code trong code, dễ đổi MySQL/H2 hoặc môi trường.
 */
public final class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String RESOURCE = "database.properties";
    private static final String DEFAULT_DRIVER = "org.h2.Driver";
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/petcare;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    static {
        load();
    }

    private static void load() {
        driver = DEFAULT_DRIVER;
        url = DEFAULT_URL;
        user = DEFAULT_USER;
        password = DEFAULT_PASSWORD;
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                if (p.getProperty("db.driver") != null) driver = p.getProperty("db.driver").trim();
                if (p.getProperty("db.url") != null) url = p.getProperty("db.url").trim();
                if (p.getProperty("db.user") != null) user = p.getProperty("db.user").trim();
                if (p.getProperty("db.password") != null) password = p.getProperty("db.password");
                logger.info("Database config loaded from " + RESOURCE + " (driver=" + driver + ")");
            } else {
                logger.warning(RESOURCE + " not found, using defaults (H2)");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load " + RESOURCE + ", using defaults", e);
        }
    }

    public static String getDriver() { return driver; }
    public static String getUrl() { return url; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }
}
