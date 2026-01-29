package com.petcare.model.entity;

/**
 * Vaccine Type Entity - DTO mapping to vaccines table (loại vaccine)
 * vaccine_id, vaccine_name, description
 */
public class VaccineTypeEntity {
    private int vaccineId;
    private String vaccineName;
    private String description;

    public VaccineTypeEntity() {
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

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
