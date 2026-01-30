package com.petcare.service;

import com.petcare.model.domain.User;
import com.petcare.model.domain.VaccineType;
import com.petcare.model.exception.PetcareException;

import java.util.List;

public interface IVaccineTypeService {
    List<VaccineType> getAllVaccineTypes() throws PetcareException;
    VaccineType getVaccineTypeById(int id) throws PetcareException;
    void createVaccineType(VaccineType vaccineType, User currentUser) throws PetcareException;
    void updateVaccineType(VaccineType vaccineType, User currentUser) throws PetcareException;
    void deleteVaccineType(int id, User currentUser) throws PetcareException;
}
