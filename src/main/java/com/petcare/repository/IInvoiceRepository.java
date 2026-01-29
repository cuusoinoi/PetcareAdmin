package com.petcare.repository;

import com.petcare.model.entity.InvoiceDetailEntity;
import com.petcare.model.entity.InvoiceDetailListDto;
import com.petcare.model.entity.InvoiceEntity;
import com.petcare.model.entity.InvoiceInfoDto;
import com.petcare.model.entity.InvoiceListDto;
import com.petcare.model.entity.ServiceRevenueDto;
import com.petcare.model.exception.PetcareException;
import java.util.List;

/**
 * Invoice Repository Interface
 */
public interface IInvoiceRepository {

    List<InvoiceListDto> findAllForList() throws PetcareException;

    InvoiceInfoDto findInfoById(int invoiceId) throws PetcareException;

    List<InvoiceDetailListDto> findDetailsByInvoiceId(int invoiceId) throws PetcareException;

    int insert(InvoiceEntity entity) throws PetcareException;

    void insertDetail(InvoiceDetailEntity entity) throws PetcareException;

    void deleteDetailsByInvoiceId(int invoiceId) throws PetcareException;

    int delete(int invoiceId) throws PetcareException;

    long sumTotalAmountThisYear() throws PetcareException;

    long sumTotalAmountByMonth(int year, int month) throws PetcareException;

    List<ServiceRevenueDto> getRevenueByServiceTypeThisYear() throws PetcareException;
}
