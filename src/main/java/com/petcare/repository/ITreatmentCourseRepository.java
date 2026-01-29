package com.petcare.repository;

import com.petcare.model.entity.TreatmentCourseEntity;
import com.petcare.model.entity.TreatmentCourseInfoDto;
import com.petcare.model.entity.TreatmentCourseListDto;
import com.petcare.model.entity.TreatmentSessionListDto;
import com.petcare.model.exception.PetcareException;
import java.util.List;

/**
 * Treatment Course Repository Interface
 */
public interface ITreatmentCourseRepository {

    List<TreatmentCourseListDto> findAllForList() throws PetcareException;

    TreatmentCourseEntity findById(int id) throws PetcareException;

    int insert(TreatmentCourseEntity entity) throws PetcareException;

    int update(TreatmentCourseEntity entity) throws PetcareException;

    int delete(int id) throws PetcareException;

    int completeCourse(int id) throws PetcareException;

    TreatmentCourseInfoDto findCourseInfoForSessions(int courseId) throws PetcareException;

    List<TreatmentSessionListDto> findSessionsByCourseId(int courseId) throws PetcareException;
}
