package com.petcare.repository;

import com.petcare.model.entity.DoctorEntity;
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
 * Doctor Repository Implementation
 * Handles all database operations for Doctor entity
 */
public class DoctorRepository implements IDoctorRepository {
    
    @Override
    public List<DoctorEntity> findAll() throws PetcareException {
        List<DoctorEntity> doctors = new ArrayList<>();
        String query = "SELECT * FROM doctors ORDER BY doctor_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to fetch all doctors", ex);
        }
        
        return doctors;
    }
    
    @Override
    public DoctorEntity findById(int id) throws PetcareException {
        String query = "SELECT * FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find doctor by ID: " + id, ex);
        }
        
        return null;
    }
    
    @Override
    public DoctorEntity findByPhone(String phone) throws PetcareException {
        String query = "SELECT * FROM doctors WHERE doctor_phone_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find doctor by phone: " + phone, ex);
        }
        
        return null;
    }
    
    @Override
    public int insert(DoctorEntity entity) throws PetcareException {
        String query = "INSERT INTO doctors (doctor_name, doctor_phone_number, " +
                      "doctor_identity_card, doctor_address, doctor_note) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, entity.getDoctorName());
            ps.setString(2, entity.getDoctorPhoneNumber());
            ps.setString(3, entity.getDoctorIdentityCard());
            ps.setString(4, entity.getDoctorAddress());
            ps.setString(5, entity.getDoctorNote());
            
            int result = ps.executeUpdate();
            
            // Get generated ID
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setDoctorId(rs.getInt(1));
                    }
                }
            }
            
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Failed to insert doctor", ex);
        }
    }
    
    @Override
    public int update(DoctorEntity entity) throws PetcareException {
        String query = "UPDATE doctors SET doctor_name = ?, doctor_phone_number = ?, " +
                      "doctor_identity_card = ?, doctor_address = ?, doctor_note = ? " +
                      "WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, entity.getDoctorName());
            ps.setString(2, entity.getDoctorPhoneNumber());
            ps.setString(3, entity.getDoctorIdentityCard());
            ps.setString(4, entity.getDoctorAddress());
            ps.setString(5, entity.getDoctorNote());
            ps.setInt(6, entity.getDoctorId());
            
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Failed to update doctor with ID: " + entity.getDoctorId(), ex);
        }
    }
    
    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Failed to delete doctor with ID: " + id, ex);
        }
    }
    
    @Override
    public boolean existsByPhone(String phone) throws PetcareException {
        String query = "SELECT COUNT(*) FROM doctors WHERE doctor_phone_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to check doctor existence by phone: " + phone, ex);
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to DoctorEntity
     */
    private DoctorEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        DoctorEntity entity = new DoctorEntity();
        entity.setDoctorId(rs.getInt("doctor_id"));
        entity.setDoctorName(rs.getString("doctor_name"));
        entity.setDoctorPhoneNumber(rs.getString("doctor_phone_number"));
        entity.setDoctorIdentityCard(rs.getString("doctor_identity_card"));
        entity.setDoctorAddress(rs.getString("doctor_address"));
        entity.setDoctorNote(rs.getString("doctor_note"));
        return entity;
    }
}
