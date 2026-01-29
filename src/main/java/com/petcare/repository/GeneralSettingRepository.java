package com.petcare.repository;

import com.petcare.model.entity.GeneralSettingEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

/**
 * General Setting Repository - table general_settings (single row)
 * checkout_hour is TIME in DB - we read as string HH:mm
 */
public class GeneralSettingRepository implements IGeneralSettingRepository {

    @Override
    public GeneralSettingEntity findFirst() throws PetcareException {
        String query = "SELECT setting_id, clinic_name, clinic_address_1, clinic_address_2, " +
                "phone_number_1, phone_number_2, representative_name, checkout_hour, " +
                "overtime_fee_per_hour, default_daily_rate, signing_place FROM general_settings LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải cài đặt", ex);
        }
        return null;
    }

    @Override
    public boolean exists() throws PetcareException {
        String query = "SELECT COUNT(*) AS cnt FROM general_settings";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi kiểm tra cài đặt", ex);
        }
        return false;
    }

    @Override
    public int insert(GeneralSettingEntity entity) throws PetcareException {
        String query = "INSERT INTO general_settings (clinic_name, clinic_address_1, clinic_address_2, " +
                "phone_number_1, phone_number_2, representative_name, checkout_hour, " +
                "overtime_fee_per_hour, default_daily_rate, signing_place) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            bindEntity(ps, entity);
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setSettingId(rs.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm cài đặt", ex);
        }
    }

    @Override
    public int update(GeneralSettingEntity entity) throws PetcareException {
        String query = "UPDATE general_settings SET clinic_name=?, clinic_address_1=?, clinic_address_2=?, " +
                "phone_number_1=?, phone_number_2=?, representative_name=?, checkout_hour=?, " +
                "overtime_fee_per_hour=?, default_daily_rate=?, signing_place=? WHERE setting_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            bindEntity(ps, entity);
            ps.setInt(11, entity.getSettingId());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi cập nhật cài đặt", ex);
        }
    }

    private void bindEntity(PreparedStatement ps, GeneralSettingEntity e) throws SQLException {
        ps.setString(1, e.getClinicName());
        ps.setString(2, e.getClinicAddress1());
        ps.setString(3, e.getClinicAddress2());
        ps.setString(4, e.getPhoneNumber1());
        ps.setString(5, e.getPhoneNumber2());
        ps.setString(6, e.getRepresentativeName());
        String hour = e.getCheckoutHour();
        if (hour != null && !hour.contains(":")) hour = "18:00";
        if (hour != null && hour.length() == 5) hour = hour + ":00";
        ps.setString(7, hour);
        ps.setInt(8, e.getOvertimeFeePerHour());
        ps.setInt(9, e.getDefaultDailyRate());
        ps.setString(10, e.getSigningPlace());
    }

    private GeneralSettingEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        GeneralSettingEntity e = new GeneralSettingEntity();
        e.setSettingId(rs.getInt("setting_id"));
        e.setClinicName(rs.getString("clinic_name"));
        e.setClinicAddress1(rs.getString("clinic_address_1"));
        e.setClinicAddress2(rs.getString("clinic_address_2"));
        e.setPhoneNumber1(rs.getString("phone_number_1"));
        e.setPhoneNumber2(rs.getString("phone_number_2"));
        e.setRepresentativeName(rs.getString("representative_name"));
        Time t = rs.getTime("checkout_hour");
        if (t != null) {
            String s = t.toString();
            e.setCheckoutHour(s.length() >= 5 ? s.substring(0, 5) : "18:00");
        } else {
            e.setCheckoutHour("18:00");
        }
        e.setOvertimeFeePerHour(rs.getInt("overtime_fee_per_hour"));
        e.setDefaultDailyRate(rs.getInt("default_daily_rate"));
        e.setSigningPlace(rs.getString("signing_place"));
        return e;
    }
}
