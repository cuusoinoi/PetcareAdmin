package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditServiceTypeDialog;
import com.petcare.model.Database;
import com.petcare.model.ServiceType;
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
 * Service Type Management Panel with CRUD operations
 */
public class ServiceTypeManagementPanel extends JPanel {
    private JTable serviceTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public ServiceTypeManagementPanel() {
        initComponents();
        loadServiceTypes();
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
        
        JLabel titleLabel = new JLabel("Quản lý Dịch vụ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddServiceDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditServiceDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteService());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Tên dịch vụ", "Mô tả", "Giá (VNĐ)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        serviceTable = new JTable(tableModel);
        serviceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        serviceTable.setRowHeight(30);
        serviceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        serviceTable.setSelectionBackground(new Color(139, 69, 19));
        serviceTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(serviceTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadServiceTypes();
    }
    
    private void loadServiceTypes() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT service_type_id, service_name, description, price FROM service_types ORDER BY service_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                String description = rs.getString("description");
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 50) + "...";
                }
                
                Object[] row = {
                    rs.getInt("service_type_id"),
                    rs.getString("service_name"),
                    description != null ? description : "",
                    formatCurrency((int)rs.getDouble("price"))
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
    
    private void showAddServiceDialog() {
        AddEditServiceTypeDialog dialog = new AddEditServiceTypeDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditServiceDialog() {
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn dịch vụ cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int serviceId = (Integer) tableModel.getValueAt(selectedRow, 0);
        ServiceType service = getServiceTypeById(serviceId);
        
        if (service != null) {
            AddEditServiceTypeDialog dialog = new AddEditServiceTypeDialog(null, service);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void deleteService() {
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn dịch vụ cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int serviceId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa dịch vụ này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM service_types WHERE service_type_id = ?";
                int result = Database.executeUpdate(query, serviceId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa dịch vụ thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa dịch vụ.", 
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
    
    private ServiceType getServiceTypeById(int serviceId) {
        try {
            String query = "SELECT * FROM service_types WHERE service_type_id = ?";
            ResultSet rs = Database.executeQuery(query, serviceId);
            
            if (rs != null && rs.next()) {
                ServiceType service = new ServiceType();
                service.setServiceTypeId(rs.getInt("service_type_id"));
                service.setServiceName(rs.getString("service_name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getDouble("price"));
                return service;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
}
