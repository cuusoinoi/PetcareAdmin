package com.petcare.repository;

import com.petcare.model.entity.ServiceTypeEntity;
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
 * Service Type Repository Implementation
 * Table: service_types (service_type_id, service_name, description, price)
 */
public class ServiceTypeRepository implements IServiceTypeRepository {

    @Override
    public List<ServiceTypeEntity> findAll() throws PetcareException {
        List<ServiceTypeEntity> list = new ArrayList<>();
        String query = "SELECT service_type_id, service_name, description, price FROM service_types ORDER BY service_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách loại dịch vụ", ex);
        }
        return list;
    }

    @Override
    public ServiceTypeEntity findById(int id) throws PetcareException {
        String query = "SELECT service_type_id, service_name, description, price FROM service_types WHERE service_type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm loại dịch vụ theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public ServiceTypeEntity findByName(String serviceName) throws PetcareException {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            return null;
        }
        String query = "SELECT service_type_id, service_name, description, price FROM service_types WHERE service_name = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, serviceName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm loại dịch vụ theo tên", ex);
        }
        return null;
    }

    @Override
    public ServiceTypeEntity findByNameStartsWith(String prefix) throws PetcareException {
        if (prefix == null || prefix.trim().isEmpty()) {
            return null;
        }
        String query = "SELECT service_type_id, service_name, description, price FROM service_types WHERE service_name LIKE ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, prefix.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm loại dịch vụ theo tên", ex);
        }
        return null;
    }

    @Override
    public int insert(ServiceTypeEntity entity) throws PetcareException {
        String query = "INSERT INTO service_types (service_name, description, price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getServiceName());
            ps.setString(2, entity.getDescription());
            ps.setDouble(3, entity.getPrice());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setServiceTypeId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm loại dịch vụ", ex);
        }
    }

    @Override
    public int update(ServiceTypeEntity entity) throws PetcareException {
        String query = "UPDATE service_types SET service_name = ?, description = ?, price = ? WHERE service_type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entity.getServiceName());
            ps.setString(2, entity.getDescription());
            ps.setDouble(3, entity.getPrice());
            ps.setInt(4, entity.getServiceTypeId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật loại dịch vụ", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM service_types WHERE service_type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa loại dịch vụ", ex);
        }
    }

    private ServiceTypeEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        ServiceTypeEntity e = new ServiceTypeEntity();
        e.setServiceTypeId(rs.getInt("service_type_id"));
        e.setServiceName(rs.getString("service_name"));
        e.setDescription(rs.getString("description"));
        e.setPrice(rs.getDouble("price"));
        return e;
    }
}
