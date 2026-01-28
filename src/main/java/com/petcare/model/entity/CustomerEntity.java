package com.petcare.model.entity;

import java.util.Date;

/**
 * Customer Entity - Data Transfer Object (DTO)
 * Simple POJO representing customer data from database
 * No business logic or validation - just data container
 */
public class CustomerEntity {
    private int customerId;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerIdentityCard;
    private String customerAddress;
    private String customerNote;
    private Date createdAt;
    
    /**
     * Default constructor
     */
    public CustomerEntity() {
    }
    
    /**
     * Constructor with all fields
     */
    public CustomerEntity(int customerId, String customerName, 
                         String customerPhoneNumber, String customerEmail,
                         String customerIdentityCard, String customerAddress, 
                         String customerNote) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerEmail = customerEmail;
        this.customerIdentityCard = customerIdentityCard;
        this.customerAddress = customerAddress;
        this.customerNote = customerNote;
    }
    
    // Getters and Setters
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }
    
    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerIdentityCard() {
        return customerIdentityCard;
    }
    
    public void setCustomerIdentityCard(String customerIdentityCard) {
        this.customerIdentityCard = customerIdentityCard;
    }
    
    public String getCustomerAddress() {
        return customerAddress;
    }
    
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
    
    public String getCustomerNote() {
        return customerNote;
    }
    
    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "CustomerEntity{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNumber='" + customerPhoneNumber + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerIdentityCard='" + customerIdentityCard + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerNote='" + customerNote + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
