package com.petcare.util;

import com.petcare.model.domain.Customer;
import com.petcare.model.domain.GeneralSetting;
import com.petcare.model.domain.MedicalRecord;
import com.petcare.model.domain.Doctor;
import com.petcare.model.domain.Pet;
import com.petcare.model.domain.PetEnclosure;
import com.petcare.model.entity.InvoiceDetailListDto;
import com.petcare.model.entity.InvoiceEntity;
import com.petcare.model.entity.InvoiceInfoDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.CustomerService;
import com.petcare.service.DoctorService;
import com.petcare.service.GeneralSettingService;
import com.petcare.service.InvoiceService;
import com.petcare.service.MedicalRecordService;
import com.petcare.service.PetEnclosureService;
import com.petcare.service.PetService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * In hóa đơn, phiếu khám: tạo HTML và mở trong trình duyệt mặc định (Ctrl+P để in).
 */
public class PrintHelper {

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String escapeHtmlMultiline(String s) {
        if (s == null) return "";
        return escapeHtml(s).replace("\n", "<br/>");
    }

    private static String fmt(int n) {
        return String.format("%,d", n);
    }

    /**
     * Mở HTML trong trình duyệt mặc định (người dùng bấm Ctrl+P để in).
     */
    public static void openInBrowser(String html, String filename) throws IOException {
        File tmp = File.createTempFile(filename, ".html");
        tmp.deleteOnExit();
        try (PrintWriter w = new PrintWriter(tmp, StandardCharsets.UTF_8)) {
            w.print(html);
        }
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(tmp.toURI());
        }
    }

    /**
     * Tạo HTML in hóa đơn và mở trong trình duyệt.
     */
    public static void printInvoice(int invoiceId) throws PetcareException, IOException {
        InvoiceInfoDto info = InvoiceService.getInstance().getInvoiceInfo(invoiceId);
        if (info == null) throw new PetcareException("Không tìm thấy hóa đơn #" + invoiceId);
        List<InvoiceDetailListDto> details = InvoiceService.getInstance().getInvoiceDetails(invoiceId);
        GeneralSetting settings = GeneralSettingService.getInstance().getSettings();
        String clinicName = settings != null ? settings.getClinicName() : "UIT PETCARE";
        String addr1 = settings != null ? settings.getClinicAddress1() : "";
        String phone1 = settings != null ? settings.getPhoneNumber1() : "";
        String phone2 = settings != null ? settings.getPhoneNumber2() : "";
        String signingPlace = settings != null ? settings.getSigningPlace() : "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String invoiceDateStr = info.getInvoiceDate() != null ? sdf.format(info.getInvoiceDate()) : "";

        StringBuilder rows = new StringBuilder();
        int stt = 1;
        if (details != null) {
            for (InvoiceDetailListDto d : details) {
                rows.append("<tr><td class=\"number\">").append(stt++)
                        .append("</td><td>").append(escapeHtml(d.getServiceName() != null ? d.getServiceName() : "Dịch vụ"))
                        .append("</td><td class=\"number\">").append(d.getQuantity())
                        .append("</td><td class=\"number\">").append(fmt(d.getUnitPrice()))
                        .append("</td><td class=\"number\">").append(fmt(d.getTotalPrice())).append("</td></tr>");
            }
        }
        if (rows.length() == 0) {
            rows.append("<tr><td colspan=\"5\" style=\"text-align: center;\">Không có dịch vụ</td></tr>");
        }

        String discountRow = (info.getDiscount() > 0)
                ? "<tr><td colspan=\"4\" style=\"text-align: right;\">Giảm giá:</td><td class=\"number\">-" + fmt(info.getDiscount()) + " VNĐ</td></tr>"
                : "";
        String depositRow = (info.getDeposit() > 0)
                ? "<tr><td colspan=\"4\" style=\"text-align: right;\">Đã đặt cọc:</td><td class=\"number\">-" + fmt(info.getDeposit()) + " VNĐ</td></tr>"
                : "";

        String html = "<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"><title>In hóa đơn</title>" +
                "<style>*{margin:0;padding:0;box-sizing:border-box}body{font-family:'Times New Roman',Times,serif;font-size:14px;line-height:1.6;padding:20px}.print-container{max-width:800px;margin:0 auto}.header{text-align:center;margin-bottom:20px;border-bottom:2px solid #000;padding-bottom:10px}.header h1{font-size:20px;text-transform:uppercase;margin-bottom:5px}.title{text-align:center;margin:20px 0}.title h2{font-size:24px;text-transform:uppercase}.info-section{display:flex;justify-content:space-between;margin-bottom:20px}.info-left,.info-right{width:48%}.info-row{margin-bottom:5px}.info-label{font-weight:bold;min-width:120px}table{width:100%;border-collapse:collapse;margin:20px 0}th,td{border:1px solid #000;padding:8px;text-align:left}th{background:#f0f0f0;font-weight:bold}td.number{text-align:right}.total-row{font-weight:bold;font-size:16px}.signature{display:flex;justify-content:space-between;margin-top:50px}.signature-box{text-align:center;width:45%}.signature-box p{margin-bottom:60px}.no-print{margin-bottom:20px;text-align:center}.btn-print{padding:10px 30px;background:#3498db;color:#fff;border:none;cursor:pointer;font-size:16px;border-radius:5px;margin:0 5px}@media print{.no-print{display:none}body{padding:0}}</style></head><body>" +
                "<div class=\"no-print\"><button class=\"btn-print\" onclick=\"window.print()\">🖨️ In hóa đơn</button><button class=\"btn-print\" onclick=\"window.close()\" style=\"background:#95a5a6\">✕ Đóng</button></div>" +
                "<div class=\"print-container\"><div class=\"header\"><h1>" + escapeHtml(clinicName) + "</h1><p>" + escapeHtml(addr1) + "</p><p>ĐT: " + escapeHtml(phone1) + (phone2 != null && !phone2.isEmpty() ? " - " + escapeHtml(phone2) : "") + "</p></div>" +
                "<div class=\"title\"><h2>HÓA ĐƠN THANH TOÁN</h2><p>Số: " + String.format("%06d", invoiceId) + "</p></div>" +
                "<div class=\"info-section\"><div class=\"info-left\"><div class=\"info-row\"><span class=\"info-label\">Khách hàng:</span><span>" + escapeHtml(info.getCustomerName()) + "</span></div>" +
                "<div class=\"info-row\"><span class=\"info-label\">Điện thoại:</span><span>" + escapeHtml(info.getCustomerPhoneNumber()) + "</span></div></div>" +
                "<div class=\"info-right\"><div class=\"info-row\"><span class=\"info-label\">Thú cưng:</span><span>" + escapeHtml(info.getPetName()) + "</span></div>" +
                "<div class=\"info-row\"><span class=\"info-label\">Ngày lập:</span><span>" + escapeHtml(invoiceDateStr) + "</span></div></div></div>" +
                "<table><thead><tr><th style=\"width:50px\">STT</th><th>Dịch vụ</th><th style=\"width:80px\">Số lượng</th><th style=\"width:120px\">Đơn giá</th><th style=\"width:120px\">Thành tiền</th></tr></thead><tbody>" + rows + "</tbody>" +
                "<tfoot><tr><td colspan=\"4\" style=\"text-align:right\">Tổng tiền dịch vụ:</td><td class=\"number\">" + fmt(info.getSubtotal()) + " VNĐ</td></tr>" + discountRow + depositRow +
                "<tr class=\"total-row\"><td colspan=\"4\" style=\"text-align:right\">CÒN PHẢI THANH TOÁN:</td><td class=\"number\">" + fmt(info.getTotalAmount()) + " VNĐ</td></tr></tfoot></table>" +
                "<p><strong>Phương thức thanh toán:</strong> Tiền mặt</p>" +
                "<div class=\"signature\"><div class=\"signature-box\"><p>Khách hàng</p><p><em>(Ký, ghi rõ họ tên)</em></p></div>" +
                "<div class=\"signature-box\"><p>" + escapeHtml(signingPlace) + ", ngày " + new SimpleDateFormat("dd").format(new java.util.Date()) + " tháng " + new SimpleDateFormat("MM").format(new java.util.Date()) + " năm " + new SimpleDateFormat("yyyy").format(new java.util.Date()) + "</p><p>Người lập phiếu</p><p><em>(Ký, ghi rõ họ tên)</em></p></div></div></div></body></html>";

        openInBrowser(html, "invoice-" + invoiceId);
    }

    /**
     * Tạo HTML in phiếu khám và mở trong trình duyệt.
     */
    public static void printMedicalRecord(int recordId) throws PetcareException, IOException {
        MedicalRecord record = MedicalRecordService.getInstance().getRecordById(recordId);
        if (record == null) throw new PetcareException("Không tìm thấy phiếu khám #" + recordId);
        Customer customer = CustomerService.getInstance().getCustomerById(record.getCustomerId());
        Pet pet = PetService.getInstance().getPetById(record.getPetId());
        Doctor doctor = DoctorService.getInstance().getDoctorById(record.getDoctorId());
        GeneralSetting settings = GeneralSettingService.getInstance().getSettings();
        String clinicName = settings != null ? settings.getClinicName() : "UIT PETCARE";
        String addr1 = settings != null ? settings.getClinicAddress1() : "";
        String phone1 = settings != null ? settings.getPhoneNumber1() : "";
        String signingPlace = settings != null ? settings.getSigningPlace() : "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String visitDateStr = record.getMedicalRecordVisitDate() != null ? sdf.format(record.getMedicalRecordVisitDate()) : "";
        String typeStr = record.getMedicalRecordType() != null ? record.getMedicalRecordType().getLabel() : "";
        String summary = record.getMedicalRecordSummary() != null ? record.getMedicalRecordSummary() : "Không có";
        String details = record.getMedicalRecordDetails() != null ? record.getMedicalRecordDetails() : "Không có";
        String customerName = customer != null ? customer.getCustomerName() : "";
        String customerPhone = customer != null ? customer.getCustomerPhoneNumber() : "";
        String petName = pet != null ? pet.getPetName() : "";
        String petSpecies = pet != null ? pet.getPetSpecies() : "";
        String doctorName = doctor != null ? doctor.getDoctorName() : "";

        String html = "<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"><title>In phiếu khám</title>" +
                "<style>*{margin:0;padding:0;box-sizing:border-box}body{font-family:'Times New Roman',Times,serif;font-size:14px;line-height:1.6;padding:20px}.print-container{max-width:800px;margin:0 auto}.header{text-align:center;margin-bottom:20px;border-bottom:2px solid #000;padding-bottom:10px}.header h1{font-size:20px;text-transform:uppercase;margin-bottom:5px}.title{text-align:center;margin:20px 0}.title h2{font-size:24px;text-transform:uppercase}.info-section{margin-bottom:20px}.info-grid{display:grid;grid-template-columns:1fr 1fr;gap:10px}.info-row{margin-bottom:5px}.info-label{font-weight:bold;min-width:150px}.section{margin:20px 0;padding:10px;border:1px solid #ddd}.section-title{font-weight:bold;font-size:16px;margin-bottom:10px;border-bottom:1px solid #000;padding-bottom:5px}.section-content{min-height:60px}.signature{display:flex;justify-content:space-between;margin-top:50px}.signature-box{text-align:center;width:45%}.signature-box p{margin-bottom:60px}.no-print{margin-bottom:20px;text-align:center}.btn-print{padding:10px 30px;background:#3498db;color:#fff;border:none;cursor:pointer;font-size:16px;border-radius:5px;margin:0 5px}@media print{.no-print{display:none}body{padding:0}}</style></head><body>" +
                "<div class=\"no-print\"><button class=\"btn-print\" onclick=\"window.print()\">🖨️ In phiếu khám</button><button class=\"btn-print\" onclick=\"window.close()\" style=\"background:#95a5a6\">✕ Đóng</button></div>" +
                "<div class=\"print-container\"><div class=\"header\"><h1>" + escapeHtml(clinicName) + "</h1><p>" + escapeHtml(addr1) + "</p><p>ĐT: " + escapeHtml(phone1) + "</p></div>" +
                "<div class=\"title\"><h2>PHIẾU KHÁM BỆNH</h2><p>Số: " + String.format("%06d", recordId) + "</p></div>" +
                "<div class=\"info-section\"><div class=\"info-grid\"><div><div class=\"info-row\"><span class=\"info-label\">Chủ thú cưng:</span><span>" + escapeHtml(customerName) + "</span></div><div class=\"info-row\"><span class=\"info-label\">Điện thoại:</span><span>" + escapeHtml(customerPhone) + "</span></div></div>" +
                "<div><div class=\"info-row\"><span class=\"info-label\">Tên thú cưng:</span><span>" + escapeHtml(petName) + "</span></div><div class=\"info-row\"><span class=\"info-label\">Loài / Giống:</span><span>" + escapeHtml(petSpecies) + "</span></div></div></div>" +
                "<div class=\"info-grid\" style=\"margin-top:10px\"><div class=\"info-row\"><span class=\"info-label\">Ngày khám:</span><span>" + escapeHtml(visitDateStr) + "</span></div><div class=\"info-row\"><span class=\"info-label\">Bác sĩ khám:</span><span>" + escapeHtml(doctorName) + "</span></div><div class=\"info-row\"><span class=\"info-label\">Loại khám:</span><span>" + escapeHtml(typeStr) + "</span></div></div></div>" +
                "<div class=\"section\"><div class=\"section-title\">Tóm tắt</div><div class=\"section-content\">" + escapeHtmlMultiline(summary) + "</div></div>" +
                "<div class=\"section\"><div class=\"section-title\">Chi tiết khám / Điều trị</div><div class=\"section-content\">" + escapeHtmlMultiline(details) + "</div></div>" +
                "<div class=\"signature\"><div class=\"signature-box\"><p>Khách hàng</p><p><em>(Ký, ghi rõ họ tên)</em></p></div>" +
                "<div class=\"signature-box\"><p>" + escapeHtml(signingPlace) + ", ngày " + new SimpleDateFormat("dd").format(new java.util.Date()) + " tháng " + new SimpleDateFormat("MM").format(new java.util.Date()) + " năm " + new SimpleDateFormat("yyyy").format(new java.util.Date()) + "</p><p>Bác sĩ khám</p><p><em>(Ký, ghi rõ họ tên)</em></p></div></div></div></body></html>";

        openInBrowser(html, "medical-record-" + recordId);
    }

    /**
     * Tạo HTML Giấy cam kết lưu chuồng (chỉ nội dung, không wrapper) để hiển thị trong panel hoặc in.
     * Trả về null nếu hóa đơn không gắn lưu chuồng.
     */
    public static String buildCommitmentHtml(int invoiceId) throws PetcareException {
        InvoiceEntity invoice = InvoiceService.getInstance().getInvoiceEntity(invoiceId);
        if (invoice == null) return "<p>Không tìm thấy hóa đơn.</p>";
        Integer peId = invoice.getPetEnclosureId();
        if (peId == null || peId <= 0) {
            return "<p>Hóa đơn này không gắn lưu chuồng. Không thể tạo giấy cam kết.</p>";
        }
        Customer customer = CustomerService.getInstance().getCustomerById(invoice.getCustomerId());
        Pet pet = PetService.getInstance().getPetById(invoice.getPetId());
        PetEnclosure enclosure = PetEnclosureService.getInstance().getEnclosureById(peId);
        GeneralSetting settings = GeneralSettingService.getInstance().getSettings();
        if (enclosure == null) return "<p>Không tìm thấy thông tin lưu chuồng.</p>";

        String clinicName = settings != null ? settings.getClinicName() : "UIT PETCARE";
        String addr1 = settings != null ? settings.getClinicAddress1() : "";
        String phone1 = settings != null ? settings.getPhoneNumber1() : "";
        String phone2 = settings != null ? settings.getPhoneNumber2() : "";
        String representativeName = settings != null ? settings.getRepresentativeName() : "";
        String signingPlace = settings != null ? settings.getSigningPlace() : "";
        int overtimeFee = settings != null ? settings.getOvertimeFeePerHour() : 0;

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String invoiceDateStr = invoice.getInvoiceDate() != null ? sdfDate.format(invoice.getInvoiceDate()) : "";
        String checkInStr = enclosure.getCheckInDate() != null ? sdfDateTime.format(enclosure.getCheckInDate()) : "-";
        String checkOutStr = enclosure.getCheckOutDate() != null ? sdfDateTime.format(enclosure.getCheckOutDate()) : "-";
        String note = enclosure.getPetEnclosureNote() != null && !enclosure.getPetEnclosureNote().isEmpty() ? enclosure.getPetEnclosureNote() : "Không có";

        String customerName = customer != null ? customer.getCustomerName() : "";
        String identityCard = customer != null ? (customer.getCustomerIdentityCard() != null ? customer.getCustomerIdentityCard() : "-") : "-";
        String customerPhone = customer != null ? (customer.getCustomerPhoneNumber() != null ? customer.getCustomerPhoneNumber() : "") : "";
        String customerAddr = customer != null ? (customer.getCustomerAddress() != null ? customer.getCustomerAddress() : "-") : "-";

        String petName = pet != null ? pet.getPetName() : "";
        String petSpecies = pet != null && pet.getPetSpecies() != null ? pet.getPetSpecies() : "-";
        String petGender = (pet != null && "1".equals(pet.getPetGender())) ? "Cái" : "Đực";
        String petDob = (pet != null && pet.getPetDob() != null) ? sdfDate.format(pet.getPetDob()) : "-";
        String petWeight = (pet != null && pet.getPetWeight() != null) ? pet.getPetWeight() + "kg" : "-";
        String petSterilization = (pet != null && "1".equals(pet.getPetSterilization())) ? "Có" : "Không";
        String petChar = pet != null ? pet.getPetCharacteristic() : null;
        String petAllergy = pet != null ? pet.getPetDrugAllergy() : null;
        String petCharAllergy = "";
        if ((petChar != null && !petChar.isEmpty()) || (petAllergy != null && !petAllergy.isEmpty())) {
            petCharAllergy = (petChar != null ? petChar : "") + ((petChar != null && !petChar.isEmpty()) && (petAllergy != null && !petAllergy.isEmpty()) ? " • " : "") + (petAllergy != null ? petAllergy : "");
        } else {
            petCharAllergy = "-";
        }

        java.util.Date now = new java.util.Date();
        String todayD = new SimpleDateFormat("dd").format(now);
        String todayM = new SimpleDateFormat("MM").format(now);
        String todayY = new SimpleDateFormat("yyyy").format(now);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"commitment-sheet\">");
        sb.append("<div style=\"text-align: center; margin-bottom: 20px;\">");
        sb.append("<h2 style=\"font-size: 20px; text-transform: uppercase; margin-bottom: 5px;\">GIẤY CAM KẾT LƯU CHUỒNG</h2>");
        sb.append("<div style=\"font-weight: bold; font-size: 18px;\">").append(escapeHtml(clinicName)).append("</div>");
        sb.append("<div style=\"font-size: 13px;\">Đ/c: ").append(escapeHtml(addr1)).append(" • ĐT: ").append(escapeHtml(phone1));
        if (phone2 != null && !phone2.isEmpty()) sb.append(", ").append(escapeHtml(phone2));
        sb.append("</div><div style=\"margin-top: 10px;\">Ngày ").append(escapeHtml(invoiceDateStr)).append("</div></div>");
        sb.append("<ol style=\"padding-left: 20px; line-height: 1.8;\">");
        sb.append("<li style=\"margin-bottom: 15px;\"><div style=\"font-weight: bold; text-decoration: underline;\">THÔNG TIN CÁC BÊN</div>");
        sb.append("<div>- Bên A (Phòng khám): ").append(escapeHtml(clinicName)).append(" • Người đại diện: ").append(escapeHtml(representativeName)).append("</div>");
        sb.append("<div>- Bên B (Chủ nuôi): ").append(escapeHtml(customerName)).append(" • CCCD: ").append(escapeHtml(identityCard)).append(" • SĐT: ").append(escapeHtml(customerPhone)).append(" • Đ/c: ").append(escapeHtml(customerAddr)).append("</div></li>");
        sb.append("<li style=\"margin-bottom: 15px;\"><div style=\"font-weight: bold; text-decoration: underline;\">THÔNG TIN THÚ CƯNG</div>");
        sb.append("<div>- Tên thú cưng: ").append(escapeHtml(petName)).append(" • Loài/giống: ").append(escapeHtml(petSpecies)).append(" • Giới tính: ").append(petGender).append("</div>");
        sb.append("<div>- Tuổi/ngày sinh: ").append(escapeHtml(petDob)).append(" • Cân nặng: ").append(escapeHtml(petWeight)).append(" • Đã triệt sản: ").append(petSterilization).append("</div>");
        sb.append("<div>- Đặc điểm/dị ứng: ").append(escapeHtml(petCharAllergy)).append("</div></li>");
        sb.append("<li style=\"margin-bottom: 15px;\"><div style=\"font-weight: bold; text-decoration: underline;\">THỜI GIAN LƯU CHUỒNG & DỊCH VỤ</div>");
        sb.append("<div>- Thời gian: từ ").append(escapeHtml(checkInStr)).append(" đến ").append(escapeHtml(checkOutStr)).append("</div>");
        sb.append("<div>- Ghi chú (dịch vụ, đồ gửi kèm...): ").append(escapeHtml(note)).append("</div></li>");
        sb.append("<li style=\"margin-bottom: 15px;\"><div style=\"font-weight: bold; text-decoration: underline;\">XỬ LÝ TÌNH HUỐNG KHẨN CẤP</div>");
        sb.append("<div>- Khi thú cưng nguy cấp, Bên A ưu tiên liên hệ Bên B. Nếu không được, Bên A được quyền cấp cứu kịp thời.</div>");
        sb.append("<div>- Giới hạn chi phí cấp cứu được phép: ").append(fmt(enclosure.getEmergencyLimit())).append(" đ.</div></li>");
        sb.append("<li style=\"margin-bottom: 15px;\"><div style=\"font-weight: bold; text-decoration: underline;\">PHÍ & THANH TOÁN</div>");
        sb.append("<div>- Đơn giá: ").append(fmt(enclosure.getDailyRate())).append(" đ/ngày. Phí phát sinh theo bảng giá/thỏa thuận.</div>");
        sb.append("<div>- Đã cọc: ").append(fmt(enclosure.getDeposit())).append(" đ. Thanh toán đủ khi nhận thú cưng.</div>");
        sb.append("<div>- Nhận trễ giờ quy định có thể phụ thu: ").append(fmt(overtimeFee)).append(" đ/giờ.</div></li></ol>");
        sb.append("<div style=\"font-style: italic; margin: 20px 0;\">* Bên B đã đọc, hiểu và đồng ý với các điều khoản về rủi ro, hành vi, an toàn... của phòng khám.</div>");
        sb.append("<table style=\"width: 100%; margin-top: 30px; text-align: center;\"><tr>");
        sb.append("<td style=\"width: 50%; padding: 10px;\"><strong>BÊN A (Phòng khám)</strong><br>(Ký, ghi rõ họ tên)<br><br><br><br></td>");
        sb.append("<td style=\"width: 50%; padding: 10px;\"><strong>BÊN B (Chủ nuôi)</strong><br>(Ký, ghi rõ họ tên)<br><br><br><br></td></tr></table>");
        sb.append("<div style=\"text-align: right; margin-top: 20px; font-style: italic;\">").append(escapeHtml(signingPlace)).append(", ngày ").append(todayD).append(" tháng ").append(todayM).append(" năm ").append(todayY).append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Tạo HTML mẫu Hóa đơn lưu chuồng (chỉ nội dung) theo load_invoice.php.
     */
    public static String buildInvoiceTemplateHtml(int invoiceId) throws PetcareException {
        InvoiceInfoDto info = InvoiceService.getInstance().getInvoiceInfo(invoiceId);
        if (info == null) return "<p>Không tìm thấy hóa đơn.</p>";
        List<InvoiceDetailListDto> details = InvoiceService.getInstance().getInvoiceDetails(invoiceId);
        GeneralSetting settings = GeneralSettingService.getInstance().getSettings();
        String clinicName = settings != null ? settings.getClinicName() : "UIT PETCARE";
        String addr1 = settings != null ? settings.getClinicAddress1() : "";
        String phone1 = settings != null ? settings.getPhoneNumber1() : "";
        String phone2 = settings != null ? settings.getPhoneNumber2() : "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String invoiceDateStr = info.getInvoiceDate() != null ? sdf.format(info.getInvoiceDate()) : "";

        StringBuilder rows = new StringBuilder();
        int stt = 1;
        if (details != null) {
            for (InvoiceDetailListDto d : details) {
                rows.append("<tr><td style=\"border: 1px solid #ddd; padding: 8px; text-align: center;\">").append(stt++)
                        .append("</td><td style=\"border: 1px solid #ddd; padding: 8px;\">").append(escapeHtml(d.getServiceName() != null ? d.getServiceName() : "Dịch vụ"))
                        .append("</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: center;\">").append(d.getQuantity())
                        .append("</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">").append(fmt(d.getUnitPrice()))
                        .append("</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">").append(fmt(d.getTotalPrice())).append("</td></tr>");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"invoice-sheet\">");
        sb.append("<div style=\"text-align: center; margin-bottom: 20px;\">");
        sb.append("<h2 style=\"font-size: 20px; text-transform: uppercase; margin-bottom: 5px;\">HÓA ĐƠN LƯU CHUỒNG</h2>");
        sb.append("<div style=\"font-weight: bold; font-size: 18px;\">").append(escapeHtml(clinicName)).append("</div>");
        sb.append("<div style=\"font-size: 13px;\">Đ/c: ").append(escapeHtml(addr1)).append(" • ĐT: ").append(escapeHtml(phone1));
        if (phone2 != null && !phone2.isEmpty()) sb.append(", ").append(escapeHtml(phone2));
        sb.append("</div></div>");
        sb.append("<div style=\"margin-bottom: 15px;\">");
        sb.append("<div>Mã HĐ: <strong>").append(invoiceId).append("</strong> • Ngày: <span>").append(escapeHtml(invoiceDateStr)).append("</span></div>");
        sb.append("<div>Khách: <span>").append(escapeHtml(info.getCustomerName())).append("</span> • SĐT: <span>").append(escapeHtml(info.getCustomerPhoneNumber())).append("</span></div>");
        InvoiceEntity invEntity = InvoiceService.getInstance().getInvoiceEntity(invoiceId);
        String petSpecies = "-";
        if (invEntity != null) {
            Pet p = PetService.getInstance().getPetById(invEntity.getPetId());
            if (p != null && p.getPetSpecies() != null && !p.getPetSpecies().isEmpty()) petSpecies = p.getPetSpecies();
        }
        sb.append("<div>Thú cưng: <span>").append(escapeHtml(info.getPetName())).append("</span> • Loài/Giống: <span>").append(escapeHtml(petSpecies)).append("</span></div></div>");
        sb.append("<table style=\"width: 100%; border-collapse: collapse; margin-bottom: 20px;\">");
        sb.append("<thead><tr style=\"background: #f5f5f5;\">");
        sb.append("<th style=\"border: 1px solid #ddd; padding: 8px; text-align: center;\">STT</th>");
        sb.append("<th style=\"border: 1px solid #ddd; padding: 8px; text-align: left;\">Tên dịch vụ / Sản phẩm</th>");
        sb.append("<th style=\"border: 1px solid #ddd; padding: 8px; text-align: center;\">SL</th>");
        sb.append("<th style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">Đơn giá</th>");
        sb.append("<th style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">Thành tiền</th></tr></thead><tbody>").append(rows).append("</tbody>");
        sb.append("<tfoot><tr><td colspan=\"4\" style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">Tạm tính</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">").append(fmt(info.getSubtotal())).append("</td></tr>");
        sb.append("<tr><td colspan=\"4\" style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">Cọc</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">").append(fmt(info.getDeposit())).append("</td></tr>");
        sb.append("<tr><td colspan=\"4\" style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">Giảm giá</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">").append(fmt(info.getDiscount())).append("</td></tr>");
        sb.append("<tr style=\"font-weight: bold; background: #f9f9f9;\"><td colspan=\"4\" style=\"border: 1px solid #ddd; padding: 8px; text-align: right;\">Tổng thanh toán</td><td style=\"border: 1px solid #ddd; padding: 8px; text-align: right; color: #dc3545;\">").append(fmt(info.getTotalAmount())).append(" đ</td></tr></tfoot></table>");
        sb.append("<div style=\"text-align: center; font-style: italic; margin-top: 20px;\">Cảm ơn Quý khách!</div></div>");
        return sb.toString();
    }
}
