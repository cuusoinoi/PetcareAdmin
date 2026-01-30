package com.petcare.persistence.strategy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseInitStrategy {
    void afterConnect(Connection conn) throws SQLException, IOException;
}
