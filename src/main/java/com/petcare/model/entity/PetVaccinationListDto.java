package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for pet vaccination list view (with joined vaccine, customer, pet, doctor names)
 */
public class PetVaccinationListDto {
    private int petVaccinationId;
    private Date vaccinationDate;
    private Date nextVaccinationDate;
    private String vaccineName;
    private String customerName;
    private String petName;
    private String doctorName;

    public int getPetVaccinationId() {
        return petVaccinationId;
    }

    public void setPetVaccinationId(int petVaccinationId) {
        this.petVaccinationId = petVaccinationId;
    }

    public Date getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(Date vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public Date getNextVaccinationDate() {
        return nextVaccinationDate;
    }

    public void setNextVaccinationDate(Date nextVaccinationDate) {
        this.nextVaccinationDate = nextVaccinationDate;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
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

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
