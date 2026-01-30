package com.petcare.service;

import com.petcare.model.domain.ServiceType;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;

import java.util.List;

public interface IServiceTypeService {
    List<ServiceType> getAllServiceTypes() throws PetcareException;
    ServiceType getServiceTypeById(int id) throws PetcareException;
    ServiceType getServiceTypeByName(String serviceName) throws PetcareException;
    ServiceType getServiceTypeByNamePrefix(String prefix) throws PetcareException;
    void createServiceType(ServiceType serviceType, User currentUser) throws PetcareException;
    void updateServiceType(ServiceType serviceType, User currentUser) throws PetcareException;
    void deleteServiceType(int id, User currentUser) throws PetcareException;
}
