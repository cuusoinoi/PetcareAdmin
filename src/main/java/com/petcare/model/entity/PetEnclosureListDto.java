package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for pet enclosure list view (with joined customer, pet names)
 */
public class PetEnclosureListDto {
    private int petEnclosureId;
    private int petEnclosureNumber;
    private String customerName;
    private String petName;
    private Date checkInDate;
    private Date checkOutDate;
    private int dailyRate;
    private int deposit;
    private String petEnclosureStatus;

    public int getPetEnclosureId() {
        return petEnclosureId;
    }

    public void setPetEnclosureId(int petEnclosureId) {
        this.petEnclosureId = petEnclosureId;
    }

    public int getPetEnclosureNumber() {
        return petEnclosureNumber;
    }

    public void setPetEnclosureNumber(int petEnclosureNumber) {
        this.petEnclosureNumber = petEnclosureNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
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

    public String getPetEnclosureStatus() {
        return petEnclosureStatus;
    }

    public void setPetEnclosureStatus(String petEnclosureStatus) {
        this.petEnclosureStatus = petEnclosureStatus;
    }
}
