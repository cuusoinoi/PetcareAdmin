package com.petcare.service;

import com.petcare.model.domain.Doctor;
import com.petcare.model.entity.DoctorEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.DoctorRepository;
import com.petcare.repository.IDoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Doctor Service - Business Logic Layer
 * Handles business rules and converts between Entity and Domain Model
 */
public class DoctorService {
    private static DoctorService instance;
    private IDoctorRepository repository;

    /**
     * Private constructor for Singleton pattern
     */
    private DoctorService() {
        this.repository = new DoctorRepository();
    }

    /**
     * Get singleton instance
     */
    public static DoctorService getInstance() {
        if (instance == null) {
            instance = new DoctorService();
        }
        return instance;
    }

    /**
     * Set repository (for dependency injection and testing)
     */
    public void setRepository(IDoctorRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    /**
     * Get all doctors
     */
    public List<Doctor> getAllDoctors() throws PetcareException {
        List<DoctorEntity> entities = repository.findAll();
        return entities.stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(int id) throws PetcareException {
        DoctorEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Get doctor by phone number
     */
    public Doctor getDoctorByPhone(String phone) throws PetcareException {
        DoctorEntity entity = repository.findByPhone(phone);
        return entity != null ? entityToDomain(entity) : null;
    }

    /**
     * Create a new doctor
     * Business rules:
     * - Phone number must be unique
     */
    public void createDoctor(Doctor doctor) throws PetcareException {
        // Business rule: Check if phone number already exists
        if (repository.existsByPhone(doctor.getDoctorPhoneNumber())) {
            throw new PetcareException("Số điện thoại đã được sử dụng bởi bác sĩ khác");
        }

        // Convert domain model to entity
        DoctorEntity entity = domainToEntity(doctor);

        // Insert into database
        int result = repository.insert(entity);

        if (result > 0) {
            // Set the generated ID back to domain model
            doctor.setDoctorId(entity.getDoctorId());
        } else {
            throw new PetcareException("Không thể tạo bác sĩ mới");
        }
    }

    /**
     * Update an existing doctor
     * Business rules:
     * - Doctor must exist
     * - Phone number must be unique (if changed)
     */
    public void updateDoctor(Doctor doctor) throws PetcareException {
        // Business rule: Check if doctor exists
        DoctorEntity existing = repository.findById(doctor.getDoctorId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy bác sĩ với ID: " + doctor.getDoctorId());
        }

        // Business rule: Check if phone number is already used by another doctor
        if (!existing.getDoctorPhoneNumber().equals(doctor.getDoctorPhoneNumber())) {
            if (repository.existsByPhone(doctor.getDoctorPhoneNumber())) {
                throw new PetcareException("Số điện thoại đã được sử dụng bởi bác sĩ khác");
            }
        }

        // Convert domain model to entity
        DoctorEntity entity = domainToEntity(doctor);

        // Update in database
        int result = repository.update(entity);

        if (result == 0) {
            throw new PetcareException("Không thể cập nhật bác sĩ");
        }
    }

    /**
     * Delete a doctor
     * Business rules:
     * - Doctor must exist
     * - TODO: Check if doctor has appointments, treatments, etc.
     */
    public void deleteDoctor(int id) throws PetcareException {
        // Business rule: Check if doctor exists
        DoctorEntity doctor = repository.findById(id);
        if (doctor == null) {
            throw new PetcareException("Không tìm thấy bác sĩ với ID: " + id);
        }

        // TODO: Business rule - Check if doctor has related records
        // This would require other repositories to check for related records
        // For now, we'll allow deletion (database foreign key constraints will handle it)

        // Delete from database
        int result = repository.delete(id);

        if (result == 0) {
            throw new PetcareException("Không thể xóa bác sĩ");
        }
    }

    /**
     * Check if doctor exists by phone number
     */
    public boolean doctorExistsByPhone(String phone) throws PetcareException {
        return repository.existsByPhone(phone);
    }

    /**
     * Convert Entity to Domain Model
     */
    private Doctor entityToDomain(DoctorEntity entity) {
        try {
            Doctor doctor = new Doctor();
            doctor.setDoctorId(entity.getDoctorId());
            doctor.setDoctorName(entity.getDoctorName());
            doctor.setDoctorPhoneNumber(entity.getDoctorPhoneNumber());
            doctor.setDoctorIdentityCard(entity.getDoctorIdentityCard());
            doctor.setDoctorAddress(entity.getDoctorAddress());
            doctor.setDoctorNote(entity.getDoctorNote());
            return doctor;
        } catch (PetcareException ex) {
            throw new RuntimeException("Invalid entity data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Convert Domain Model to Entity
     */
    private DoctorEntity domainToEntity(Doctor doctor) {
        DoctorEntity entity = new DoctorEntity();
        entity.setDoctorId(doctor.getDoctorId());
        entity.setDoctorName(doctor.getDoctorName());
        entity.setDoctorPhoneNumber(doctor.getDoctorPhoneNumber());
        entity.setDoctorIdentityCard(doctor.getDoctorIdentityCard());
        entity.setDoctorAddress(doctor.getDoctorAddress());
        entity.setDoctorNote(doctor.getDoctorNote());
        return entity;
    }
}
