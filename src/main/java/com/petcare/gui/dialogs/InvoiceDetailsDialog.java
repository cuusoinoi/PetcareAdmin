package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.entity.InvoiceDetailListDto;
import com.petcare.model.entity.InvoiceInfoDto;
import com.petcare.model.entity.InvoiceMedicineDetailListDto;
import com.petcare.model.entity.InvoiceVaccinationDetailListDto;
import com.petcare.service.InvoiceService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.PrintHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Dialog for viewing invoice details
 */
public class InvoiceDetailsDialog extends JDialog {
    private final int invoiceId;

    public InvoiceDetailsDialog(JDialog parent, int invoiceId) {
        super(parent, true);
        this.invoiceId = invoiceId;
        initComponents(invoiceId);
    }

    private void initComponents(int invoiceId) {
        setSize(720, 560);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Chi tiết Hóa đơn #" + invoiceId);
        getContentPane().setBackground(ThemeManager.getContentBackground());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.getContentBackground());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Invoice info (khách, thú cưng, ngày)
        JPanel infoPanel = new JPanel(new java.awt.GridLayout(0, 2, 15, 10));
        infoPanel.setBackground(ThemeManager.getContentBackground());
        int totalServices = 0, totalMedicines = 0, totalVaccinations = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        InvoiceInfoDto invoiceInfo = null;
        try {
            invoiceInfo = InvoiceService.getInstance().getInvoiceInfo(invoiceId);
            if (invoiceInfo != null) {
                String invoiceDateStr = invoiceInfo.getInvoiceDate() != null ? sdf.format(invoiceInfo.getInvoiceDate()) : "";
                infoPanel.add(createInfoLabel("Khách hàng:"));
                infoPanel.add(createInfoValue(invoiceInfo.getCustomerName()));
                infoPanel.add(createInfoLabel("Số điện thoại:"));
                infoPanel.add(createInfoValue(invoiceInfo.getCustomerPhoneNumber()));
                infoPanel.add(createInfoLabel("Thú cưng:"));
                infoPanel.add(createInfoValue(invoiceInfo.getPetName()));
                if (invoiceInfo.getPetEnclosureNumber() != null && invoiceInfo.getPetEnclosureNumber() > 0) {
                    infoPanel.add(createInfoLabel("Số chuồng:"));
                    infoPanel.add(createInfoValue(String.valueOf(invoiceInfo.getPetEnclosureNumber())));
                }
                infoPanel.add(createInfoLabel("Ngày hóa đơn:"));
                infoPanel.add(createInfoValue(invoiceDateStr));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Tables + summary in scrollable content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(ThemeManager.getContentBackground());

        // Invoice details table
        String[] columns = {"Dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            List<InvoiceDetailListDto> details = InvoiceService.getInstance().getInvoiceDetails(invoiceId);
            for (InvoiceDetailListDto dto : details) {
                totalServices += dto.getTotalPrice();
                tableModel.addRow(new Object[]{
                        dto.getServiceName() != null ? dto.getServiceName() : "",
                        dto.getQuantity(),
                        formatCurrency(dto.getUnitPrice()),
                        formatCurrency(dto.getTotalPrice())
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JTable detailsTable = new JTable(tableModel);
        detailsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsTable.setRowHeight(30);
        detailsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.applyTableTheme(detailsTable);

        JScrollPane detailsScroll = new JScrollPane(detailsTable);
        detailsScroll.setBackground(ThemeManager.getContentBackground());
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết dịch vụ"));
        detailsScroll.setPreferredSize(new Dimension(0, 120));
        JPanel tablesPanel = new JPanel(new BorderLayout(0, 10));
        tablesPanel.setBackground(ThemeManager.getContentBackground());
        tablesPanel.add(detailsScroll, BorderLayout.NORTH);

        // Medicine details table
        String[] medColumns = {"Thuốc", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel medTableModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            List<InvoiceMedicineDetailListDto> medDetails = InvoiceService.getInstance().getInvoiceMedicineDetails(invoiceId);
            for (InvoiceMedicineDetailListDto dto : medDetails) {
                totalMedicines += dto.getTotalPrice();
                medTableModel.addRow(new Object[]{
                        dto.getMedicineName() != null ? dto.getMedicineName() : "",
                        dto.getQuantity(),
                        formatCurrency(dto.getUnitPrice()),
                        formatCurrency(dto.getTotalPrice())
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JTable medTable = new JTable(medTableModel);
        medTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medTable.setRowHeight(30);
        medTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.applyTableTheme(medTable);
        JScrollPane medScroll = new JScrollPane(medTable);
        medScroll.setBackground(ThemeManager.getContentBackground());
        medScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết thuốc"));
        medScroll.setPreferredSize(new Dimension(0, 100));
        tablesPanel.add(medScroll, BorderLayout.CENTER);

        // Vaccination details table
        String[] vacColumns = {"Vaccine", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel vacTableModel = new DefaultTableModel(vacColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            List<InvoiceVaccinationDetailListDto> vacDetails = InvoiceService.getInstance().getInvoiceVaccinationDetails(invoiceId);
            for (InvoiceVaccinationDetailListDto dto : vacDetails) {
                totalVaccinations += dto.getTotalPrice();
                vacTableModel.addRow(new Object[]{
                        dto.getVaccineName() != null ? dto.getVaccineName() : "",
                        dto.getQuantity(),
                        formatCurrency(dto.getUnitPrice()),
                        formatCurrency(dto.getTotalPrice())
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JTable vacTable = new JTable(vacTableModel);
        vacTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vacTable.setRowHeight(30);
        vacTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.applyTableTheme(vacTable);
        JScrollPane vacScroll = new JScrollPane(vacTable);
        vacScroll.setBackground(ThemeManager.getContentBackground());
        vacScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết tiêm chủng"));
        vacScroll.setPreferredSize(new Dimension(0, 100));
        tablesPanel.add(vacScroll, BorderLayout.SOUTH);

        contentPanel.add(tablesPanel, BorderLayout.CENTER);

        // Tổng kê: tổng dịch vụ, tổng thuốc, tổng vaccine, tổng tiền, giảm giá, đặt cọc, thành tiền
        JPanel summaryPanel = new JPanel(new java.awt.GridLayout(0, 2, 12, 8));
        summaryPanel.setBackground(ThemeManager.getContentBackground());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tổng kê"));
        summaryPanel.add(createInfoLabel("Tổng dịch vụ:"));
        summaryPanel.add(createInfoValue(formatCurrency(totalServices)));
        summaryPanel.add(createInfoLabel("Tổng thuốc:"));
        summaryPanel.add(createInfoValue(formatCurrency(totalMedicines)));
        summaryPanel.add(createInfoLabel("Tổng tiêm chủng:"));
        summaryPanel.add(createInfoValue(formatCurrency(totalVaccinations)));
        summaryPanel.add(createInfoLabel("Tổng tiền (trước giảm):"));
        int subtotal = totalServices + totalMedicines + totalVaccinations;
        summaryPanel.add(createInfoValue(formatCurrency(invoiceInfo != null ? invoiceInfo.getSubtotal() : subtotal)));
        summaryPanel.add(createInfoLabel("Giảm giá:"));
        summaryPanel.add(createInfoValue(formatCurrency(invoiceInfo != null ? invoiceInfo.getDiscount() : 0)));
        summaryPanel.add(createInfoLabel("Đặt cọc:"));
        summaryPanel.add(createInfoValue(formatCurrency(invoiceInfo != null ? invoiceInfo.getDeposit() : 0)));
        summaryPanel.add(createInfoLabel("Còn phải thanh toán:"));
        JLabel finalLabel = createInfoValue(formatCurrency(invoiceInfo != null ? invoiceInfo.getTotalAmount() : subtotal));
        finalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        finalLabel.setForeground(ThemeManager.isDarkMode() ? new Color(255, 180, 100) : new Color(139, 69, 19));
        summaryPanel.add(finalLabel);

        contentPanel.add(summaryPanel, BorderLayout.SOUTH);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(ThemeManager.getContentBackground());
        mainScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(mainScroll, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());

        JButton printButton = new JButton("In hóa đơn");
        printButton.setIcon(EmojiFontHelper.createEmojiIcon("🖨️", ThemeManager.getIconColor()));
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        printButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        printButton.addActionListener(e -> {
            try {
                PrintHelper.printInvoice(this.invoiceId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi mở in hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(printButton);

        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ThemeManager.getTitleForeground());
        return label;
    }

    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text != null ? text : "");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.getTableForeground());
        return label;
    }

    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
}
