package com.petcare.service;

import com.petcare.aop.PermissionHandler;
import com.petcare.aop.RequireAdmin;
import com.petcare.model.domain.User;
import com.petcare.model.domain.VaccineType;
import com.petcare.model.entity.VaccineTypeEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IVaccineTypeRepository;
import com.petcare.repository.VaccineTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vaccine Type Service - Entity ↔ Domain.
 */
public class VaccineTypeService implements IVaccineTypeService {
    private static IVaccineTypeService instance;
    private IVaccineTypeRepository repository;

    VaccineTypeService() {
        this.repository = new VaccineTypeRepository();
    }

    public static IVaccineTypeService getInstance() {
        if (instance == null) {
            VaccineTypeService impl = new VaccineTypeService();
            instance = PermissionHandler.createProxy(impl, IVaccineTypeService.class);
        }
        return instance;
    }

    public void setRepository(IVaccineTypeRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    @Override
    public List<VaccineType> getAllVaccineTypes() throws PetcareException {
        return repository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public VaccineType getVaccineTypeById(int id) throws PetcareException {
        VaccineTypeEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    @RequireAdmin
    @Override
    public void createVaccineType(VaccineType vaccineType, User currentUser) throws PetcareException {
        VaccineTypeEntity entity = domainToEntity(vaccineType);
        int result = repository.insert(entity);
        if (result > 0) {
            vaccineType.setVaccineId(entity.getVaccineId());
        } else {
            throw new PetcareException("Không thể tạo vaccine mới");
        }
    }

    @RequireAdmin
    @Override
    public void updateVaccineType(VaccineType vaccineType, User currentUser) throws PetcareException {
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

    @RequireAdmin
    @Override
    public void deleteVaccineType(int id, User currentUser) throws PetcareException {
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
            v.setUnitPrice(e.getUnitPrice());
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
        e.setUnitPrice(v.getUnitPrice());
        return e;
    }
}
