package com.petcare.repository;

import com.petcare.model.entity.MedicalRecordMedicineEntity;
import com.petcare.model.entity.MedicalRecordMedicineListDto;
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
 * Repository for medical_record_medicines (thuốc kê trong lượt khám).
 */
public class MedicalRecordMedicineRepository implements IMedicalRecordMedicineRepository {

    @Override
    public List<MedicalRecordMedicineListDto> findByMedicalRecordId(int medicalRecordId) throws PetcareException {
        String query = "SELECT mrm.record_medicine_id, mrm.medicine_id, m.medicine_name, mrm.quantity, mrm.unit_price, mrm.total_price " +
                "FROM medical_record_medicines mrm " +
                "INNER JOIN medicines m ON mrm.medicine_id = m.medicine_id " +
                "WHERE mrm.medical_record_id = ? ORDER BY mrm.record_medicine_id";
        List<MedicalRecordMedicineListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MedicalRecordMedicineListDto dto = new MedicalRecordMedicineListDto();
                    dto.setRecordMedicineId(rs.getInt("record_medicine_id"));
                    dto.setMedicineId(rs.getInt("medicine_id"));
                    dto.setMedicineName(rs.getString("medicine_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getInt("unit_price"));
                    dto.setTotalPrice(rs.getInt("total_price"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải thuốc kê theo lượt khám", ex);
        }
        return list;
    }

    @Override
    public int insert(MedicalRecordMedicineEntity entity) throws PetcareException {
        String query = "INSERT INTO medical_record_medicines (medical_record_id, medicine_id, quantity, unit_price, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getMedicalRecordId());
            ps.setInt(2, entity.getMedicineId());
            ps.setInt(3, entity.getQuantity());
            ps.setInt(4, entity.getUnitPrice());
            ps.setInt(5, entity.getTotalPrice());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setRecordMedicineId(rs.getInt(1));
                        return entity.getRecordMedicineId();
                    }
                }
            }
            return 0;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm thuốc vào lượt khám", ex);
        }
    }

    @Override
    public int delete(int recordMedicineId) throws PetcareException {
        String query = "DELETE FROM medical_record_medicines WHERE record_medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, recordMedicineId);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa thuốc khỏi lượt khám", ex);
        }
    }

    @Override
    public void deleteByMedicalRecordId(int medicalRecordId) throws PetcareException {
        String query = "DELETE FROM medical_record_medicines WHERE medical_record_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, medicalRecordId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa thuốc theo lượt khám", ex);
        }
    }
}
