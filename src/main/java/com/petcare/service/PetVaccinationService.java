package com.petcare.service;

import com.petcare.model.domain.PetVaccination;
import com.petcare.model.entity.PetVaccinationEntity;
import com.petcare.model.entity.PetVaccinationListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IPetVaccinationRepository;
import com.petcare.repository.PetVaccinationRepository;
import java.util.List;

/**
 * Pet Vaccination Service - Entity ↔ Domain
 */
public class PetVaccinationService {
    private static PetVaccinationService instance;
    private IPetVaccinationRepository repository;

    private PetVaccinationService() {
        this.repository = new PetVaccinationRepository();
    }

    public static PetVaccinationService getInstance() {
        if (instance == null) {
            instance = new PetVaccinationService();
        }
        return instance;
    }

    public void setRepository(IPetVaccinationRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<PetVaccinationListDto> getVaccinationsForList() throws PetcareException {
        return repository.findAllForList();
    }

    public PetVaccination getVaccinationById(int id) throws PetcareException {
        PetVaccinationEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createVaccination(PetVaccination vaccination) throws PetcareException {
        PetVaccinationEntity entity = domainToEntity(vaccination);
        int result = repository.insert(entity);
        if (result > 0) {
            vaccination.setPetVaccinationId(entity.getPetVaccinationId());
        } else {
            throw new PetcareException("Không thể tạo bản ghi tiêm chủng mới");
        }
    }

    public void updateVaccination(PetVaccination vaccination) throws PetcareException {
        PetVaccinationEntity entity = domainToEntity(vaccination);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật bản ghi tiêm chủng");
        }
    }

    public void deleteVaccination(int id) throws PetcareException {
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa bản ghi tiêm chủng");
        }
    }

    private PetVaccination entityToDomain(PetVaccinationEntity e) {
        PetVaccination pv = new PetVaccination();
        pv.setPetVaccinationId(e.getPetVaccinationId());
        pv.setVaccineId(e.getVaccineId());
        pv.setCustomerId(e.getCustomerId());
        pv.setPetId(e.getPetId());
        pv.setDoctorId(e.getDoctorId());
        pv.setVaccinationDate(e.getVaccinationDate());
        pv.setNextVaccinationDate(e.getNextVaccinationDate());
        pv.setNotes(e.getNotes());
        return pv;
    }

    private PetVaccinationEntity domainToEntity(PetVaccination pv) {
        PetVaccinationEntity e = new PetVaccinationEntity();
        e.setPetVaccinationId(pv.getPetVaccinationId());
        e.setVaccineId(pv.getVaccineId());
        e.setCustomerId(pv.getCustomerId());
        e.setPetId(pv.getPetId());
        e.setDoctorId(pv.getDoctorId());
        e.setVaccinationDate(pv.getVaccinationDate());
        e.setNextVaccinationDate(pv.getNextVaccinationDate());
        e.setNotes(pv.getNotes());
        return e;
    }
}
