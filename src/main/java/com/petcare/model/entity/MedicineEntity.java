package com.petcare.model.entity;

/**
 * Medicine Entity - DTO mapping to medicines table
 * medicine_id, medicine_name, medicine_route (PO, IM, IV, SC)
 */
public class MedicineEntity {
    private int medicineId;
    private String medicineName;
    private String medicineRoute;

    public MedicineEntity() {
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

    public String getMedicineRoute() {
        return medicineRoute;
    }

    public void setMedicineRoute(String medicineRoute) {
        this.medicineRoute = medicineRoute;
    }
}
