package com.petcare.repository;

import com.petcare.model.entity.MedicalRecordServiceItemEntity;
import com.petcare.model.entity.MedicalRecordServiceItemListDto;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * Repository for medical_record_services (dịch vụ đã thêm trong lượt khám).
 */
public interface IMedicalRecordServiceItemRepository {

    List<MedicalRecordServiceItemListDto> findByMedicalRecordId(int medicalRecordId) throws PetcareException;

    int insert(MedicalRecordServiceItemEntity entity) throws PetcareException;

    int delete(int recordServiceId) throws PetcareException;

    void deleteByMedicalRecordId(int medicalRecordId) throws PetcareException;
}
