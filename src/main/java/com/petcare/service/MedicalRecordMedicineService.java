package com.petcare.service;

import com.petcare.model.entity.MedicalRecordMedicineEntity;
import com.petcare.model.entity.MedicalRecordMedicineListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IMedicalRecordMedicineRepository;
import com.petcare.repository.MedicalRecordMedicineRepository;

import java.util.List;

/**
 * Service: thuốc kê trong lượt khám (medical_record_medicines).
 */
public class MedicalRecordMedicineService {
    private static MedicalRecordMedicineService instance;
    private IMedicalRecordMedicineRepository repository;

    private MedicalRecordMedicineService() {
        this.repository = new MedicalRecordMedicineRepository();
    }

    public static MedicalRecordMedicineService getInstance() {
        if (instance == null) {
            instance = new MedicalRecordMedicineService();
        }
        return instance;
    }

    public void setRepository(IMedicalRecordMedicineRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<MedicalRecordMedicineListDto> getMedicinesByMedicalRecordId(int medicalRecordId) throws PetcareException {
        return repository.findByMedicalRecordId(medicalRecordId);
    }

    public int addMedicine(int medicalRecordId, int medicineId, int quantity, int unitPrice) throws PetcareException {
        MedicalRecordMedicineEntity entity = new MedicalRecordMedicineEntity();
        entity.setMedicalRecordId(medicalRecordId);
        entity.setMedicineId(medicineId);
        entity.setQuantity(quantity);
        entity.setUnitPrice(unitPrice);
        entity.setTotalPrice(quantity * unitPrice);
        return repository.insert(entity);
    }

    public int removeMedicine(int recordMedicineId) throws PetcareException {
        return repository.delete(recordMedicineId);
    }
}
