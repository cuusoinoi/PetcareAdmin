package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.Doctor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for adding/editing doctor
 */
public class AddEditDoctorDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField identityCardField;
    private JTextField addressField;
    private JTextArea noteArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Doctor doctor;
    
    public AddEditDoctorDialog(JDialog parent, Doctor doctor) {
        super(parent, true);
        this.doctor = doctor;
        initComponents();
        
        if (doctor != null) {
            loadDoctorData();
            setTitle("Sửa bác sĩ");
        } else {
            setTitle("Thêm bác sĩ mới");
        }
    }
    
    private void initComponents() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Name
        formPanel.add(createLabel("Tên bác sĩ *:"));
        nameField = createTextField();
        formPanel.add(nameField);
        
        // Phone
        formPanel.add(createLabel("Số điện thoại *:"));
        phoneField = createTextField();
        formPanel.add(phoneField);
        
        // Identity Card
        formPanel.add(createLabel("CMND/CCCD:"));
        identityCardField = createTextField();
        formPanel.add(identityCardField);
        
        // Address
        formPanel.add(createLabel("Địa chỉ *:"));
        addressField = createTextField();
        formPanel.add(addressField);
        
        // Note
        formPanel.add(createLabel("Ghi chú:"));
        noteArea = new JTextArea(3, 20);
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noteArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        noteArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(noteArea);
        
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
        saveButton.addActionListener(e -> saveDoctor());
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
    
    private void loadDoctorData() {
        if (doctor != null) {
            nameField.setText(doctor.getDoctorName());
            phoneField.setText(doctor.getDoctorPhoneNumber());
            identityCardField.setText(doctor.getDoctorIdentityCard() != null ? doctor.getDoctorIdentityCard() : "");
            addressField.setText(doctor.getDoctorAddress());
            noteArea.setText(doctor.getDoctorNote() != null ? doctor.getDoctorNote() : "");
        }
    }
    
    private void saveDoctor() {
        // Validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return;
        }
        
        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            addressField.requestFocus();
            return;
        }
        
        try {
            if (doctor == null) {
                // Insert
                String query = "INSERT INTO doctors (doctor_name, doctor_phone_number, doctor_identity_card, " +
                              "doctor_address, doctor_note) VALUES (?, ?, ?, ?, ?)";
                
                int result = Database.executeUpdate(query,
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim(),
                    addressField.getText().trim(),
                    noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm bác sĩ thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE doctors SET doctor_name = ?, doctor_phone_number = ?, " +
                              "doctor_identity_card = ?, doctor_address = ?, doctor_note = ? " +
                              "WHERE doctor_id = ?";
                
                int result = Database.executeUpdate(query,
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim(),
                    addressField.getText().trim(),
                    noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim(),
                    doctor.getDoctorId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật bác sĩ thành công!", "Thành công", 
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
