package com.petcare.service;

import com.petcare.model.domain.InvoiceDetailItem;
import com.petcare.model.domain.InvoiceMedicineItem;
import com.petcare.model.domain.InvoiceVaccinationItem;
import com.petcare.model.entity.InvoiceDetailEntity;
import com.petcare.model.entity.InvoiceDetailListDto;
import com.petcare.model.entity.InvoiceEntity;
import com.petcare.model.entity.InvoiceInfoDto;
import com.petcare.model.entity.InvoiceListDto;
import com.petcare.model.entity.InvoiceMedicineDetailEntity;
import com.petcare.model.entity.InvoiceMedicineDetailListDto;
import com.petcare.model.entity.InvoiceVaccinationDetailEntity;
import com.petcare.model.entity.InvoiceVaccinationDetailListDto;
import com.petcare.model.entity.ServiceRevenueDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.repository.IInvoiceRepository;
import com.petcare.repository.InvoiceRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Invoice Service - Entity ↔ Domain; create invoice with details; revenue stats
 */
public class InvoiceService {
    private static InvoiceService instance;
    private IInvoiceRepository repository;

    private InvoiceService() {
        this.repository = new InvoiceRepository();
    }

    public static InvoiceService getInstance() {
        if (instance == null) {
            instance = new InvoiceService();
        }
        return instance;
    }

    public void setRepository(IInvoiceRepository repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }

    public List<InvoiceListDto> getInvoicesForList() throws PetcareException {
        return repository.findAllForList();
    }

    public InvoiceEntity getInvoiceEntity(int invoiceId) throws PetcareException {
        return repository.findEntityById(invoiceId);
    }

    /** Tìm hóa đơn theo lưu chuồng (null nếu chưa có hóa đơn). */
    public Integer getInvoiceIdByPetEnclosureId(int petEnclosureId) throws PetcareException {
        return repository.findInvoiceIdByPetEnclosureId(petEnclosureId);
    }

    public InvoiceInfoDto getInvoiceInfo(int invoiceId) throws PetcareException {
        return repository.findInfoById(invoiceId);
    }

    public List<InvoiceDetailListDto> getInvoiceDetails(int invoiceId) throws PetcareException {
        return repository.findDetailsByInvoiceId(invoiceId);
    }

    public List<InvoiceMedicineDetailListDto> getInvoiceMedicineDetails(int invoiceId) throws PetcareException {
        return repository.findMedicineDetailsByInvoiceId(invoiceId);
    }

    public List<InvoiceVaccinationDetailListDto> getInvoiceVaccinationDetails(int invoiceId) throws PetcareException {
        return repository.findVaccinationDetailsByInvoiceId(invoiceId);
    }

    public int createInvoice(int customerId, int petId, Integer petEnclosureId, Date invoiceDate,
                             int discount, int subtotal, int deposit, int totalAmount,
                             List<InvoiceDetailItem> details) throws PetcareException {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setCustomerId(customerId);
        entity.setPetId(petId);
        entity.setPetEnclosureId(petEnclosureId);
        entity.setMedicalRecordId(null);
        entity.setInvoiceDate(invoiceDate);
        entity.setDiscount(discount);
        entity.setSubtotal(subtotal);
        entity.setDeposit(deposit);
        entity.setTotalAmount(totalAmount);
        int invoiceId = repository.insert(entity);
        if (invoiceId <= 0) {
            throw new PetcareException("Không thể tạo hóa đơn");
        }
        for (InvoiceDetailItem item : details) {
            InvoiceDetailEntity detail = new InvoiceDetailEntity();
            detail.setInvoiceId(invoiceId);
            detail.setServiceTypeId(item.getServiceTypeId());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setTotalPrice(item.getTotalPrice());
            repository.insertDetail(detail);
        }
        return invoiceId;
    }

    /** Tạo hóa đơn gắn với lượt khám: dịch vụ (details) + thuốc (medicineDetails) + tiêm chủng (vaccinationDetails). */
    public int createInvoiceFromVisit(int customerId, int petId, Integer medicalRecordId,
                                       Date invoiceDate, int discount, int subtotal, int deposit, int totalAmount,
                                       List<InvoiceDetailItem> details, List<InvoiceMedicineItem> medicineDetails,
                                       List<InvoiceVaccinationItem> vaccinationDetails) throws PetcareException {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setCustomerId(customerId);
        entity.setPetId(petId);
        entity.setPetEnclosureId(null);
        entity.setMedicalRecordId(medicalRecordId);
        entity.setInvoiceDate(invoiceDate);
        entity.setDiscount(discount);
        entity.setSubtotal(subtotal);
        entity.setDeposit(deposit);
        entity.setTotalAmount(totalAmount);
        int invoiceId = repository.insert(entity);
        if (invoiceId <= 0) {
            throw new PetcareException("Không thể tạo hóa đơn");
        }
        if (details != null) {
            for (InvoiceDetailItem item : details) {
                InvoiceDetailEntity detail = new InvoiceDetailEntity();
                detail.setInvoiceId(invoiceId);
                detail.setServiceTypeId(item.getServiceTypeId());
                detail.setQuantity(item.getQuantity());
                detail.setUnitPrice(item.getUnitPrice());
                detail.setTotalPrice(item.getTotalPrice());
                repository.insertDetail(detail);
            }
        }
        if (medicineDetails != null) {
            for (InvoiceMedicineItem item : medicineDetails) {
                InvoiceMedicineDetailEntity med = new InvoiceMedicineDetailEntity();
                med.setInvoiceId(invoiceId);
                med.setMedicineId(item.getMedicineId());
                med.setQuantity(item.getQuantity());
                med.setUnitPrice(item.getUnitPrice());
                med.setTotalPrice(item.getTotalPrice());
                repository.insertMedicineDetail(med);
            }
        }
        if (vaccinationDetails != null) {
            for (InvoiceVaccinationItem item : vaccinationDetails) {
                InvoiceVaccinationDetailEntity vac = new InvoiceVaccinationDetailEntity();
                vac.setInvoiceId(invoiceId);
                vac.setVaccineId(item.getVaccineId());
                vac.setQuantity(item.getQuantity());
                vac.setUnitPrice(item.getUnitPrice());
                vac.setTotalPrice(item.getTotalPrice());
                repository.insertVaccinationDetail(vac);
            }
        }
        return invoiceId;
    }

    public void deleteInvoice(int invoiceId) throws PetcareException {
        int result = repository.delete(invoiceId);
        if (result == 0) {
            throw new PetcareException("Không thể xóa hóa đơn");
        }
    }

    /** Doanh thu năm (tổng từ chi tiết hóa đơn, khớp với biểu đồ theo loại dịch vụ). */
    public long getRevenueThisYear() throws PetcareException {
        return repository.sumDetailTotalPriceThisYear();
    }

    public long getRevenueByMonth(int year, int month) throws PetcareException {
        return repository.sumDetailTotalPriceByMonth(year, month);
    }

    public Map<String, Long> getMonthlyRevenueStats() throws PetcareException {
        Map<String, Long> result = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 11; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            long revenue = repository.sumDetailTotalPriceByMonth(year, month);
            result.put(String.format("%04d-%02d", year, month), revenue);
        }
        return result;
    }

    public List<ServiceRevenueDto> getRevenueByServiceTypeThisYear() throws PetcareException {
        return repository.getRevenueByServiceTypeThisYear();
    }
}
