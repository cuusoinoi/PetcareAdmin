package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for medical record list view (with joined customer, pet, doctor names)
 */
public class MedicalRecordListDto {
    private int medicalRecordId;
    private Date visitDate;
    private String medicalRecordType;
    private String customerName;
    private String petName;
    private String doctorName;
    private String summary;

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getMedicalRecordType() {
        return medicalRecordType;
    }

    public void setMedicalRecordType(String medicalRecordType) {
        this.medicalRecordType = medicalRecordType;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
