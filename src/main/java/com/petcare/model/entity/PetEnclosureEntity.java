package com.petcare.model.entity;

import java.util.Date;

/**
 * Pet Enclosure Entity - maps to pet_enclosures table
 */
public class PetEnclosureEntity {
    private int petEnclosureId;
    private int customerId;
    private int petId;
    private int petEnclosureNumber;
    private Date checkInDate;
    private Date checkOutDate;
    private int dailyRate;
    private int deposit;
    private int emergencyLimit;
    private String petEnclosureNote;
    private String petEnclosureStatus;  // "Check In", "Check Out"

    public PetEnclosureEntity() {
    }

    public int getPetEnclosureId() {
        return petEnclosureId;
    }

    public void setPetEnclosureId(int petEnclosureId) {
        this.petEnclosureId = petEnclosureId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getPetEnclosureNumber() {
        return petEnclosureNumber;
    }

    public void setPetEnclosureNumber(int petEnclosureNumber) {
        this.petEnclosureNumber = petEnclosureNumber;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(int dailyRate) {
        this.dailyRate = dailyRate;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getEmergencyLimit() {
        return emergencyLimit;
    }

    public void setEmergencyLimit(int emergencyLimit) {
        this.emergencyLimit = emergencyLimit;
    }

    public String getPetEnclosureNote() {
        return petEnclosureNote;
    }

    public void setPetEnclosureNote(String petEnclosureNote) {
        this.petEnclosureNote = petEnclosureNote;
    }

    public String getPetEnclosureStatus() {
        return petEnclosureStatus;
    }

    public void setPetEnclosureStatus(String petEnclosureStatus) {
        this.petEnclosureStatus = petEnclosureStatus;
    }
}
