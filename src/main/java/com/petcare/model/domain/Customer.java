package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;
import java.util.Objects;

/**
 * Customer Domain Model
 * Contains business logic and validation rules
 * This is the domain model used in business layer, not the entity from database
 */
public class Customer {
    private int customerId;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerIdentityCard;
    private String customerAddress;
    private String customerNote;
    
    /**
     * Default constructor
     */
    public Customer() {
    }
    
    /**
     * Constructor with validation
     * 
     * @param customerName customer name
     * @param customerPhoneNumber phone number
     * @param customerEmail email (optional)
     * @param customerIdentityCard identity card (optional)
     * @param customerAddress address (optional)
     * @param customerNote note (optional)
     * @throws PetcareException if validation fails
     */
    public Customer(String customerName, String customerPhoneNumber, 
                   String customerEmail, String customerIdentityCard, 
                   String customerAddress, String customerNote) throws PetcareException {
        setCustomerName(customerName);
        setCustomerPhoneNumber(customerPhoneNumber);
        setCustomerEmail(customerEmail);
        setCustomerIdentityCard(customerIdentityCard);
        setCustomerAddress(customerAddress);
        setCustomerNote(customerNote);
    }
    
    // Getters and Setters with validation
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Set customer name with validation
     * 
     * @param customerName customer name
     * @throws PetcareException if name is null, blank, or exceeds max length
     */
    public final void setCustomerName(String customerName) throws PetcareException {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new PetcareException("Tên khách hàng không được để trống");
        }
        if (customerName.trim().length() > 255) {
            throw new PetcareException("Tên khách hàng không được vượt quá 255 ký tự");
        }
        this.customerName = customerName.trim();
    }
    
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }
    
    /**
     * Set customer phone number with validation
     * 
     * @param customerPhoneNumber phone number
     * @throws PetcareException if phone is null, blank, or invalid format
     */
    public final void setCustomerPhoneNumber(String customerPhoneNumber) throws PetcareException {
        if (customerPhoneNumber == null || customerPhoneNumber.trim().isEmpty()) {
            throw new PetcareException("Số điện thoại không được để trống");
        }
        
        // Remove all non-digit characters
        String phone = customerPhoneNumber.trim().replaceAll("[^0-9]", "");
        
        // Validate phone format (Vietnamese phone: 10-11 digits, starting with 0 or +84)
        if (phone.length() < 10 || phone.length() > 11) {
            throw new PetcareException("Số điện thoại phải có 10-11 chữ số");
        }
        
        if (!phone.startsWith("0") && !phone.startsWith("84")) {
            throw new PetcareException("Số điện thoại không hợp lệ (phải bắt đầu bằng 0 hoặc 84)");
        }
        
        this.customerPhoneNumber = phone;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    /**
     * Set customer email with validation
     * 
     * @param customerEmail email address (optional)
     * @throws PetcareException if email format is invalid
     */
    public final void setCustomerEmail(String customerEmail) throws PetcareException {
        if (customerEmail != null && !customerEmail.trim().isEmpty()) {
            String email = customerEmail.trim();
            
            // Basic email validation regex
            String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
            if (!email.matches(emailRegex)) {
                throw new PetcareException("Email không hợp lệ");
            }
            
            if (email.length() > 255) {
                throw new PetcareException("Email không được vượt quá 255 ký tự");
            }
            
            this.customerEmail = email;
        } else {
            this.customerEmail = null;
        }
    }
    
    public String getCustomerIdentityCard() {
        return customerIdentityCard;
    }
    
    /**
     * Set customer identity card with validation
     * 
     * @param customerIdentityCard identity card number (optional)
     * @throws PetcareException if identity card format is invalid
     */
    public final void setCustomerIdentityCard(String customerIdentityCard) throws PetcareException {
        if (customerIdentityCard != null && !customerIdentityCard.trim().isEmpty()) {
            String idCard = customerIdentityCard.trim();
            
            // Vietnamese ID card: 9 or 12 digits
            if (!idCard.matches("^[0-9]{9}$|^[0-9]{12}$")) {
                throw new PetcareException("CMND/CCCD phải có 9 hoặc 12 chữ số");
            }
            
            if (idCard.length() > 12) {
                throw new PetcareException("CMND/CCCD không được vượt quá 12 ký tự");
            }
            
            this.customerIdentityCard = idCard;
        } else {
            this.customerIdentityCard = null;
        }
    }
    
    public String getCustomerAddress() {
        return customerAddress;
    }
    
    /**
     * Set customer address
     * 
     * @param customerAddress address (optional)
     * @throws PetcareException if address exceeds max length
     */
    public final void setCustomerAddress(String customerAddress) throws PetcareException {
        if (customerAddress != null && !customerAddress.trim().isEmpty()) {
            if (customerAddress.trim().length() > 255) {
                throw new PetcareException("Địa chỉ không được vượt quá 255 ký tự");
            }
            this.customerAddress = customerAddress.trim();
        } else {
            this.customerAddress = null;
        }
    }
    
    public String getCustomerNote() {
        return customerNote;
    }
    
    /**
     * Set customer note
     * 
     * @param customerNote note (optional)
     */
    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote != null ? customerNote.trim() : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId || 
               Objects.equals(customerPhoneNumber, customer.customerPhoneNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerPhoneNumber);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNumber='" + customerPhoneNumber + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                '}';
    }
}
