package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditMedicalRecordDialog;
import com.petcare.model.entity.MedicalRecordListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.MedicalRecordService;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<MedicalRecordListDto> list = MedicalRecordService.getInstance().getRecordsForList();
            for (MedicalRecordListDto dto : list) {
                String visitDateStr = dto.getVisitDate() != null ? sdf.format(dto.getVisitDate()) : "";
                String summary = dto.getSummary();
                if (summary != null && summary.length() > 50) {
                    summary = summary.substring(0, 50) + "...";
                }
                tableModel.addRow(new Object[]{
                    dto.getMedicalRecordId(),
                    visitDateStr,
                    dto.getMedicalRecordType() != null ? dto.getMedicalRecordType() : "",
                    dto.getCustomerName() != null ? dto.getCustomerName() : "",
                    dto.getPetName() != null ? dto.getPetName() : "",
                    dto.getDoctorName() != null ? dto.getDoctorName() : "",
                    summary != null ? summary : ""
                });
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
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
        try {
            com.petcare.model.domain.MedicalRecord record = MedicalRecordService.getInstance().getRecordById(recordId);
            if (record != null) {
                AddEditMedicalRecordDialog dialog = new AddEditMedicalRecordDialog(null, record);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                MedicalRecordService.getInstance().deleteRecord(recordId);
                JOptionPane.showMessageDialog(this, "Xóa hồ sơ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
