package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.entity.InvoiceDetailListDto;
import com.petcare.model.entity.InvoiceInfoDto;
import com.petcare.service.InvoiceService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.PrintHelper;
import com.petcare.util.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Chi tiết Hóa đơn #" + invoiceId);
        getContentPane().setBackground(ThemeManager.getContentBackground());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.getContentBackground());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Invoice info
        JPanel infoPanel = new JPanel(new java.awt.GridLayout(0, 2, 15, 10));
        infoPanel.setBackground(ThemeManager.getContentBackground());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            InvoiceInfoDto invoiceInfo = InvoiceService.getInstance().getInvoiceInfo(invoiceId);
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
                infoPanel.add(createInfoLabel("Tổng tiền:"));
                infoPanel.add(createInfoValue(formatCurrency(invoiceInfo.getSubtotal())));
                infoPanel.add(createInfoLabel("Giảm giá:"));
                infoPanel.add(createInfoValue(formatCurrency(invoiceInfo.getDiscount())));
                infoPanel.add(createInfoLabel("Đặt cọc:"));
                infoPanel.add(createInfoValue(formatCurrency(invoiceInfo.getDeposit())));
                infoPanel.add(createInfoLabel("Thành tiền:"));
                JLabel totalLabel = createInfoValue(formatCurrency(invoiceInfo.getTotalAmount()));
                totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                totalLabel.setForeground(ThemeManager.isDarkMode() ? new Color(255, 180, 100) : new Color(139, 69, 19));
                infoPanel.add(totalLabel);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mainPanel.add(infoPanel, BorderLayout.NORTH);

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
        
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setBackground(ThemeManager.getContentBackground());
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết dịch vụ"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
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
