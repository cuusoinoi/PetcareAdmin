package com.petcare.persistence.strategy;

import org.h2.tools.RunScript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * H2: sau khi kết nối, chạy schema-and-data-h2.sql (DROP bảng cũ, tạo mới, INSERT toàn bộ).
 */
public class H2DatabaseInitStrategy implements DatabaseInitStrategy {
    private static final Logger logger = Logger.getLogger(H2DatabaseInitStrategy.class.getName());
    private static final String SCRIPT = "schema-and-data-h2.sql";

    @Override
    public void afterConnect(Connection conn) throws SQLException, IOException {
        logger.info("H2: chạy " + SCRIPT + " (tạo DB mới + insert dữ liệu)...");
        try (InputStream raw = getClass().getClassLoader().getResourceAsStream(SCRIPT)) {
            if (raw == null) {
                logger.severe("H2: " + SCRIPT + " not found in classpath.");
                return;
            }
            InputStream withoutBom = skipUtf8Bom(raw);
            try (var reader = new InputStreamReader(withoutBom, StandardCharsets.UTF_8)) {
                RunScript.execute(conn, reader);
            }
            logger.info("H2: " + SCRIPT + " OK. Login: admin / 123456");
        } catch (IOException | SQLException ex) {
            logger.log(Level.WARNING, "H2: lỗi khi chạy " + SCRIPT, ex);
        }
    }

    private static InputStream skipUtf8Bom(InputStream in) throws IOException {
        PushbackInputStream pin = new PushbackInputStream(in, 3);
        byte[] buf = new byte[3];
        int n = pin.read(buf);
        if (n == 3 && buf[0] == (byte) 0xEF && buf[1] == (byte) 0xBB && buf[2] == (byte) 0xBF) {
            return pin;
        }
        if (n > 0) {
            pin.unread(buf, 0, n);
        }
        return pin;
    }
}
