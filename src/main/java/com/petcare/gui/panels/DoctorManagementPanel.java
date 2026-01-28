package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditDoctorDialog;
import com.petcare.model.domain.Doctor;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.DoctorService;
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

/**
 * Doctor Management Panel with CRUD operations
 */
public class DoctorManagementPanel extends JPanel {
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private DoctorService doctorService;
    
    public DoctorManagementPanel() {
        this.doctorService = DoctorService.getInstance();
        initComponents();
        loadDoctors();
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
        
        JLabel titleLabel = new JLabel("Quản lý Bác sĩ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddDoctorDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditDoctorDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteDoctor());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Tên bác sĩ", "Số điện thoại", "CMND/CCCD", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorTable.setRowHeight(30);
        doctorTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        doctorTable.setSelectionBackground(new Color(139, 69, 19));
        doctorTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadDoctors();
    }
    
    private void loadDoctors() {
        tableModel.setRowCount(0);
        
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            
            for (Doctor doctor : doctors) {
                Object[] row = {
                    doctor.getDoctorId(),
                    doctor.getDoctorName(),
                    doctor.getDoctorPhoneNumber(),
                    doctor.getDoctorIdentityCard() != null ? doctor.getDoctorIdentityCard() : "",
                    doctor.getDoctorAddress()
                };
                tableModel.addRow(row);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showAddDoctorDialog() {
        AddEditDoctorDialog dialog = new AddEditDoctorDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditDoctorDialog() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn bác sĩ cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            if (doctor != null) {
                AddEditDoctorDialog dialog = new AddEditDoctorDialog(null, doctor);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy bác sĩ với ID: " + doctorId, 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải thông tin bác sĩ: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void deleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn bác sĩ cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa bác sĩ: " + doctorName + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                doctorService.deleteDoctor(doctorId);
                JOptionPane.showMessageDialog(this, 
                    "Xóa bác sĩ thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
