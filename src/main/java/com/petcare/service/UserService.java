package com.petcare.service;

import com.petcare.aop.PermissionHandler;
import com.petcare.aop.RequireAdmin;
import com.petcare.model.domain.User;
import com.petcare.model.entity.UserEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IUserRepository;
import com.petcare.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service - Business logic for User. Singleton; Entity ↔ Domain; BCrypt.
 */
public class UserService implements IUserService {
    private static IUserService instance;
    private IUserRepository repository;

    UserService() {
        this.repository = new UserRepository();
    }

    public static IUserService getInstance() {
        if (instance == null) {
            UserService impl = new UserService();
            instance = PermissionHandler.createProxy(impl, IUserService.class);
        }
        return instance;
    }

    public void setRepository(IUserRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    /** Authenticate by username and password (BCrypt). */
    @Override
    public User authenticate(String username, String plainPassword) throws PetcareException {
        UserEntity entity = repository.findByUsername(username);
        if (entity == null) {
            return null;
        }
        String storedHash = entity.getPassword();
        if (!isBcryptHash(storedHash) || !BCrypt.checkpw(plainPassword, storedHash)) {
            return null;
        }
        return entityToDomain(entity);
    }

    @Override
    public List<User> getAllUsers() throws PetcareException {
        return repository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(int id) throws PetcareException {
        UserEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    @RequireAdmin
    @Override
    public void createUser(User user, String plainPassword, User currentUser) throws PetcareException {
        if (repository.existsByUsername(user.getUsername())) {
            throw new PetcareException("Username đã tồn tại!");
        }
        UserEntity entity = domainToEntity(user);
        entity.setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
        entity.setRole(roleToDb(user.getRole()));
        int result = repository.insert(entity);
        if (result > 0) {
            user.setUserId(entity.getId());
        } else {
            throw new PetcareException("Không thể tạo người dùng mới");
        }
    }

    @RequireAdmin
    @Override
    public void updateUser(User user, User currentUser) throws PetcareException {
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

    @RequireAdmin
    @Override
    public void deleteUser(int id, User currentUser) throws PetcareException {
        UserEntity existing = repository.findById(id);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy người dùng với ID: " + id);
        }
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa người dùng");
        }
    }

    @Override
    public void changePassword(int userId, String newPlainPassword, User currentUser) throws PetcareException {
        if (currentUser == null) {
            throw new PetcareException("Không xác định người thực hiện.");
        }
        boolean allowed = currentUser.getUserId() == userId || currentUser.getRole() == User.Role.ADMIN;
        if (!allowed) {
            throw new PetcareException("Bạn không có quyền đổi mật khẩu người dùng này.");
        }
        UserEntity existing = repository.findById(userId);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy người dùng với ID: " + userId);
        }
        String hashed = BCrypt.hashpw(newPlainPassword, BCrypt.gensalt());
        int result = repository.updatePassword(userId, hashed);
        if (result == 0) {
            throw new PetcareException("Không thể đổi mật khẩu");
        }
    }

    @Override
    public boolean existsByUsername(String username) throws PetcareException {
        return repository.existsByUsername(username);
    }

    private static boolean isBcryptHash(String s) {
        return s != null && (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$"));
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
