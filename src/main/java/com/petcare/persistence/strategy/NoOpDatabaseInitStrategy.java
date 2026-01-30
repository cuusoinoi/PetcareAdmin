package com.petcare.persistence.strategy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class NoOpDatabaseInitStrategy implements DatabaseInitStrategy {
    @Override
    public void afterConnect(Connection conn) throws SQLException, IOException {
    }
}
