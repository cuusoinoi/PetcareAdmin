package com.petcare.model;

/**
 * Vaccine model (master data)
 */
public class Vaccine {
    private int vaccineId;
    private String vaccineName;
    private String description;
    
    public Vaccine() {
    }
    
    // Getters and Setters
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
