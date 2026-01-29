package com.petcare.repository;

import com.petcare.model.entity.VaccineTypeEntity;
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
 * Vaccine Type Repository - table: vaccines (vaccine_id, vaccine_name, description)
 */
public class VaccineTypeRepository implements IVaccineTypeRepository {

    @Override
    public List<VaccineTypeEntity> findAll() throws PetcareException {
        List<VaccineTypeEntity> list = new ArrayList<>();
        String query = "SELECT vaccine_id, vaccine_name, description FROM vaccines ORDER BY vaccine_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách vaccine", ex);
        }
        return list;
    }

    @Override
    public VaccineTypeEntity findById(int id) throws PetcareException {
        String query = "SELECT vaccine_id, vaccine_name, description FROM vaccines WHERE vaccine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm vaccine theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public int insert(VaccineTypeEntity entity) throws PetcareException {
        String query = "INSERT INTO vaccines (vaccine_name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getVaccineName());
            ps.setString(2, entity.getDescription());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setVaccineId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm vaccine", ex);
        }
    }

    @Override
    public int update(VaccineTypeEntity entity) throws PetcareException {
        String query = "UPDATE vaccines SET vaccine_name = ?, description = ? WHERE vaccine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entity.getVaccineName());
            ps.setString(2, entity.getDescription());
            ps.setInt(3, entity.getVaccineId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật vaccine", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM vaccines WHERE vaccine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa vaccine", ex);
        }
    }

    private VaccineTypeEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        VaccineTypeEntity e = new VaccineTypeEntity();
        e.setVaccineId(rs.getInt("vaccine_id"));
        e.setVaccineName(rs.getString("vaccine_name"));
        e.setDescription(rs.getString("description"));
        return e;
    }
}
