package com.petcare.repository;

import com.petcare.model.entity.PetVaccinationEntity;
import com.petcare.model.entity.PetVaccinationListDto;
import com.petcare.model.entity.VaccinationForInvoiceDto;
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
 * Pet Vaccination Repository - table pet_vaccinations; list with JOINs
 */
public class PetVaccinationRepository implements IPetVaccinationRepository {

    @Override
    public List<PetVaccinationListDto> findAllForList() throws PetcareException {
        String query = "SELECT pv.pet_vaccination_id, pv.vaccination_date, pv.next_vaccination_date, " +
                "v.vaccine_name, c.customer_name, p.pet_name, d.doctor_name " +
                "FROM pet_vaccinations pv " +
                "INNER JOIN vaccines v ON pv.vaccine_id = v.vaccine_id " +
                "INNER JOIN customers c ON pv.customer_id = c.customer_id " +
                "INNER JOIN pets p ON pv.pet_id = p.pet_id " +
                "INNER JOIN doctors d ON pv.doctor_id = d.doctor_id " +
                "ORDER BY pv.pet_vaccination_id DESC";
        List<PetVaccinationListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PetVaccinationListDto dto = new PetVaccinationListDto();
                dto.setPetVaccinationId(rs.getInt("pet_vaccination_id"));
                if (rs.getDate("vaccination_date") != null) {
                    dto.setVaccinationDate(new java.util.Date(rs.getDate("vaccination_date").getTime()));
                }
                if (rs.getDate("next_vaccination_date") != null) {
                    dto.setNextVaccinationDate(new java.util.Date(rs.getDate("next_vaccination_date").getTime()));
                }
                dto.setVaccineName(rs.getString("vaccine_name"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setPetName(rs.getString("pet_name"));
                dto.setDoctorName(rs.getString("doctor_name"));
                list.add(dto);
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách tiêm chủng", ex);
        }
        return list;
    }

    @Override
    public PetVaccinationEntity findById(int id) throws PetcareException {
        String query = "SELECT pet_vaccination_id, vaccine_id, medical_record_id, customer_id, pet_id, doctor_id, " +
                "vaccination_date, next_vaccination_date, notes FROM pet_vaccinations WHERE pet_vaccination_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm bản ghi tiêm chủng theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public List<VaccinationForInvoiceDto> findByMedicalRecordId(int medicalRecordId) throws PetcareException {
        String query = "SELECT pv.vaccine_id, v.vaccine_name, v.unit_price " +
                "FROM pet_vaccinations pv " +
                "INNER JOIN vaccines v ON pv.vaccine_id = v.vaccine_id " +
                "WHERE pv.medical_record_id = ? ORDER BY pv.pet_vaccination_id";
        List<VaccinationForInvoiceDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VaccinationForInvoiceDto dto = new VaccinationForInvoiceDto();
                    dto.setVaccineId(rs.getInt("vaccine_id"));
                    dto.setVaccineName(rs.getString("vaccine_name"));
                    dto.setUnitPrice(rs.getInt("unit_price"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải tiêm chủng theo lượt khám", ex);
        }
        return list;
    }

    @Override
    public int insert(PetVaccinationEntity entity) throws PetcareException {
        String query = "INSERT INTO pet_vaccinations (vaccine_id, medical_record_id, customer_id, pet_id, doctor_id, " +
                "vaccination_date, next_vaccination_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            bindEntity(ps, entity);
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setPetVaccinationId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm bản ghi tiêm chủng", ex);
        }
    }

    @Override
    public int update(PetVaccinationEntity entity) throws PetcareException {
        String query = "UPDATE pet_vaccinations SET vaccine_id=?, medical_record_id=?, customer_id=?, pet_id=?, doctor_id=?, " +
                "vaccination_date=?, next_vaccination_date=?, notes=? WHERE pet_vaccination_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            bindEntity(ps, entity);
            ps.setInt(9, entity.getPetVaccinationId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật bản ghi tiêm chủng", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM pet_vaccinations WHERE pet_vaccination_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa bản ghi tiêm chủng", ex);
        }
    }

    private void bindEntity(PreparedStatement ps, PetVaccinationEntity e) throws SQLException {
        ps.setInt(1, e.getVaccineId());
        if (e.getMedicalRecordId() != null) {
            ps.setInt(2, e.getMedicalRecordId());
        } else {
            ps.setNull(2, java.sql.Types.INTEGER);
        }
        ps.setInt(3, e.getCustomerId());
        ps.setInt(4, e.getPetId());
        ps.setInt(5, e.getDoctorId());
        ps.setDate(6, e.getVaccinationDate() != null ? new java.sql.Date(e.getVaccinationDate().getTime()) : null);
        ps.setDate(7, e.getNextVaccinationDate() != null ? new java.sql.Date(e.getNextVaccinationDate().getTime()) : null);
        ps.setString(8, e.getNotes());
    }

    private PetVaccinationEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        PetVaccinationEntity e = new PetVaccinationEntity();
        e.setPetVaccinationId(rs.getInt("pet_vaccination_id"));
        e.setVaccineId(rs.getInt("vaccine_id"));
        int mrId = rs.getInt("medical_record_id");
        e.setMedicalRecordId(rs.wasNull() || mrId <= 0 ? null : mrId);
        e.setCustomerId(rs.getInt("customer_id"));
        e.setPetId(rs.getInt("pet_id"));
        e.setDoctorId(rs.getInt("doctor_id"));
        if (rs.getDate("vaccination_date") != null) {
            e.setVaccinationDate(new java.util.Date(rs.getDate("vaccination_date").getTime()));
        }
        if (rs.getDate("next_vaccination_date") != null) {
            e.setNextVaccinationDate(new java.util.Date(rs.getDate("next_vaccination_date").getTime()));
        }
        e.setNotes(rs.getString("notes"));
        return e;
    }
}
