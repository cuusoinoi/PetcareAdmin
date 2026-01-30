package com.petcare.persistence.strategy;

import com.petcare.config.DatabaseConfig;

/**
 * Chọn strategy theo driver trong database.properties: H2 → chạy schema+data, MySQL → chỉ kết nối.
 */
public final class DatabaseInitStrategyFactory {

    private DatabaseInitStrategyFactory() {
    }

    public static DatabaseInitStrategy getStrategy() {
        String driver = DatabaseConfig.getDriver();
        if (driver != null && driver.toLowerCase().contains("h2")) {
            return new H2DatabaseInitStrategy();
        }
        return new NoOpDatabaseInitStrategy();
    }
}
