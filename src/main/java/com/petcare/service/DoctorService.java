package com.petcare.service;

import com.petcare.model.domain.Doctor;
import com.petcare.model.entity.DoctorEntity;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.DoctorRepository;
import com.petcare.repository.IDoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Doctor Service - business logic; Entity ↔ Domain. */
public class DoctorService {
    private static DoctorService instance;
    private IDoctorRepository repository;

    private DoctorService() {
        this.repository = new DoctorRepository();
    }

    public static DoctorService getInstance() {
        if (instance == null) {
            instance = new DoctorService();
        }
        return instance;
    }

    public void setRepository(IDoctorRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

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
        if (repository.existsByPhone(doctor.getDoctorPhoneNumber())) {
            throw new PetcareException("Số điện thoại đã được sử dụng bởi bác sĩ khác");
        }
        DoctorEntity entity = domainToEntity(doctor);
        int result = repository.insert(entity);
        if (result > 0) {
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
        DoctorEntity existing = repository.findById(doctor.getDoctorId());
        if (existing == null) {
            throw new PetcareException("Không tìm thấy bác sĩ với ID: " + doctor.getDoctorId());
        }
        if (!existing.getDoctorPhoneNumber().equals(doctor.getDoctorPhoneNumber())) {
            if (repository.existsByPhone(doctor.getDoctorPhoneNumber())) {
                throw new PetcareException("Số điện thoại đã được sử dụng bởi bác sĩ khác");
            }
        }
        DoctorEntity entity = domainToEntity(doctor);
        int result = repository.update(entity);

        if (result == 0) {
            throw new PetcareException("Không thể cập nhật bác sĩ");
        }
    }

    /**
     * Delete a doctor
     * Business rules:
     * - Doctor must exist
     */
    public void deleteDoctor(int id) throws PetcareException {
        DoctorEntity doctor = repository.findById(id);
        if (doctor == null) {
            throw new PetcareException("Không tìm thấy bác sĩ với ID: " + id);
        }

        int result = repository.delete(id);

        if (result == 0) {
            throw new PetcareException("Không thể xóa bác sĩ");
        }
    }

    public boolean doctorExistsByPhone(String phone) throws PetcareException {
        return repository.existsByPhone(phone);
    }

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
