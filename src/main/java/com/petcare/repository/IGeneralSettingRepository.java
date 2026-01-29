package com.petcare.repository;

import com.petcare.model.entity.GeneralSettingEntity;
import com.petcare.model.exception.PetcareException;

/**
 * General Setting Repository - single row config (general_settings)
 */
public interface IGeneralSettingRepository {

    /** Get first row or null if table empty */
    GeneralSettingEntity findFirst() throws PetcareException;

    boolean exists() throws PetcareException;

    int insert(GeneralSettingEntity entity) throws PetcareException;

    int update(GeneralSettingEntity entity) throws PetcareException;
}
