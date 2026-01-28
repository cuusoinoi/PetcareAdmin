package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddInvoiceDialog;
import com.petcare.gui.dialogs.InvoiceDetailsDialog;
import com.petcare.model.Database;
import com.petcare.model.Invoice;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddInvoiceDialog());
        buttonPanel.add(addButton);
        
        viewButton = new JButton("👁️ Xem chi tiết");
        viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewButton.addActionListener(e -> showInvoiceDetails());
        buttonPanel.add(viewButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteInvoice());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
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
        
        try {
            String query = "SELECT i.invoice_id, i.invoice_date, c.customer_name, p.pet_name, " +
                          "i.subtotal, i.discount, i.deposit, i.total_amount " +
                          "FROM invoices i " +
                          "INNER JOIN customers c ON i.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON i.pet_id = p.pet_id " +
                          "ORDER BY i.invoice_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            while (rs != null && rs.next()) {
                String invoiceDate = "";
                if (rs.getTimestamp("invoice_date") != null) {
                    invoiceDate = sdf.format(rs.getTimestamp("invoice_date"));
                }
                
                Object[] row = {
                    rs.getInt("invoice_id"),
                    invoiceDate,
                    rs.getString("customer_name"),
                    rs.getString("pet_name"),
                    formatCurrency(rs.getInt("subtotal")),
                    formatCurrency(rs.getInt("discount")),
                    formatCurrency(rs.getInt("deposit")),
                    formatCurrency(rs.getInt("total_amount"))
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
                // Delete invoice details first
                String deleteDetailsQuery = "DELETE FROM invoice_details WHERE invoice_id = ?";
                Database.executeUpdate(deleteDetailsQuery, invoiceId);
                
                // Delete invoice
                String deleteInvoiceQuery = "DELETE FROM invoices WHERE invoice_id = ?";
                int result = Database.executeUpdate(deleteInvoiceQuery, invoiceId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa hóa đơn thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa hóa đơn.", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
}
