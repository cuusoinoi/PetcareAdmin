package com.petcare.model;

/**
 * Doctor model
 */
public class Doctor {
    private int doctorId;
    private String doctorName;
    private String doctorPhoneNumber;
    private String doctorIdentityCard;
    private String doctorAddress;
    private String doctorNote;
    
    public Doctor() {
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
}
