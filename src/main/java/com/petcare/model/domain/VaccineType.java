package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

/**
 * Vaccine Type Domain Model (loại vaccine) - validation in setters
 */
public class VaccineType {
    private int vaccineId;
    private String vaccineName;
    private String description;

    public VaccineType() {
    }

    public int getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    /**
     * Set vaccine name with validation.
     */
    public final void setVaccineName(String vaccineName) throws PetcareException {
        if (vaccineName == null || vaccineName.trim().isEmpty()) {
            throw new PetcareException("Tên vaccine không được để trống");
        }
        if (vaccineName.trim().length() > 255) {
            throw new PetcareException("Tên vaccine không được vượt quá 255 ký tự");
        }
        this.vaccineName = vaccineName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }
}
