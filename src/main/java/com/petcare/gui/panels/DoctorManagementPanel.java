package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditDoctorDialog;
import com.petcare.model.domain.Doctor;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.DoctorService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Doctor Management Panel with CRUD operations
 */
public class DoctorManagementPanel extends JPanel {
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel searchPanel;
    private JPanel sideButtonPanel;
    private JLabel titleLabel;
    private JTextField searchField;
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
        setBackground(ThemeManager.getContentBackground());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.getHeaderBackground());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        titleLabel = new JLabel("Quản lý Bác sĩ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
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
        ThemeManager.applyTableTheme(doctorTable);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(null);
        searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(ThemeManager.getContentBackground());
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo tên, SDT, CMND/CCCD, địa chỉ...");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        java.awt.Color iconColor = ThemeManager.isDarkMode() ? new Color(0xc0c0c0) : new Color(60, 60, 60);
        sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
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
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.getContentBackground());
        paginationPanel = new TablePaginationPanel(doctorTable);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                paginationPanel.setSearchText(searchField.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                paginationPanel.setSearchText(searchField.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                paginationPanel.setSearchText(searchField.getText().trim());
            }
        });
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }

    public void refreshData() {
        loadDoctors();
    }

    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        if (searchPanel != null) searchPanel.setBackground(ThemeManager.getContentBackground());
        if (headerPanel != null) {
            headerPanel.setBackground(ThemeManager.getHeaderBackground());
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
        }
        if (titleLabel != null) titleLabel.setForeground(ThemeManager.getTitleForeground());
        if (centerPanel != null) centerPanel.setBackground(ThemeManager.getContentBackground());
        if (sideButtonPanel != null) {
            sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
            for (java.awt.Component c : sideButtonPanel.getComponents()) {
                if (c instanceof JButton) {
                    ((JButton) c).setBackground(ThemeManager.getButtonBackground());
                    ((JButton) c).setForeground(ThemeManager.getButtonForeground());
                }
            }
            java.awt.Color iconColor = ThemeManager.getIconColor();
            addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
            editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
            deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
            refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        }
        if (searchField != null) {
            searchField.setBackground(ThemeManager.getTextFieldBackground());
            searchField.setForeground(ThemeManager.getTextFieldForeground());
        }
        if (searchPanel != null) {
            for (java.awt.Component c : searchPanel.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(ThemeManager.getTitleForeground());
            }
        }
        if (paginationPanel != null) paginationPanel.updateTheme();
        ThemeManager.applyTableTheme(doctorTable);
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
