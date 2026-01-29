package com.petcare.service;

import com.petcare.model.domain.Customer;
import com.petcare.model.entity.CustomerEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.CustomerRepository;
import com.petcare.repository.ICustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Customer Service - Business Logic Layer
 * Handles business rules and converts between Entity and Domain Model
 * Uses Singleton pattern for service instance management
 */
public class CustomerService {
    private static CustomerService instance;
    private ICustomerRepository repository;

    /**
     * Private constructor for Singleton pattern
     */
    private CustomerService() {
        this.repository = new CustomerRepository(); // Default implementation
    }

    /**
     * Get singleton instance
     *
     * @return CustomerService instance
     */
    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    /**
     * Set repository (for dependency injection and testing)
     *
     * @param repository ICustomerRepository implementation
     */
    public void setRepository(ICustomerRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    /**
     * Get all customers
     *
     * @return List of Customer domain models
     * @throws PetcareException if database operation fails
     */
    public List<Customer> getAllCustomers() throws PetcareException {
        List<CustomerEntity> entities = repository.findAll();
        return entities.stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get customer by ID
     *
     * @param id customer ID
     * @return Customer domain model if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    public Customer getCustomerById(int id) throws PetcareException {
        CustomerEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Get customer by phone number
     *
     * @param phone phone number
     * @return Customer domain model if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    public Customer getCustomerByPhone(String phone) throws PetcareException {
        CustomerEntity entity = repository.findByPhone(phone);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Get customer by email
     *
     * @param email email address
     * @return Customer domain model if found, null otherwise
     * @throws PetcareException if database operation fails
     */
    public Customer getCustomerByEmail(String email) throws PetcareException {
        CustomerEntity entity = repository.findByEmail(email);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Create a new customer
     * Business rules:
     * - Phone number must be unique
     * - Email must be unique (if provided)
     *
     * @param customer Customer domain model
     * @throws PetcareException if validation fails or database operation fails
     */
    public void createCustomer(Customer customer) throws PetcareException {
        // Business rule: Check if phone already exists
        if (repository.existsByPhone(customer.getCustomerPhoneNumber())) {
            throw new PetcareException("Số điện thoại đã tồn tại trong hệ thống");
        }

        // Business rule: Check if email already exists (if provided)
        if (customer.getCustomerEmail() != null && !customer.getCustomerEmail().isEmpty()) {
            if (repository.existsByEmail(customer.getCustomerEmail())) {
                throw new PetcareException("Email đã tồn tại trong hệ thống");
            }
        }

        // Convert domain model to entity
        CustomerEntity entity = domainToEntity(customer);

        // Insert into database
        int result = repository.insert(entity);

        if (result > 0) {
            // Set the generated ID back to domain model
            customer.setCustomerId(entity.getCustomerId());
        } else {
            throw new PetcareException("Không thể tạo khách hàng mới");
        }
    }

    /**
     * Update an existing customer
     * Business rules:
     * - Customer must exist
     * - Phone number must be unique (if changed)
     * - Email must be unique (if changed and provided)
     *
     * @param customer Customer domain model with updated data
     * @throws PetcareException if validation fails or database operation fails
     */
    public void updateCustomer(Customer customer) throws PetcareException {
        // Business rule: Check if customer exists
        CustomerEntity existing = repository.findById(customer.getCustomerId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy khách hàng với ID: " + customer.getCustomerId());
        }

        // Business rule: Check phone uniqueness (if changed)
        if (!existing.getCustomerPhoneNumber().equals(customer.getCustomerPhoneNumber())) {
            if (repository.existsByPhone(customer.getCustomerPhoneNumber())) {
                throw new PetcareException("Số điện thoại đã được sử dụng bởi khách hàng khác");
            }
        }

        // Business rule: Check email uniqueness (if changed and provided)
        if (customer.getCustomerEmail() != null && !customer.getCustomerEmail().isEmpty()) {
            if (!customer.getCustomerEmail().equals(existing.getCustomerEmail())) {
                if (repository.existsByEmail(customer.getCustomerEmail())) {
                    throw new PetcareException("Email đã được sử dụng bởi khách hàng khác");
                }
            }
        }

        // Convert domain model to entity
        CustomerEntity entity = domainToEntity(customer);

        // Update in database
        int result = repository.update(entity);

        if (result == 0) {
            throw new PetcareException("Không thể cập nhật khách hàng");
        }
    }

    /**
     * Delete a customer
     * Business rules:
     * - Customer must exist
     * - TODO: Check if customer has pets (would require PetRepository)
     *
     * @param id customer ID to delete
     * @throws PetcareException if validation fails or database operation fails
     */
    public void deleteCustomer(int id) throws PetcareException {
        // Business rule: Check if customer exists
        CustomerEntity customer = repository.findById(id);
        if (customer == null) {
            throw new PetcareException("Không tìm thấy khách hàng với ID: " + id);
        }

        // TODO: Business rule - Check if customer has pets
        // This would require PetRepository to check for related records
        // For now, we'll allow deletion (database foreign key constraints will handle it)

        // Delete from database
        int result = repository.delete(id);

        if (result == 0) {
            throw new PetcareException("Không thể xóa khách hàng");
        }
    }

    /**
     * Check if customer exists by phone
     *
     * @param phone phone number
     * @return true if exists, false otherwise
     * @throws PetcareException if database operation fails
     */
    public boolean customerExistsByPhone(String phone) throws PetcareException {
        return repository.existsByPhone(phone);
    }

    /**
     * Check if customer exists by email
     *
     * @param email email address
     * @return true if exists, false otherwise
     * @throws PetcareException if database operation fails
     */
    public boolean customerExistsByEmail(String email) throws PetcareException {
        return repository.existsByEmail(email);
    }

    /**
     * Convert Entity to Domain Model
     * Helper method to transform data from database layer to business layer
     *
     * @param entity CustomerEntity from database
     * @return Customer domain model
     */
    private Customer entityToDomain(CustomerEntity entity) {
        try {
            Customer customer = new Customer();
            customer.setCustomerId(entity.getCustomerId());
            customer.setCustomerName(entity.getCustomerName());
            customer.setCustomerPhoneNumber(entity.getCustomerPhoneNumber());
            customer.setCustomerEmail(entity.getCustomerEmail());
            customer.setCustomerIdentityCard(entity.getCustomerIdentityCard());
            customer.setCustomerAddress(entity.getCustomerAddress());
            customer.setCustomerNote(entity.getCustomerNote());
            return customer;
        } catch (PetcareException ex) {
            // Should not happen if entity data is valid
            // But if it does, wrap it in RuntimeException
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Convert Domain Model to Entity
     * Helper method to transform data from business layer to database layer
     *
     * @param customer Customer domain model
     * @return CustomerEntity for database
     */
    private CustomerEntity domainToEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setCustomerId(customer.getCustomerId());
        entity.setCustomerName(customer.getCustomerName());
        entity.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
        entity.setCustomerEmail(customer.getCustomerEmail());
        entity.setCustomerIdentityCard(customer.getCustomerIdentityCard());
        entity.setCustomerAddress(customer.getCustomerAddress());
        entity.setCustomerNote(customer.getCustomerNote());
        return entity;
    }
}
