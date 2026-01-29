package com.petcare.repository;

import com.petcare.model.entity.ServiceTypeEntity;
import com.petcare.model.exception.PetcareException;
import java.util.List;

/**
 * Service Type Repository Interface
 */
public interface IServiceTypeRepository {

    List<ServiceTypeEntity> findAll() throws PetcareException;

    ServiceTypeEntity findById(int id) throws PetcareException;

    ServiceTypeEntity findByName(String serviceName) throws PetcareException;

    /** Find first service type whose name starts with prefix (e.g. "Lưu chuồng"). */
    ServiceTypeEntity findByNameStartsWith(String prefix) throws PetcareException;

    int insert(ServiceTypeEntity entity) throws PetcareException;

    int update(ServiceTypeEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;
}
