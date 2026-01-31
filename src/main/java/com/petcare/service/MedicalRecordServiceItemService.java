package com.petcare.service;

import com.petcare.model.entity.MedicalRecordServiceItemEntity;
import com.petcare.model.entity.MedicalRecordServiceItemListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IMedicalRecordServiceItemRepository;
import com.petcare.repository.MedicalRecordServiceItemRepository;

import java.util.List;

/**
 * Service: dịch vụ đã thêm trong lượt khám (medical_record_services).
 */
public class MedicalRecordServiceItemService {
    private static MedicalRecordServiceItemService instance;
    private IMedicalRecordServiceItemRepository repository;

    private MedicalRecordServiceItemService() {
        this.repository = new MedicalRecordServiceItemRepository();
    }

    public static MedicalRecordServiceItemService getInstance() {
        if (instance == null) {
            instance = new MedicalRecordServiceItemService();
        }
        return instance;
    }

    public void setRepository(IMedicalRecordServiceItemRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<MedicalRecordServiceItemListDto> getServicesByMedicalRecordId(int medicalRecordId) throws PetcareException {
        return repository.findByMedicalRecordId(medicalRecordId);
    }

    public int addService(int medicalRecordId, int serviceTypeId, int quantity, int unitPrice) throws PetcareException {
        MedicalRecordServiceItemEntity entity = new MedicalRecordServiceItemEntity();
        entity.setMedicalRecordId(medicalRecordId);
        entity.setServiceTypeId(serviceTypeId);
        entity.setQuantity(quantity);
        entity.setUnitPrice(unitPrice);
        entity.setTotalPrice(quantity * unitPrice);
        return repository.insert(entity);
    }

    public int removeService(int recordServiceId) throws PetcareException {
        return repository.delete(recordServiceId);
    }
}
