package com.petcare.repository;

import com.petcare.model.entity.AppointmentEntity;
import com.petcare.model.entity.AppointmentListDto;
import com.petcare.model.exception.PetcareException;
import java.util.List;

/**
 * Appointment Repository Interface
 */
public interface IAppointmentRepository {

    List<AppointmentListDto> findAllForList(String statusCodeOrNull) throws PetcareException;

    AppointmentEntity findById(int id) throws PetcareException;

    int insert(AppointmentEntity entity) throws PetcareException;

    int update(AppointmentEntity entity) throws PetcareException;

    int updateStatus(int id, String statusCode) throws PetcareException;

    int delete(int id) throws PetcareException;
}
