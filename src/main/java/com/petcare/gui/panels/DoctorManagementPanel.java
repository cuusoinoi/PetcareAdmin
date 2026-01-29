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
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;

/**
 * Doctor Management Panel with CRUD operations
 */
public class DoctorManagementPanel extends JPanel {
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
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
        java.awt.Color iconColor = new Color(60, 60, 60);
        JPanel sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(new Color(245, 245, 245));
        sideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideButtonPanel.setMinimumSize(new java.awt.Dimension(175, 0));
        sideButtonPanel.setPreferredSize(new java.awt.Dimension(175, 0));
        addButton = new JButton("Thêm");
        addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showAddDoctorDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditDoctorDialog());
        sideButtonPanel.add(editButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteDoctor());
        sideButtonPanel.add(deleteButton);
        refreshButton = new JButton("Làm mới");
        refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
        refreshButton.addActionListener(e -> refreshData());
        sideButtonPanel.add(refreshButton);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        paginationPanel = new TablePaginationPanel(doctorTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
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
            if (paginationPanel != null) paginationPanel.refresh();
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
        int modelRow = doctorTable.convertRowIndexToModel(selectedRow);
        int doctorId = (Integer) tableModel.getValueAt(modelRow, 0);
        
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
        int modelRow = doctorTable.convertRowIndexToModel(selectedRow);
        int doctorId = (Integer) tableModel.getValueAt(modelRow, 0);
        String doctorName = (String) tableModel.getValueAt(modelRow, 1);
        
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
