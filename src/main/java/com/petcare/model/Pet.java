package com.petcare.model;

import java.util.Date;

/**
 * Pet model
 */
public class Pet {
    private int petId;
    private int customerId;
    private String petName;
    private String petSpecies;
    private Gender petGender;
    private Date petDob;
    private Double petWeight;
    private Boolean petSterilization;
    private String petCharacteristic;
    private String petDrugAllergy;
    
    public enum Gender {
        MALE("0", "Đực"),
        FEMALE("1", "Cái");
        
        private final String code;
        private final String label;
        
        Gender(String code, String label) {
            this.code = code;
            this.label = label;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getLabel() {
            return label;
        }
        
        public static Gender fromCode(String code) {
            for (Gender g : values()) {
                if (g.code.equals(code)) {
                    return g;
                }
            }
            return null;
        }
    }
    
    public Pet() {
    }
    
    // Getters and Setters
    public int getPetId() {
        return petId;
    }
    
    public void setPetId(int petId) {
        this.petId = petId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getPetName() {
        return petName;
    }
    
    public void setPetName(String petName) {
        this.petName = petName;
    }
    
    public String getPetSpecies() {
        return petSpecies;
    }
    
    public void setPetSpecies(String petSpecies) {
        this.petSpecies = petSpecies;
    }
    
    public Gender getPetGender() {
        return petGender;
    }
    
    public void setPetGender(Gender petGender) {
        this.petGender = petGender;
    }
    
    public Date getPetDob() {
        return petDob;
    }
    
    public void setPetDob(Date petDob) {
        this.petDob = petDob;
    }
    
    public Double getPetWeight() {
        return petWeight;
    }
    
    public void setPetWeight(Double petWeight) {
        this.petWeight = petWeight;
    }
    
    public Boolean getPetSterilization() {
        return petSterilization;
    }
    
    public void setPetSterilization(Boolean petSterilization) {
        this.petSterilization = petSterilization;
    }
    
    public String getPetCharacteristic() {
        return petCharacteristic;
    }
    
    public void setPetCharacteristic(String petCharacteristic) {
        this.petCharacteristic = petCharacteristic;
    }
    
    public String getPetDrugAllergy() {
        return petDrugAllergy;
    }
    
    public void setPetDrugAllergy(String petDrugAllergy) {
        this.petDrugAllergy = petDrugAllergy;
    }
}
