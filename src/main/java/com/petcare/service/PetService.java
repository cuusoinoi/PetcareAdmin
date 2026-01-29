package com.petcare.service;

import com.petcare.model.domain.Customer;
import com.petcare.model.domain.Pet;
import com.petcare.model.entity.PetEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IPetRepository;
import com.petcare.repository.PetRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pet Service - Business Logic Layer
 * Handles business rules and converts between Entity and Domain Model
 */
public class PetService {
    private static PetService instance;
    private IPetRepository repository;
    private CustomerService customerService;

    /**
     * Private constructor for Singleton pattern
     */
    private PetService() {
        this.repository = new PetRepository();
        this.customerService = CustomerService.getInstance();
    }

    /**
     * Get singleton instance
     */
    public static PetService getInstance() {
        if (instance == null) {
            instance = new PetService();
        }
        return instance;
    }

    /**
     * Set repository (for dependency injection and testing)
     */
    public void setRepository(IPetRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    /**
     * Get all pets
     */
    public List<Pet> getAllPets() throws PetcareException {
        List<PetEntity> entities = repository.findAll();
        return entities.stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get pet by ID
     */
    public Pet getPetById(int id) throws PetcareException {
        PetEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Get pets by customer ID
     */
    public List<Pet> getPetsByCustomerId(int customerId) throws PetcareException {
        List<PetEntity> entities = repository.findByCustomerId(customerId);
        return entities.stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Create a new pet
     * Business rules:
     * - Customer must exist
     */
    public void createPet(Pet pet) throws PetcareException {
        // Business rule: Check if customer exists
        Customer customer = customerService.getCustomerById(pet.getCustomerId());
        if (customer == null) {
            throw new PetcareException("Không tìm thấy khách hàng với ID: " + pet.getCustomerId());
        }

        // Convert domain model to entity
        PetEntity entity = domainToEntity(pet);

        // Insert into database
        int result = repository.insert(entity);

        if (result > 0) {
            // Set the generated ID back to domain model
            pet.setPetId(entity.getPetId());
        } else {
            throw new PetcareException("Không thể tạo thú cưng mới");
        }
    }

    /**
     * Update an existing pet
     * Business rules:
     * - Pet must exist
     * - Customer must exist (if changed)
     */
    public void updatePet(Pet pet) throws PetcareException {
        // Business rule: Check if pet exists
        PetEntity existing = repository.findById(pet.getPetId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy thú cưng với ID: " + pet.getPetId());
        }

        // Business rule: Check if customer exists (if changed)
        if (existing.getCustomerId() != pet.getCustomerId()) {
            Customer customer = customerService.getCustomerById(pet.getCustomerId());
            if (customer == null) {
                throw new PetcareException("Không tìm thấy khách hàng với ID: " + pet.getCustomerId());
            }
        }

        // Convert domain model to entity
        PetEntity entity = domainToEntity(pet);

        // Update in database
        int result = repository.update(entity);

        if (result == 0) {
            throw new PetcareException("Không thể cập nhật thú cưng");
        }
    }

    /**
     * Delete a pet
     * Business rules:
     * - Pet must exist
     * - TODO: Check if pet has medical records, appointments, etc.
     */
    public void deletePet(int id) throws PetcareException {
        // Business rule: Check if pet exists
        PetEntity pet = repository.findById(id);
        if (pet == null) {
            throw new PetcareException("Không tìm thấy thú cưng với ID: " + id);
        }

        // TODO: Business rule - Check if pet has related records
        // This would require other repositories to check for related records
        // For now, we'll allow deletion (database foreign key constraints will handle it)

        // Delete from database
        int result = repository.delete(id);

        if (result == 0) {
            throw new PetcareException("Không thể xóa thú cưng");
        }
    }

    /**
     * Convert Entity to Domain Model
     */
    private Pet entityToDomain(PetEntity entity) {
        try {
            Pet pet = new Pet();
            pet.setPetId(entity.getPetId());
            pet.setCustomerId(entity.getCustomerId());
            pet.setPetName(entity.getPetName());
            pet.setPetSpecies(entity.getPetSpecies());
            pet.setPetGender(entity.getPetGender());
            pet.setPetDob(entity.getPetDob());
            pet.setPetWeight(entity.getPetWeight());
            pet.setPetSterilization(entity.getPetSterilization());
            pet.setPetCharacteristic(entity.getPetCharacteristic());
            pet.setPetDrugAllergy(entity.getPetDrugAllergy());
            return pet;
        } catch (PetcareException ex) {
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Convert Domain Model to Entity
     */
    private PetEntity domainToEntity(Pet pet) {
        PetEntity entity = new PetEntity();
        entity.setPetId(pet.getPetId());
        entity.setCustomerId(pet.getCustomerId());
        entity.setPetName(pet.getPetName());
        entity.setPetSpecies(pet.getPetSpecies());
        entity.setPetGender(pet.getPetGender());
        entity.setPetDob(pet.getPetDob());
        entity.setPetWeight(pet.getPetWeight());
        entity.setPetSterilization(pet.getPetSterilization());
        entity.setPetCharacteristic(pet.getPetCharacteristic());
        entity.setPetDrugAllergy(pet.getPetDrugAllergy());
        return entity;
    }
}
