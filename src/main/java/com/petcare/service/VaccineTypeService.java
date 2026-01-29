package com.petcare.service;

import com.petcare.model.domain.VaccineType;
import com.petcare.model.entity.VaccineTypeEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IVaccineTypeRepository;
import com.petcare.repository.VaccineTypeRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vaccine Type Service - Entity ↔ Domain
 */
public class VaccineTypeService {
    private static VaccineTypeService instance;
    private IVaccineTypeRepository repository;

    private VaccineTypeService() {
        this.repository = new VaccineTypeRepository();
    }

    public static VaccineTypeService getInstance() {
        if (instance == null) {
            instance = new VaccineTypeService();
        }
        return instance;
    }

    public void setRepository(IVaccineTypeRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<VaccineType> getAllVaccineTypes() throws PetcareException {
        return repository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    public VaccineType getVaccineTypeById(int id) throws PetcareException {
        VaccineTypeEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createVaccineType(VaccineType vaccineType) throws PetcareException {
        VaccineTypeEntity entity = domainToEntity(vaccineType);
        int result = repository.insert(entity);
        if (result > 0) {
            vaccineType.setVaccineId(entity.getVaccineId());
        } else {
            throw new PetcareException("Không thể tạo vaccine mới");
        }
    }

    public void updateVaccineType(VaccineType vaccineType) throws PetcareException {
        VaccineTypeEntity existing = repository.findById(vaccineType.getVaccineId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy vaccine với ID: " + vaccineType.getVaccineId());
        }
        VaccineTypeEntity entity = domainToEntity(vaccineType);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật vaccine");
        }
    }

    public void deleteVaccineType(int id) throws PetcareException {
        VaccineTypeEntity existing = repository.findById(id);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy vaccine với ID: " + id);
        }
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa vaccine");
        }
    }

    private VaccineType entityToDomain(VaccineTypeEntity e) {
        try {
            VaccineType v = new VaccineType();
            v.setVaccineId(e.getVaccineId());
            v.setVaccineName(e.getVaccineName());
            v.setDescription(e.getDescription());
            return v;
        } catch (PetcareException ex) {
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    private VaccineTypeEntity domainToEntity(VaccineType v) {
        VaccineTypeEntity e = new VaccineTypeEntity();
        e.setVaccineId(v.getVaccineId());
        e.setVaccineName(v.getVaccineName());
        e.setDescription(v.getDescription());
        return e;
    }
}
