package com.petcare.model.entity;

/**
 * DTO for revenue by service type (dashboard)
 */
public class ServiceRevenueDto {
    private String serviceName;
    private long revenue;

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public long getRevenue() { return revenue; }
    public void setRevenue(long revenue) { this.revenue = revenue; }
}
