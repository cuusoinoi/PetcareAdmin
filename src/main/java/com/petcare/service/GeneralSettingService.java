package com.petcare.service;

import com.petcare.aop.PermissionHandler;
import com.petcare.aop.RequireAdmin;
import com.petcare.model.domain.GeneralSetting;
import com.petcare.model.domain.User;
import com.petcare.model.entity.GeneralSettingEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.GeneralSettingRepository;
import com.petcare.repository.IGeneralSettingRepository;

/**
 * General Setting Service - single row config; Entity ↔ Domain.
 */
public class GeneralSettingService implements IGeneralSettingService {
    private static IGeneralSettingService instance;
    private IGeneralSettingRepository repository;

    GeneralSettingService() {
        this.repository = new GeneralSettingRepository();
    }

    public static IGeneralSettingService getInstance() {
        if (instance == null) {
            GeneralSettingService impl = new GeneralSettingService();
            instance = PermissionHandler.createProxy(impl, IGeneralSettingService.class);
        }
        return instance;
    }

    public void setRepository(IGeneralSettingRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    @Override
    public GeneralSetting getSettings() throws PetcareException {
        GeneralSettingEntity entity = repository.findFirst();
        return entity != null ? entityToDomain(entity) : null;
    }

    @RequireAdmin
    @Override
    public void saveSettings(GeneralSetting setting, User currentUser) throws PetcareException {
        setting.validate();
        GeneralSettingEntity entity = domainToEntity(setting);
        if (repository.exists()) {
            GeneralSettingEntity existing = repository.findFirst();
            if (existing != null) {
                entity.setSettingId(existing.getSettingId());
                repository.update(entity);
            } else {
                repository.insert(entity);
            }
        } else {
            repository.insert(entity);
            if (entity.getSettingId() > 0) {
                setting.setSettingId(entity.getSettingId());
            }
        }
    }

    @Override
    public int getCheckoutHour() throws PetcareException {
        GeneralSetting s = getSettings();
        if (s == null || s.getCheckoutHour() == null) return 18;
        String h = s.getCheckoutHour().trim();
        if (h.length() >= 2) {
            try {
                return Integer.parseInt(h.substring(0, h.indexOf(':')));
            } catch (Exception ignored) {
            }
        }
        return 18;
    }

    @Override
    public int getOvertimeFeePerHour() throws PetcareException {
        GeneralSetting s = getSettings();
        return s != null && s.getOvertimeFeePerHour() > 0 ? s.getOvertimeFeePerHour() : 25000;
    }

    private GeneralSetting entityToDomain(GeneralSettingEntity e) {
        GeneralSetting s = new GeneralSetting();
        s.setSettingId(e.getSettingId());
        s.setClinicName(e.getClinicName());
        s.setClinicAddress1(e.getClinicAddress1());
        s.setClinicAddress2(e.getClinicAddress2());
        s.setPhoneNumber1(e.getPhoneNumber1());
        s.setPhoneNumber2(e.getPhoneNumber2());
        s.setRepresentativeName(e.getRepresentativeName());
        s.setCheckoutHour(e.getCheckoutHour());
        s.setOvertimeFeePerHour(e.getOvertimeFeePerHour());
        s.setDefaultDailyRate(e.getDefaultDailyRate());
        s.setSigningPlace(e.getSigningPlace());
        return s;
    }

    private GeneralSettingEntity domainToEntity(GeneralSetting s) {
        GeneralSettingEntity e = new GeneralSettingEntity();
        e.setSettingId(s.getSettingId());
        e.setClinicName(s.getClinicName());
        e.setClinicAddress1(s.getClinicAddress1());
        e.setClinicAddress2(s.getClinicAddress2());
        e.setPhoneNumber1(s.getPhoneNumber1());
        e.setPhoneNumber2(s.getPhoneNumber2());
        e.setRepresentativeName(s.getRepresentativeName());
        e.setCheckoutHour(s.getCheckoutHour());
        e.setOvertimeFeePerHour(s.getOvertimeFeePerHour());
        e.setDefaultDailyRate(s.getDefaultDailyRate());
        e.setSigningPlace(s.getSigningPlace());
        return e;
    }
}
