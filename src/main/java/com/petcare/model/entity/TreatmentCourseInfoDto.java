package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for treatment course info in sessions dialog (customer, pet, start date)
 */
public class TreatmentCourseInfoDto {
    private int treatmentCourseId;
    private Date startDate;
    private String customerName;
    private String petName;

    public int getTreatmentCourseId() { return treatmentCourseId; }
    public void setTreatmentCourseId(int treatmentCourseId) { this.treatmentCourseId = treatmentCourseId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
}
