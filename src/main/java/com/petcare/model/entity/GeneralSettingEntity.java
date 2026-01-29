package com.petcare.model.entity;

/**
 * General Setting Entity - DTO mapping to general_settings table
 * Single row config: clinic info, checkout_hour, overtime_fee_per_hour, default_daily_rate, etc.
 */
public class GeneralSettingEntity {
    private int settingId;
    private String clinicName;
    private String clinicAddress1;
    private String clinicAddress2;
    private String phoneNumber1;
    private String phoneNumber2;
    private String representativeName;
    private String checkoutHour;      // "HH:mm" or "HH:mm:ss"
    private int overtimeFeePerHour;
    private int defaultDailyRate;
    private String signingPlace;

    public GeneralSettingEntity() {
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
}
