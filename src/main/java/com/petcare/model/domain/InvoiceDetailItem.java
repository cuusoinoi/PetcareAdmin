package com.petcare.model.domain;

/**
 * One line item when creating invoice (service_type_id, quantity, unit_price, total_price)
 */
public class InvoiceDetailItem {
    private int serviceTypeId;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public int getServiceTypeId() { return serviceTypeId; }
    public void setServiceTypeId(int serviceTypeId) { this.serviceTypeId = serviceTypeId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
}
