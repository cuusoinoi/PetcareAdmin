package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.UserService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Dialog for adding/editing user - uses UserService and domain User only
 */
public class AddEditUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullnameField;
    private JComboBox<String> roleCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private User user;
    private final UserService userService = UserService.getInstance();

    public AddEditUserDialog(JDialog parent, User user) {
        super(parent, true);
        this.user = user;
        initComponents();
        if (user != null) {
            loadUserData();
            setTitle("Sửa người dùng");
            passwordField.setEnabled(false);
        } else {
            setTitle("Thêm người dùng mới");
        }
    }

    private void initComponents() {
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createLabel("Username *:"));
        usernameField = createTextField();
        formPanel.add(usernameField);

        formPanel.add(createLabel("Mật khẩu" + (user == null ? " *:" : ":")));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(passwordField);

        formPanel.add(createLabel("Họ tên *:"));
        fullnameField = createTextField();
        formPanel.add(fullnameField);

        formPanel.add(createLabel("Vai trò *:"));
        roleCombo = new JComboBox<>();
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        roleCombo.addItem("Quản trị viên");
        roleCombo.addItem("Nhân viên");
        formPanel.add(roleCombo);

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
        saveButton.addActionListener(e -> saveUser());
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

    private void loadUserData() {
        if (user != null) {
            usernameField.setText(user.getUsername());
            fullnameField.setText(user.getFullname());
            if (user.getRole() != null) {
                roleCombo.setSelectedItem(user.getRole() == User.Role.ADMIN ? "Quản trị viên" : "Nhân viên");
            }
        }
    }

    private void saveUser() {
        String username = usernameField.getText().trim();
        String fullname = fullnameField.getText().trim();
        String roleLabel = (String) roleCombo.getSelectedItem();
        User.Role role = "Quản trị viên".equals(roleLabel) ? User.Role.ADMIN : User.Role.STAFF;

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập username!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        if (user == null && passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        if (fullname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            fullnameField.requestFocus();
            return;
        }

        try {
            if (user == null) {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setFullname(fullname);
                newUser.setRole(role);
                String plainPassword = new String(passwordField.getPassword());
                userService.createUser(newUser, plainPassword);
                JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                user.setUsername(username);
                user.setFullname(fullname);
                user.setRole(role);
                userService.updateUser(user);
                JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
