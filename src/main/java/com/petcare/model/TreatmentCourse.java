package com.petcare.model;

import java.util.Date;

/**
 * Treatment Course model
 */
public class TreatmentCourse {
    private int treatmentCourseId;
    private int customerId;
    private int petId;
    private Date startDate;
    private Date endDate;
    private Status status;
    
    public enum Status {
        ACTIVE("1", "Đang điều trị"),
        COMPLETED("0", "Kết thúc");
        
        private final String code;
        private final String label;
        
        Status(String code, String label) {
            this.code = code;
            this.label = label;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getLabel() {
            return label;
        }
        
        public static Status fromCode(String code) {
            for (Status s : values()) {
                if (s.code.equals(code)) {
                    return s;
                }
            }
            return null;
        }
    }
    
    public TreatmentCourse() {
    }
    
    // Getters and Setters
    public int getTreatmentCourseId() {
        return treatmentCourseId;
    }
    
    public void setTreatmentCourseId(int treatmentCourseId) {
        this.treatmentCourseId = treatmentCourseId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public int getPetId() {
        return petId;
    }
    
    public void setPetId(int petId) {
        this.petId = petId;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
}
