package com.petcare.model.domain;

/**
 * One vaccination line when creating invoice (vaccine_id, quantity, unit_price, total_price).
 */
public class InvoiceVaccinationItem {
    private int vaccineId;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public int getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
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
