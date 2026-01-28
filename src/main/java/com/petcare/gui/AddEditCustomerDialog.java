package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Customer;
import com.petcare.model.Database;
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
 * Dialog for adding/editing customer
 */
public class AddEditCustomerDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField identityCardField;
    private JTextField addressField;
    private JTextArea noteArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Customer customer;
    
    public AddEditCustomerDialog(JDialog parent, Customer customer) {
        super(parent, true);
        this.customer = customer;
        initComponents();
        
        if (customer != null) {
            loadCustomerData();
            setTitle("Sửa khách hàng");
        } else {
            setTitle("Thêm khách hàng mới");
        }
    }
    
    private void initComponents() {
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Name
        formPanel.add(createLabel("Tên khách hàng *:"));
        nameField = createTextField();
        formPanel.add(nameField);
        
        // Phone
        formPanel.add(createLabel("Số điện thoại *:"));
        phoneField = createTextField();
        formPanel.add(phoneField);
        
        // Email
        formPanel.add(createLabel("Email:"));
        emailField = createTextField();
        formPanel.add(emailField);
        
        // Identity Card
        formPanel.add(createLabel("CMND/CCCD:"));
        identityCardField = createTextField();
        formPanel.add(identityCardField);
        
        // Address
        formPanel.add(createLabel("Địa chỉ:"));
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
        saveButton.addActionListener(e -> saveCustomer());
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
    
    private void loadCustomerData() {
        if (customer != null) {
            nameField.setText(customer.getCustomerName());
            phoneField.setText(customer.getCustomerPhoneNumber());
            emailField.setText(customer.getCustomerEmail() != null ? customer.getCustomerEmail() : "");
            identityCardField.setText(customer.getCustomerIdentityCard() != null ? customer.getCustomerIdentityCard() : "");
            addressField.setText(customer.getCustomerAddress() != null ? customer.getCustomerAddress() : "");
            noteArea.setText(customer.getCustomerNote() != null ? customer.getCustomerNote() : "");
        }
    }
    
    private void saveCustomer() {
        // Validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return;
        }
        
        try {
            if (customer == null) {
                // Insert
                String query = "INSERT INTO customers (customer_name, customer_phone_number, customer_email, " +
                              "customer_identity_card, customer_address, customer_note) " +
                              "VALUES (?, ?, ?, ?, ?, ?)";
                
                int result = Database.executeUpdate(query,
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim().isEmpty() ? null : emailField.getText().trim(),
                    identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim(),
                    addressField.getText().trim().isEmpty() ? null : addressField.getText().trim(),
                    noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE customers SET customer_name = ?, customer_phone_number = ?, " +
                              "customer_email = ?, customer_identity_card = ?, customer_address = ?, " +
                              "customer_note = ? WHERE customer_id = ?";
                
                int result = Database.executeUpdate(query,
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim().isEmpty() ? null : emailField.getText().trim(),
                    identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim(),
                    addressField.getText().trim().isEmpty() ? null : addressField.getText().trim(),
                    noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim(),
                    customer.getCustomerId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thành công", 
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
