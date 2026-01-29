package com.petcare.repository;

import com.petcare.model.entity.PetVaccinationEntity;
import com.petcare.model.entity.PetVaccinationListDto;
import com.petcare.model.exception.PetcareException;
import java.util.List;

/**
 * Pet Vaccination Repository Interface
 */
public interface IPetVaccinationRepository {

    List<PetVaccinationListDto> findAllForList() throws PetcareException;

    PetVaccinationEntity findById(int id) throws PetcareException;

    int insert(PetVaccinationEntity entity) throws PetcareException;

    int update(PetVaccinationEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;
}
