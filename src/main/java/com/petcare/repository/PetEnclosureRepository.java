package com.petcare.repository;

import com.petcare.model.entity.PetEnclosureEntity;
import com.petcare.model.entity.PetEnclosureListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pet Enclosure Repository - table pet_enclosures; list with JOINs; count for dashboard
 */
public class PetEnclosureRepository implements IPetEnclosureRepository {

    @Override
    public List<PetEnclosureListDto> findAllForList() throws PetcareException {
        String query = "SELECT pe.pet_enclosure_id, pe.pet_enclosure_number, c.customer_name, " +
                "p.pet_name, pe.check_in_date, pe.check_out_date, pe.daily_rate, " +
                "pe.deposit, pe.pet_enclosure_status " +
                "FROM pet_enclosures pe " +
                "INNER JOIN customers c ON pe.customer_id = c.customer_id " +
                "INNER JOIN pets p ON pe.pet_id = p.pet_id " +
                "ORDER BY pe.pet_enclosure_id DESC";
        List<PetEnclosureListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PetEnclosureListDto dto = new PetEnclosureListDto();
                dto.setPetEnclosureId(rs.getInt("pet_enclosure_id"));
                dto.setPetEnclosureNumber(rs.getInt("pet_enclosure_number"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setPetName(rs.getString("pet_name"));
                if (rs.getTimestamp("check_in_date") != null) {
                    dto.setCheckInDate(new Date(rs.getTimestamp("check_in_date").getTime()));
                }
                if (rs.getTimestamp("check_out_date") != null) {
                    dto.setCheckOutDate(new Date(rs.getTimestamp("check_out_date").getTime()));
                }
                dto.setDailyRate(rs.getInt("daily_rate"));
                dto.setDeposit(rs.getInt("deposit"));
                dto.setPetEnclosureStatus(rs.getString("pet_enclosure_status"));
                list.add(dto);
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách lưu chuồng", ex);
        }
        return list;
    }

    @Override
    public PetEnclosureEntity findById(int id) throws PetcareException {
        String query = "SELECT pet_enclosure_id, customer_id, pet_id, pet_enclosure_number, " +
                "check_in_date, check_out_date, daily_rate, deposit, emergency_limit, " +
                "pet_enclosure_note, pet_enclosure_status FROM pet_enclosures WHERE pet_enclosure_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm lưu chuồng theo ID: " + id, ex);
        }
        return null;
    }

    @Override
    public int insert(PetEnclosureEntity entity) throws PetcareException {
        String query = "INSERT INTO pet_enclosures (customer_id, pet_id, pet_enclosure_number, " +
                "check_in_date, daily_rate, deposit, emergency_limit, pet_enclosure_note, pet_enclosure_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Check In')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            bindEntityForInsert(ps, entity);
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setPetEnclosureId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm lưu chuồng", ex);
        }
    }

    @Override
    public int update(PetEnclosureEntity entity) throws PetcareException {
        String query = "UPDATE pet_enclosures SET customer_id=?, pet_id=?, pet_enclosure_number=?, " +
                "check_in_date=?, daily_rate=?, deposit=?, emergency_limit=?, pet_enclosure_note=? " +
                "WHERE pet_enclosure_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            bindEntityForUpdate(ps, entity);
            ps.setInt(9, entity.getPetEnclosureId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật lưu chuồng", ex);
        }
    }

    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM pet_enclosures WHERE pet_enclosure_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa lưu chuồng", ex);
        }
    }

    @Override
    public int updateCheckOut(int id, Date checkOutDate) throws PetcareException {
        String query = "UPDATE pet_enclosures SET check_out_date = ?, pet_enclosure_status = 'Check Out' WHERE pet_enclosure_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setTimestamp(1, checkOutDate != null ? new Timestamp(checkOutDate.getTime()) : null);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật check-out", ex);
        }
    }

    @Override
    public int countThisMonth() throws PetcareException {
        String query = "SELECT COUNT(*) as count FROM pet_enclosures " +
                "WHERE YEAR(check_in_date) = YEAR(CURRENT_DATE) AND MONTH(check_in_date) = MONTH(CURRENT_DATE)";
        return countQuery(query);
    }

    @Override
    public int countByDate(Date date) throws PetcareException {
        String query = "SELECT COUNT(*) as count FROM pet_enclosures WHERE DATE(check_in_date) = ?";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, dateStr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("count");
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi đếm lưu chuồng theo ngày", ex);
        }
        return 0;
    }

    @Override
    public Map<String, Map<String, Integer>> getCheckinCheckoutStats(int days) throws PetcareException {
        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String checkinQuery = "SELECT COUNT(*) as count FROM pet_enclosures WHERE DATE(check_in_date) = ?";
        String checkoutQuery = "SELECT COUNT(*) as count FROM pet_enclosures WHERE DATE(check_out_date) = ?";
        for (int i = days - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = sdf.format(cal.getTime());
            Map<String, Integer> dayStats = new HashMap<>();
            try (Connection conn = DatabaseConnection.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(checkinQuery)) {
                    ps.setString(1, dateStr);
                    try (ResultSet rs = ps.executeQuery()) {
                        dayStats.put("checkin", rs.next() ? rs.getInt("count") : 0);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(checkoutQuery)) {
                    ps.setString(1, dateStr);
                    try (ResultSet rs = ps.executeQuery()) {
                        dayStats.put("checkout", rs.next() ? rs.getInt("count") : 0);
                    }
                }
            } catch (SQLException ex) {
                throw new PetcareException("Lỗi khi đếm check-in/check-out", ex);
            }
            result.put(dateStr, dayStats);
        }
        return result;
    }

    private int countQuery(String query) throws PetcareException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi đếm lưu chuồng", ex);
        }
        return 0;
    }

    private void bindEntityForInsert(PreparedStatement ps, PetEnclosureEntity e) throws SQLException {
        ps.setInt(1, e.getCustomerId());
        ps.setInt(2, e.getPetId());
        ps.setInt(3, e.getPetEnclosureNumber());
        ps.setTimestamp(4, e.getCheckInDate() != null ? new Timestamp(e.getCheckInDate().getTime()) : null);
        ps.setInt(5, e.getDailyRate());
        ps.setInt(6, e.getDeposit());
        ps.setInt(7, e.getEmergencyLimit());
        ps.setString(8, e.getPetEnclosureNote());
    }

    private void bindEntityForUpdate(PreparedStatement ps, PetEnclosureEntity e) throws SQLException {
        ps.setInt(1, e.getCustomerId());
        ps.setInt(2, e.getPetId());
        ps.setInt(3, e.getPetEnclosureNumber());
        ps.setTimestamp(4, e.getCheckInDate() != null ? new Timestamp(e.getCheckInDate().getTime()) : null);
        ps.setInt(5, e.getDailyRate());
        ps.setInt(6, e.getDeposit());
        ps.setInt(7, e.getEmergencyLimit());
        ps.setString(8, e.getPetEnclosureNote());
    }

    private PetEnclosureEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        PetEnclosureEntity e = new PetEnclosureEntity();
        e.setPetEnclosureId(rs.getInt("pet_enclosure_id"));
        e.setCustomerId(rs.getInt("customer_id"));
        e.setPetId(rs.getInt("pet_id"));
        e.setPetEnclosureNumber(rs.getInt("pet_enclosure_number"));
        if (rs.getTimestamp("check_in_date") != null) {
            e.setCheckInDate(new Date(rs.getTimestamp("check_in_date").getTime()));
        }
        if (rs.getTimestamp("check_out_date") != null) {
            e.setCheckOutDate(new Date(rs.getTimestamp("check_out_date").getTime()));
        }
        e.setDailyRate(rs.getInt("daily_rate"));
        e.setDeposit(rs.getInt("deposit"));
        e.setEmergencyLimit(rs.getInt("emergency_limit"));
        e.setPetEnclosureNote(rs.getString("pet_enclosure_note"));
        e.setPetEnclosureStatus(rs.getString("pet_enclosure_status"));
        return e;
    }
}
