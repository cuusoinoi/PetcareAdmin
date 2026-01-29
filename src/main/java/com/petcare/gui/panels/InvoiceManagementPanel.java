package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddInvoiceDialog;
import com.petcare.gui.dialogs.InvoiceDetailsDialog;
import com.petcare.model.entity.InvoiceListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.InvoiceService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.petcare.util.EmojiFontHelper;

/**
 * Invoice Management Panel with CRUD operations
 */
public class InvoiceManagementPanel extends JPanel {
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public InvoiceManagementPanel() {
        initComponents();
        loadInvoices();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Quản lý Hóa đơn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton(EmojiFontHelper.withEmoji("➕", "Thêm"));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddInvoiceDialog());
        buttonPanel.add(addButton);
        
        viewButton = new JButton(EmojiFontHelper.withEmoji("👁️", "Xem chi tiết"));
        viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewButton.addActionListener(e -> showInvoiceDetails());
        buttonPanel.add(viewButton);
        
        deleteButton = new JButton(EmojiFontHelper.withEmoji("🗑️", "Xóa"));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteInvoice());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton(EmojiFontHelper.withEmoji("🔄", "Làm mới"));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Ngày", "Khách hàng", "Thú cưng", "Tổng tiền", "Giảm giá", "Đặt cọc", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        invoiceTable.setRowHeight(30);
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        invoiceTable.setSelectionBackground(new Color(139, 69, 19));
        invoiceTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadInvoices();
    }
    
    private void loadInvoices() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            List<InvoiceListDto> list = InvoiceService.getInstance().getInvoicesForList();
            for (InvoiceListDto dto : list) {
                String dateStr = dto.getInvoiceDate() != null ? sdf.format(dto.getInvoiceDate()) : "";
                tableModel.addRow(new Object[]{
                    dto.getInvoiceId(),
                    dateStr,
                    dto.getCustomerName() != null ? dto.getCustomerName() : "",
                    dto.getPetName() != null ? dto.getPetName() : "",
                    formatCurrency(dto.getSubtotal()),
                    formatCurrency(dto.getDiscount()),
                    formatCurrency(dto.getDeposit()),
                    formatCurrency(dto.getTotalAmount())
                });
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddInvoiceDialog() {
        AddInvoiceDialog dialog = new AddInvoiceDialog(null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showInvoiceDetails() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn hóa đơn cần xem!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int invoiceId = (Integer) tableModel.getValueAt(selectedRow, 0);
        InvoiceDetailsDialog dialog = new InvoiceDetailsDialog(null, invoiceId);
        dialog.setVisible(true);
    }
    
    private void deleteInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn hóa đơn cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int invoiceId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa hóa đơn #" + invoiceId + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                InvoiceService.getInstance().deleteInvoice(invoiceId);
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
}
