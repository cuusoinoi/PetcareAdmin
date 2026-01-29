package com.petcare.repository;

import com.petcare.model.entity.DoctorEntity;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * Doctor Repository Interface
 * Defines data access operations for Doctor entity
 */
public interface IDoctorRepository {

    /**
     * Find all doctors
     *
     * @return List of all DoctorEntity objects
     * @throws PetcareException if database operation fails
     */
    List<DoctorEntity> findAll() throws PetcareException;

    /**
     * Find doctor by ID
     *
     * @param id doctor ID
     * @return DoctorEntity if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    DoctorEntity findById(int id) throws PetcareException;

    /**
     * Find doctor by phone number
     *
     * @param phone phone number
     * @return DoctorEntity if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    DoctorEntity findByPhone(String phone) throws PetcareException;

    /**
     * Insert a new doctor
     *
     * @param entity DoctorEntity to insert
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int insert(DoctorEntity entity) throws PetcareException;

    /**
     * Update an existing doctor
     *
     * @param entity DoctorEntity with updated data
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int update(DoctorEntity entity) throws PetcareException;

    /**
     * Delete a doctor by ID
     *
     * @param id doctor ID to delete
     * @return number of affected rows (should be 1)
     * @throws PetcareException if database operation fails
     */
    int delete(int id) throws PetcareException;

    /**
     * Check if doctor exists by phone number
     *
     * @param phone phone number
     * @return true if exists, false otherwise
     * @throws PetcareException if database operation fails
     */
    boolean existsByPhone(String phone) throws PetcareException;
}
