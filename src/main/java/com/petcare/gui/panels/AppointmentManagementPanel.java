package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditAppointmentDialog;
import com.petcare.model.domain.Appointment;
import com.petcare.model.entity.AppointmentListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.AppointmentService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

/**
 * Appointment Management Panel - uses AppointmentService only
 */
public class AppointmentManagementPanel extends JPanel {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JPanel headerPanel;
    private JPanel filterPanel;
    private JPanel centerPanel;
    private JPanel sideButtonPanel;
    private JLabel titleLabel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton updateStatusButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> statusFilterCombo;
    private final AppointmentService appointmentService = AppointmentService.getInstance();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public AppointmentManagementPanel() {
        initComponents();
        loadAppointments();
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

        titleLabel = new JLabel("Quản lý Lịch hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);

        filterPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        filterPanel.setBackground(ThemeManager.getHeaderBackground());
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Chờ xác nhận", "Đã xác nhận", "Hoàn thành", "Đã hủy"});
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.addActionListener(e -> loadAppointments());
        filterPanel.add(statusFilterCombo);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Ngày giờ", "Loại", "Khách hàng", "Thú cưng", "Bác sĩ", "Dịch vụ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        appointmentTable.setRowHeight(30);
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        appointmentTable.setSelectionBackground(new Color(139, 69, 19));
        appointmentTable.setSelectionForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(null);

        JPanel searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(ThemeManager.getContentBackground());
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo ngày, loại, khách hàng, thú cưng, bác sĩ...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
            @Override
            public void removeUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
            @Override
            public void changedUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
        });
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);

        sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
        sideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideButtonPanel.setMinimumSize(new java.awt.Dimension(175, 0));
        sideButtonPanel.setPreferredSize(new java.awt.Dimension(175, 0));
        java.awt.Color iconColor = ThemeManager.isDarkMode() ? new Color(0xc0c0c0) : new Color(60, 60, 60);
        addButton = new JButton("Thêm");
        addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showAddAppointmentDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditAppointmentDialog());
        sideButtonPanel.add(editButton);
        updateStatusButton = new JButton("Đổi trạng thái");
        updateStatusButton.setIcon(EmojiFontHelper.createEmojiIcon("✅", Color.WHITE));
        updateStatusButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(updateStatusButton);
        updateStatusButton.setBackground(new Color(40, 167, 69));
        updateStatusButton.setForeground(Color.WHITE);
        updateStatusButton.addActionListener(e -> showUpdateStatusDialog());
        sideButtonPanel.add(updateStatusButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteAppointment());
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
        paginationPanel = new TablePaginationPanel(appointmentTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }

    public void refreshData() {
        loadAppointments();
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
        if (filterPanel != null) filterPanel.setBackground(ThemeManager.getHeaderBackground());
        if (centerPanel != null) centerPanel.setBackground(ThemeManager.getContentBackground());
        if (sideButtonPanel != null) sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        try {
            String selectedStatus = (String) statusFilterCombo.getSelectedItem();
            List<AppointmentListDto> list = appointmentService.getAppointmentsForList(selectedStatus != null ? selectedStatus : "Tất cả");
            for (AppointmentListDto dto : list) {
                String dateStr = dto.getAppointmentDate() != null ? SDF.format(dto.getAppointmentDate()) : "";
                String statusLabel = AppointmentService.statusCodeToLabel(dto.getStatus());
                tableModel.addRow(new Object[]{
                    dto.getAppointmentId(),
                    dateStr,
                    dto.getAppointmentType(),
                    dto.getCustomerName(),
                    dto.getPetName(),
                    dto.getDoctorName() != null ? dto.getDoctorName() : "",
                    dto.getServiceName() != null ? dto.getServiceName() : "",
                    statusLabel
                });
            }
            if (paginationPanel != null) paginationPanel.refresh();
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAppointmentDialog() {
        AddEditAppointmentDialog dialog = new AddEditAppointmentDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }

    private void showEditAppointmentDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        int appointmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment != null) {
                AddEditAppointmentDialog dialog = new AddEditAppointmentDialog(null, appointment);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUpdateStatusDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        int appointmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 7);
        String[] statusOptions = {"Chờ xác nhận", "Đã xác nhận", "Hoàn thành", "Đã hủy"};
        String selected = (String) JOptionPane.showInputDialog(this, "Chọn trạng thái mới:", "Cập nhật trạng thái",
            JOptionPane.QUESTION_MESSAGE, null, statusOptions, currentStatus);
        if (selected != null && !selected.equals(currentStatus)) {
            try {
                appointmentService.updateStatus(appointmentId, selected);
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        int appointmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lịch hẹn này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentService.deleteAppointment(appointmentId);
                JOptionPane.showMessageDialog(this, "Xóa lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
