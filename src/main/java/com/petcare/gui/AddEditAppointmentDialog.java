package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Appointment;
import com.petcare.model.Database;
import com.petcare.model.ServiceType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for adding/editing appointment
 */
public class AddEditAppointmentDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JComboBox<String> doctorCombo;
    private JComboBox<String> serviceCombo;
    private JComboBox<String> typeCombo;
    private JTextField appointmentDateField;
    private JTextField appointmentTimeField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Appointment appointment;
    
    public AddEditAppointmentDialog(JDialog parent, Appointment appointment) {
        super(parent, true);
        this.appointment = appointment;
        initComponents();
        loadCustomers();
        loadDoctors();
        loadServiceTypes();
        
        if (appointment != null) {
            loadAppointmentData();
            setTitle("Sửa lịch hẹn");
        } else {
            setTitle("Thêm lịch hẹn mới");
        }
    }
    
    private void initComponents() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Customer
        formPanel.add(createLabel("Khách hàng *:"));
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        customerCombo.addActionListener(e -> loadPetsByCustomer());
        formPanel.add(customerCombo);
        
        // Pet
        formPanel.add(createLabel("Thú cưng *:"));
        petCombo = new JComboBox<>();
        petCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(petCombo);
        
        // Doctor
        formPanel.add(createLabel("Bác sĩ:"));
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(doctorCombo);
        
        // Service
        formPanel.add(createLabel("Dịch vụ:"));
        serviceCombo = new JComboBox<>();
        serviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        serviceCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(serviceCombo);
        
        // Type
        formPanel.add(createLabel("Loại *:"));
        typeCombo = new JComboBox<>();
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        typeCombo.addItem("Khám");
        typeCombo.addItem("Spa");
        typeCombo.addItem("Tiêm chủng");
        formPanel.add(typeCombo);
        
        // Appointment Date
        formPanel.add(createLabel("Ngày hẹn * (dd/MM/yyyy):"));
        appointmentDateField = createTextField();
        appointmentDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(appointmentDateField);
        
        // Appointment Time
        formPanel.add(createLabel("Giờ hẹn * (HH:mm):"));
        appointmentTimeField = createTextField();
        appointmentTimeField.putClientProperty("JTextField.placeholderText", "HH:mm");
        formPanel.add(appointmentTimeField);
        
        // Status
        formPanel.add(createLabel("Trạng thái:"));
        statusCombo = new JComboBox<>();
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        statusCombo.addItem("Chờ xác nhận");
        statusCombo.addItem("Đã xác nhận");
        statusCombo.addItem("Hoàn thành");
        statusCombo.addItem("Đã hủy");
        formPanel.add(statusCombo);
        
        // Notes
        formPanel.add(createLabel("Ghi chú:"));
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        notesArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(notesArea);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        saveButton = new JButton("💾 Lưu");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveAppointment());
        buttonPanel.add(saveButton);
        
        cancelButton = new JButton("❌ Hủy");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        return field;
    }
    
    private void loadCustomers() {
        customerCombo.removeAllItems();
        customerCombo.addItem("-- Chọn khách hàng --");
        
        try {
            String query = "SELECT customer_id, customer_name FROM customers ORDER BY customer_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("customer_id") + " - " + rs.getString("customer_name");
                customerCombo.addItem(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadPetsByCustomer() {
        petCombo.removeAllItems();
        petCombo.addItem("-- Chọn thú cưng --");
        
        if (customerCombo.getSelectedIndex() == 0) {
            return;
        }
        
        try {
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            
            String query = "SELECT pet_id, pet_name FROM pets WHERE customer_id = ? ORDER BY pet_name";
            ResultSet rs = Database.executeQuery(query, customerId);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("pet_id") + " - " + rs.getString("pet_name");
                petCombo.addItem(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadDoctors() {
        doctorCombo.removeAllItems();
        doctorCombo.addItem("-- Chọn bác sĩ (tùy chọn) --");
        
        try {
            String query = "SELECT doctor_id, doctor_name FROM doctors ORDER BY doctor_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("doctor_id") + " - " + rs.getString("doctor_name");
                doctorCombo.addItem(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadServiceTypes() {
        serviceCombo.removeAllItems();
        serviceCombo.addItem("-- Chọn dịch vụ (tùy chọn) --");
        
        try {
            String query = "SELECT service_type_id, service_name FROM service_types ORDER BY service_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("service_type_id") + " - " + rs.getString("service_name");
                serviceCombo.addItem(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadAppointmentData() {
        if (appointment != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(appointment.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    loadPetsByCustomer();
                    break;
                }
            }
            
            // Set pet
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                String item = petCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(appointment.getPetId()))) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set doctor
            if (appointment.getDoctorId() != null) {
                for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                    String item = doctorCombo.getItemAt(i);
                    if (item.startsWith(String.valueOf(appointment.getDoctorId()))) {
                        doctorCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Set service
            if (appointment.getServiceTypeId() != null) {
                for (int i = 0; i < serviceCombo.getItemCount(); i++) {
                    String item = serviceCombo.getItemAt(i);
                    if (item.startsWith(String.valueOf(appointment.getServiceTypeId()))) {
                        serviceCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Set type
            if (appointment.getAppointmentType() != null) {
                typeCombo.setSelectedItem(appointment.getAppointmentType().getLabel());
            }
            
            // Set date and time
            if (appointment.getAppointmentDate() != null) {
                SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
                appointmentDateField.setText(dateSdf.format(appointment.getAppointmentDate()));
                appointmentTimeField.setText(timeSdf.format(appointment.getAppointmentDate()));
            }
            
            // Set status
            if (appointment.getStatus() != null) {
                statusCombo.setSelectedItem(appointment.getStatus().getLabel());
            }
            
            notesArea.setText(appointment.getNotes() != null ? appointment.getNotes() : "");
        }
    }
    
    private void saveAppointment() {
        // Validation
        if (customerCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerCombo.requestFocus();
            return;
        }
        
        if (petCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thú cưng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            petCombo.requestFocus();
            return;
        }
        
        if (typeCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            typeCombo.requestFocus();
            return;
        }
        
        if (appointmentDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            appointmentDateField.requestFocus();
            return;
        }
        
        if (appointmentTimeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            appointmentTimeField.requestFocus();
            return;
        }
        
        try {
            // Get IDs
            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);
            
            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);
            
            Integer doctorId = null;
            if (doctorCombo.getSelectedIndex() > 0) {
                String doctorSelected = (String) doctorCombo.getSelectedItem();
                doctorId = Integer.parseInt(doctorSelected.split(" - ")[0]);
            }
            
            Integer serviceTypeId = null;
            if (serviceCombo.getSelectedIndex() > 0) {
                String serviceSelected = (String) serviceCombo.getSelectedItem();
                serviceTypeId = Integer.parseInt(serviceSelected.split(" - ")[0]);
            }
            
            String typeStr = (String) typeCombo.getSelectedItem();
            String statusLabel = (String) statusCombo.getSelectedItem();
            String statusCode = getStatusCodeFromLabel(statusLabel);
            
            // Parse date and time
            SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            Date appointmentDate;
            try {
                Date date = dateSdf.parse(appointmentDateField.getText().trim());
                Date time = timeSdf.parse(appointmentTimeField.getText().trim());
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(time);
                cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                cal.set(Calendar.SECOND, 0);
                
                appointmentDate = cal.getTime();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Ngày hoặc giờ không đúng định dạng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (appointment == null) {
                // Insert
                String query = "INSERT INTO appointments (customer_id, pet_id, doctor_id, service_type_id, " +
                              "appointment_date, appointment_type, status, notes) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(appointmentDate.getTime());
                
                int result = Database.executeUpdate(query,
                    customerId,
                    petId,
                    doctorId,
                    serviceTypeId,
                    sqlTimestamp,
                    typeStr,
                    statusCode,
                    notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm lịch hẹn thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE appointments SET customer_id = ?, pet_id = ?, doctor_id = ?, " +
                              "service_type_id = ?, appointment_date = ?, appointment_type = ?, " +
                              "status = ?, notes = ? WHERE appointment_id = ?";
                
                java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(appointmentDate.getTime());
                
                int result = Database.executeUpdate(query,
                    customerId,
                    petId,
                    doctorId,
                    serviceTypeId,
                    sqlTimestamp,
                    typeStr,
                    statusCode,
                    notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim(),
                    appointment.getAppointmentId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật lịch hẹn thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String getStatusCodeFromLabel(String label) {
        switch (label) {
            case "Chờ xác nhận": return "pending";
            case "Đã xác nhận": return "confirmed";
            case "Hoàn thành": return "completed";
            case "Đã hủy": return "cancelled";
            default: return "pending";
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
