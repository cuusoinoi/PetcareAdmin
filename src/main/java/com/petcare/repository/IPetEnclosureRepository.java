package com.petcare.repository;

import com.petcare.model.entity.PetEnclosureEntity;
import com.petcare.model.entity.PetEnclosureListDto;
import com.petcare.model.exception.PetcareException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Pet Enclosure Repository Interface
 */
public interface IPetEnclosureRepository {

    List<PetEnclosureListDto> findAllForList() throws PetcareException;

    PetEnclosureEntity findById(int id) throws PetcareException;

    int insert(PetEnclosureEntity entity) throws PetcareException;

    int update(PetEnclosureEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;

    int updateCheckOut(int id, Date checkOutDate) throws PetcareException;

    int countThisMonth() throws PetcareException;

    int countByDate(Date date) throws PetcareException;

    Map<String, Map<String, Integer>> getCheckinCheckoutStats(int days) throws PetcareException;
}
