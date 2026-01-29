package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditCustomerDialog;
import com.petcare.model.domain.Customer;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.CustomerService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
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
import com.petcare.util.GUIUtil;

/**
 * Customer Management Panel with CRUD operations
 */
public class CustomerManagementPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private CustomerService customerService;
    
    public CustomerManagementPanel() {
        this.customerService = CustomerService.getInstance();
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
        
        addButton = new JButton(EmojiFontHelper.withEmoji("➕", "Thêm"));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showAddCustomerDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton(EmojiFontHelper.withEmoji("✏️", "Sửa"));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditCustomerDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton(EmojiFontHelper.withEmoji("🗑️", "Xóa"));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteCustomer());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton(EmojiFontHelper.withEmoji("🔄", "Làm mới"));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
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

        paginationPanel = new TablePaginationPanel(customerTable);
        add(paginationPanel, BorderLayout.SOUTH);
    }
    
    public void refreshData() {
        loadCustomers();
    }
    
    private void loadCustomers() {
        tableModel.setRowCount(0);
        
        try {
            List<Customer> customers = customerService.getAllCustomers();
            for (Customer customer : customers) {
                Object[] row = {
                    customer.getCustomerId(),
                    customer.getCustomerName(),
                    customer.getCustomerPhoneNumber(),
                    customer.getCustomerEmail() != null ? customer.getCustomerEmail() : "",
                    customer.getCustomerIdentityCard() != null ? customer.getCustomerIdentityCard() : "",
                    customer.getCustomerAddress() != null ? customer.getCustomerAddress() : ""
                };
                tableModel.addRow(row);
            }
            if (paginationPanel != null) paginationPanel.refresh();
        } catch (PetcareException ex) {
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
        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        try {
            int customerId = (Integer) tableModel.getValueAt(modelRow, 0);
            Customer customer = customerService.getCustomerById(customerId);
            
            if (customer != null) {
                AddEditCustomerDialog dialog = new AddEditCustomerDialog(null, customer);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy khách hàng!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
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
        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        int customerId = (Integer) tableModel.getValueAt(modelRow, 0);
        String customerName = (String) tableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa khách hàng: " + customerName + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerService.deleteCustomer(customerId);
                JOptionPane.showMessageDialog(this, 
                    "Xóa khách hàng thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}
