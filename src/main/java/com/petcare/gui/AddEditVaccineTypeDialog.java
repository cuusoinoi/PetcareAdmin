package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.Vaccine;
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
 * Dialog for adding/editing vaccine type
 */
public class AddEditVaccineTypeDialog extends JDialog {
    private JTextField vaccineNameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Vaccine vaccine;
    
    public AddEditVaccineTypeDialog(JDialog parent, Vaccine vaccine) {
        super(parent, true);
        this.vaccine = vaccine;
        initComponents();
        
        if (vaccine != null) {
            loadVaccineData();
            setTitle("Sửa vaccine");
        } else {
            setTitle("Thêm vaccine mới");
        }
    }
    
    private void initComponents() {
        setSize(500, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Vaccine Name
        formPanel.add(createLabel("Tên vaccine *:"));
        vaccineNameField = createTextField();
        formPanel.add(vaccineNameField);
        
        // Description
        formPanel.add(createLabel("Mô tả:"));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        descriptionArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(descriptionArea);
        
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
        saveButton.addActionListener(e -> saveVaccine());
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
    
    private void loadVaccineData() {
        if (vaccine != null) {
            vaccineNameField.setText(vaccine.getVaccineName());
            descriptionArea.setText(vaccine.getDescription() != null ? vaccine.getDescription() : "");
        }
    }
    
    private void saveVaccine() {
        // Validation
        if (vaccineNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên vaccine!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            vaccineNameField.requestFocus();
            return;
        }
        
        try {
            if (vaccine == null) {
                // Insert
                String query = "INSERT INTO vaccines (vaccine_name, description) VALUES (?, ?)";
                
                int result = Database.executeUpdate(query,
                    vaccineNameField.getText().trim(),
                    descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm vaccine thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE vaccines SET vaccine_name = ?, description = ? " +
                              "WHERE vaccine_id = ?";
                
                int result = Database.executeUpdate(query,
                    vaccineNameField.getText().trim(),
                    descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim(),
                    vaccine.getVaccineId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật vaccine thành công!", "Thành công", 
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
