package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for treatment session list (in sessions dialog)
 */
public class TreatmentSessionListDto {
    private int treatmentSessionId;
    private Date treatmentSessionDatetime;
    private String doctorName;
    private double temperature;
    private double weight;
    private Integer pulseRate;
    private Integer respiratoryRate;

    public int getTreatmentSessionId() {
        return treatmentSessionId;
    }

    public void setTreatmentSessionId(int treatmentSessionId) {
        this.treatmentSessionId = treatmentSessionId;
    }

    public Date getTreatmentSessionDatetime() {
        return treatmentSessionDatetime;
    }

    public void setTreatmentSessionDatetime(Date treatmentSessionDatetime) {
        this.treatmentSessionDatetime = treatmentSessionDatetime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Integer getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(Integer pulseRate) {
        this.pulseRate = pulseRate;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }
}
