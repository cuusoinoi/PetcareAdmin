package com.petcare.gui.panels;

import com.petcare.model.entity.PetEnclosureListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.InvoiceService;
import com.petcare.service.PetEnclosureService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.PrintHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PrintingTemplatePanel extends JPanel {
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JPanel filterPanel;
    private JComboBox<PetEnclosureListDto> enclosureCombo;
    private JButton btnCommit;
    private JButton btnInvoice;
    private JButton btnPrint;
    private JScrollPane scrollPane;
    private JEditorPane previewPane;
    private final PetEnclosureService enclosureService = PetEnclosureService.getInstance();
    private final InvoiceService invoiceService = InvoiceService.getInstance();
    private static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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

        JLabel lblEnclosure = new JLabel("Chọn lưu chuồng:");
        lblEnclosure.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEnclosure.setForeground(ThemeManager.getTitleForeground());
        filterPanel.add(lblEnclosure);

        enclosureCombo = new JComboBox<>();
        enclosureCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        enclosureCombo.setPreferredSize(new java.awt.Dimension(380, 28));
        enclosureCombo.setBackground(ThemeManager.getTextFieldBackground());
        enclosureCombo.setForeground(ThemeManager.getTableForeground());
        enclosureCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel();
            l.setFont(enclosureCombo.getFont());
            l.setOpaque(true);
            l.setBackground(isSelected ? ThemeManager.getButtonBackground() : ThemeManager.getTextFieldBackground());
            l.setForeground(isSelected ? ThemeManager.getButtonForeground() : ThemeManager.getTableForeground());
            if (value != null) {
                int num = value.getPetEnclosureNumber();
                String petName = value.getPetName() != null ? value.getPetName() : "";
                String custName = value.getCustomerName() != null ? value.getCustomerName() : "";
                String dateStr = value.getCheckInDate() != null ? SDF_DATE_TIME.format(value.getCheckInDate()) : "";
                l.setText("Chuồng " + num + " - " + petName + " (" + custName + ") - " + dateStr);
            } else {
                l.setText("-- Chọn lưu chuồng --");
            }
            return l;
        });
        filterPanel.add(enclosureCombo);

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
        PetEnclosureListDto dto = (PetEnclosureListDto) enclosureCombo.getSelectedItem();
        if (dto == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String html = PrintHelper.buildCommitmentHtmlByEnclosureId(dto.getPetEnclosureId());
            lastPreviewFragment = html;
            previewPane.setText(wrapHtml(html));
            previewPane.setCaretPosition(0);
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInvoiceTemplate() {
        PetEnclosureListDto dto = (PetEnclosureListDto) enclosureCombo.getSelectedItem();
        if (dto == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Integer invoiceId = invoiceService.getInvoiceIdByPetEnclosureId(dto.getPetEnclosureId());
            if (invoiceId == null) {
                JOptionPane.showMessageDialog(this, "Lưu chuồng này chưa có hóa đơn. Vui lòng xuất hóa đơn từ màn Thanh toán / Check-out trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String html = PrintHelper.buildInvoiceTemplateHtml(invoiceId);
            lastPreviewFragment = html;
            previewPane.setText(wrapHtml(html));
            previewPane.setCaretPosition(0);
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printCurrentPage() {
        String html = previewPane.getText();
        if (html == null || html.trim().isEmpty() || html.contains("Chọn lưu chuồng và bấm")) {
            JOptionPane.showMessageDialog(this, "Chưa có nội dung để in! Hãy xem Giấy cam kết hoặc Hóa đơn trước.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PrintHelper.openInBrowser(html, "print-preview");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở trình duyệt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String wrapHtml(String bodyContent) {
        boolean dark = ThemeManager.isDarkMode();
        String bgHex = dark ? "#2d2d2d" : "#ffffff";
        String fgHex = dark ? "#e0e0e0" : "#333333";
        String borderHex = dark ? "#555555" : "#dddddd";
        String thBgHex = dark ? "#3d3d3d" : "#f5f5f5";
        String trAltHex = dark ? "#383838" : "#f9f9f9";
        String totalColorHex = dark ? "#ff8a80" : "#dc3545";
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
            inner = "<p style='margin:0'>Chọn lưu chuồng và bấm \"Xem Giấy cam kết\" hoặc \"Xem Hóa đơn\".</p>";
        }
        return "<html><head><meta charset=\"UTF-8\">" + styleBlock + "</head><body style='font-family: Times New Roman, serif; padding: 20px; background: " + bgHex + "; color: " + fgHex + ";'>" + inner + "</body></html>";
    }

    public void refreshData() {
        try {
            List<PetEnclosureListDto> list = enclosureService.getEnclosuresForList();
            PetEnclosureListDto selected = (PetEnclosureListDto) enclosureCombo.getSelectedItem();
            enclosureCombo.removeAllItems();
            for (PetEnclosureListDto dto : list) {
                enclosureCombo.addItem(dto);
            }
            if (selected != null && list.contains(selected)) {
                enclosureCombo.setSelectedItem(selected);
            } else if (enclosureCombo.getItemCount() > 0) {
                enclosureCombo.setSelectedIndex(0);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách lưu chuồng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        if (enclosureCombo != null) {
            enclosureCombo.setBackground(ThemeManager.getTextFieldBackground());
            enclosureCombo.setForeground(ThemeManager.getTableForeground());
            enclosureCombo.repaint();
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
