package com.petcare.repository;

import com.petcare.model.entity.CustomerEntity;
import com.petcare.model.exception.PetcareException;
import java.util.List;

/**
 * Customer Repository Interface
 * Defines data access operations for Customer entity
 * Follows Repository Pattern for separation of concerns
 */
public interface ICustomerRepository {
    
    /**
     * Find all customers
     * 
     * @return List of all CustomerEntity objects
     * @throws PetcareException if database operation fails
     */
    List<CustomerEntity> findAll() throws PetcareException;
    
    /**
     * Find customer by ID
     * 
     * @param id customer ID
     * @return CustomerEntity if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    CustomerEntity findById(int id) throws PetcareException;
    
    /**
     * Find customer by phone number
     * 
     * @param phone phone number
     * @return CustomerEntity if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    CustomerEntity findByPhone(String phone) throws PetcareException;
    
    /**
     * Find customer by email
     * 
     * @param email email address
     * @return CustomerEntity if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    CustomerEntity findByEmail(String email) throws PetcareException;
    
    /**
     * Insert a new customer
     * 
     * @param entity CustomerEntity to insert
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int insert(CustomerEntity entity) throws PetcareException;
    
    /**
     * Update an existing customer
     * 
     * @param entity CustomerEntity with updated data
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int update(CustomerEntity entity) throws PetcareException;
    
    /**
     * Delete a customer by ID
     * 
     * @param id customer ID to delete
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int delete(int id) throws PetcareException;
    
    /**
     * Check if customer exists by phone number
     * 
     * @param phone phone number to check
     * @return true if exists, false otherwise
     * @throws PetcareException if database operation fails
     */
    boolean existsByPhone(String phone) throws PetcareException;
    
    /**
     * Check if customer exists by email
     * 
     * @param email email to check
     * @return true if exists, false otherwise
     * @throws PetcareException if database operation fails
     */
    boolean existsByEmail(String email) throws PetcareException;
}
