package com.petcare.model.domain;

import java.util.Date;

/**
 * Pet Vaccination Domain Model
 */
public class PetVaccination {
    private int petVaccinationId;
    private int vaccineId;
    private int customerId;
    private int petId;
    private int doctorId;
    private Date vaccinationDate;
    private Date nextVaccinationDate;
    private String notes;

    public int getPetVaccinationId() { return petVaccinationId; }
    public void setPetVaccinationId(int petVaccinationId) { this.petVaccinationId = petVaccinationId; }
    public int getVaccineId() { return vaccineId; }
    public void setVaccineId(int vaccineId) { this.vaccineId = vaccineId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public Date getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(Date vaccinationDate) { this.vaccinationDate = vaccinationDate; }
    public Date getNextVaccinationDate() { return nextVaccinationDate; }
    public void setNextVaccinationDate(Date nextVaccinationDate) { this.nextVaccinationDate = nextVaccinationDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
