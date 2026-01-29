package com.petcare.model.domain;

import java.util.Date;

/**
 * Invoice Domain Model (header only; details passed separately when creating)
 */
public class Invoice {
    private int invoiceId;
    private int customerId;
    private int petId;
    private Integer petEnclosureId;
    private Date invoiceDate;
    private int discount;
    private int subtotal;
    private int deposit;
    private int totalAmount;

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public Integer getPetEnclosureId() { return petEnclosureId; }
    public void setPetEnclosureId(Integer petEnclosureId) { this.petEnclosureId = petEnclosureId; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }
    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public int getDeposit() { return deposit; }
    public void setDeposit(int deposit) { this.deposit = deposit; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
}
