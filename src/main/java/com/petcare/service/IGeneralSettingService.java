package com.petcare.service;

import com.petcare.model.domain.GeneralSetting;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;

public interface IGeneralSettingService {
    GeneralSetting getSettings() throws PetcareException;
    void saveSettings(GeneralSetting setting, User currentUser) throws PetcareException;
    int getCheckoutHour() throws PetcareException;
    int getOvertimeFeePerHour() throws PetcareException;
}
