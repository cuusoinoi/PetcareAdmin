package com.petcare.util;

import org.mindrot.jbcrypt.BCrypt;

/** Sinh BCrypt hash cố định cho SQL seed (admin/staff mẫu). User mới dùng BCrypt.gensalt() qua UserService. */
public class PasswordHashGenerator {
    private static final String SALT_123456 = "$2a$10$N9qo8uLOickgx2ZMRZoMye";
    private static final String SALT_654321 = "$2a$10$EixZaYVK1fsbw1ZfbX3OXe";

    public static void main(String[] args) {
        String h123456 = BCrypt.hashpw("123456", SALT_123456);
        String h654321 = BCrypt.hashpw("654321", SALT_654321);
        System.out.println("-- BCrypt (salt cố định, dán vào INSERT users):");
        System.out.println("-- 123456: " + h123456);
        System.out.println("-- 654321: " + h654321);
    }

    public static String hash123456() {
        return BCrypt.hashpw("123456", SALT_123456);
    }

    public static String hash654321() {
        return BCrypt.hashpw("654321", SALT_654321);
    }
}
