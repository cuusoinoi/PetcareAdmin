package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

/**
 * General Setting Domain Model - single row config
 */
public class GeneralSetting {
    private int settingId;
    private String clinicName;
    private String clinicAddress1;
    private String clinicAddress2;
    private String phoneNumber1;
    private String phoneNumber2;
    private String representativeName;
    private String checkoutHour;   // "HH:mm" or "HH:mm:ss"
    private int overtimeFeePerHour;
    private int defaultDailyRate;
    private String signingPlace;

    public GeneralSetting() {
    }

    public int getSettingId() { return settingId; }
    public void setSettingId(int settingId) { this.settingId = settingId; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
    public String getClinicAddress1() { return clinicAddress1; }
    public void setClinicAddress1(String clinicAddress1) { this.clinicAddress1 = clinicAddress1; }
    public String getClinicAddress2() { return clinicAddress2; }
    public void setClinicAddress2(String clinicAddress2) { this.clinicAddress2 = clinicAddress2; }
    public String getPhoneNumber1() { return phoneNumber1; }
    public void setPhoneNumber1(String phoneNumber1) { this.phoneNumber1 = phoneNumber1; }
    public String getPhoneNumber2() { return phoneNumber2; }
    public void setPhoneNumber2(String phoneNumber2) { this.phoneNumber2 = phoneNumber2; }
    public String getRepresentativeName() { return representativeName; }
    public void setRepresentativeName(String representativeName) { this.representativeName = representativeName; }
    public String getCheckoutHour() { return checkoutHour; }
    public void setCheckoutHour(String checkoutHour) { this.checkoutHour = checkoutHour; }
    public int getOvertimeFeePerHour() { return overtimeFeePerHour; }
    public void setOvertimeFeePerHour(int overtimeFeePerHour) { this.overtimeFeePerHour = overtimeFeePerHour; }
    public int getDefaultDailyRate() { return defaultDailyRate; }
    public void setDefaultDailyRate(int defaultDailyRate) { this.defaultDailyRate = defaultDailyRate; }
    public String getSigningPlace() { return signingPlace; }
    public void setSigningPlace(String signingPlace) { this.signingPlace = signingPlace; }

    /** Validation for save - throws PetcareException if invalid */
    public void validate() throws PetcareException {
        if (clinicName == null || clinicName.trim().isEmpty())
            throw new PetcareException("Vui lòng nhập tên phòng khám!");
        if (clinicAddress1 == null || clinicAddress1.trim().isEmpty())
            throw new PetcareException("Vui lòng nhập địa chỉ 1!");
        if (phoneNumber1 == null || phoneNumber1.trim().isEmpty())
            throw new PetcareException("Vui lòng nhập số điện thoại 1!");
        if (representativeName == null || representativeName.trim().isEmpty())
            throw new PetcareException("Vui lòng nhập tên người đại diện!");
        if (signingPlace == null || signingPlace.trim().isEmpty())
            throw new PetcareException("Vui lòng nhập nơi ký!");
        if (overtimeFeePerHour < 0)
            throw new PetcareException("Phí trễ giờ không được âm!");
        if (defaultDailyRate < 0)
            throw new PetcareException("Phí lưu chuồng không được âm!");
    }
}
