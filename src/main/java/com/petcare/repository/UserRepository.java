package com.petcare.repository;

import com.petcare.model.entity.UserEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * User Repository Implementation
 * Uses PreparedStatement; try-with-resources; maps ResultSet to UserEntity
 * DB table: users (id, username, password, fullname, avatar, role)
 */
public class UserRepository implements IUserRepository {

    @Override
    public List<UserEntity> findAll() throws PetcareException {
        List<UserEntity> list = new ArrayList<>();
        String query = "SELECT id, username, password, fullname, avatar, role FROM users ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách người dùng", ex);
        }
        return list;
    }

    @Override
    public UserEntity findById(int id) throws PetcareException {
        String query = "SELECT id, username, password, fullname, avatar, role FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm người dùng theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public UserEntity findByUsernameAndPassword(String username, String hashedPassword) throws PetcareException {
        String query = "SELECT id, username, password, fullname, avatar, role FROM users " +
                "WHERE username = ? AND password = ? AND role IN ('admin', 'staff')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi xác thực đăng nhập", ex);
        }
        return null;
    }

    @Override
    public int insert(UserEntity entity) throws PetcareException {
        String query = "INSERT INTO users (username, password, fullname, avatar, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getPassword());
            ps.setString(3, entity.getFullname());
            ps.setString(4, entity.getAvatar());
            ps.setString(5, entity.getRole());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm người dùng", ex);
        }
    }

    @Override
    public int update(UserEntity entity) throws PetcareException {
        String query = "UPDATE users SET username = ?, fullname = ?, avatar = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getFullname());
            ps.setString(3, entity.getAvatar());
            ps.setString(4, entity.getRole());
            ps.setInt(5, entity.getId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật người dùng", ex);
        }
    }

    @Override
    public int updatePassword(int id, String hashedPassword) throws PetcareException {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi đổi mật khẩu", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa người dùng", ex);
        }
    }

    @Override
    public boolean existsByUsername(String username) throws PetcareException {
        String query = "SELECT COUNT(*) AS cnt FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi kiểm tra username", ex);
        }
        return false;
    }

    @Override
    public boolean existsByUsernameExcludingId(String username, int excludeId) throws PetcareException {
        String query = "SELECT COUNT(*) AS cnt FROM users WHERE username = ? AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi kiểm tra username", ex);
        }
        return false;
    }

    private UserEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        UserEntity e = new UserEntity();
        e.setId(rs.getInt("id"));
        e.setUsername(rs.getString("username"));
        e.setPassword(rs.getString("password"));
        e.setFullname(rs.getString("fullname"));
        e.setAvatar(rs.getString("avatar"));
        e.setRole(rs.getString("role"));
        return e;
    }
}
