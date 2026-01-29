package com.petcare.model.entity;

import java.util.Date;

/**
 * DTO for invoice details dialog (invoice header + customer, pet, enclosure info)
 */
public class InvoiceInfoDto {
    private int invoiceId;
    private Date invoiceDate;
    private String customerName;
    private String customerPhoneNumber;
    private String petName;
    private Integer petEnclosureNumber;
    private int subtotal;
    private int discount;
    private int deposit;
    private int totalAmount;

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhoneNumber() { return customerPhoneNumber; }
    public void setCustomerPhoneNumber(String customerPhoneNumber) { this.customerPhoneNumber = customerPhoneNumber; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public Integer getPetEnclosureNumber() { return petEnclosureNumber; }
    public void setPetEnclosureNumber(Integer petEnclosureNumber) { this.petEnclosureNumber = petEnclosureNumber; }
    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }
    public int getDeposit() { return deposit; }
    public void setDeposit(int deposit) { this.deposit = deposit; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
}
