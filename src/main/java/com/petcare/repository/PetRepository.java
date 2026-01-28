package com.petcare.repository;

import com.petcare.model.entity.PetEntity;
import com.petcare.model.exception.PetcareException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Pet Repository Implementation
 * Handles all database operations for Pet entity
 */
public class PetRepository implements IPetRepository {
    
    @Override
    public List<PetEntity> findAll() throws PetcareException {
        List<PetEntity> pets = new ArrayList<>();
        String query = "SELECT * FROM pets ORDER BY pet_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pets.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to fetch all pets", ex);
        }
        
        return pets;
    }
    
    @Override
    public PetEntity findById(int id) throws PetcareException {
        String query = "SELECT * FROM pets WHERE pet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find pet by ID: " + id, ex);
        }
        
        return null;
    }
    
    @Override
    public List<PetEntity> findByCustomerId(int customerId) throws PetcareException {
        List<PetEntity> pets = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE customer_id = ? ORDER BY pet_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find pets by customer ID: " + customerId, ex);
        }
        
        return pets;
    }
    
    @Override
    public int insert(PetEntity entity) throws PetcareException {
        String query = "INSERT INTO pets (customer_id, pet_name, pet_species, " +
                      "pet_gender, pet_dob, pet_weight, pet_sterilization, " +
                      "pet_characteristic, pet_drug_allergy) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, entity.getCustomerId());
            ps.setString(2, entity.getPetName());
            ps.setString(3, entity.getPetSpecies());
            ps.setString(4, entity.getPetGender());
            if (entity.getPetDob() != null) {
                ps.setDate(5, new java.sql.Date(entity.getPetDob().getTime()));
            } else {
                ps.setDate(5, null);
            }
            if (entity.getPetWeight() != null) {
                ps.setDouble(6, entity.getPetWeight());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }
            ps.setString(7, entity.getPetSterilization());
            ps.setString(8, entity.getPetCharacteristic());
            ps.setString(9, entity.getPetDrugAllergy());
            
            int result = ps.executeUpdate();
            
            // Get generated ID
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setPetId(rs.getInt(1));
                    }
                }
            }
            
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Failed to insert pet", ex);
        }
    }
    
    @Override
    public int update(PetEntity entity) throws PetcareException {
        String query = "UPDATE pets SET customer_id = ?, pet_name = ?, pet_species = ?, " +
                      "pet_gender = ?, pet_dob = ?, pet_weight = ?, pet_sterilization = ?, " +
                      "pet_characteristic = ?, pet_drug_allergy = ? WHERE pet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, entity.getCustomerId());
            ps.setString(2, entity.getPetName());
            ps.setString(3, entity.getPetSpecies());
            ps.setString(4, entity.getPetGender());
            if (entity.getPetDob() != null) {
                ps.setDate(5, new java.sql.Date(entity.getPetDob().getTime()));
            } else {
                ps.setDate(5, null);
            }
            if (entity.getPetWeight() != null) {
                ps.setDouble(6, entity.getPetWeight());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }
            ps.setString(7, entity.getPetSterilization());
            ps.setString(8, entity.getPetCharacteristic());
            ps.setString(9, entity.getPetDrugAllergy());
            ps.setInt(10, entity.getPetId());
            
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Failed to update pet with ID: " + entity.getPetId(), ex);
        }
    }
    
    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM pets WHERE pet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Failed to delete pet with ID: " + id, ex);
        }
    }
    
    /**
     * Map ResultSet to PetEntity
     */
    private PetEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        PetEntity entity = new PetEntity();
        entity.setPetId(rs.getInt("pet_id"));
        entity.setCustomerId(rs.getInt("customer_id"));
        entity.setPetName(rs.getString("pet_name"));
        entity.setPetSpecies(rs.getString("pet_species"));
        entity.setPetGender(rs.getString("pet_gender"));
        
        if (rs.getDate("pet_dob") != null) {
            entity.setPetDob(new java.util.Date(rs.getDate("pet_dob").getTime()));
        }
        
        if (rs.getObject("pet_weight") != null) {
            entity.setPetWeight(rs.getDouble("pet_weight"));
        }
        
        entity.setPetSterilization(rs.getString("pet_sterilization"));
        entity.setPetCharacteristic(rs.getString("pet_characteristic"));
        entity.setPetDrugAllergy(rs.getString("pet_drug_allergy"));
        
        if (rs.getTimestamp("created_at") != null) {
            entity.setCreatedAt(new java.util.Date(rs.getTimestamp("created_at").getTime()));
        }
        
        return entity;
    }
}
