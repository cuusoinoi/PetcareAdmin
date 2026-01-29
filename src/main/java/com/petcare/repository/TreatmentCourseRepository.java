package com.petcare.repository;

import com.petcare.model.entity.TreatmentCourseEntity;
import com.petcare.model.entity.TreatmentCourseInfoDto;
import com.petcare.model.entity.TreatmentCourseListDto;
import com.petcare.model.entity.TreatmentSessionListDto;
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
 * Treatment Course Repository - treatment_courses; list with JOINs; sessions for dialog
 */
public class TreatmentCourseRepository implements ITreatmentCourseRepository {

    @Override
    public List<TreatmentCourseListDto> findAllForList() throws PetcareException {
        String query = "SELECT tc.treatment_course_id, tc.start_date, tc.end_date, " +
                "c.customer_name, p.pet_name, tc.status " +
                "FROM treatment_courses tc " +
                "INNER JOIN customers c ON tc.customer_id = c.customer_id " +
                "INNER JOIN pets p ON tc.pet_id = p.pet_id " +
                "ORDER BY tc.treatment_course_id DESC";
        List<TreatmentCourseListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TreatmentCourseListDto dto = new TreatmentCourseListDto();
                dto.setTreatmentCourseId(rs.getInt("treatment_course_id"));
                if (rs.getDate("start_date") != null) {
                    dto.setStartDate(new java.util.Date(rs.getDate("start_date").getTime()));
                }
                if (rs.getDate("end_date") != null) {
                    dto.setEndDate(new java.util.Date(rs.getDate("end_date").getTime()));
                }
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setPetName(rs.getString("pet_name"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách liệu trình", ex);
        }
        return list;
    }

    @Override
    public TreatmentCourseEntity findById(int id) throws PetcareException {
        String query = "SELECT treatment_course_id, customer_id, pet_id, start_date, end_date, status " +
                "FROM treatment_courses WHERE treatment_course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm liệu trình theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public int insert(TreatmentCourseEntity entity) throws PetcareException {
        String query = "INSERT INTO treatment_courses (customer_id, pet_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            bindEntity(ps, entity);
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setTreatmentCourseId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm liệu trình", ex);
        }
    }

    @Override
    public int update(TreatmentCourseEntity entity) throws PetcareException {
        String query = "UPDATE treatment_courses SET customer_id=?, pet_id=?, start_date=?, end_date=?, status=? WHERE treatment_course_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            bindEntity(ps, entity);
            ps.setInt(6, entity.getTreatmentCourseId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật liệu trình", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM treatment_courses WHERE treatment_course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa liệu trình", ex);
        }
    }

    @Override
    public int completeCourse(int id) throws PetcareException {
        String query = "UPDATE treatment_courses SET end_date = CURRENT_DATE, status = '0' WHERE treatment_course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi kết thúc liệu trình", ex);
        }
    }

    @Override
    public TreatmentCourseInfoDto findCourseInfoForSessions(int courseId) throws PetcareException {
        String query = "SELECT tc.treatment_course_id, tc.start_date, c.customer_name, p.pet_name " +
                "FROM treatment_courses tc " +
                "INNER JOIN customers c ON tc.customer_id = c.customer_id " +
                "INNER JOIN pets p ON tc.pet_id = p.pet_id " +
                "WHERE tc.treatment_course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TreatmentCourseInfoDto dto = new TreatmentCourseInfoDto();
                    dto.setTreatmentCourseId(rs.getInt("treatment_course_id"));
                    if (rs.getDate("start_date") != null) {
                        dto.setStartDate(new java.util.Date(rs.getDate("start_date").getTime()));
                    }
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setPetName(rs.getString("pet_name"));
                    return dto;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải thông tin liệu trình", ex);
        }
        return null;
    }

    @Override
    public List<TreatmentSessionListDto> findSessionsByCourseId(int courseId) throws PetcareException {
        String query = "SELECT ts.treatment_session_id, ts.treatment_session_datetime, " +
                "d.doctor_name, ts.temperature, ts.weight, ts.pulse_rate, ts.respiratory_rate " +
                "FROM treatment_sessions ts " +
                "INNER JOIN doctors d ON ts.doctor_id = d.doctor_id " +
                "WHERE ts.treatment_course_id = ? ORDER BY ts.treatment_session_datetime DESC";
        List<TreatmentSessionListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TreatmentSessionListDto dto = new TreatmentSessionListDto();
                    dto.setTreatmentSessionId(rs.getInt("treatment_session_id"));
                    if (rs.getTimestamp("treatment_session_datetime") != null) {
                        dto.setTreatmentSessionDatetime(new java.util.Date(rs.getTimestamp("treatment_session_datetime").getTime()));
                    }
                    dto.setDoctorName(rs.getString("doctor_name"));
                    dto.setTemperature(rs.getDouble("temperature"));
                    dto.setWeight(rs.getDouble("weight"));
                    int pr = rs.getInt("pulse_rate");
                    dto.setPulseRate(rs.wasNull() ? null : pr);
                    int rr = rs.getInt("respiratory_rate");
                    dto.setRespiratoryRate(rs.wasNull() ? null : rr);
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách buổi điều trị", ex);
        }
        return list;
    }

    private void bindEntity(PreparedStatement ps, TreatmentCourseEntity e) throws SQLException {
        ps.setInt(1, e.getCustomerId());
        ps.setInt(2, e.getPetId());
        ps.setDate(3, e.getStartDate() != null ? new java.sql.Date(e.getStartDate().getTime()) : null);
        ps.setDate(4, e.getEndDate() != null ? new java.sql.Date(e.getEndDate().getTime()) : null);
        ps.setString(5, e.getStatus());
    }

    private TreatmentCourseEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        TreatmentCourseEntity e = new TreatmentCourseEntity();
        e.setTreatmentCourseId(rs.getInt("treatment_course_id"));
        e.setCustomerId(rs.getInt("customer_id"));
        e.setPetId(rs.getInt("pet_id"));
        if (rs.getDate("start_date") != null) {
            e.setStartDate(new java.util.Date(rs.getDate("start_date").getTime()));
        }
        if (rs.getDate("end_date") != null) {
            e.setEndDate(new java.util.Date(rs.getDate("end_date").getTime()));
        }
        e.setStatus(rs.getString("status"));
        return e;
    }
}
