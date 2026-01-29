package com.petcare.service;

import com.petcare.model.domain.PetEnclosure;
import com.petcare.model.entity.PetEnclosureEntity;
import com.petcare.model.entity.PetEnclosureListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IPetEnclosureRepository;
import com.petcare.repository.PetEnclosureRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Pet Enclosure Service - Entity ↔ Domain
 */
public class PetEnclosureService {
    private static PetEnclosureService instance;
    private IPetEnclosureRepository repository;

    private PetEnclosureService() {
        this.repository = new PetEnclosureRepository();
    }

    public static PetEnclosureService getInstance() {
        if (instance == null) {
            instance = new PetEnclosureService();
        }
        return instance;
    }

    public void setRepository(IPetEnclosureRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<PetEnclosureListDto> getEnclosuresForList() throws PetcareException {
        return repository.findAllForList();
    }

    public PetEnclosure getEnclosureById(int id) throws PetcareException {
        PetEnclosureEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createEnclosure(PetEnclosure enclosure) throws PetcareException {
        PetEnclosureEntity entity = domainToEntity(enclosure);
        int result = repository.insert(entity);
        if (result > 0) {
            enclosure.setPetEnclosureId(entity.getPetEnclosureId());
        } else {
            throw new PetcareException("Không thể tạo lưu chuồng mới");
        }
    }

    public void updateEnclosure(PetEnclosure enclosure) throws PetcareException {
        PetEnclosureEntity entity = domainToEntity(enclosure);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật lưu chuồng");
        }
    }

    public void deleteEnclosure(int id) throws PetcareException {
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa lưu chuồng");
        }
    }

    public void updateCheckOut(int enclosureId, Date checkOutDate) throws PetcareException {
        int result = repository.updateCheckOut(enclosureId, checkOutDate);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật check-out");
        }
    }

    public int getCountThisMonth() throws PetcareException {
        return repository.countThisMonth();
    }

    public int getCountByDate(Date date) throws PetcareException {
        return repository.countByDate(date);
    }

    public Map<String, Map<String, Integer>> getCheckinCheckoutStats(int days) throws PetcareException {
        return repository.getCheckinCheckoutStats(days);
    }

    private PetEnclosure entityToDomain(PetEnclosureEntity e) {
        PetEnclosure pe = new PetEnclosure();
        pe.setPetEnclosureId(e.getPetEnclosureId());
        pe.setCustomerId(e.getCustomerId());
        pe.setPetId(e.getPetId());
        pe.setPetEnclosureNumber(e.getPetEnclosureNumber());
        pe.setCheckInDate(e.getCheckInDate());
        pe.setCheckOutDate(e.getCheckOutDate());
        pe.setDailyRate(e.getDailyRate());
        pe.setDeposit(e.getDeposit());
        pe.setEmergencyLimit(e.getEmergencyLimit());
        pe.setPetEnclosureNote(e.getPetEnclosureNote());
        pe.setPetEnclosureStatus(PetEnclosure.Status.fromLabel(e.getPetEnclosureStatus()));
        return pe;
    }

    private PetEnclosureEntity domainToEntity(PetEnclosure pe) {
        PetEnclosureEntity e = new PetEnclosureEntity();
        e.setPetEnclosureId(pe.getPetEnclosureId());
        e.setCustomerId(pe.getCustomerId());
        e.setPetId(pe.getPetId());
        e.setPetEnclosureNumber(pe.getPetEnclosureNumber());
        e.setCheckInDate(pe.getCheckInDate());
        e.setCheckOutDate(pe.getCheckOutDate());
        e.setDailyRate(pe.getDailyRate());
        e.setDeposit(pe.getDeposit());
        e.setEmergencyLimit(pe.getEmergencyLimit());
        e.setPetEnclosureNote(pe.getPetEnclosureNote());
        e.setPetEnclosureStatus(pe.getPetEnclosureStatus() != null ? pe.getPetEnclosureStatus().getLabel() : null);
        return e;
    }
}
