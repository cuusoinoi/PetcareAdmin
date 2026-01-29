package com.petcare.service;

import com.petcare.model.domain.Appointment;
import com.petcare.model.entity.AppointmentEntity;
import com.petcare.model.entity.AppointmentListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IAppointmentRepository;
import com.petcare.repository.AppointmentRepository;
import java.util.List;

/**
 * Appointment Service - Entity ↔ Domain; status label ↔ code
 */
public class AppointmentService {
    private static AppointmentService instance;
    private IAppointmentRepository repository;

    private AppointmentService() {
        this.repository = new AppointmentRepository();
    }

    public static AppointmentService getInstance() {
        if (instance == null) {
            instance = new AppointmentService();
        }
        return instance;
    }

    public void setRepository(IAppointmentRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    /** statusFilterLabel: "Tất cả" or "Chờ xác nhận", "Đã xác nhận", "Hoàn thành", "Đã hủy" */
    public List<AppointmentListDto> getAppointmentsForList(String statusFilterLabel) throws PetcareException {
        String code = statusFilterLabel == null || statusFilterLabel.equals("Tất cả") ? null : labelToStatusCode(statusFilterLabel);
        return repository.findAllForList(code);
    }

    public Appointment getAppointmentById(int id) throws PetcareException {
        AppointmentEntity entity = repository.findById(id);
        return entity != null ? entityToDomain(entity) : null;
    }

    public void createAppointment(Appointment appointment) throws PetcareException {
        AppointmentEntity entity = domainToEntity(appointment);
        int result = repository.insert(entity);
        if (result > 0) {
            appointment.setAppointmentId(entity.getAppointmentId());
        } else {
            throw new PetcareException("Không thể tạo lịch hẹn mới");
        }
    }

    public void updateAppointment(Appointment appointment) throws PetcareException {
        AppointmentEntity entity = domainToEntity(appointment);
        int result = repository.update(entity);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật lịch hẹn");
        }
    }

    public void updateStatus(int appointmentId, String statusLabel) throws PetcareException {
        String code = labelToStatusCode(statusLabel);
        int result = repository.updateStatus(appointmentId, code);
        if (result == 0) {
            throw new PetcareException("Không thể cập nhật trạng thái lịch hẹn");
        }
    }

    public void deleteAppointment(int id) throws PetcareException {
        int result = repository.delete(id);
        if (result == 0) {
            throw new PetcareException("Không thể xóa lịch hẹn");
        }
    }

    public static String statusCodeToLabel(String code) {
        if (code == null) return "";
        switch (code) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return code;
        }
    }

    private static String labelToStatusCode(String label) {
        if (label == null) return "";
        switch (label) {
            case "Chờ xác nhận": return "pending";
            case "Đã xác nhận": return "confirmed";
            case "Hoàn thành": return "completed";
            case "Đã hủy": return "cancelled";
            default: return "";
        }
    }

    private Appointment entityToDomain(AppointmentEntity e) {
        Appointment a = new Appointment();
        a.setAppointmentId(e.getAppointmentId());
        a.setCustomerId(e.getCustomerId());
        a.setPetId(e.getPetId());
        a.setDoctorId(e.getDoctorId());
        a.setServiceTypeId(e.getServiceTypeId());
        a.setAppointmentDate(e.getAppointmentDate());
        a.setAppointmentType(Appointment.AppointmentType.fromLabel(e.getAppointmentType()));
        a.setStatus(Appointment.Status.fromCode(e.getStatus()));
        a.setNotes(e.getNotes());
        a.setCreatedAt(e.getCreatedAt());
        return a;
    }

    private AppointmentEntity domainToEntity(Appointment a) {
        AppointmentEntity e = new AppointmentEntity();
        e.setAppointmentId(a.getAppointmentId());
        e.setCustomerId(a.getCustomerId());
        e.setPetId(a.getPetId());
        e.setDoctorId(a.getDoctorId());
        e.setServiceTypeId(a.getServiceTypeId());
        e.setAppointmentDate(a.getAppointmentDate());
        e.setAppointmentType(a.getAppointmentType() != null ? a.getAppointmentType().getLabel() : null);
        e.setStatus(a.getStatus() != null ? a.getStatus().getCode() : null);
        e.setNotes(a.getNotes());
        e.setCreatedAt(a.getCreatedAt());
        return e;
    }
}
