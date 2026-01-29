package com.petcare.repository;

import com.petcare.model.entity.VaccineTypeEntity;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * Vaccine Type Repository Interface (table: vaccines)
 */
public interface IVaccineTypeRepository {

    List<VaccineTypeEntity> findAll() throws PetcareException;

    VaccineTypeEntity findById(int id) throws PetcareException;

    int insert(VaccineTypeEntity entity) throws PetcareException;

    int update(VaccineTypeEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;
}
