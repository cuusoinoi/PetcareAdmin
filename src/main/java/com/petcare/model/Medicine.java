package com.petcare.model;

/**
 * Medicine model
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
    
    // Getters and Setters
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
    
    public Route getMedicineRoute() {
        return medicineRoute;
    }
    
    public void setMedicineRoute(Route medicineRoute) {
        this.medicineRoute = medicineRoute;
    }
}
