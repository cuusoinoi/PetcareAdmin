package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

import java.util.Date;
import java.util.Objects;

/**
 * Pet Domain Model
 * Contains business logic and validation rules
 */
public class Pet {
    private int petId;
    private int customerId;
    private String petName;
    private String petSpecies;
    private String petGender; // "0" = Đực, "1" = Cái
    private Date petDob; // Date of birth
    private Double petWeight;
    private String petSterilization; // "0" = chưa triệt sản, "1" = đã triệt sản
    private String petCharacteristic;
    private String petDrugAllergy;

    /**
     * Default constructor
     */
    public Pet() {
    }

    /**
     * Constructor with validation
     */
    public Pet(int customerId, String petName, String petSpecies,
               String petGender, Date petDob, Double petWeight,
               String petSterilization, String petCharacteristic, String petDrugAllergy)
            throws PetcareException {
        setCustomerId(customerId);
        setPetName(petName);
        setPetSpecies(petSpecies);
        setPetGender(petGender);
        setPetDob(petDob);
        setPetWeight(petWeight);
        setPetSterilization(petSterilization);
        setPetCharacteristic(petCharacteristic);
        setPetDrugAllergy(petDrugAllergy);
    }

    // Getters and Setters with validation

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getCustomerId() {
        return customerId;
    }

    /**
     * Set customer ID with validation
     */
    public final void setCustomerId(int customerId) throws PetcareException {
        if (customerId <= 0) {
            throw new PetcareException("Khách hàng không hợp lệ");
        }
        this.customerId = customerId;
    }

    public String getPetName() {
        return petName;
    }

    /**
     * Set pet name with validation
     */
    public final void setPetName(String petName) throws PetcareException {
        if (petName == null || petName.trim().isEmpty()) {
            throw new PetcareException("Tên thú cưng không được để trống");
        }
        if (petName.trim().length() > 255) {
            throw new PetcareException("Tên thú cưng không được vượt quá 255 ký tự");
        }
        this.petName = petName.trim();
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    /**
     * Set pet species with validation
     */
    public final void setPetSpecies(String petSpecies) throws PetcareException {
        if (petSpecies != null && petSpecies.trim().length() > 255) {
            throw new PetcareException("Loài thú cưng không được vượt quá 255 ký tự");
        }
        this.petSpecies = petSpecies != null ? petSpecies.trim() : null;
    }

    public String getPetGender() {
        return petGender;
    }

    /**
     * Set pet gender with validation
     */
    public final void setPetGender(String petGender) throws PetcareException {
        if (petGender != null && !petGender.trim().isEmpty()) {
            String gender = petGender.trim();
            // Accept: "0" (Đực), "1" (Cái), or Vietnamese labels
            if (!gender.equals("0") && !gender.equals("1") &&
                    !gender.equals("Đực") && !gender.equals("Cái")) {
                throw new PetcareException("Giới tính phải là: Đực (0) hoặc Cái (1)");
            }
            // Normalize to "0" or "1"
            if (gender.equals("Đực")) {
                this.petGender = "0";
            } else if (gender.equals("Cái")) {
                this.petGender = "1";
            } else {
                this.petGender = gender;
            }
        } else {
            this.petGender = null;
        }
    }

    public Date getPetDob() {
        return petDob;
    }

    /**
     * Set pet date of birth with validation
     */
    public final void setPetDob(Date petDob) throws PetcareException {
        if (petDob != null && petDob.after(new Date())) {
            throw new PetcareException("Ngày sinh không thể ở tương lai");
        }
        this.petDob = petDob;
    }

    public Double getPetWeight() {
        return petWeight;
    }

    /**
     * Set pet weight with validation
     */
    public void setPetWeight(Double petWeight) {
        if (petWeight != null && petWeight < 0) {
            throw new IllegalArgumentException("Cân nặng không thể âm");
        }
        this.petWeight = petWeight;
    }

    public String getPetSterilization() {
        return petSterilization;
    }

    /**
     * Set pet sterilization status
     */
    public void setPetSterilization(String petSterilization) {
        if (petSterilization != null && !petSterilization.trim().isEmpty()) {
            String status = petSterilization.trim();
            if (!status.equals("0") && !status.equals("1") &&
                    !status.equals("Chưa triệt sản") && !status.equals("Đã triệt sản")) {
                throw new IllegalArgumentException("Trạng thái triệt sản không hợp lệ");
            }
            // Normalize to "0" or "1"
            if (status.equals("Chưa triệt sản")) {
                this.petSterilization = "0";
            } else if (status.equals("Đã triệt sản")) {
                this.petSterilization = "1";
            } else {
                this.petSterilization = status;
            }
        } else {
            this.petSterilization = null;
        }
    }

    public String getPetCharacteristic() {
        return petCharacteristic;
    }

    /**
     * Set pet characteristic
     */
    public void setPetCharacteristic(String petCharacteristic) {
        this.petCharacteristic = petCharacteristic != null ? petCharacteristic.trim() : null;
    }

    public String getPetDrugAllergy() {
        return petDrugAllergy;
    }

    /**
     * Set pet drug allergy
     */
    public void setPetDrugAllergy(String petDrugAllergy) {
        this.petDrugAllergy = petDrugAllergy != null ? petDrugAllergy.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return petId == pet.petId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(petId);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", petName='" + petName + '\'' +
                ", petSpecies='" + petSpecies + '\'' +
                ", petGender='" + petGender + '\'' +
                '}';
    }
}
