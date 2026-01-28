package com.petcare.gui;

import com.petcare.model.Database;
import com.petcare.model.MedicalRecord;
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
 * Medical Record Management Panel with CRUD operations
 */
public class MedicalRecordManagementPanel extends JPanel {
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public MedicalRecordManagementPanel() {
        initComponents();
        loadMedicalRecords();
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
        
        JLabel titleLabel = new JLabel("Quản lý Hồ sơ Khám bệnh");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddRecordDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditRecordDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteRecord());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Ngày khám", "Loại", "Khách hàng", "Thú cưng", "Bác sĩ", "Tóm tắt"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        recordTable = new JTable(tableModel);
        recordTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recordTable.setRowHeight(30);
        recordTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        recordTable.setSelectionBackground(new Color(139, 69, 19));
        recordTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadMedicalRecords();
    }
    
    private void loadMedicalRecords() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT mr.medical_record_id, mr.medical_record_visit_date, " +
                          "mr.medical_record_type, c.customer_name, p.pet_name, d.doctor_name, " +
                          "mr.medical_record_summary " +
                          "FROM medical_records mr " +
                          "INNER JOIN customers c ON mr.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON mr.pet_id = p.pet_id " +
                          "INNER JOIN doctors d ON mr.doctor_id = d.doctor_id " +
                          "ORDER BY mr.medical_record_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs != null && rs.next()) {
                String visitDate = "";
                if (rs.getDate("medical_record_visit_date") != null) {
                    visitDate = sdf.format(rs.getDate("medical_record_visit_date"));
                }
                
                String summary = rs.getString("medical_record_summary");
                if (summary != null && summary.length() > 50) {
                    summary = summary.substring(0, 50) + "...";
                }
                
                Object[] row = {
                    rs.getInt("medical_record_id"),
                    visitDate,
                    rs.getString("medical_record_type"),
                    rs.getString("customer_name"),
                    rs.getString("pet_name"),
                    rs.getString("doctor_name"),
                    summary != null ? summary : ""
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
    
    private void showAddRecordDialog() {
        AddEditMedicalRecordDialog dialog = new AddEditMedicalRecordDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditRecordDialog() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn hồ sơ cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int recordId = (Integer) tableModel.getValueAt(selectedRow, 0);
        MedicalRecord record = getMedicalRecordById(recordId);
        
        if (record != null) {
            AddEditMedicalRecordDialog dialog = new AddEditMedicalRecordDialog(null, record);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void deleteRecord() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn hồ sơ cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int recordId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa hồ sơ này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM medical_records WHERE medical_record_id = ?";
                int result = Database.executeUpdate(query, recordId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa hồ sơ thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa hồ sơ.", 
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
    
    private MedicalRecord getMedicalRecordById(int recordId) {
        try {
            String query = "SELECT * FROM medical_records WHERE medical_record_id = ?";
            ResultSet rs = Database.executeQuery(query, recordId);
            
            if (rs != null && rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setMedicalRecordId(rs.getInt("medical_record_id"));
                record.setCustomerId(rs.getInt("customer_id"));
                record.setPetId(rs.getInt("pet_id"));
                record.setDoctorId(rs.getInt("doctor_id"));
                
                String typeStr = rs.getString("medical_record_type");
                if (typeStr != null) {
                    record.setMedicalRecordType(MedicalRecord.RecordType.fromLabel(typeStr));
                }
                
                if (rs.getDate("medical_record_visit_date") != null) {
                    record.setMedicalRecordVisitDate(new java.util.Date(
                        rs.getDate("medical_record_visit_date").getTime()));
                }
                
                record.setMedicalRecordSummary(rs.getString("medical_record_summary"));
                record.setMedicalRecordDetails(rs.getString("medical_record_details"));
                
                return record;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
