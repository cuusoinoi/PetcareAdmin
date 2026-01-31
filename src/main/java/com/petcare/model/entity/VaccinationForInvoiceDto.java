package com.petcare.model.entity;

/**
 * DTO for vaccination line when creating invoice from visit (vaccine_id, vaccine_name, unit_price from vaccines).
 */
public class VaccinationForInvoiceDto {
    private int vaccineId;
    private String vaccineName;
    private int unitPrice;

    public int getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
}
