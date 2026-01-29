package com.petcare.service;

import com.petcare.model.domain.ServiceType;
import com.petcare.model.entity.ServiceTypeEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IServiceTypeRepository;
import com.petcare.repository.ServiceTypeRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Type Service - business logic; Entity ↔ Domain
 */
public class ServiceTypeService {
    private static ServiceTypeService instance;
    private IServiceTypeRepository repository;

    private ServiceTypeService() {
        this.repository = new ServiceTypeRepository();
    }

    public static ServiceTypeService getInstance() {
        if (instance == null) {
            instance = new ServiceTypeService();
        }
        return instance;
    }

    public void setRepository(IServiceTypeRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<ServiceType> getAllServiceTypes() throws PetcareException {
        return repository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    public ServiceType getServiceTypeById(int id) throws PetcareException {
        ServiceTypeEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Find by exact service name (for AddInvoiceDialog, CheckoutDialog).
     */
    public ServiceType getServiceTypeByName(String serviceName) throws PetcareException {
        ServiceTypeEntity entity = repository.findByName(serviceName);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Find first service type whose name starts with prefix (e.g. "Lưu chuồng", "Phụ thu trễ giờ").
     */
    public ServiceType getServiceTypeByNamePrefix(String prefix) throws PetcareException {
        ServiceTypeEntity entity = repository.findByNameStartsWith(prefix);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createServiceType(ServiceType serviceType) throws PetcareException {
        ServiceTypeEntity entity = domainToEntity(serviceType);
        int result = repository.insert(entity);
        if (result > 0) {
            serviceType.setServiceTypeId(entity.getServiceTypeId());
        } else {
            throw new PetcareException("Không thể tạo loại dịch vụ mới");
        }
    }

    public void updateServiceType(ServiceType serviceType) throws PetcareException {
        ServiceTypeEntity existing = repository.findById(serviceType.getServiceTypeId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy loại dịch vụ với ID: " + serviceType.getServiceTypeId());
        }
        ServiceTypeEntity entity = domainToEntity(serviceType);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật loại dịch vụ");
        }
    }

    public void deleteServiceType(int id) throws PetcareException {
        ServiceTypeEntity existing = repository.findById(id);
        if (existing == null) {
            throw new PetcareException("Không tìm thấy loại dịch vụ với ID: " + id);
        }
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa loại dịch vụ");
        }
    }

    private ServiceType entityToDomain(ServiceTypeEntity e) {
        try {
            ServiceType s = new ServiceType();
            s.setServiceTypeId(e.getServiceTypeId());
            s.setServiceName(e.getServiceName());
            s.setDescription(e.getDescription());
            s.setPrice(e.getPrice());
            return s;
        } catch (PetcareException ex) {
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    private ServiceTypeEntity domainToEntity(ServiceType s) {
        ServiceTypeEntity e = new ServiceTypeEntity();
        e.setServiceTypeId(s.getServiceTypeId());
        e.setServiceName(s.getServiceName());
        e.setDescription(s.getDescription());
        e.setPrice(s.getPrice());
        return e;
    }
}
