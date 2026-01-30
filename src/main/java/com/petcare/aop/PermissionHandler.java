package com.petcare.aop;

import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/** InvocationHandler cho AOP phân quyền: @RequireAdmin → kiểm tra User trong args, ném PetcareException nếu không ADMIN. */
public class PermissionHandler implements InvocationHandler {
    private final Object target;

    public PermissionHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (targetMethod.isAnnotationPresent(RequireAdmin.class)) {
            User currentUser = findUserParam(args);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                throw new PetcareException("Chỉ quản trị viên mới được thực hiện thao tác này.");
            }
        }
        return method.invoke(target, args);
    }

    private static User findUserParam(Object[] args) {
        if (args == null) return null;
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i] instanceof User) {
                return (User) args[i];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Object target, Class<T> interfaceType) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[] { interfaceType },
                new PermissionHandler(target)
        );
    }
}
