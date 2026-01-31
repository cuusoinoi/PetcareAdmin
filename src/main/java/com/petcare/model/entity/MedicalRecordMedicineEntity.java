package com.petcare.model.entity;

/**
 * Entity - maps to medical_record_medicines (thuốc kê trong lượt khám).
 */
public class MedicalRecordMedicineEntity {
    private int recordMedicineId;
    private int medicalRecordId;
    private int medicineId;
    private int quantity;
    private int unitPrice;
    private int totalPrice;

    public MedicalRecordMedicineEntity() {
    }

    public int getRecordMedicineId() {
        return recordMedicineId;
    }

    public void setRecordMedicineId(int recordMedicineId) {
        this.recordMedicineId = recordMedicineId;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
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
