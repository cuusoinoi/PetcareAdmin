package com.petcare.model;

/**
 * Customer model
 */
public class Customer {
    private int customerId;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerIdentityCard;
    private String customerAddress;
    private String customerNote;

    public Customer() {
    }

    public Customer(int customerId, String customerName, String customerPhoneNumber,
                    String customerEmail, String customerIdentityCard,
                    String customerAddress, String customerNote) {
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
}
