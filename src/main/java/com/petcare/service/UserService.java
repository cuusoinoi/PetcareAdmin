package com.petcare.service;

import com.petcare.model.domain.User;
import com.petcare.model.entity.UserEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IUserRepository;
import com.petcare.repository.UserRepository;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service - Business logic for User
 * Singleton; Entity ↔ Domain; MD5 hashing for password
 */
public class UserService {
    private static UserService instance;
    private IUserRepository repository;

    private UserService() {
        this.repository = new UserRepository();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void setRepository(IUserRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    /**
     * Authenticate user by username and plain password.
     * Returns domain User if valid, null otherwise.
     */
    public User authenticate(String username, String plainPassword) throws PetcareException {
        String hashed = md5(plainPassword);
        UserEntity entity = repository.findByUsernameAndPassword(username, hashed);
        return entity != null ? entityToDomain(entity) : null;
    }

    public List<User> getAllUsers() throws PetcareException {
        return repository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    public User getUserById(int id) throws PetcareException {
        UserEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Create user. Password is plain text; hashed inside.
     */
    public void createUser(User user, String plainPassword) throws PetcareException {
        if (repository.existsByUsername(user.getUsername())) {
            throw new PetcareException("Username đã tồn tại!");
        }
        UserEntity entity = domainToEntity(user);
        entity.setPassword(md5(plainPassword));
        entity.setRole(roleToDb(user.getRole()));
        int result = repository.insert(entity);
        if (result > 0) {
            user.setUserId(entity.getId());
        } else {
            throw new PetcareException("Không thể tạo người dùng mới");
        }
    }

    /**
     * Update user (no password change).
     */
    public void updateUser(User user) throws PetcareException {
        UserEntity existing = repository.findById(user.getUserId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy người dùng với ID: " + user.getUserId());
        }
        if (repository.existsByUsernameExcludingId(user.getUsername(), user.getUserId())) {
            throw new PetcareException("Username đã được sử dụng bởi người dùng khác");
        }
        UserEntity entity = domainToEntity(user);
        entity.setRole(roleToDb(user.getRole()));
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật người dùng");
        }
    }

    public void deleteUser(int id) throws PetcareException {
        UserEntity existing = repository.findById(id);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy người dùng với ID: " + id);
        }
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa người dùng");
        }
    }

    public void changePassword(int userId, String newPlainPassword) throws PetcareException {
        UserEntity existing = repository.findById(userId);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy người dùng với ID: " + userId);
        }
        String hashed = md5(newPlainPassword);
        int result = repository.updatePassword(userId, hashed);
        if (result == 0) {
            throw new PetcareException("Không thể đổi mật khẩu");
        }
    }

    public boolean existsByUsername(String username) throws PetcareException {
        return repository.existsByUsername(username);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("MD5 hash failed", ex);
        }
    }

    private static String roleToDb(User.Role role) {
        return role == User.Role.ADMIN ? "admin" : "staff";
    }

    private static User.Role roleFromDb(String role) {
        if (role == null) return User.Role.STAFF;
        return "admin".equalsIgnoreCase(role) ? User.Role.ADMIN : User.Role.STAFF;
    }

    private User entityToDomain(UserEntity e) {
        try {
            User u = new User();
            u.setUserId(e.getId());
            u.setUsername(e.getUsername());
            u.setFullname(e.getFullname());
            u.setAvatar(e.getAvatar());
            u.setRole(roleFromDb(e.getRole()));
            return u;
        } catch (PetcareException ex) {
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    private UserEntity domainToEntity(User u) {
        UserEntity e = new UserEntity();
        e.setId(u.getUserId());
        e.setUsername(u.getUsername());
        e.setFullname(u.getFullname());
        e.setAvatar(u.getAvatar());
        return e;
    }
}
