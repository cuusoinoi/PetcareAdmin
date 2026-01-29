package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditServiceTypeDialog;
import com.petcare.model.domain.ServiceType;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.ServiceTypeService;
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

/**
 * Service Type Management Panel - uses ServiceTypeService only
 */
public class ServiceTypeManagementPanel extends JPanel {
    private JTable serviceTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private final ServiceTypeService serviceTypeService = ServiceTypeService.getInstance();

    public ServiceTypeManagementPanel() {
        initComponents();
        loadServiceTypes();
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

        JLabel titleLabel = new JLabel("Quản lý Dịch vụ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên dịch vụ", "Mô tả", "Giá (VNĐ)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable = new JTable(tableModel);
        serviceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        serviceTable.setRowHeight(30);
        serviceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        serviceTable.setSelectionBackground(new Color(139, 69, 19));
        serviceTable.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(serviceTable);
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
        addButton.addActionListener(e -> showAddServiceDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditServiceDialog());
        sideButtonPanel.add(editButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteService());
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
        paginationPanel = new TablePaginationPanel(serviceTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }

    public void refreshData() {
        loadServiceTypes();
    }

    private void loadServiceTypes() {
        tableModel.setRowCount(0);
        try {
            for (ServiceType s : serviceTypeService.getAllServiceTypes()) {
                String description = s.getDescription();
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 50) + "...";
                }
                tableModel.addRow(new Object[]{
                    s.getServiceTypeId(),
                    s.getServiceName(),
                    description != null ? description : "",
                    formatCurrency((int) s.getPrice())
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

    private void showAddServiceDialog() {
        AddEditServiceTypeDialog dialog = new AddEditServiceTypeDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }

    private void showEditServiceDialog() {
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn dịch vụ cần sửa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = serviceTable.convertRowIndexToModel(selectedRow);
        int serviceId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            ServiceType service = serviceTypeService.getServiceTypeById(serviceId);
            if (service != null) {
                AddEditServiceTypeDialog dialog = new AddEditServiceTypeDialog(null, service);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteService() {
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn dịch vụ cần xóa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = serviceTable.convertRowIndexToModel(selectedRow);
        int serviceId = (Integer) tableModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa dịch vụ này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                serviceTypeService.deleteServiceType(serviceId);
                JOptionPane.showMessageDialog(this,
                    "Xóa dịch vụ thành công!",
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

    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
}
