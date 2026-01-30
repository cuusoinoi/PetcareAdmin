package com.petcare.service;

import com.petcare.model.domain.Medicine;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;

import java.util.List;

public interface IMedicineService {
    List<Medicine> getAllMedicines() throws PetcareException;
    Medicine getMedicineById(int id) throws PetcareException;
    void createMedicine(Medicine medicine, User currentUser) throws PetcareException;
    void updateMedicine(Medicine medicine, User currentUser) throws PetcareException;
    void deleteMedicine(int id, User currentUser) throws PetcareException;
}
