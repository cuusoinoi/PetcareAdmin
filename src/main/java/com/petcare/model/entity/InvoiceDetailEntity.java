package com.petcare.model.entity;

/**
 * Invoice Detail Entity - maps to invoice_details table
 */
public class InvoiceDetailEntity {
    private int detailId;
    private int invoiceId;
    private int serviceTypeId;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public InvoiceDetailEntity() {
    }

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }
    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public int getServiceTypeId() { return serviceTypeId; }
    public void setServiceTypeId(int serviceTypeId) { this.serviceTypeId = serviceTypeId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
}
