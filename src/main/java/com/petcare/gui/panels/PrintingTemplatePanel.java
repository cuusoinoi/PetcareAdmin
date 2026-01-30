package com.petcare.gui.panels;

import com.petcare.model.entity.InvoiceListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.InvoiceService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.PrintHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel Mẫu in lưu chuồng: chọn hóa đơn, xem Giấy cam kết / Hóa đơn lưu chuồng, in trang hiện tại.
 */
public class PrintingTemplatePanel extends JPanel {
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JPanel filterPanel;
    private JComboBox<InvoiceListDto> invoiceCombo;
    private JButton btnCommit;
    private JButton btnInvoice;
    private JButton btnPrint;
    private JScrollPane scrollPane;
    private JEditorPane previewPane;
    private final InvoiceService invoiceService = InvoiceService.getInstance();
    private static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    /**
     * Fragment HTML đang xem (để áp dụng lại theme khi đổi sáng/tối).
     */
    private String lastPreviewFragment;

    public PrintingTemplatePanel() {
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getContentBackground());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.getHeaderBackground());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        titleLabel = new JLabel("Mẫu in lưu chuồng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        filterPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(ThemeManager.getFormBackground());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblInvoice = new JLabel("Chọn hóa đơn:");
        lblInvoice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInvoice.setForeground(ThemeManager.getTitleForeground());
        filterPanel.add(lblInvoice);

        invoiceCombo = new JComboBox<>();
        invoiceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        invoiceCombo.setPreferredSize(new java.awt.Dimension(380, 28));
        invoiceCombo.setBackground(ThemeManager.getTextFieldBackground());
        invoiceCombo.setForeground(ThemeManager.getTableForeground());
        invoiceCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel();
            l.setFont(invoiceCombo.getFont());
            l.setOpaque(true);
            l.setBackground(isSelected ? ThemeManager.getButtonBackground() : ThemeManager.getTextFieldBackground());
            l.setForeground(isSelected ? ThemeManager.getButtonForeground() : ThemeManager.getTableForeground());
            if (value != null) {
                String petName = value.getPetName() != null ? value.getPetName() : "";
                String custName = value.getCustomerName() != null ? value.getCustomerName() : "";
                String dateStr = value.getInvoiceDate() != null ? SDF_DATE_TIME.format(value.getInvoiceDate()) : "";
                l.setText(petName + " (" + custName + ") - " + dateStr);
            } else {
                l.setText("-- Chọn hóa đơn --");
            }
            return l;
        });
        filterPanel.add(invoiceCombo);

        Color iconColor = ThemeManager.getIconColor();
        btnCommit = new JButton("Xem Giấy cam kết");
        btnCommit.setIcon(EmojiFontHelper.createEmojiIcon("📄", iconColor));
        btnCommit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCommit.setBackground(ThemeManager.getButtonBackground());
        btnCommit.setForeground(ThemeManager.getButtonForeground());
        btnCommit.addActionListener(e -> showCommitment());
        filterPanel.add(btnCommit);

        btnInvoice = new JButton("Xem Hóa đơn");
        btnInvoice.setIcon(EmojiFontHelper.createEmojiIcon("🧾", iconColor));
        btnInvoice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnInvoice.setBackground(ThemeManager.getButtonBackground());
        btnInvoice.setForeground(ThemeManager.getButtonForeground());
        btnInvoice.addActionListener(e -> showInvoiceTemplate());
        filterPanel.add(btnInvoice);

        btnPrint = new JButton("In Trang này");
        btnPrint.setIcon(EmojiFontHelper.createEmojiIcon("🖨️", iconColor));
        btnPrint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnPrint.setBackground(ThemeManager.getButtonBackground());
        btnPrint.setForeground(ThemeManager.getButtonForeground());
        btnPrint.addActionListener(e -> printCurrentPage());
        filterPanel.add(btnPrint);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ThemeManager.getFormBackground());
        topBar.add(filterPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        previewPane = new JEditorPane();
        previewPane.setContentType("text/html");
        previewPane.setEditable(false);
        previewPane.setBackground(ThemeManager.getContentBackground());
        previewPane.setForeground(ThemeManager.getTableForeground());
        previewPane.setText(wrapHtml(""));

        scrollPane = new JScrollPane(previewPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        scrollPane.setBackground(ThemeManager.getContentBackground());
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setBackground(ThemeManager.getContentBackground());
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getVerticalScrollBar().setBackground(ThemeManager.getContentBackground());
        scrollPane.getVerticalScrollBar().setOpaque(true);
        scrollPane.getHorizontalScrollBar().setBackground(ThemeManager.getContentBackground());
        scrollPane.getHorizontalScrollBar().setOpaque(true);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showCommitment() {
        InvoiceListDto dto = (InvoiceListDto) invoiceCombo.getSelectedItem();
        if (dto == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String html = PrintHelper.buildCommitmentHtml(dto.getInvoiceId());
            lastPreviewFragment = html;
            previewPane.setText(wrapHtml(html));
            previewPane.setCaretPosition(0);
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInvoiceTemplate() {
        InvoiceListDto dto = (InvoiceListDto) invoiceCombo.getSelectedItem();
        if (dto == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String html = PrintHelper.buildInvoiceTemplateHtml(dto.getInvoiceId());
            lastPreviewFragment = html;
            previewPane.setText(wrapHtml(html));
            previewPane.setCaretPosition(0);
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printCurrentPage() {
        String html = previewPane.getText();
        if (html == null || html.trim().isEmpty() || html.contains("Chọn hóa đơn và bấm")) {
            JOptionPane.showMessageDialog(this, "Chưa có nội dung để in! Hãy xem Giấy cam kết hoặc Hóa đơn trước.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PrintHelper.openInBrowser(html, "print-preview");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở trình duyệt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Bọc nội dung HTML và áp dụng theme (sáng/tối) cho vùng xem trước.
     * Luôn dùng bản gốc bodyContent: khi tối chỉ thay màu bản sao để hiển thị, không lưu bản đã thay.
     * bodyContent null hoặc rỗng = hiển thị hướng dẫn.
     */
    private String wrapHtml(String bodyContent) {
        boolean dark = ThemeManager.isDarkMode();
        String bgHex = dark ? "#2d2d2d" : "#ffffff";
        String fgHex = dark ? "#e0e0e0" : "#333333";
        String borderHex = dark ? "#555555" : "#dddddd";
        String thBgHex = dark ? "#3d3d3d" : "#f5f5f5";
        String trAltHex = dark ? "#383838" : "#f9f9f9";
        String totalColorHex = dark ? "#ff8a80" : "#dc3545";
        // Cả sáng và tối đều có style block để JEditorPane luôn áp theme mới, tránh cache cũ
        String styleBlock = "<style type=\"text/css\">"
                + "body { background: " + bgHex + " !important; color: " + fgHex + " !important; }"
                + "div.invoice-sheet, div.commitment-sheet { background: " + bgHex + " !important; color: " + fgHex + " !important; }"
                + "table { border-color: " + borderHex + " !important; }"
                + "thead th, table th, th, td { background: " + thBgHex + " !important; color: " + fgHex + " !important; border-color: " + borderHex + " !important; }"
                + "tbody td { background: " + bgHex + " !important; color: " + fgHex + " !important; }"
                + "tr[style*='background'] td { background: " + trAltHex + " !important; color: " + fgHex + " !important; }"
                + "td[style*='color: #dc3545'], td[style*='color:#dc3545'], td[style*='color: #ff8a80'], td[style*='color:#ff8a80'] { color: " + totalColorHex + " !important; }"
                + "</style>";
        String inner;
        if (bodyContent != null && !bodyContent.isEmpty()) {
            // Chế độ tối: thay màu inline trên bản sao để hiển thị (JEditorPane có thể bỏ qua CSS)
            // Chế độ sáng: dùng nguyên bản (màu trắng/xám sáng). Không lưu bản đã thay vào lastPreviewFragment.
            if (dark) {
                inner = bodyContent
                        .replace("background: #f5f5f5", "background: #3d3d3d")
                        .replace("background:#f5f5f5", "background:#3d3d3d")
                        .replace("background: #f9f9f9", "background: #383838")
                        .replace("background:#f9f9f9", "background:#383838")
                        .replace("border: 1px solid #ddd", "border: 1px solid #555555")
                        .replace("border:1px solid #ddd", "border:1px solid #555555")
                        .replace("color: #dc3545", "color: #ff8a80")
                        .replace("color:#dc3545", "color:#ff8a80");
            } else {
                // Đảm bảo chế độ sáng: đưa lại màu gốc nếu fragment đã từng bị thay (ví dụ do lỗi)
                inner = bodyContent
                        .replace("background: #3d3d3d", "background: #f5f5f5")
                        .replace("background:#3d3d3d", "background:#f5f5f5")
                        .replace("background: #383838", "background: #f9f9f9")
                        .replace("background:#383838", "background:#f9f9f9")
                        .replace("border: 1px solid #555555", "border: 1px solid #ddd")
                        .replace("border:1px solid #555555", "border:1px solid #ddd")
                        .replace("color: #ff8a80", "color: #dc3545")
                        .replace("color:#ff8a80", "color:#dc3545");
            }
        } else {
            inner = "<p style='margin:0'>Chọn hóa đơn và bấm \"Xem Giấy cam kết\" hoặc \"Xem Hóa đơn\".</p>";
        }
        return "<html><head><meta charset=\"UTF-8\">" + styleBlock + "</head><body style='font-family: Times New Roman, serif; padding: 20px; background: " + bgHex + "; color: " + fgHex + ";'>" + inner + "</body></html>";
    }

    public void refreshData() {
        try {
            List<InvoiceListDto> list = invoiceService.getInvoicesForList();
            InvoiceListDto selected = (InvoiceListDto) invoiceCombo.getSelectedItem();
            invoiceCombo.removeAllItems();
            for (InvoiceListDto dto : list) {
                invoiceCombo.addItem(dto);
            }
            if (selected != null && list.contains(selected)) {
                invoiceCombo.setSelectedItem(selected);
            } else if (invoiceCombo.getItemCount() > 0) {
                invoiceCombo.setSelectedIndex(0);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        if (headerPanel != null) {
            headerPanel.setBackground(ThemeManager.getHeaderBackground());
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
        }
        if (titleLabel != null) titleLabel.setForeground(ThemeManager.getTitleForeground());
        if (filterPanel != null) filterPanel.setBackground(ThemeManager.getFormBackground());
        if (invoiceCombo != null) {
            invoiceCombo.setBackground(ThemeManager.getTextFieldBackground());
            invoiceCombo.setForeground(ThemeManager.getTableForeground());
            invoiceCombo.repaint();
        }
        Color iconColor = ThemeManager.getIconColor();
        if (btnCommit != null) {
            btnCommit.setBackground(ThemeManager.getButtonBackground());
            btnCommit.setForeground(ThemeManager.getButtonForeground());
            btnCommit.setIcon(EmojiFontHelper.createEmojiIcon("📄", iconColor));
        }
        if (btnInvoice != null) {
            btnInvoice.setBackground(ThemeManager.getButtonBackground());
            btnInvoice.setForeground(ThemeManager.getButtonForeground());
            btnInvoice.setIcon(EmojiFontHelper.createEmojiIcon("🧾", iconColor));
        }
        if (btnPrint != null) {
            btnPrint.setBackground(ThemeManager.getButtonBackground());
            btnPrint.setForeground(ThemeManager.getButtonForeground());
            btnPrint.setIcon(EmojiFontHelper.createEmojiIcon("🖨️", iconColor));
        }
        if (previewPane != null) {
            previewPane.setBackground(ThemeManager.getContentBackground());
            previewPane.setForeground(ThemeManager.getTableForeground());
        }
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
            scrollPane.setBackground(ThemeManager.getContentBackground());
            scrollPane.setOpaque(true);
            scrollPane.getViewport().setBackground(ThemeManager.getContentBackground());
            scrollPane.getViewport().setOpaque(true);
            scrollPane.getVerticalScrollBar().setBackground(ThemeManager.getContentBackground());
            scrollPane.getVerticalScrollBar().setOpaque(true);
            scrollPane.getHorizontalScrollBar().setBackground(ThemeManager.getContentBackground());
            scrollPane.getHorizontalScrollBar().setOpaque(true);
        }
        // Áp lại nội dung xem trước theo theme mới (cả placeholder "Chọn hóa đơn và bấm..." và nội dung đã xem)
        if (previewPane != null) {
            previewPane.setText("");
            previewPane.setContentType("text/html");
            previewPane.setText(wrapHtml(lastPreviewFragment != null ? lastPreviewFragment : ""));
            previewPane.setCaretPosition(0);
            previewPane.revalidate();
            previewPane.repaint();
        }
    }
}
