package com.petcare.persistence.strategy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * MySQL (hoặc DB khác): chỉ kết nối, không tạo schema/data.
 */
public class NoOpDatabaseInitStrategy implements DatabaseInitStrategy {

    @Override
    public void afterConnect(Connection conn) throws SQLException, IOException {
        // Không làm gì: chỉ giữ kết nối.
    }
}
