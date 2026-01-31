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
import com.petcare.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice Repository - invoices + invoice_details; list with JOINs; revenue stats
 */
public class InvoiceRepository implements IInvoiceRepository {

    @Override
    public List<InvoiceListDto> findAllForList() throws PetcareException {
        String query = "SELECT i.invoice_id, i.invoice_date, c.customer_name, p.pet_name, " +
                "i.subtotal, i.discount, i.deposit, i.total_amount " +
                "FROM invoices i " +
                "INNER JOIN customers c ON i.customer_id = c.customer_id " +
                "INNER JOIN pets p ON i.pet_id = p.pet_id " +
                "ORDER BY i.invoice_id DESC";
        List<InvoiceListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                InvoiceListDto dto = new InvoiceListDto();
                dto.setInvoiceId(rs.getInt("invoice_id"));
                if (rs.getTimestamp("invoice_date") != null) {
                    dto.setInvoiceDate(new java.util.Date(rs.getTimestamp("invoice_date").getTime()));
                }
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setPetName(rs.getString("pet_name"));
                dto.setSubtotal(rs.getInt("subtotal"));
                dto.setDiscount(rs.getInt("discount"));
                dto.setDeposit(rs.getInt("deposit"));
                dto.setTotalAmount(rs.getInt("total_amount"));
                list.add(dto);
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải danh sách hóa đơn", ex);
        }
        return list;
    }

    @Override
    public InvoiceEntity findEntityById(int invoiceId) throws PetcareException {
        String query = "SELECT invoice_id, customer_id, pet_id, pet_enclosure_id, medical_record_id, invoice_date, " +
                "discount, subtotal, deposit, total_amount FROM invoices WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InvoiceEntity e = new InvoiceEntity();
                    e.setInvoiceId(rs.getInt("invoice_id"));
                    e.setCustomerId(rs.getInt("customer_id"));
                    e.setPetId(rs.getInt("pet_id"));
                    int peId = rs.getInt("pet_enclosure_id");
                    e.setPetEnclosureId(rs.wasNull() || peId <= 0 ? null : peId);
                    int mrId = rs.getInt("medical_record_id");
                    e.setMedicalRecordId(rs.wasNull() || mrId <= 0 ? null : mrId);
                    if (rs.getTimestamp("invoice_date") != null) {
                        e.setInvoiceDate(new java.util.Date(rs.getTimestamp("invoice_date").getTime()));
                    }
                    e.setDiscount(rs.getInt("discount"));
                    e.setSubtotal(rs.getInt("subtotal"));
                    e.setDeposit(rs.getInt("deposit"));
                    e.setTotalAmount(rs.getInt("total_amount"));
                    return e;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải hóa đơn theo ID: " + invoiceId, ex);
        }
        return null;
    }

    @Override
    public InvoiceInfoDto findInfoById(int invoiceId) throws PetcareException {
        String query = "SELECT i.invoice_id, i.invoice_date, i.subtotal, i.discount, i.deposit, i.total_amount, " +
                "c.customer_name, c.customer_phone_number, p.pet_name, pe.pet_enclosure_number " +
                "FROM invoices i " +
                "INNER JOIN customers c ON i.customer_id = c.customer_id " +
                "INNER JOIN pets p ON i.pet_id = p.pet_id " +
                "LEFT JOIN pet_enclosures pe ON i.pet_enclosure_id = pe.pet_enclosure_id " +
                "WHERE i.invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InvoiceInfoDto dto = new InvoiceInfoDto();
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    if (rs.getTimestamp("invoice_date") != null) {
                        dto.setInvoiceDate(new java.util.Date(rs.getTimestamp("invoice_date").getTime()));
                    }
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setCustomerPhoneNumber(rs.getString("customer_phone_number"));
                    dto.setPetName(rs.getString("pet_name"));
                    int peNum = rs.getInt("pet_enclosure_number");
                    dto.setPetEnclosureNumber(rs.wasNull() || peNum <= 0 ? null : peNum);
                    dto.setSubtotal(rs.getInt("subtotal"));
                    dto.setDiscount(rs.getInt("discount"));
                    dto.setDeposit(rs.getInt("deposit"));
                    dto.setTotalAmount(rs.getInt("total_amount"));
                    return dto;
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải thông tin hóa đơn: " + invoiceId, ex);
        }
        return null;
    }

    @Override
    public List<InvoiceDetailListDto> findDetailsByInvoiceId(int invoiceId) throws PetcareException {
        String query = "SELECT st.service_name, id.quantity, id.unit_price, id.total_price " +
                "FROM invoice_details id " +
                "INNER JOIN service_types st ON id.service_type_id = st.service_type_id " +
                "WHERE id.invoice_id = ?";
        List<InvoiceDetailListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetailListDto dto = new InvoiceDetailListDto();
                    dto.setServiceName(rs.getString("service_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getInt("unit_price"));
                    dto.setTotalPrice(rs.getInt("total_price"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải chi tiết hóa đơn", ex);
        }
        return list;
    }

    @Override
    public int insert(InvoiceEntity entity) throws PetcareException {
        String query = "INSERT INTO invoices (customer_id, pet_id, pet_enclosure_id, medical_record_id, invoice_date, " +
                "discount, subtotal, deposit, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getCustomerId());
            ps.setInt(2, entity.getPetId());
            if (entity.getPetEnclosureId() != null) {
                ps.setInt(3, entity.getPetEnclosureId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (entity.getMedicalRecordId() != null) {
                ps.setInt(4, entity.getMedicalRecordId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(5, entity.getInvoiceDate() != null ? new Timestamp(entity.getInvoiceDate().getTime()) : null);
            ps.setInt(6, entity.getDiscount());
            ps.setInt(7, entity.getSubtotal());
            ps.setInt(8, entity.getDeposit());
            ps.setInt(9, entity.getTotalAmount());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setInvoiceId(id);
                        return id;
                    }
                }
            }
            return 0;
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm hóa đơn", ex);
        }
    }

    @Override
    public void insertDetail(InvoiceDetailEntity entity) throws PetcareException {
        String query = "INSERT INTO invoice_details (invoice_id, service_type_id, quantity, unit_price, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, entity.getInvoiceId());
            ps.setInt(2, entity.getServiceTypeId());
            ps.setInt(3, entity.getQuantity());
            ps.setInt(4, entity.getUnitPrice());
            ps.setInt(5, entity.getTotalPrice());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm chi tiết hóa đơn", ex);
        }
    }

    @Override
    public void insertMedicineDetail(InvoiceMedicineDetailEntity entity) throws PetcareException {
        String query = "INSERT INTO invoice_medicine_details (invoice_id, medicine_id, quantity, unit_price, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, entity.getInvoiceId());
            ps.setInt(2, entity.getMedicineId());
            ps.setInt(3, entity.getQuantity());
            ps.setInt(4, entity.getUnitPrice());
            ps.setInt(5, entity.getTotalPrice());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm chi tiết thuốc vào hóa đơn", ex);
        }
    }

    @Override
    public List<InvoiceMedicineDetailListDto> findMedicineDetailsByInvoiceId(int invoiceId) throws PetcareException {
        String query = "SELECT m.medicine_name, imd.quantity, imd.unit_price, imd.total_price " +
                "FROM invoice_medicine_details imd " +
                "INNER JOIN medicines m ON imd.medicine_id = m.medicine_id " +
                "WHERE imd.invoice_id = ?";
        List<InvoiceMedicineDetailListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceMedicineDetailListDto dto = new InvoiceMedicineDetailListDto();
                    dto.setMedicineName(rs.getString("medicine_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getInt("unit_price"));
                    dto.setTotalPrice(rs.getInt("total_price"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải chi tiết thuốc hóa đơn", ex);
        }
        return list;
    }

    @Override
    public void insertVaccinationDetail(InvoiceVaccinationDetailEntity entity) throws PetcareException {
        String query = "INSERT INTO invoice_vaccination_details (invoice_id, vaccine_id, quantity, unit_price, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, entity.getInvoiceId());
            ps.setInt(2, entity.getVaccineId());
            ps.setInt(3, entity.getQuantity());
            ps.setInt(4, entity.getUnitPrice());
            ps.setInt(5, entity.getTotalPrice());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi thêm chi tiết tiêm chủng vào hóa đơn", ex);
        }
    }

    @Override
    public List<InvoiceVaccinationDetailListDto> findVaccinationDetailsByInvoiceId(int invoiceId) throws PetcareException {
        String query = "SELECT v.vaccine_name, ivd.quantity, ivd.unit_price, ivd.total_price " +
                "FROM invoice_vaccination_details ivd " +
                "INNER JOIN vaccines v ON ivd.vaccine_id = v.vaccine_id " +
                "WHERE ivd.invoice_id = ?";
        List<InvoiceVaccinationDetailListDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceVaccinationDetailListDto dto = new InvoiceVaccinationDetailListDto();
                    dto.setVaccineName(rs.getString("vaccine_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getInt("unit_price"));
                    dto.setTotalPrice(rs.getInt("total_price"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải chi tiết tiêm chủng hóa đơn", ex);
        }
        return list;
    }

    @Override
    public void deleteVaccinationDetailsByInvoiceId(int invoiceId) throws PetcareException {
        String query = "DELETE FROM invoice_vaccination_details WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa chi tiết tiêm chủng hóa đơn", ex);
        }
    }

    @Override
    public void deleteMedicineDetailsByInvoiceId(int invoiceId) throws PetcareException {
        String query = "DELETE FROM invoice_medicine_details WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa chi tiết thuốc hóa đơn", ex);
        }
    }

    @Override
    public void deleteDetailsByInvoiceId(int invoiceId) throws PetcareException {
        String query = "DELETE FROM invoice_details WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa chi tiết hóa đơn", ex);
        }
    }

    @Override
    public int delete(int invoiceId) throws PetcareException {
        deleteVaccinationDetailsByInvoiceId(invoiceId);
        deleteMedicineDetailsByInvoiceId(invoiceId);
        deleteDetailsByInvoiceId(invoiceId);
        String query = "DELETE FROM invoices WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, invoiceId);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi xóa hóa đơn", ex);
        }
    }

    @Override
    public long sumTotalAmountThisYear() throws PetcareException {
        String query = "SELECT SUM(total_amount) as total FROM invoices WHERE YEAR(invoice_date) = YEAR(CURRENT_DATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong("total");
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tính doanh thu năm", ex);
        }
        return 0;
    }

    @Override
    public long sumDetailTotalPriceThisYear() throws PetcareException {
        String query = "SELECT COALESCE(SUM(id.total_price), 0) as total FROM invoice_details id " +
                "INNER JOIN invoices i ON id.invoice_id = i.invoice_id " +
                "WHERE YEAR(i.invoice_date) = YEAR(CURRENT_DATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong("total");
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tính doanh thu năm theo chi tiết", ex);
        }
        return 0;
    }

    @Override
    public long sumTotalAmountByMonth(int year, int month) throws PetcareException {
        String query = "SELECT SUM(total_amount) as total FROM invoices WHERE YEAR(invoice_date) = ? AND MONTH(invoice_date) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("total");
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tính doanh thu tháng", ex);
        }
        return 0;
    }

    @Override
    public long sumDetailTotalPriceByMonth(int year, int month) throws PetcareException {
        String query = "SELECT COALESCE(SUM(id.total_price), 0) as total FROM invoice_details id " +
                "INNER JOIN invoices i ON id.invoice_id = i.invoice_id " +
                "WHERE YEAR(i.invoice_date) = ? AND MONTH(i.invoice_date) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("total");
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tính doanh thu tháng theo chi tiết", ex);
        }
        return 0;
    }

    @Override
    public List<ServiceRevenueDto> getRevenueByServiceTypeThisYear() throws PetcareException {
        List<ServiceRevenueDto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Dịch vụ (invoice_details)
            String queryServices = "SELECT st.service_name, SUM(id.total_price) as total_revenue " +
                    "FROM invoice_details id " +
                    "INNER JOIN service_types st ON id.service_type_id = st.service_type_id " +
                    "INNER JOIN invoices i ON id.invoice_id = i.invoice_id " +
                    "WHERE YEAR(i.invoice_date) = YEAR(CURRENT_DATE) " +
                    "GROUP BY st.service_type_id, st.service_name ORDER BY total_revenue DESC";
            try (PreparedStatement ps = conn.prepareStatement(queryServices);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceRevenueDto dto = new ServiceRevenueDto();
                    dto.setServiceName(rs.getString("service_name"));
                    dto.setRevenue(rs.getLong("total_revenue"));
                    list.add(dto);
                }
            }
            // Thuốc (invoice_medicine_details)
            String queryMedicine = "SELECT COALESCE(SUM(imd.total_price), 0) as total_revenue " +
                    "FROM invoice_medicine_details imd " +
                    "INNER JOIN invoices i ON imd.invoice_id = i.invoice_id " +
                    "WHERE YEAR(i.invoice_date) = YEAR(CURRENT_DATE)";
            try (PreparedStatement ps = conn.prepareStatement(queryMedicine);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long rev = rs.getLong("total_revenue");
                    if (rev > 0) {
                        ServiceRevenueDto dto = new ServiceRevenueDto();
                        dto.setServiceName("Thuốc");
                        dto.setRevenue(rev);
                        list.add(dto);
                    }
                }
            }
            // Tiêm chủng (invoice_vaccination_details)
            String queryVaccination = "SELECT COALESCE(SUM(ivd.total_price), 0) as total_revenue " +
                    "FROM invoice_vaccination_details ivd " +
                    "INNER JOIN invoices i ON ivd.invoice_id = i.invoice_id " +
                    "WHERE YEAR(i.invoice_date) = YEAR(CURRENT_DATE)";
            try (PreparedStatement ps = conn.prepareStatement(queryVaccination);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long rev = rs.getLong("total_revenue");
                    if (rev > 0) {
                        ServiceRevenueDto dto = new ServiceRevenueDto();
                        dto.setServiceName("Tiêm chủng");
                        dto.setRevenue(rev);
                        list.add(dto);
                    }
                }
            }
            // Sắp xếp theo doanh thu giảm dần để chart hiển thị đúng thứ tự
            list.sort((a, b) -> Long.compare(b.getRevenue(), a.getRevenue()));
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tải doanh thu theo loại", ex);
        }
        return list;
    }

    @Override
    public Integer findInvoiceIdByPetEnclosureId(int petEnclosureId) throws PetcareException {
        String query = "SELECT invoice_id FROM invoices WHERE pet_enclosure_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, petEnclosureId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("invoice_id");
            }
        } catch (SQLException ex) {
            throw new PetcareException("Lỗi khi tìm hóa đơn theo lưu chuồng", ex);
        }
        return null;
    }
}
