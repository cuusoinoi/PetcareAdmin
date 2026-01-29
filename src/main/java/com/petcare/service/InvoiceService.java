package com.petcare.service;

import com.petcare.model.domain.Invoice;
import com.petcare.model.domain.InvoiceDetailItem;
import com.petcare.model.entity.InvoiceDetailEntity;
import com.petcare.model.entity.InvoiceEntity;
import com.petcare.model.entity.InvoiceInfoDto;
import com.petcare.model.entity.InvoiceListDto;
import com.petcare.model.entity.InvoiceDetailListDto;
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

    public InvoiceInfoDto getInvoiceInfo(int invoiceId) throws PetcareException {
        return repository.findInfoById(invoiceId);
    }

    public List<InvoiceDetailListDto> getInvoiceDetails(int invoiceId) throws PetcareException {
        return repository.findDetailsByInvoiceId(invoiceId);
    }

    /**
     * Create invoice header and details. Returns generated invoice ID.
     */
    public int createInvoice(int customerId, int petId, Integer petEnclosureId, Date invoiceDate,
                             int discount, int subtotal, int deposit, int totalAmount,
                             List<InvoiceDetailItem> details) throws PetcareException {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setCustomerId(customerId);
        entity.setPetId(petId);
        entity.setPetEnclosureId(petEnclosureId);
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

    public void deleteInvoice(int invoiceId) throws PetcareException {
        int result = repository.delete(invoiceId);
        if (result == 0) {
            throw new PetcareException("Không thể xóa hóa đơn");
        }
    }

    public long getRevenueThisYear() throws PetcareException {
        return repository.sumTotalAmountThisYear();
    }

    public long getRevenueByMonth(int year, int month) throws PetcareException {
        return repository.sumTotalAmountByMonth(year, month);
    }

    public Map<String, Long> getMonthlyRevenueStats() throws PetcareException {
        Map<String, Long> result = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 11; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            long revenue = repository.sumTotalAmountByMonth(year, month);
            result.put(String.format("%04d-%02d", year, month), revenue);
        }
        return result;
    }

    public List<ServiceRevenueDto> getRevenueByServiceTypeThisYear() throws PetcareException {
        return repository.getRevenueByServiceTypeThisYear();
    }
}
