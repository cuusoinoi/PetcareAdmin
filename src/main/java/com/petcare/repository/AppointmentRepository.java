package com.petcare.repository;

import com.petcare.model.entity.AppointmentEntity;
import com.petcare.model.entity.AppointmentListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.persistence.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointment Repository - table appointments; list query with JOINs
 */
public class AppointmentRepository implements IAppointmentRepository {

    @Override
    public List<AppointmentListDto> findAllForList(String statusCodeOrNull) throws PetcareException {
        List<AppointmentListDto> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT a.appointment_id, a.appointment_date, a.appointment_type, " +
            "c.customer_name, p.pet_name, d.doctor_name, st.service_name, a.status " +
            "FROM appointments a " +
            "INNER JOIN customers c ON a.customer_id = c.customer_id " +
            "INNER JOIN pets p ON a.pet_id = p.pet_id " +
            "LEFT JOIN doctors d ON a.doctor_id = d.doctor_id " +
            "LEFT JOIN service_types st ON a.service_type_id = st.service_type_id " +
            "WHERE 1=1"
        );
        if (statusCodeOrNull != null && !statusCodeOrNull.isEmpty()) {
            sql.append(" AND a.status = ?");
        }
        sql.append(" ORDER BY a.appointment_date DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (statusCodeOrNull != null && !statusCodeOrNull.isEmpty()) {
                ps.setString(1, statusCodeOrNull);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentListDto dto = new AppointmentListDto();
                    dto.setAppointmentId(rs.getInt("appointment_id"));
                    if (rs.getTimestamp("appointment_date") != null) {
                        dto.setAppointmentDate(new java.util.Date(rs.getTimestamp("appointment_date").getTime()));
                    }
                    dto.setAppointmentType(rs.getString("appointment_type"));
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setPetName(rs.getString("pet_name"));
                    dto.setDoctorName(rs.getString("doctor_name"));
                    dto.setServiceName(rs.getString("service_name"));
                    dto.setStatus(rs.getString("status"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách lịch hẹn", ex);
        }
        return list;
    }

    @Override
    public AppointmentEntity findById(int id) throws PetcareException {
        String query = "SELECT appointment_id, customer_id, pet_id, doctor_id, service_type_id, " +
                "appointment_date, appointment_type, status, notes, created_at FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm lịch hẹn theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public int insert(AppointmentEntity entity) throws PetcareException {
        String query = "INSERT INTO appointments (customer_id, pet_id, doctor_id, service_type_id, " +
                "appointment_date, appointment_type, status, notes) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            bindEntity(ps, entity);
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setAppointmentId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm lịch hẹn", ex);
        }
    }

    @Override
    public int update(AppointmentEntity entity) throws PetcareException {
        String query = "UPDATE appointments SET customer_id=?, pet_id=?, doctor_id=?, service_type_id=?, " +
                "appointment_date=?, appointment_type=?, status=?, notes=? WHERE appointment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            bindEntity(ps, entity);
            ps.setInt(9, entity.getAppointmentId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật lịch hẹn", ex);
        }
    }

    @Override
    public int updateStatus(int id, String statusCode) throws PetcareException {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, statusCode);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật trạng thái lịch hẹn", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa lịch hẹn", ex);
        }
    }

    private void bindEntity(PreparedStatement ps, AppointmentEntity e) throws SQLException {
        ps.setInt(1, e.getCustomerId());
        ps.setInt(2, e.getPetId());
        if (e.getDoctorId() != null) {
            ps.setInt(3, e.getDoctorId());
        } else {
            ps.setNull(3, java.sql.Types.INTEGER);
        }
        if (e.getServiceTypeId() != null) {
            ps.setInt(4, e.getServiceTypeId());
        } else {
            ps.setNull(4, java.sql.Types.INTEGER);
        }
        ps.setTimestamp(5, e.getAppointmentDate() != null ? new Timestamp(e.getAppointmentDate().getTime()) : null);
        ps.setString(6, e.getAppointmentType());
        ps.setString(7, e.getStatus());
        ps.setString(8, e.getNotes());
    }

    private AppointmentEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        AppointmentEntity e = new AppointmentEntity();
        e.setAppointmentId(rs.getInt("appointment_id"));
        e.setCustomerId(rs.getInt("customer_id"));
        e.setPetId(rs.getInt("pet_id"));
        int doctorId = rs.getInt("doctor_id");
        e.setDoctorId(rs.wasNull() ? null : doctorId);
        int serviceTypeId = rs.getInt("service_type_id");
        e.setServiceTypeId(rs.wasNull() ? null : serviceTypeId);
        if (rs.getTimestamp("appointment_date") != null) {
            e.setAppointmentDate(new java.util.Date(rs.getTimestamp("appointment_date").getTime()));
        }
        e.setAppointmentType(rs.getString("appointment_type"));
        e.setStatus(rs.getString("status"));
        e.setNotes(rs.getString("notes"));
        if (rs.getTimestamp("created_at") != null) {
            e.setCreatedAt(new java.util.Date(rs.getTimestamp("created_at").getTime()));
        }
        return e;
    }
}
