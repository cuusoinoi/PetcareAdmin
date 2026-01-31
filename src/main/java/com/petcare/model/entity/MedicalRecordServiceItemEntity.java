package com.petcare.model.entity;

/**
 * Entity - maps to medical_record_services (dịch vụ đã thêm trong lượt khám).
 */
public class MedicalRecordServiceItemEntity {
    private int recordServiceId;
    private int medicalRecordId;
    private int serviceTypeId;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public MedicalRecordServiceItemEntity() {
    }

    public int getRecordServiceId() {
        return recordServiceId;
    }

    public void setRecordServiceId(int recordServiceId) {
        this.recordServiceId = recordServiceId;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
