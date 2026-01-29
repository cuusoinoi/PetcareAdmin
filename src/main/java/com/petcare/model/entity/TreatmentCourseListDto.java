package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for treatment course list view (with joined customer, pet names)
 */
public class TreatmentCourseListDto {
    private int treatmentCourseId;
    private Date startDate;
    private Date endDate;
    private String customerName;
    private String petName;
    private String status;

    public int getTreatmentCourseId() { return treatmentCourseId; }
    public void setTreatmentCourseId(int treatmentCourseId) { this.treatmentCourseId = treatmentCourseId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
