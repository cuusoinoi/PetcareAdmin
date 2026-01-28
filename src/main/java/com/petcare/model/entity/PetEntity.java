package com.petcare.model.entity;

import java.util.Date;

/**
 * Pet Entity - Data Transfer Object (DTO)
 * Simple POJO representing pet data from database
 * No business logic or validation - just data container
 */
public class PetEntity {
    private int petId;
    private int customerId;
    private String petName;
    private String petSpecies;
    private String petGender; // ENUM('0', '1') - 0: đực, 1: cái
    private Date petDob; // Date of birth
    private Double petWeight; // DECIMAL(10,2)
    private String petSterilization; // ENUM('0', '1') - 0: chưa triệt sản, 1: đã triệt sản
    private String petCharacteristic;
    private String petDrugAllergy;
    private Date createdAt;
    
    /**
     * Default constructor
     */
    public PetEntity() {
    }
    
    /**
     * Constructor with all fields
     */
    public PetEntity(int petId, int customerId, String petName, String petSpecies,
                    String petGender, Date petDob, Double petWeight,
                    String petSterilization, String petCharacteristic, String petDrugAllergy) {
        this.petId = petId;
        this.customerId = customerId;
        this.petName = petName;
        this.petSpecies = petSpecies;
        this.petGender = petGender;
        this.petDob = petDob;
        this.petWeight = petWeight;
        this.petSterilization = petSterilization;
        this.petCharacteristic = petCharacteristic;
        this.petDrugAllergy = petDrugAllergy;
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
    
    public String getPetGender() {
        return petGender;
    }
    
    public void setPetGender(String petGender) {
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
    
    public String getPetSterilization() {
        return petSterilization;
    }
    
    public void setPetSterilization(String petSterilization) {
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "PetEntity{" +
                "petId=" + petId +
                ", customerId=" + customerId +
                ", petName='" + petName + '\'' +
                ", petSpecies='" + petSpecies + '\'' +
                ", petGender='" + petGender + '\'' +
                '}';
    }
}
