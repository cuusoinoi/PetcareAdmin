package com.petcare.model.entity;

import java.util.Date;

/**
 * Medical Record Entity - maps to medical_records table
 */
public class MedicalRecordEntity {
    private int medicalRecordId;
    private int customerId;
    private int petId;
    private int doctorId;
    private String medicalRecordType;  // "Khám", "Điều trị", "Vaccine" in DB
    private Date medicalRecordVisitDate;
    private String medicalRecordSummary;
    private String medicalRecordDetails;

    public MedicalRecordEntity() {
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
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

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getMedicalRecordType() {
        return medicalRecordType;
    }

    public void setMedicalRecordType(String medicalRecordType) {
        this.medicalRecordType = medicalRecordType;
    }

    public Date getMedicalRecordVisitDate() {
        return medicalRecordVisitDate;
    }

    public void setMedicalRecordVisitDate(Date medicalRecordVisitDate) {
        this.medicalRecordVisitDate = medicalRecordVisitDate;
    }

    public String getMedicalRecordSummary() {
        return medicalRecordSummary;
    }

    public void setMedicalRecordSummary(String medicalRecordSummary) {
        this.medicalRecordSummary = medicalRecordSummary;
    }

    public String getMedicalRecordDetails() {
        return medicalRecordDetails;
    }

    public void setMedicalRecordDetails(String medicalRecordDetails) {
        this.medicalRecordDetails = medicalRecordDetails;
    }
}
