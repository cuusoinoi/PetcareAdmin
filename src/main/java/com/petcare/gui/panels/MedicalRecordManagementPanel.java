package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditMedicalRecordDialog;
import com.petcare.model.entity.MedicalRecordListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.MedicalRecordService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.PrintHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Medical Record Management Panel with CRUD operations
 */
public class MedicalRecordManagementPanel extends JPanel {
    private JTable recordTable;
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
    private JButton printButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public MedicalRecordManagementPanel() {
        initComponents();
        loadMedicalRecords();
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

        titleLabel = new JLabel("Quản lý Hồ sơ Khám bệnh");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

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
        ThemeManager.applyTableTheme(recordTable);

        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setBorder(null);

        searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(ThemeManager.getContentBackground());
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo ngày, loại, khách hàng, thú cưng, bác sĩ...");
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
        addButton.addActionListener(e -> showAddRecordDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditRecordDialog());
        sideButtonPanel.add(editButton);
        printButton = new JButton("In phiếu khám");
        printButton.setIcon(EmojiFontHelper.createEmojiIcon("🖨️", iconColor));
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(printButton);
        printButton.addActionListener(e -> printMedicalRecord());
        sideButtonPanel.add(printButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteRecord());
        sideButtonPanel.add(deleteButton);
        refreshButton = new JButton("Làm mới");
        refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
        refreshButton.addActionListener(e -> refreshData());
        sideButtonPanel.add(refreshButton);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.getContentBackground());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        paginationPanel = new TablePaginationPanel(recordTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }

    public void refreshData() {
        loadMedicalRecords();
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
            if (printButton != null) printButton.setIcon(EmojiFontHelper.createEmojiIcon("🖨️", iconColor));
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
        ThemeManager.applyTableTheme(recordTable);
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
            if (paginationPanel != null) paginationPanel.refresh();
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
        int modelRow = recordTable.convertRowIndexToModel(selectedRow);
        int recordId = (Integer) tableModel.getValueAt(modelRow, 0);
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

    private void printMedicalRecord() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn hồ sơ cần in phiếu khám!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = recordTable.convertRowIndexToModel(selectedRow);
        int recordId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            PrintHelper.printMedicalRecord(recordId);
        } catch (PetcareException | java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        int modelRow = recordTable.convertRowIndexToModel(selectedRow);
        int recordId = (Integer) tableModel.getValueAt(modelRow, 0);
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
