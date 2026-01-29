package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.Doctor;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.DoctorService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

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
    private DoctorService doctorService;

    public AddEditDoctorDialog(JDialog parent, Doctor doctor) {
        super(parent, true);
        this.doctor = doctor;
        this.doctorService = DoctorService.getInstance();
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
        getContentPane().setBackground(ThemeManager.getContentBackground());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

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
        noteArea.setBackground(ThemeManager.getTextFieldBackground());
        noteArea.setForeground(ThemeManager.getTextFieldForeground());
        noteArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        noteArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(noteArea);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());

        saveButton = new JButton(EmojiFontHelper.withEmoji("💾", "Lưu"));
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveDoctor());
        buttonPanel.add(saveButton);

        cancelButton = new JButton(EmojiFontHelper.withEmoji("❌", "Hủy"));
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(ThemeManager.getButtonBackground());
        cancelButton.setForeground(ThemeManager.getButtonForeground());
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.getTitleForeground());
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(ThemeManager.getTextFieldBackground());
        field.setForeground(ThemeManager.getTextFieldForeground());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
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
        try {
            // Create Doctor domain model - validation will happen in setters
            if (doctor == null) {
                // Create new doctor
                Doctor doctorToSave = new Doctor(
                        nameField.getText().trim(),
                        phoneField.getText().trim(),
                        identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim(),
                        addressField.getText().trim(),
                        noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim()
                );

                // Save via service
                doctorService.createDoctor(doctorToSave);
                JOptionPane.showMessageDialog(this, "Thêm bác sĩ thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                // Update existing doctor
                doctor.setDoctorName(nameField.getText().trim());
                doctor.setDoctorPhoneNumber(phoneField.getText().trim());
                doctor.setDoctorIdentityCard(identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim());
                doctor.setDoctorAddress(addressField.getText().trim());
                doctor.setDoctorNote(noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim());

                // Save via service
                doctorService.updateDoctor(doctor);
                JOptionPane.showMessageDialog(this, "Cập nhật bác sĩ thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
