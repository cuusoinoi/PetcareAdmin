package com.petcare.model.domain;

import java.util.Date;

/**
 * Appointment Domain Model - same enums as legacy model
 */
public class Appointment {
    private int appointmentId;
    private int customerId;
    private int petId;
    private Integer doctorId;
    private Integer serviceTypeId;
    private Date appointmentDate;
    private AppointmentType appointmentType;
    private Status status;
    private String notes;
    private Date createdAt;

    public enum AppointmentType {
        EXAM("Khám"),
        SPA("Spa"),
        VACCINE("Tiêm chủng");

        private final String label;

        AppointmentType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static AppointmentType fromLabel(String label) {
            if (label == null) return null;
            for (AppointmentType type : values()) {
                if (type.label.equals(label)) return type;
            }
            return null;
        }
    }

    public enum Status {
        PENDING("pending", "Chờ xác nhận"),
        CONFIRMED("confirmed", "Đã xác nhận"),
        COMPLETED("completed", "Hoàn thành"),
        CANCELLED("cancelled", "Đã hủy");

        private final String code;
        private final String label;

        Status(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getCode() { return code; }
        public String getLabel() { return label; }

        public static Status fromCode(String code) {
            if (code == null) return null;
            for (Status s : values()) {
                if (s.code.equals(code)) return s;
            }
            return null;
        }
    }

    public Appointment() {
    }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public Integer getServiceTypeId() { return serviceTypeId; }
    public void setServiceTypeId(Integer serviceTypeId) { this.serviceTypeId = serviceTypeId; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public AppointmentType getAppointmentType() { return appointmentType; }
    public void setAppointmentType(AppointmentType appointmentType) { this.appointmentType = appointmentType; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
