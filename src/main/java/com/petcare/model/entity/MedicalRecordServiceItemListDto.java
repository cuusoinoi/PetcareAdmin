package com.petcare.model.entity;

/**
 * DTO for service line in medical record (display: service name, quantity, unit_price, total_price).
 */
public class MedicalRecordServiceItemListDto {
    private int recordServiceId;
    private int serviceTypeId;
    private String serviceName;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public int getRecordServiceId() {
        return recordServiceId;
    }

    public void setRecordServiceId(int recordServiceId) {
        this.recordServiceId = recordServiceId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
