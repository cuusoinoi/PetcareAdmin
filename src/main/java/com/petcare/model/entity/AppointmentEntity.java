package com.petcare.model.entity;

import java.util.Date;

/**
 * Appointment Entity - DTO mapping to appointments table
 */
public class AppointmentEntity {
    private int appointmentId;
    private int customerId;
    private int petId;
    private Integer doctorId;
    private Integer serviceTypeId;
    private Date appointmentDate;
    private String appointmentType;   // "Khám", "Spa", "Tiêm chủng" in DB
    private String status;            // "pending", "confirmed", "completed", "cancelled" in DB
    private String notes;
    private Date createdAt;

    public AppointmentEntity() {
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
    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
