package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

/**
 * Service Type Domain Model - validation in setters
 */
public class ServiceType {
    private int serviceTypeId;
    private String serviceName;
    private String description;
    private double price;

    public ServiceType() {
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

    /**
     * Set service name with validation.
     */
    public final void setServiceName(String serviceName) throws PetcareException {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new PetcareException("Tên dịch vụ không được để trống");
        }
        if (serviceName.trim().length() > 255) {
            throw new PetcareException("Tên dịch vụ không được vượt quá 255 ký tự");
        }
        this.serviceName = serviceName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Set price with validation (>= 0).
     */
    public final void setPrice(double price) throws PetcareException {
        if (price < 0) {
            throw new PetcareException("Giá dịch vụ không được âm");
        }
        this.price = price;
    }
}
