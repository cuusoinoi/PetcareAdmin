package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditVaccineTypeDialog;
import com.petcare.model.Database;
import com.petcare.model.Vaccine;
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
 * Vaccine Type Management Panel with CRUD operations
 */
public class VaccineTypeManagementPanel extends JPanel {
    private JTable vaccineTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public VaccineTypeManagementPanel() {
        initComponents();
        loadVaccines();
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
        
        JLabel titleLabel = new JLabel("Quản lý Vaccine");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddVaccineDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditVaccineDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteVaccine());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Tên vaccine", "Mô tả"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vaccineTable = new JTable(tableModel);
        vaccineTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vaccineTable.setRowHeight(30);
        vaccineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        vaccineTable.setSelectionBackground(new Color(139, 69, 19));
        vaccineTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(vaccineTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadVaccines();
    }
    
    private void loadVaccines() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT vaccine_id, vaccine_name, description FROM vaccines ORDER BY vaccine_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                String description = rs.getString("description");
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 50) + "...";
                }
                
                Object[] row = {
                    rs.getInt("vaccine_id"),
                    rs.getString("vaccine_name"),
                    description != null ? description : ""
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
    
    private void showAddVaccineDialog() {
        AddEditVaccineTypeDialog dialog = new AddEditVaccineTypeDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditVaccineDialog() {
        int selectedRow = vaccineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn vaccine cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vaccineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Vaccine vaccine = getVaccineById(vaccineId);
        
        if (vaccine != null) {
            AddEditVaccineTypeDialog dialog = new AddEditVaccineTypeDialog(null, vaccine);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void deleteVaccine() {
        int selectedRow = vaccineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn vaccine cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vaccineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa vaccine này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM vaccines WHERE vaccine_id = ?";
                int result = Database.executeUpdate(query, vaccineId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa vaccine thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa vaccine.", 
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
    
    private Vaccine getVaccineById(int vaccineId) {
        try {
            String query = "SELECT * FROM vaccines WHERE vaccine_id = ?";
            ResultSet rs = Database.executeQuery(query, vaccineId);
            
            if (rs != null && rs.next()) {
                Vaccine vaccine = new Vaccine();
                vaccine.setVaccineId(rs.getInt("vaccine_id"));
                vaccine.setVaccineName(rs.getString("vaccine_name"));
                vaccine.setDescription(rs.getString("description"));
                return vaccine;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
