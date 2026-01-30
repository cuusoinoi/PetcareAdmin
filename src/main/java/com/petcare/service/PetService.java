package com.petcare.service;

import com.petcare.model.domain.Customer;
import com.petcare.model.domain.Pet;
import com.petcare.model.entity.PetEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IPetRepository;
import com.petcare.repository.PetRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Pet Service - business logic; Entity ↔ Domain. */
public class PetService {
    private static PetService instance;
    private IPetRepository repository;
    private CustomerService customerService;

    private PetService() {
        this.repository = new PetRepository();
        this.customerService = CustomerService.getInstance();
    }

    public static PetService getInstance() {
        if (instance == null) {
            instance = new PetService();
        }
        return instance;
    }

    public void setRepository(IPetRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    public List<Pet> getAllPets() throws PetcareException {
        List<PetEntity> entities = repository.findAll();
        return entities.stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    public Pet getPetById(int id) throws PetcareException {
        PetEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

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
        Customer customer = customerService.getCustomerById(pet.getCustomerId());
        if (customer == null) {
            throw new PetcareException("Không tìm thấy khách hàng với ID: " + pet.getCustomerId());
        }
        PetEntity entity = domainToEntity(pet);
        int result = repository.insert(entity);
        if (result > 0) {
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
        PetEntity existing = repository.findById(pet.getPetId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy thú cưng với ID: " + pet.getPetId());
        }
        if (existing.getCustomerId() != pet.getCustomerId()) {
            Customer customer = customerService.getCustomerById(pet.getCustomerId());
            if (customer == null) {
                throw new PetcareException("Không tìm thấy khách hàng với ID: " + pet.getCustomerId());
            }
        }
        PetEntity entity = domainToEntity(pet);
        int result = repository.update(entity);

        if (result == 0) {
            throw new PetcareException("Không thể cập nhật thú cưng");
        }
    }

    /**
     * Delete a pet
     * Business rules:
     * - Pet must exist
     */
    public void deletePet(int id) throws PetcareException {
        PetEntity pet = repository.findById(id);
        if (pet == null) {
            throw new PetcareException("Không tìm thấy thú cưng với ID: " + id);
        }

        int result = repository.delete(id);

        if (result == 0) {
            throw new PetcareException("Không thể xóa thú cưng");
        }
    }

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
