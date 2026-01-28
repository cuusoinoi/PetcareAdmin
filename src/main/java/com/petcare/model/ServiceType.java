package com.petcare.model;

/**
 * Service Type model
 */
public class ServiceType {
    private int serviceTypeId;
    private String serviceName;
    private String description;
    private double price;
    
    public ServiceType() {
    }
    
    // Getters and Setters
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
