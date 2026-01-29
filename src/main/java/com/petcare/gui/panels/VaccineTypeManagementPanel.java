package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditVaccineTypeDialog;
import com.petcare.model.domain.VaccineType;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.VaccineTypeService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
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
import com.petcare.util.ThemeManager;

/**
 * Vaccine Type Management Panel - uses VaccineTypeService only
 */
public class VaccineTypeManagementPanel extends JPanel {
    private JTable vaccineTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel sideButtonPanel;
    private JLabel titleLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private final VaccineTypeService vaccineTypeService = VaccineTypeService.getInstance();

    public VaccineTypeManagementPanel() {
        initComponents();
        loadVaccines();
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

        titleLabel = new JLabel("Quản lý Vaccine");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

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
        addButton.addActionListener(e -> showAddVaccineDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditVaccineDialog());
        sideButtonPanel.add(editButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteVaccine());
        sideButtonPanel.add(deleteButton);
        refreshButton = new JButton("Làm mới");
        refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
        refreshButton.addActionListener(e -> refreshData());
        sideButtonPanel.add(refreshButton);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.getContentBackground());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        paginationPanel = new TablePaginationPanel(vaccineTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }

    public void refreshData() {
        loadVaccines();
    }

    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        if (headerPanel != null) {
            headerPanel.setBackground(ThemeManager.getHeaderBackground());
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
        }
        if (titleLabel != null) titleLabel.setForeground(ThemeManager.getTitleForeground());
        if (centerPanel != null) centerPanel.setBackground(ThemeManager.getContentBackground());
        if (sideButtonPanel != null) sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
    }

    private void loadVaccines() {
        tableModel.setRowCount(0);
        try {
            for (VaccineType v : vaccineTypeService.getAllVaccineTypes()) {
                String description = v.getDescription();
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 50) + "...";
                }
                tableModel.addRow(new Object[]{
                    v.getVaccineId(),
                    v.getVaccineName(),
                    description != null ? description : ""
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
        int modelRow = vaccineTable.convertRowIndexToModel(selectedRow);
        int vaccineId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            VaccineType vaccine = vaccineTypeService.getVaccineTypeById(vaccineId);
            if (vaccine != null) {
                AddEditVaccineTypeDialog dialog = new AddEditVaccineTypeDialog(null, vaccine);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        int modelRow = vaccineTable.convertRowIndexToModel(selectedRow);
        int vaccineId = (Integer) tableModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa vaccine này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                vaccineTypeService.deleteVaccineType(vaccineId);
                JOptionPane.showMessageDialog(this,
                    "Xóa vaccine thành công!",
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
