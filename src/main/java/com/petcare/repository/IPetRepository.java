package com.petcare.repository;

import com.petcare.model.entity.PetEntity;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * Pet Repository Interface
 * Defines data access operations for Pet entity
 */
public interface IPetRepository {

    /**
     * Find all pets
     *
     * @return List of all PetEntity objects
     * @throws PetcareException if database operation fails
     */
    List<PetEntity> findAll() throws PetcareException;

    /**
     * Find pet by ID
     *
     * @param id pet ID
     * @return PetEntity if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    PetEntity findById(int id) throws PetcareException;

    /**
     * Find pets by customer ID
     *
     * @param customerId customer ID
     * @return List of PetEntity objects
     * @throws PetcareException if database operation fails
     */
    List<PetEntity> findByCustomerId(int customerId) throws PetcareException;

    /**
     * Insert a new pet
     *
     * @param entity PetEntity to insert
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int insert(PetEntity entity) throws PetcareException;

    /**
     * Update an existing pet
     *
     * @param entity PetEntity with updated data
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int update(PetEntity entity) throws PetcareException;

    /**
     * Delete a pet by ID
     *
     * @param id pet ID to delete
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int delete(int id) throws PetcareException;
}
