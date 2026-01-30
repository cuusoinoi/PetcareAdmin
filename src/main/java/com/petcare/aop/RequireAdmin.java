package com.petcare.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Đánh dấu method chỉ được gọi khi currentUser có vai trò ADMIN.
 * Dùng với AOP thủ công (PermissionHandler): lấy tham số kiểu User trong args làm currentUser và kiểm tra role.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAdmin {
}
