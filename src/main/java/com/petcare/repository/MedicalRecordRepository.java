package com.petcare.repository;

import com.petcare.model.entity.MedicalRecordEntity;
import com.petcare.model.entity.MedicalRecordListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Medical Record Repository - table medical_records; list with JOINs; count for dashboard
 */
public class MedicalRecordRepository implements IMedicalRecordRepository {

    @Override
    public List<MedicalRecordListDto> findAllForList() throws PetcareException {
        String query = "SELECT mr.medical_record_id, mr.medical_record_visit_date, " +
                "mr.medical_record_type, c.customer_name, p.pet_name, d.doctor_name, " +
                "mr.medical_record_summary " +
                "FROM medical_records mr " +
                "INNER JOIN customers c ON mr.customer_id = c.customer_id " +
                "INNER JOIN pets p ON mr.pet_id = p.pet_id " +
                "INNER JOIN doctors d ON mr.doctor_id = d.doctor_id " +
                "ORDER BY mr.medical_record_id DESC";
        List<MedicalRecordListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MedicalRecordListDto dto = new MedicalRecordListDto();
                dto.setMedicalRecordId(rs.getInt("medical_record_id"));
                if (rs.getDate("medical_record_visit_date") != null) {
                    dto.setVisitDate(new Date(rs.getDate("medical_record_visit_date").getTime()));
                }
                dto.setMedicalRecordType(rs.getString("medical_record_type"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setPetName(rs.getString("pet_name"));
                dto.setDoctorName(rs.getString("doctor_name"));
                dto.setSummary(rs.getString("medical_record_summary"));
                list.add(dto);
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách hồ sơ khám bệnh", ex);
        }
        return list;
    }

    @Override
    public MedicalRecordEntity findById(int id) throws PetcareException {
        String query = "SELECT medical_record_id, customer_id, pet_id, doctor_id, " +
                "medical_record_type, medical_record_visit_date, medical_record_summary, medical_record_details " +
                "FROM medical_records WHERE medical_record_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm hồ sơ khám bệnh theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public int insert(MedicalRecordEntity entity) throws PetcareException {
        String query = "INSERT INTO medical_records (customer_id, pet_id, doctor_id, " +
                "medical_record_type, medical_record_visit_date, medical_record_summary, medical_record_details) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            bindEntity(ps, entity);
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setMedicalRecordId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm hồ sơ khám bệnh", ex);
        }
    }

    @Override
    public int update(MedicalRecordEntity entity) throws PetcareException {
        String query = "UPDATE medical_records SET customer_id=?, pet_id=?, doctor_id=?, " +
                "medical_record_type=?, medical_record_visit_date=?, medical_record_summary=?, medical_record_details=? " +
                "WHERE medical_record_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            bindEntity(ps, entity);
            ps.setInt(8, entity.getMedicalRecordId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật hồ sơ khám bệnh", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM medical_records WHERE medical_record_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa hồ sơ khám bệnh", ex);
        }
    }

    @Override
    public int countThisMonth() throws PetcareException {
        String query = "SELECT COUNT(*) as count FROM medical_records " +
                "WHERE YEAR(medical_record_visit_date) = YEAR(CURRENT_DATE) " +
                "AND MONTH(medical_record_visit_date) = MONTH(CURRENT_DATE)";
        return countQuery(query);
    }

    @Override
    public int countByDate(Date date) throws PetcareException {
        String query = "SELECT COUNT(*) as count FROM medical_records WHERE DATE(medical_record_visit_date) = ?";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, dateStr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("count");
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi đếm hồ sơ theo ngày", ex);
        }
        return 0;
    }

    @Override
    public Map<String, Integer> countByDay(int days) throws PetcareException {
        Map<String, Integer> result = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String query = "SELECT COUNT(*) as count FROM medical_records WHERE DATE(medical_record_visit_date) = ?";
        for (int i = days - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = sdf.format(cal.getTime());
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, dateStr);
                try (ResultSet rs = ps.executeQuery()) {
                    result.put(dateStr, rs.next() ? rs.getInt("count") : 0);
                }
            } catch (SQLException ex) {
                throw new PetcareException("Lỗi khi đếm hồ sơ theo ngày", ex);
            }
        }
        return result;
    }

    private int countQuery(String query) throws PetcareException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi đếm hồ sơ", ex);
        }
        return 0;
    }

    private void bindEntity(PreparedStatement ps, MedicalRecordEntity e) throws SQLException {
        ps.setInt(1, e.getCustomerId());
        ps.setInt(2, e.getPetId());
        ps.setInt(3, e.getDoctorId());
        ps.setString(4, e.getMedicalRecordType());
        ps.setDate(5, e.getMedicalRecordVisitDate() != null ? new java.sql.Date(e.getMedicalRecordVisitDate().getTime()) : null);
        ps.setString(6, e.getMedicalRecordSummary());
        ps.setString(7, e.getMedicalRecordDetails());
    }

    private MedicalRecordEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        MedicalRecordEntity e = new MedicalRecordEntity();
        e.setMedicalRecordId(rs.getInt("medical_record_id"));
        e.setCustomerId(rs.getInt("customer_id"));
        e.setPetId(rs.getInt("pet_id"));
        e.setDoctorId(rs.getInt("doctor_id"));
        e.setMedicalRecordType(rs.getString("medical_record_type"));
        if (rs.getDate("medical_record_visit_date") != null) {
            e.setMedicalRecordVisitDate(new Date(rs.getDate("medical_record_visit_date").getTime()));
        }
        e.setMedicalRecordSummary(rs.getString("medical_record_summary"));
        e.setMedicalRecordDetails(rs.getString("medical_record_details"));
        return e;
    }
}
