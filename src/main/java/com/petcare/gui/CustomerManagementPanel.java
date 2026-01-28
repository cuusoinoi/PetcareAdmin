package com.petcare.gui;

import com.petcare.model.Customer;
import com.petcare.model.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Customer Management Panel with CRUD operations
 */
public class CustomerManagementPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public CustomerManagementPanel() {
        initComponents();
        loadCustomers();
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
        
        JLabel titleLabel = new JLabel("Quản lý Khách hàng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddCustomerDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditCustomerDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteCustomer());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Tên khách hàng", "Số điện thoại", "Email", "CMND/CCCD", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setRowHeight(30);
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.setSelectionBackground(new Color(139, 69, 19));
        customerTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadCustomers();
    }
    
    private void loadCustomers() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT customer_id, customer_name, customer_phone_number, " +
                          "customer_email, customer_identity_card, customer_address " +
                          "FROM customers ORDER BY customer_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                Object[] row = {
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_phone_number"),
                    rs.getString("customer_email") != null ? rs.getString("customer_email") : "",
                    rs.getString("customer_identity_card") != null ? rs.getString("customer_identity_card") : "",
                    rs.getString("customer_address") != null ? rs.getString("customer_address") : ""
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
    
    private void showAddCustomerDialog() {
        AddEditCustomerDialog dialog = new AddEditCustomerDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn khách hàng cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Customer customer = getCustomerById(customerId);
        
        if (customer != null) {
            AddEditCustomerDialog dialog = new AddEditCustomerDialog(null, customer);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn khách hàng cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa khách hàng: " + customerName + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM customers WHERE customer_id = ?";
                int result = Database.executeUpdate(query, customerId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa khách hàng thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa khách hàng. Có thể khách hàng đang có dữ liệu liên quan.", 
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
    
    private Customer getCustomerById(int customerId) {
        try {
            String query = "SELECT * FROM customers WHERE customer_id = ?";
            ResultSet rs = Database.executeQuery(query, customerId);
            
            if (rs != null && rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCustomerName(rs.getString("customer_name"));
                customer.setCustomerPhoneNumber(rs.getString("customer_phone_number"));
                customer.setCustomerEmail(rs.getString("customer_email"));
                customer.setCustomerIdentityCard(rs.getString("customer_identity_card"));
                customer.setCustomerAddress(rs.getString("customer_address"));
                customer.setCustomerNote(rs.getString("customer_note"));
                return customer;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
