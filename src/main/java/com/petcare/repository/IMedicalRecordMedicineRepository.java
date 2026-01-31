package com.petcare.repository;

import com.petcare.model.entity.MedicalRecordMedicineEntity;
import com.petcare.model.entity.MedicalRecordMedicineListDto;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * Repository for medical_record_medicines (thuốc kê trong lượt khám).
 */
public interface IMedicalRecordMedicineRepository {

    List<MedicalRecordMedicineListDto> findByMedicalRecordId(int medicalRecordId) throws PetcareException;

    int insert(MedicalRecordMedicineEntity entity) throws PetcareException;

    int delete(int recordMedicineId) throws PetcareException;

    void deleteByMedicalRecordId(int medicalRecordId) throws PetcareException;
}
