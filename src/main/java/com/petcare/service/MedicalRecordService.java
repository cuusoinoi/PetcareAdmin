package com.petcare.service;

import com.petcare.model.domain.MedicalRecord;
import com.petcare.model.entity.MedicalRecordEntity;
import com.petcare.model.entity.MedicalRecordListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IMedicalRecordRepository;
import com.petcare.repository.MedicalRecordRepository;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Medical Record Service - Entity ↔ Domain
 */
public class MedicalRecordService {
    private static MedicalRecordService instance;
    private IMedicalRecordRepository repository;

    private MedicalRecordService() {
        this.repository = new MedicalRecordRepository();
    }

    public static MedicalRecordService getInstance() {
        if (instance == null) {
            instance = new MedicalRecordService();
        }
        return instance;
    }

    public void setRepository(IMedicalRecordRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<MedicalRecordListDto> getRecordsForList() throws PetcareException {
        return repository.findAllForList();
    }

    public MedicalRecord getRecordById(int id) throws PetcareException {
        MedicalRecordEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createRecord(MedicalRecord record) throws PetcareException {
        MedicalRecordEntity entity = domainToEntity(record);
        int result = repository.insert(entity);
        if (result > 0) {
            record.setMedicalRecordId(entity.getMedicalRecordId());
        } else {
            throw new PetcareException("Không thể tạo hồ sơ khám bệnh mới");
        }
    }

    public void updateRecord(MedicalRecord record) throws PetcareException {
        MedicalRecordEntity entity = domainToEntity(record);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật hồ sơ khám bệnh");
        }
    }

    public void deleteRecord(int id) throws PetcareException {
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa hồ sơ khám bệnh");
        }
    }

    public int getCountThisMonth() throws PetcareException {
        return repository.countThisMonth();
    }

    public int getCountByDate(Date date) throws PetcareException {
        return repository.countByDate(date);
    }

    public Map<String, Integer> getCountByDay(int days) throws PetcareException {
        return repository.countByDay(days);
    }

    private MedicalRecord entityToDomain(MedicalRecordEntity e) {
        MedicalRecord m = new MedicalRecord();
        m.setMedicalRecordId(e.getMedicalRecordId());
        m.setCustomerId(e.getCustomerId());
        m.setPetId(e.getPetId());
        m.setDoctorId(e.getDoctorId());
        m.setMedicalRecordType(MedicalRecord.RecordType.fromLabel(e.getMedicalRecordType()));
        m.setMedicalRecordVisitDate(e.getMedicalRecordVisitDate());
        m.setMedicalRecordSummary(e.getMedicalRecordSummary());
        m.setMedicalRecordDetails(e.getMedicalRecordDetails());
        return m;
    }

    private MedicalRecordEntity domainToEntity(MedicalRecord m) {
        MedicalRecordEntity e = new MedicalRecordEntity();
        e.setMedicalRecordId(m.getMedicalRecordId());
        e.setCustomerId(m.getCustomerId());
        e.setPetId(m.getPetId());
        e.setDoctorId(m.getDoctorId());
        e.setMedicalRecordType(m.getMedicalRecordType() != null ? m.getMedicalRecordType().getLabel() : null);
        e.setMedicalRecordVisitDate(m.getMedicalRecordVisitDate());
        e.setMedicalRecordSummary(m.getMedicalRecordSummary());
        e.setMedicalRecordDetails(m.getMedicalRecordDetails());
        return e;
    }
}
