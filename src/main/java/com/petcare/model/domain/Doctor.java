package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

import java.util.Objects;

/**
 * Doctor Domain Model
 * Contains business logic and validation rules
 */
public class Doctor {
    private int doctorId;
    private String doctorName;
    private String doctorPhoneNumber;
    private String doctorIdentityCard;
    private String doctorAddress;
    private String doctorNote;

    /**
     * Default constructor
     */
    public Doctor() {
    }

    /**
     * Constructor with validation
     */
    public Doctor(String doctorName, String doctorPhoneNumber,
                  String doctorIdentityCard, String doctorAddress, String doctorNote)
            throws PetcareException {
        setDoctorName(doctorName);
        setDoctorPhoneNumber(doctorPhoneNumber);
        setDoctorIdentityCard(doctorIdentityCard);
        setDoctorAddress(doctorAddress);
        setDoctorNote(doctorNote);
    }

    // Getters and Setters with validation

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    /**
     * Set doctor name with validation
     */
    public final void setDoctorName(String doctorName) throws PetcareException {
        if (doctorName == null || doctorName.trim().isEmpty()) {
            throw new PetcareException("Tên bác sĩ không được để trống");
        }
        if (doctorName.trim().length() > 255) {
            throw new PetcareException("Tên bác sĩ không được vượt quá 255 ký tự");
        }
        this.doctorName = doctorName.trim();
    }

    public String getDoctorPhoneNumber() {
        return doctorPhoneNumber;
    }

    /**
     * Set doctor phone number with validation
     */
    public final void setDoctorPhoneNumber(String doctorPhoneNumber) throws PetcareException {
        if (doctorPhoneNumber == null || doctorPhoneNumber.trim().isEmpty()) {
            throw new PetcareException("Số điện thoại không được để trống");
        }
        // Remove spaces and dashes for validation
        String cleaned = doctorPhoneNumber.trim().replaceAll("[\\s-]", "");
        if (cleaned.length() < 10 || cleaned.length() > 11) {
            throw new PetcareException("Số điện thoại phải có từ 10 đến 11 chữ số");
        }
        if (!cleaned.matches("^[0-9]+$")) {
            throw new PetcareException("Số điện thoại chỉ được chứa chữ số");
        }
        this.doctorPhoneNumber = cleaned;
    }

    public String getDoctorIdentityCard() {
        return doctorIdentityCard;
    }

    /**
     * Set doctor identity card with validation
     */
    public final void setDoctorIdentityCard(String doctorIdentityCard) throws PetcareException {
        if (doctorIdentityCard != null && !doctorIdentityCard.trim().isEmpty()) {
            String cleaned = doctorIdentityCard.trim();
            if (cleaned.length() > 12) {
                throw new PetcareException("Số CMND/CCCD không được vượt quá 12 ký tự");
            }
            if (!cleaned.matches("^[0-9]+$")) {
                throw new PetcareException("Số CMND/CCCD chỉ được chứa chữ số");
            }
            this.doctorIdentityCard = cleaned;
        } else {
            this.doctorIdentityCard = null;
        }
    }

    public String getDoctorAddress() {
        return doctorAddress;
    }

    /**
     * Set doctor address with validation
     */
    public final void setDoctorAddress(String doctorAddress) throws PetcareException {
        if (doctorAddress == null || doctorAddress.trim().isEmpty()) {
            throw new PetcareException("Địa chỉ không được để trống");
        }
        if (doctorAddress.trim().length() > 255) {
            throw new PetcareException("Địa chỉ không được vượt quá 255 ký tự");
        }
        this.doctorAddress = doctorAddress.trim();
    }

    public String getDoctorNote() {
        return doctorNote;
    }

    /**
     * Set doctor note (optional)
     */
    public void setDoctorNote(String doctorNote) {
        this.doctorNote = doctorNote != null ? doctorNote.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return doctorId == doctor.doctorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", doctorPhoneNumber='" + doctorPhoneNumber + '\'' +
                '}';
    }
}
