package com.petcare.model.entity;

/**
 * DTO for medicine line in medical record (display: medicine name, quantity, unit_price, total_price).
 */
public class MedicalRecordMedicineListDto {
    private int recordMedicineId;
    private int medicineId;
    private String medicineName;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public int getRecordMedicineId() {
        return recordMedicineId;
    }

    public void setRecordMedicineId(int recordMedicineId) {
        this.recordMedicineId = recordMedicineId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

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
