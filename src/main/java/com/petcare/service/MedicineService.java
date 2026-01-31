package com.petcare.service;

import com.petcare.aop.PermissionHandler;
import com.petcare.aop.RequireAdmin;
import com.petcare.model.domain.Medicine;
import com.petcare.model.domain.User;
import com.petcare.model.entity.MedicineEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IMedicineRepository;
import com.petcare.repository.MedicineRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Medicine Service - Entity ↔ Domain; Route enum mapping.
 */
public class MedicineService implements IMedicineService {
    private static IMedicineService instance;
    private IMedicineRepository repository;

    MedicineService() {
        this.repository = new MedicineRepository();
    }

    public static IMedicineService getInstance() {
        if (instance == null) {
            MedicineService impl = new MedicineService();
            instance = PermissionHandler.createProxy(impl, IMedicineService.class);
        }
        return instance;
    }

    public void setRepository(IMedicineRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    @Override
    public List<Medicine> getAllMedicines() throws PetcareException {
        return repository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Medicine getMedicineById(int id) throws PetcareException {
        MedicineEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    @RequireAdmin
    @Override
    public void createMedicine(Medicine medicine, User currentUser) throws PetcareException {
        MedicineEntity entity = domainToEntity(medicine);
        int result = repository.insert(entity);
        if (result > 0) {
            medicine.setMedicineId(entity.getMedicineId());
        } else {
            throw new PetcareException("Không thể tạo thuốc mới");
        }
    }

    @RequireAdmin
    @Override
    public void updateMedicine(Medicine medicine, User currentUser) throws PetcareException {
        MedicineEntity existing = repository.findById(medicine.getMedicineId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy thuốc với ID: " + medicine.getMedicineId());
        }
        MedicineEntity entity = domainToEntity(medicine);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật thuốc");
        }
    }

    @RequireAdmin
    @Override
    public void deleteMedicine(int id, User currentUser) throws PetcareException {
        MedicineEntity existing = repository.findById(id);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy thuốc với ID: " + id);
        }
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa thuốc");
        }
    }

    private Medicine entityToDomain(MedicineEntity e) {
        try {
            Medicine m = new Medicine();
            m.setMedicineId(e.getMedicineId());
            m.setMedicineName(e.getMedicineName());
            m.setMedicineRoute(Medicine.Route.fromCode(e.getMedicineRoute()));
            m.setUnitPrice(e.getUnitPrice());
            return m;
        } catch (PetcareException ex) {
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    private MedicineEntity domainToEntity(Medicine m) {
        MedicineEntity e = new MedicineEntity();
        e.setMedicineId(m.getMedicineId());
        e.setMedicineName(m.getMedicineName());
        e.setMedicineRoute(m.getMedicineRoute() != null ? m.getMedicineRoute().getCode() : null);
        e.setUnitPrice(m.getUnitPrice());
        return e;
    }
}
