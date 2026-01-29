package com.petcare.model.entity;

import java.util.Date;

/**
 * Treatment Course Entity - maps to treatment_courses table
 */
public class TreatmentCourseEntity {
    private int treatmentCourseId;
    private int customerId;
    private int petId;
    private Date startDate;
    private Date endDate;
    private String status;  // "1" = Đang điều trị, "0" = Kết thúc

    public TreatmentCourseEntity() {
    }

    public int getTreatmentCourseId() { return treatmentCourseId; }
    public void setTreatmentCourseId(int treatmentCourseId) { this.treatmentCourseId = treatmentCourseId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
