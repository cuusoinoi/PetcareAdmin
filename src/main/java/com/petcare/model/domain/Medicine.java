package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

/**
 * Medicine Domain Model - validation in setters
 * Route enum: PO, IM, IV, SC
 */
public class Medicine {
    private int medicineId;
    private String medicineName;
    private Route medicineRoute;

    public enum Route {
        PO("PO", "Uống"),
        IM("IM", "Tiêm bắp"),
        IV("IV", "Tiêm tĩnh mạch"),
        SC("SC", "Tiêm dưới da");

        private final String code;
        private final String label;

        Route(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

        public static Route fromCode(String code) {
            if (code == null) return null;
            for (Route r : values()) {
                if (r.code.equals(code)) {
                    return r;
                }
            }
            return null;
        }
    }

    public Medicine() {
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

    /**
     * Set medicine name with validation.
     */
    public final void setMedicineName(String medicineName) throws PetcareException {
        if (medicineName == null || medicineName.trim().isEmpty()) {
            throw new PetcareException("Tên thuốc không được để trống");
        }
        if (medicineName.trim().length() > 255) {
            throw new PetcareException("Tên thuốc không được vượt quá 255 ký tự");
        }
        this.medicineName = medicineName.trim();
    }

    public Route getMedicineRoute() {
        return medicineRoute;
    }

    /**
     * Set route (PO, IM, IV, SC).
     */
    public final void setMedicineRoute(Route medicineRoute) throws PetcareException {
        if (medicineRoute == null) {
            throw new PetcareException("Đường dùng không được để trống");
        }
        this.medicineRoute = medicineRoute;
    }
}
