package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.MedicalRecord;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * Dialog for adding/editing medical record
 */
public class AddEditMedicalRecordDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JComboBox<String> doctorCombo;
    private JComboBox<String> typeCombo;
    private JTextField visitDateField;
    private JTextArea summaryArea;
    private JTextArea detailsArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private MedicalRecord record;
    
    public AddEditMedicalRecordDialog(JDialog parent, MedicalRecord record) {
        super(parent, true);
        this.record = record;
        initComponents();
        loadCustomers();
        loadDoctors();
        
        if (record != null) {
            loadRecordData();
            setTitle("Sửa hồ sơ khám bệnh");
        } else {
            setTitle("Thêm hồ sơ khám bệnh mới");
        }
    }
    
    private void initComponents() {
        setSize(600, 550);
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
        formPanel.add(createLabel("Bác sĩ *:"));
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(doctorCombo);
        
        // Type
        formPanel.add(createLabel("Loại *:"));
        typeCombo = new JComboBox<>();
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        typeCombo.addItem("Khám");
        typeCombo.addItem("Điều trị");
        typeCombo.addItem("Vaccine");
        formPanel.add(typeCombo);
        
        // Visit Date
        formPanel.add(createLabel("Ngày khám * (dd/MM/yyyy):"));
        visitDateField = createTextField();
        visitDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(visitDateField);
        
        // Summary
        formPanel.add(createLabel("Tóm tắt:"));
        summaryArea = new JTextArea(3, 20);
        summaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        summaryArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(summaryArea);
        
        // Details
        formPanel.add(createLabel("Chi tiết:"));
        detailsArea = new JTextArea(4, 20);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        detailsArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(detailsArea);
        
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
        saveButton.addActionListener(e -> saveRecord());
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
        doctorCombo.addItem("-- Chọn bác sĩ --");
        
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
    
    private void loadRecordData() {
        if (record != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(record.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    loadPetsByCustomer();
                    break;
                }
            }
            
            // Set pet
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                String item = petCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(record.getPetId()))) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set doctor
            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                String item = doctorCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(record.getDoctorId()))) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set type
            if (record.getMedicalRecordType() != null) {
                typeCombo.setSelectedItem(record.getMedicalRecordType().getLabel());
            }
            
            // Set date
            if (record.getMedicalRecordVisitDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                visitDateField.setText(sdf.format(record.getMedicalRecordVisitDate()));
            }
            
            summaryArea.setText(record.getMedicalRecordSummary() != null ? record.getMedicalRecordSummary() : "");
            detailsArea.setText(record.getMedicalRecordDetails() != null ? record.getMedicalRecordDetails() : "");
        }
    }
    
    private void saveRecord() {
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
        
        if (doctorCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            doctorCombo.requestFocus();
            return;
        }
        
        if (typeCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            typeCombo.requestFocus();
            return;
        }
        
        if (visitDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày khám!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            visitDateField.requestFocus();
            return;
        }
        
        try {
            // Get IDs
            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);
            
            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);
            
            String doctorSelected = (String) doctorCombo.getSelectedItem();
            int doctorId = Integer.parseInt(doctorSelected.split(" - ")[0]);
            
            String typeStr = (String) typeCombo.getSelectedItem();
            
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date visitDate;
            try {
                visitDate = sdf.parse(visitDateField.getText().trim());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày khám không đúng định dạng (dd/MM/yyyy)!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                visitDateField.requestFocus();
                return;
            }
            
            if (record == null) {
                // Insert
                String query = "INSERT INTO medical_records (customer_id, pet_id, doctor_id, " +
                              "medical_record_type, medical_record_visit_date, medical_record_summary, " +
                              "medical_record_details) VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                java.sql.Date sqlDate = new java.sql.Date(visitDate.getTime());
                
                int result = Database.executeUpdate(query,
                    customerId,
                    petId,
                    doctorId,
                    typeStr,
                    sqlDate,
                    summaryArea.getText().trim().isEmpty() ? null : summaryArea.getText().trim(),
                    detailsArea.getText().trim().isEmpty() ? null : detailsArea.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm hồ sơ khám bệnh thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE medical_records SET customer_id = ?, pet_id = ?, doctor_id = ?, " +
                              "medical_record_type = ?, medical_record_visit_date = ?, " +
                              "medical_record_summary = ?, medical_record_details = ? " +
                              "WHERE medical_record_id = ?";
                
                java.sql.Date sqlDate = new java.sql.Date(visitDate.getTime());
                
                int result = Database.executeUpdate(query,
                    customerId,
                    petId,
                    doctorId,
                    typeStr,
                    sqlDate,
                    summaryArea.getText().trim().isEmpty() ? null : summaryArea.getText().trim(),
                    detailsArea.getText().trim().isEmpty() ? null : detailsArea.getText().trim(),
                    record.getMedicalRecordId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ khám bệnh thành công!", "Thành công", 
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
    
    public boolean isSaved() {
        return saved;
    }
}
