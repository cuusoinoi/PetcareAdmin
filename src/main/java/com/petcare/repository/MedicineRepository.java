package com.petcare.repository;

import com.petcare.model.entity.MedicineEntity;
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
 * Medicine Repository Implementation
 * Table: medicines (medicine_id, medicine_name, medicine_route)
 */
public class MedicineRepository implements IMedicineRepository {

    @Override
    public List<MedicineEntity> findAll() throws PetcareException {
        List<MedicineEntity> list = new ArrayList<>();
        String query = "SELECT medicine_id, medicine_name, medicine_route, unit_price FROM medicines ORDER BY medicine_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách thuốc", ex);
        }
        return list;
    }

    @Override
    public MedicineEntity findById(int id) throws PetcareException {
        String query = "SELECT medicine_id, medicine_name, medicine_route, unit_price FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm thuốc theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public int insert(MedicineEntity entity) throws PetcareException {
        String query = "INSERT INTO medicines (medicine_name, medicine_route, unit_price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getMedicineName());
            ps.setString(2, entity.getMedicineRoute());
            ps.setInt(3, entity.getUnitPrice());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setMedicineId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm thuốc", ex);
        }
    }

    @Override
    public int update(MedicineEntity entity) throws PetcareException {
        String query = "UPDATE medicines SET medicine_name = ?, medicine_route = ?, unit_price = ? WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entity.getMedicineName());
            ps.setString(2, entity.getMedicineRoute());
            ps.setInt(3, entity.getUnitPrice());
            ps.setInt(4, entity.getMedicineId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật thuốc", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa thuốc", ex);
        }
    }

    private MedicineEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        MedicineEntity e = new MedicineEntity();
        e.setMedicineId(rs.getInt("medicine_id"));
        e.setMedicineName(rs.getString("medicine_name"));
        e.setMedicineRoute(rs.getString("medicine_route"));
        e.setUnitPrice(rs.getInt("unit_price"));
        return e;
    }
}
