package com.petcare.repository;

import com.petcare.model.entity.CustomerEntity;
import com.petcare.model.exception.PetcareException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer Repository Implementation
 * Handles all database operations for Customer entity
 * Uses PreparedStatement to prevent SQL Injection
 */
public class CustomerRepository implements ICustomerRepository {
    
    @Override
    public List<CustomerEntity> findAll() throws PetcareException {
        List<CustomerEntity> customers = new ArrayList<>();
        String query = "SELECT * FROM customers ORDER BY customer_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                customers.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to fetch all customers", ex);
        }
        
        return customers;
    }
    
    @Override
    public CustomerEntity findById(int id) throws PetcareException {
        String query = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find customer by ID: " + id, ex);
        }
        
        return null;
    }
    
    @Override
    public CustomerEntity findByPhone(String phone) throws PetcareException {
        String query = "SELECT * FROM customers WHERE customer_phone_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find customer by phone: " + phone, ex);
        }
        
        return null;
    }
    
    @Override
    public CustomerEntity findByEmail(String email) throws PetcareException {
        String query = "SELECT * FROM customers WHERE customer_email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to find customer by email: " + email, ex);
        }
        
        return null;
    }
    
    @Override
    public int insert(CustomerEntity entity) throws PetcareException {
        String query = "INSERT INTO customers (customer_name, customer_phone_number, " +
                      "customer_email, customer_identity_card, customer_address, customer_note) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, entity.getCustomerName());
            ps.setString(2, entity.getCustomerPhoneNumber());
            ps.setString(3, entity.getCustomerEmail());
            ps.setString(4, entity.getCustomerIdentityCard());
            ps.setString(5, entity.getCustomerAddress());
            ps.setString(6, entity.getCustomerNote());
            
            int result = ps.executeUpdate();
            
            // Get generated ID
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setCustomerId(rs.getInt(1));
                    }
                }
            }
            
            return result;
        } catch (SQLException ex) {
            throw new PetcareException("Failed to insert customer", ex);
        }
    }
    
    @Override
    public int update(CustomerEntity entity) throws PetcareException {
        String query = "UPDATE customers SET customer_name = ?, customer_phone_number = ?, " +
                      "customer_email = ?, customer_identity_card = ?, customer_address = ?, " +
                      "customer_note = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, entity.getCustomerName());
            ps.setString(2, entity.getCustomerPhoneNumber());
            ps.setString(3, entity.getCustomerEmail());
            ps.setString(4, entity.getCustomerIdentityCard());
            ps.setString(5, entity.getCustomerAddress());
            ps.setString(6, entity.getCustomerNote());
            ps.setInt(7, entity.getCustomerId());
            
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Failed to update customer with ID: " + entity.getCustomerId(), ex);
        }
    }
    
    @Override
    public int delete(int id) throws PetcareException {
        String query = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Failed to delete customer with ID: " + id, ex);
        }
    }
    
    @Override
    public boolean existsByPhone(String phone) throws PetcareException {
        String query = "SELECT COUNT(*) as count FROM customers WHERE customer_phone_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to check customer existence by phone: " + phone, ex);
        }
        
        return false;
    }
    
    @Override
    public boolean existsByEmail(String email) throws PetcareException {
        String query = "SELECT COUNT(*) as count FROM customers WHERE customer_email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Failed to check customer existence by email: " + email, ex);
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to CustomerEntity
     * Helper method to convert database row to entity object
     */
    private CustomerEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        CustomerEntity entity = new CustomerEntity();
        entity.setCustomerId(rs.getInt("customer_id"));
        entity.setCustomerName(rs.getString("customer_name"));
        entity.setCustomerPhoneNumber(rs.getString("customer_phone_number"));
        entity.setCustomerEmail(rs.getString("customer_email"));
        entity.setCustomerIdentityCard(rs.getString("customer_identity_card"));
        entity.setCustomerAddress(rs.getString("customer_address"));
        entity.setCustomerNote(rs.getString("customer_note"));
        return entity;
    }
}
