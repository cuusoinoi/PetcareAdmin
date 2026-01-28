package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.ServiceType;
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
 * Dialog for adding/editing service type
 */
public class AddEditServiceTypeDialog extends JDialog {
    private JTextField serviceNameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private ServiceType service;
    
    public AddEditServiceTypeDialog(JDialog parent, ServiceType service) {
        super(parent, true);
        this.service = service;
        initComponents();
        
        if (service != null) {
            loadServiceData();
            setTitle("Sửa dịch vụ");
        } else {
            setTitle("Thêm dịch vụ mới");
        }
    }
    
    private void initComponents() {
        setSize(500, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Service Name
        formPanel.add(createLabel("Tên dịch vụ *:"));
        serviceNameField = createTextField();
        formPanel.add(serviceNameField);
        
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
        
        // Price
        formPanel.add(createLabel("Giá (VNĐ) *:"));
        priceField = createTextField();
        priceField.putClientProperty("JTextField.placeholderText", "0");
        formPanel.add(priceField);
        
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
        saveButton.addActionListener(e -> saveService());
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
    
    private void loadServiceData() {
        if (service != null) {
            serviceNameField.setText(service.getServiceName());
            descriptionArea.setText(service.getDescription() != null ? service.getDescription() : "");
            priceField.setText(String.valueOf((int)service.getPrice()));
        }
    }
    
    private void saveService() {
        // Validation
        if (serviceNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            serviceNameField.requestFocus();
            return;
        }
        
        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return;
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            
            if (service == null) {
                // Insert
                String query = "INSERT INTO service_types (service_name, description, price) VALUES (?, ?, ?)";
                
                int result = Database.executeUpdate(query,
                    serviceNameField.getText().trim(),
                    descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim(),
                    price
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE service_types SET service_name = ?, description = ?, price = ? " +
                              "WHERE service_type_id = ?";
                
                int result = Database.executeUpdate(query,
                    serviceNameField.getText().trim(),
                    descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim(),
                    price,
                    service.getServiceTypeId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
