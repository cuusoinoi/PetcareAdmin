package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.UserService;
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
import javax.swing.JPasswordField;

/**
 * Dialog for changing user password - uses UserService only
 */
public class ChangePasswordDialog extends JDialog {
    private final int userId;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton saveButton;
    private JButton cancelButton;
    private final UserService userService = UserService.getInstance();

    public ChangePasswordDialog(JDialog parent, int userId) {
        super(parent, true);
        this.userId = userId;
        initComponents();
        setTitle("Đổi mật khẩu");
    }

    private void initComponents() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createLabel("Mật khẩu mới *:"));
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        newPasswordField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(newPasswordField);

        formPanel.add(createLabel("Xác nhận mật khẩu *:"));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        confirmPasswordField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(confirmPasswordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        saveButton = new JButton("💾 Lưu");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> changePassword());
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

    private void changePassword() {
        if (newPasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.requestFocus();
            return;
        }
        try {
            userService.changePassword(userId, newPassword);
            JOptionPane.showMessageDialog(this,
                "Đổi mật khẩu thành công!",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
