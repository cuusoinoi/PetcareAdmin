package com.petcare.persistence;

import com.petcare.model.exception.PetcareException;
import com.petcare.util.DatabaseConfig;
import org.h2.tools.RunScript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Quản lý kết nối DB. Cấu hình từ src/main/resources/database.properties.
 * Khi dùng H2: mỗi lần start app chạy schema-and-data-h2.sql (tạo bảng mới + insert toàn bộ dữ liệu, kể cả admin).
 */
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
                if (DatabaseConfig.getDriver().contains("h2") && !schemaChecked) {
                    schemaChecked = true;
                    runH2Init(connection);
                    // RunScript có thể đóng connection; lấy connection mới cho các thao tác sau
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(
                                DatabaseConfig.getUrl(),
                                DatabaseConfig.getUser(),
                                DatabaseConfig.getPassword()
                        );
                        logger.info("H2: new connection opened after schema/data load.");
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

    /**
     * H2: chạy schema-and-data-h2.sql mỗi lần start app (DROP bảng cũ, tạo mới, INSERT toàn bộ kể cả admin).
     */
    private static void runH2Init(Connection conn) {
        logger.info("H2: chạy schema-and-data-h2.sql (tạo DB mới + insert dữ liệu)...");
        try (InputStream raw = DatabaseConnection.class.getClassLoader().getResourceAsStream("schema-and-data-h2.sql")) {
            if (raw == null) {
                logger.severe("H2: schema-and-data-h2.sql not found in classpath.");
                return;
            }
            InputStream withoutBom = skipUtf8Bom(raw);
            try (var reader = new InputStreamReader(withoutBom, StandardCharsets.UTF_8)) {
                RunScript.execute(conn, reader);
            }
            logger.info("H2: schema-and-data-h2.sql OK. Login: admin / 123456");
        } catch (IOException | SQLException ex) {
            logger.log(Level.WARNING, "H2: lỗi khi chạy schema-and-data-h2.sql.", ex);
        }
    }

    /**
     * Bỏ BOM UTF-8 (EF BB BF) ở đầu stream nếu có, tránh lỗi cú pháp H2.
     */
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

    /**
     * Trả về connection wrapper: gọi close() trên connection này sẽ KHÔNG đóng connection thật,
     * để tránh repository/service dùng try-with-resources đóng nhầm connection dùng chung.
     */
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
