package com.petcare.repository;

import com.petcare.model.entity.MedicalRecordEntity;
import com.petcare.model.entity.MedicalRecordListDto;
import com.petcare.model.exception.PetcareException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Medical Record Repository Interface
 */
public interface IMedicalRecordRepository {

    List<MedicalRecordListDto> findAllForList() throws PetcareException;

    MedicalRecordEntity findById(int id) throws PetcareException;

    int insert(MedicalRecordEntity entity) throws PetcareException;

    int update(MedicalRecordEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;

    int countThisMonth() throws PetcareException;

    int countByDate(Date date) throws PetcareException;

    Map<String, Integer> countByDay(int days) throws PetcareException;
}
