package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.Customer;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.CustomerService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

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
    private CustomerService customerService;

    public AddEditCustomerDialog(JDialog parent, Customer customer) {
        super(parent, true);
        this.customer = customer;
        this.customerService = CustomerService.getInstance();
        initComponents();

        if (customer != null) {
            loadCustomerData();
            setTitle("Sửa khách hàng");
        } else {
            setTitle("Thêm khách hàng mới");
        }
    }

    private void initComponents() {
        setSize(560, 460);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getContentBackground());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

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
        noteArea = new JTextArea(3, GUIUtil.TEXT_FIELD_COLUMNS);
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
        saveButton.addActionListener(e -> saveCustomer());
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
        JTextField field = new JTextField(GUIUtil.TEXT_FIELD_COLUMNS);
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
        try {
            if (customer == null) {
                // Create new customer - validation will be done by domain model
                Customer newCustomer = new Customer(
                        nameField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim().isEmpty() ? null : emailField.getText().trim(),
                        identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim(),
                        addressField.getText().trim().isEmpty() ? null : addressField.getText().trim(),
                        noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim()
                );

                customerService.createCustomer(newCustomer);
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                // Update existing customer - validation will be done by domain model
                customer.setCustomerName(nameField.getText().trim());
                customer.setCustomerPhoneNumber(phoneField.getText().trim());
                customer.setCustomerEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                customer.setCustomerIdentityCard(identityCardField.getText().trim().isEmpty() ? null : identityCardField.getText().trim());
                customer.setCustomerAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                customer.setCustomerNote(noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim());

                customerService.updateCustomer(customer);
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
