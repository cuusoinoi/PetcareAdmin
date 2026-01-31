package com.petcare.repository;

import com.petcare.model.entity.MedicalRecordServiceItemEntity;
import com.petcare.model.entity.MedicalRecordServiceItemListDto;
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
 * Repository for medical_record_services (dịch vụ đã thêm trong lượt khám).
 */
public class MedicalRecordServiceItemRepository implements IMedicalRecordServiceItemRepository {

    @Override
    public List<MedicalRecordServiceItemListDto> findByMedicalRecordId(int medicalRecordId) throws PetcareException {
        String query = "SELECT mrs.record_service_id, mrs.service_type_id, st.service_name, mrs.quantity, mrs.unit_price, mrs.total_price " +
                "FROM medical_record_services mrs " +
                "INNER JOIN service_types st ON mrs.service_type_id = st.service_type_id " +
                "WHERE mrs.medical_record_id = ? ORDER BY mrs.record_service_id";
        List<MedicalRecordServiceItemListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MedicalRecordServiceItemListDto dto = new MedicalRecordServiceItemListDto();
                    dto.setRecordServiceId(rs.getInt("record_service_id"));
                    dto.setServiceTypeId(rs.getInt("service_type_id"));
                    dto.setServiceName(rs.getString("service_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getInt("unit_price"));
                    dto.setTotalPrice(rs.getInt("total_price"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải dịch vụ theo lượt khám", ex);
        }
        return list;
    }

    @Override
    public int insert(MedicalRecordServiceItemEntity entity) throws PetcareException {
        String query = "INSERT INTO medical_record_services (medical_record_id, service_type_id, quantity, unit_price, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getMedicalRecordId());
            ps.setInt(2, entity.getServiceTypeId());
            ps.setInt(3, entity.getQuantity());
            ps.setInt(4, entity.getUnitPrice());
            ps.setInt(5, entity.getTotalPrice());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setRecordServiceId(rs.getInt(1));
                        return entity.getRecordServiceId();
                    }
                }
            }
            return 0;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm dịch vụ vào lượt khám", ex);
        }
    }

    @Override
    public int delete(int recordServiceId) throws PetcareException {
        String query = "DELETE FROM medical_record_services WHERE record_service_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, recordServiceId);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa dịch vụ khỏi lượt khám", ex);
        }
    }

    @Override
    public void deleteByMedicalRecordId(int medicalRecordId) throws PetcareException {
        String query = "DELETE FROM medical_record_services WHERE medical_record_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, medicalRecordId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa dịch vụ theo lượt khám", ex);
        }
    }
}
