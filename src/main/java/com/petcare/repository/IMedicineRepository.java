package com.petcare.repository;

import com.petcare.model.entity.MedicineEntity;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * Medicine Repository Interface
 */
public interface IMedicineRepository {

    List<MedicineEntity> findAll() throws PetcareException;

    MedicineEntity findById(int id) throws PetcareException;

    int insert(MedicineEntity entity) throws PetcareException;

    int update(MedicineEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;
}
