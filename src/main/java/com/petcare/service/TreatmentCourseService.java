package com.petcare.service;

import com.petcare.model.domain.TreatmentCourse;
import com.petcare.model.entity.TreatmentCourseEntity;
import com.petcare.model.entity.TreatmentCourseInfoDto;
import com.petcare.model.entity.TreatmentCourseListDto;
import com.petcare.model.entity.TreatmentSessionListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.ITreatmentCourseRepository;
import com.petcare.repository.TreatmentCourseRepository;

import java.util.List;

/**
 * Treatment Course Service - Entity ↔ Domain
 */
public class TreatmentCourseService {
    private static TreatmentCourseService instance;
    private ITreatmentCourseRepository repository;

    private TreatmentCourseService() {
        this.repository = new TreatmentCourseRepository();
    }

    public static TreatmentCourseService getInstance() {
        if (instance == null) {
            instance = new TreatmentCourseService();
        }
        return instance;
    }

    public void setRepository(ITreatmentCourseRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<TreatmentCourseListDto> getCoursesForList() throws PetcareException {
        return repository.findAllForList();
    }

    public TreatmentCourse getCourseById(int id) throws PetcareException {
        TreatmentCourseEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createCourse(TreatmentCourse course) throws PetcareException {
        TreatmentCourseEntity entity = domainToEntity(course);
        int result = repository.insert(entity);
        if (result > 0) {
            course.setTreatmentCourseId(entity.getTreatmentCourseId());
        } else {
            throw new PetcareException("Không thể tạo liệu trình mới");
        }
    }

    public void updateCourse(TreatmentCourse course) throws PetcareException {
        TreatmentCourseEntity entity = domainToEntity(course);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật liệu trình");
        }
    }

    public void deleteCourse(int id) throws PetcareException {
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa liệu trình");
        }
    }

    public void completeCourse(int id) throws PetcareException {
        int result = repository.completeCourse(id);
        if (result == 0) {
            throw new PetcareException("Không thể kết thúc liệu trình");
        }
    }

    public TreatmentCourseInfoDto getCourseInfoForSessions(int courseId) throws PetcareException {
        return repository.findCourseInfoForSessions(courseId);
    }

    public List<TreatmentSessionListDto> getSessionsByCourseId(int courseId) throws PetcareException {
        return repository.findSessionsByCourseId(courseId);
    }

    public static String statusCodeToLabel(String code) {
        if (code == null) return "";
        return "1".equals(code) ? "Đang điều trị" : "Kết thúc";
    }

    private TreatmentCourse entityToDomain(TreatmentCourseEntity e) {
        TreatmentCourse tc = new TreatmentCourse();
        tc.setTreatmentCourseId(e.getTreatmentCourseId());
        tc.setMedicalRecordId(e.getMedicalRecordId());
        tc.setCustomerId(e.getCustomerId());
        tc.setPetId(e.getPetId());
        tc.setStartDate(e.getStartDate());
        tc.setEndDate(e.getEndDate());
        tc.setStatus(TreatmentCourse.Status.fromCode(e.getStatus()));
        return tc;
    }

    private TreatmentCourseEntity domainToEntity(TreatmentCourse tc) {
        TreatmentCourseEntity e = new TreatmentCourseEntity();
        e.setTreatmentCourseId(tc.getTreatmentCourseId());
        e.setMedicalRecordId(tc.getMedicalRecordId());
        e.setCustomerId(tc.getCustomerId());
        e.setPetId(tc.getPetId());
        e.setStartDate(tc.getStartDate());
        e.setEndDate(tc.getEndDate());
        e.setStatus(tc.getStatus() != null ? tc.getStatus().getCode() : null);
        return e;
    }
}
