package com.petcare.gui;

import com.petcare.model.Appointment;
import com.petcare.model.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Appointment Management Panel with CRUD operations
 */
public class AppointmentManagementPanel extends JPanel {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton updateStatusButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> statusFilterCombo;
    
    public AppointmentManagementPanel() {
        initComponents();
        loadAppointments();
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
        
        JLabel titleLabel = new JLabel("Quản lý Lịch hẹn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Chờ xác nhận", "Đã xác nhận", "Hoàn thành", "Đã hủy"});
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.addActionListener(e -> loadAppointments());
        filterPanel.add(statusFilterCombo);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddAppointmentDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditAppointmentDialog());
        buttonPanel.add(editButton);
        
        updateStatusButton = new JButton("✅ Cập nhật trạng thái");
        updateStatusButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateStatusButton.setBackground(new Color(40, 167, 69));
        updateStatusButton.setForeground(Color.WHITE);
        updateStatusButton.addActionListener(e -> showUpdateStatusDialog());
        buttonPanel.add(updateStatusButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteAppointment());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
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
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadAppointments();
    }
    
    private void loadAppointments() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT a.appointment_id, a.appointment_date, a.appointment_type, " +
                          "c.customer_name, p.pet_name, d.doctor_name, st.service_name, a.status " +
                          "FROM appointments a " +
                          "INNER JOIN customers c ON a.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON a.pet_id = p.pet_id " +
                          "LEFT JOIN doctors d ON a.doctor_id = d.doctor_id " +
                          "LEFT JOIN service_types st ON a.service_type_id = st.service_type_id " +
                          "WHERE 1=1";
            
            String selectedStatus = (String) statusFilterCombo.getSelectedItem();
            if (selectedStatus != null && !selectedStatus.equals("Tất cả")) {
                String statusCode = getStatusCodeFromLabel(selectedStatus);
                query += " AND a.status = '" + statusCode + "'";
            }
            
            query += " ORDER BY a.appointment_date DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            while (rs != null && rs.next()) {
                String appointmentDate = "";
                if (rs.getTimestamp("appointment_date") != null) {
                    appointmentDate = sdf.format(rs.getTimestamp("appointment_date"));
                }
                
                String statusLabel = getStatusLabelFromCode(rs.getString("status"));
                
                Object[] row = {
                    rs.getInt("appointment_id"),
                    appointmentDate,
                    rs.getString("appointment_type"),
                    rs.getString("customer_name"),
                    rs.getString("pet_name"),
                    rs.getString("doctor_name") != null ? rs.getString("doctor_name") : "",
                    rs.getString("service_name") != null ? rs.getString("service_name") : "",
                    statusLabel
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String getStatusCodeFromLabel(String label) {
        switch (label) {
            case "Chờ xác nhận": return "pending";
            case "Đã xác nhận": return "confirmed";
            case "Hoàn thành": return "completed";
            case "Đã hủy": return "cancelled";
            default: return "";
        }
    }
    
    private String getStatusLabelFromCode(String code) {
        if (code == null) return "";
        switch (code) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return code;
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
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn lịch hẹn cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (appointment != null) {
            AddEditAppointmentDialog dialog = new AddEditAppointmentDialog(null, appointment);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void showUpdateStatusDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn lịch hẹn cần cập nhật!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        String[] statusOptions = {"Chờ xác nhận", "Đã xác nhận", "Hoàn thành", "Đã hủy"};
        String selected = (String) JOptionPane.showInputDialog(this,
            "Chọn trạng thái mới:",
            "Cập nhật trạng thái",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus);
        
        if (selected != null && !selected.equals(currentStatus)) {
            try {
                String statusCode = getStatusCodeFromLabel(selected);
                String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
                int result = Database.executeUpdate(query, statusCode, appointmentId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Cập nhật trạng thái thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn lịch hẹn cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa lịch hẹn này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM appointments WHERE appointment_id = ?";
                int result = Database.executeUpdate(query, appointmentId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa lịch hẹn thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa lịch hẹn.", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private Appointment getAppointmentById(int appointmentId) {
        try {
            String query = "SELECT * FROM appointments WHERE appointment_id = ?";
            ResultSet rs = Database.executeQuery(query, appointmentId);
            
            if (rs != null && rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setCustomerId(rs.getInt("customer_id"));
                appointment.setPetId(rs.getInt("pet_id"));
                
                int doctorId = rs.getInt("doctor_id");
                if (!rs.wasNull()) {
                    appointment.setDoctorId(doctorId);
                }
                
                int serviceTypeId = rs.getInt("service_type_id");
                if (!rs.wasNull()) {
                    appointment.setServiceTypeId(serviceTypeId);
                }
                
                if (rs.getTimestamp("appointment_date") != null) {
                    appointment.setAppointmentDate(new java.util.Date(
                        rs.getTimestamp("appointment_date").getTime()));
                }
                
                String typeStr = rs.getString("appointment_type");
                if (typeStr != null) {
                    appointment.setAppointmentType(Appointment.AppointmentType.fromLabel(typeStr));
                }
                
                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    appointment.setStatus(Appointment.Status.fromCode(statusStr));
                }
                
                appointment.setNotes(rs.getString("notes"));
                
                if (rs.getTimestamp("created_at") != null) {
                    appointment.setCreatedAt(new java.util.Date(
                        rs.getTimestamp("created_at").getTime()));
                }
                
                return appointment;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
