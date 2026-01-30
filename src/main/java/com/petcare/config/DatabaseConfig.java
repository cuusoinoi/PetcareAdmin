package com.petcare.config;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String RESOURCE = "database.properties";

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    static {
        load();
    }

    private static void load() {
        driver = "";
        url = "";
        user = "";
        password = "";
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
                logger.warning(RESOURCE + " not found");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load " + RESOURCE, e);
        }
    }

    public static String getDriver() {
        return driver;
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }
}
