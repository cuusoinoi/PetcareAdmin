package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditMedicineDialog;
import com.petcare.model.domain.Medicine;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.MedicineService;
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

/**
 * Medicine Management Panel - uses MedicineService only
 */
public class MedicineManagementPanel extends JPanel {
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private final MedicineService medicineService = MedicineService.getInstance();

    public MedicineManagementPanel() {
        initComponents();
        loadMedicines();
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

        JLabel titleLabel = new JLabel("Quản lý Thuốc");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddMedicineDialog());
        buttonPanel.add(addButton);

        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditMedicineDialog());
        buttonPanel.add(editButton);

        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteMedicine());
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên thuốc", "Đường dùng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medicineTable.setRowHeight(30);
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        medicineTable.setSelectionBackground(new Color(139, 69, 19));
        medicineTable.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadMedicines();
    }

    private void loadMedicines() {
        tableModel.setRowCount(0);
        try {
            for (Medicine m : medicineService.getAllMedicines()) {
                String routeLabel = getRouteLabel(m.getMedicineRoute());
                tableModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getMedicineName(),
                    routeLabel
                });
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getRouteLabel(Medicine.Route route) {
        if (route == null) return "";
        return route.getLabel();
    }

    private void showAddMedicineDialog() {
        AddEditMedicineDialog dialog = new AddEditMedicineDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }

    private void showEditMedicineDialog() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn thuốc cần sửa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int medicineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            Medicine medicine = medicineService.getMedicineById(medicineId);
            if (medicine != null) {
                AddEditMedicineDialog dialog = new AddEditMedicineDialog(null, medicine);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn thuốc cần xóa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int medicineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa thuốc này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                medicineService.deleteMedicine(medicineId);
                JOptionPane.showMessageDialog(this,
                    "Xóa thuốc thành công!",
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
