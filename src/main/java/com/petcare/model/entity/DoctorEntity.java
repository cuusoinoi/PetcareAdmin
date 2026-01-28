package com.petcare.model.entity;

import java.util.Date;

/**
 * Doctor Entity - Data Transfer Object (DTO)
 * Simple POJO representing doctor data from database
 * No business logic or validation - just data container
 */
public class DoctorEntity {
    private int doctorId;
    private String doctorName;
    private String doctorPhoneNumber;
    private String doctorIdentityCard;
    private String doctorAddress;
    private String doctorNote;
    private Date createdAt;
    
    /**
     * Default constructor
     */
    public DoctorEntity() {
    }
    
    /**
     * Constructor with all fields
     */
    public DoctorEntity(int doctorId, String doctorName, String doctorPhoneNumber,
                       String doctorIdentityCard, String doctorAddress, String doctorNote) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorPhoneNumber = doctorPhoneNumber;
        this.doctorIdentityCard = doctorIdentityCard;
        this.doctorAddress = doctorAddress;
        this.doctorNote = doctorNote;
    }
    
    // Getters and Setters
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getDoctorPhoneNumber() {
        return doctorPhoneNumber;
    }
    
    public void setDoctorPhoneNumber(String doctorPhoneNumber) {
        this.doctorPhoneNumber = doctorPhoneNumber;
    }
    
    public String getDoctorIdentityCard() {
        return doctorIdentityCard;
    }
    
    public void setDoctorIdentityCard(String doctorIdentityCard) {
        this.doctorIdentityCard = doctorIdentityCard;
    }
    
    public String getDoctorAddress() {
        return doctorAddress;
    }
    
    public void setDoctorAddress(String doctorAddress) {
        this.doctorAddress = doctorAddress;
    }
    
    public String getDoctorNote() {
        return doctorNote;
    }
    
    public void setDoctorNote(String doctorNote) {
        this.doctorNote = doctorNote;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "DoctorEntity{" +
                "doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", doctorPhoneNumber='" + doctorPhoneNumber + '\'' +
                '}';
    }
}
