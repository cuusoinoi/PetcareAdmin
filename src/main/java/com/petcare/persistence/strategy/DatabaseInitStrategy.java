package com.petcare.persistence.strategy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Strategy: sau khi kết nối DB, có thể chạy init (H2: schema+data) hoặc không làm gì (MySQL).
 */
public interface DatabaseInitStrategy {

    /**
     * Gọi ngay sau khi đã kết nối thành công.
     * H2: chạy schema-and-data-h2.sql. MySQL: không làm gì.
     *
     * @param conn connection đã mở (có thể bị đóng bởi strategy, ví dụ RunScript)
     */
    void afterConnect(Connection conn) throws SQLException, IOException;
}
