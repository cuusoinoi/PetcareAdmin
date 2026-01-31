package com.petcare.repository;

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

import java.util.List;

/**
 * Invoice Repository Interface
 */
public interface IInvoiceRepository {

    List<InvoiceListDto> findAllForList() throws PetcareException;

    InvoiceEntity findEntityById(int invoiceId) throws PetcareException;

    InvoiceInfoDto findInfoById(int invoiceId) throws PetcareException;

    List<InvoiceDetailListDto> findDetailsByInvoiceId(int invoiceId) throws PetcareException;

    int insert(InvoiceEntity entity) throws PetcareException;

    void insertDetail(InvoiceDetailEntity entity) throws PetcareException;

    void insertMedicineDetail(InvoiceMedicineDetailEntity entity) throws PetcareException;

    List<InvoiceMedicineDetailListDto> findMedicineDetailsByInvoiceId(int invoiceId) throws PetcareException;

    void insertVaccinationDetail(InvoiceVaccinationDetailEntity entity) throws PetcareException;

    List<InvoiceVaccinationDetailListDto> findVaccinationDetailsByInvoiceId(int invoiceId) throws PetcareException;

    void deleteVaccinationDetailsByInvoiceId(int invoiceId) throws PetcareException;

    void deleteMedicineDetailsByInvoiceId(int invoiceId) throws PetcareException;

    void deleteDetailsByInvoiceId(int invoiceId) throws PetcareException;

    int delete(int invoiceId) throws PetcareException;

    long sumTotalAmountThisYear() throws PetcareException;

    /** Tổng doanh thu theo chi tiết hóa đơn (cùng nguồn với biểu đồ theo dịch vụ) để số liệu khớp. */
    long sumDetailTotalPriceThisYear() throws PetcareException;

    long sumTotalAmountByMonth(int year, int month) throws PetcareException;

    /** Doanh thu tháng theo chi tiết hóa đơn (cùng nguồn với card và biểu đồ tròn). */
    long sumDetailTotalPriceByMonth(int year, int month) throws PetcareException;

    List<ServiceRevenueDto> getRevenueByServiceTypeThisYear() throws PetcareException;

    /** Tìm invoice_id theo pet_enclosure_id (một lưu chuồng có tối đa một hóa đơn). */
    Integer findInvoiceIdByPetEnclosureId(int petEnclosureId) throws PetcareException;
}
