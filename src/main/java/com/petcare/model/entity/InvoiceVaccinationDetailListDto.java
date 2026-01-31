package com.petcare.model.entity;

/**
 * DTO for invoice vaccination line in details/print (vaccine_name, quantity, unit_price, total_price).
 */
public class InvoiceVaccinationDetailListDto {
    private String vaccineName;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
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
