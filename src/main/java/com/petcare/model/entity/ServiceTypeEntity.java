package com.petcare.model.entity;

/**
 * Service Type Entity - DTO mapping to service_types table
 * service_type_id, service_name, description, price
 */
public class ServiceTypeEntity {
    private int serviceTypeId;
    private String serviceName;
    private String description;
    private double price;

    public ServiceTypeEntity() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
