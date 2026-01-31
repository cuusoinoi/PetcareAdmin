package com.petcare.model.entity;

/**
 * DTO for invoice medicine line in details/print (medicine_name, quantity, unit_price, total_price).
 */
public class InvoiceMedicineDetailListDto {
    private String medicineName;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
