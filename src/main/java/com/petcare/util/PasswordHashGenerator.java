package com.petcare.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Sinh BCrypt hash cố định (salt hardcode trong code) chỉ để ghi vào file SQL seed (admin/staff mẫu).
 * Salt không đọc từ file/DB; dùng salt cố định để hash giống nhau mỗi lần, dán được vào cả H2 và MySQL.
 * Khi thêm người dùng mới trong ứng dụng: dùng UserService → BCrypt.gensalt() (salt ngẫu nhiên).
 */
public class PasswordHashGenerator {
    /** Salt cố định chỉ cho seed SQL; không dùng khi tạo user mới. */
    private static final String SALT_123456 = "$2a$10$N9qo8uLOickgx2ZMRZoMye";
    private static final String SALT_654321 = "$2a$10$EixZaYVK1fsbw1ZfbX3OXe";

    public static void main(String[] args) {
        String h123456 = BCrypt.hashpw("123456", SALT_123456);
        String h654321 = BCrypt.hashpw("654321", SALT_654321);
        System.out.println("-- BCrypt (salt cố định, dán vào INSERT users):");
        System.out.println("-- 123456: " + h123456);
        System.out.println("-- 654321: " + h654321);
    }

    /** Hash cố định cho "123456" (dùng trong SQL seed). */
    public static String hash123456() {
        return BCrypt.hashpw("123456", SALT_123456);
    }

    /** Hash cố định cho "654321" (dùng trong SQL seed). */
    public static String hash654321() {
        return BCrypt.hashpw("654321", SALT_654321);
    }
}
