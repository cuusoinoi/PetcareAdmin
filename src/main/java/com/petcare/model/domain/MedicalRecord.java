package com.petcare.model.domain;

import java.util.Date;

/**
 * Medical Record Domain Model - validation and RecordType enum
 */
public class MedicalRecord {
    private int medicalRecordId;
    private int customerId;
    private int petId;
    private int doctorId;
    private RecordType medicalRecordType;
    private Date medicalRecordVisitDate;
    private String medicalRecordSummary;
    private String medicalRecordDetails;

    public enum RecordType {
        EXAM("Khám"),
        TREATMENT("Điều trị"),
        VACCINE("Vaccine");

        private final String label;

        RecordType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static RecordType fromLabel(String label) {
            if (label == null) return null;
            for (RecordType type : values()) {
                if (type.label.equals(label)) return type;
            }
            return null;
        }
    }

    public int getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(int medicalRecordId) { this.medicalRecordId = medicalRecordId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public RecordType getMedicalRecordType() { return medicalRecordType; }
    public void setMedicalRecordType(RecordType medicalRecordType) { this.medicalRecordType = medicalRecordType; }
    public Date getMedicalRecordVisitDate() { return medicalRecordVisitDate; }
    public void setMedicalRecordVisitDate(Date medicalRecordVisitDate) { this.medicalRecordVisitDate = medicalRecordVisitDate; }
    public String getMedicalRecordSummary() { return medicalRecordSummary; }
    public void setMedicalRecordSummary(String medicalRecordSummary) { this.medicalRecordSummary = medicalRecordSummary; }
    public String getMedicalRecordDetails() { return medicalRecordDetails; }
    public void setMedicalRecordDetails(String medicalRecordDetails) { this.medicalRecordDetails = medicalRecordDetails; }
}
