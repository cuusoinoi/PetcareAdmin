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

/**
 * Vaccine Type Management Panel - uses VaccineTypeService only
 */
public class VaccineTypeManagementPanel extends JPanel {
    private JTable vaccineTable;
    private DefaultTableModel tableModel;
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
        setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Quản lý Vaccine");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));

addButton = new JButton(EmojiFontHelper.withEmoji("➕", "Thêm"));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddVaccineDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton(EmojiFontHelper.withEmoji("✏️", "Sửa"));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditVaccineDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton(EmojiFontHelper.withEmoji("🗑️", "Xóa"));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteVaccine());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton(EmojiFontHelper.withEmoji("🔄", "Làm mới"));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
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
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadVaccines();
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
        int vaccineId = (Integer) tableModel.getValueAt(selectedRow, 0);
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
        int vaccineId = (Integer) tableModel.getValueAt(selectedRow, 0);
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
