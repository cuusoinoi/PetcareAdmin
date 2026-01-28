package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.security.MessageDigest;
import java.sql.ResultSet;
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
 * Dialog for adding/editing user
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
    
    public AddEditUserDialog(JDialog parent, User user) {
        super(parent, true);
        this.user = user;
        initComponents();
        
        if (user != null) {
            loadUserData();
            setTitle("Sửa người dùng");
            passwordField.setEnabled(false); // Disable password field when editing
        } else {
            setTitle("Thêm người dùng mới");
        }
    }
    
    private void initComponents() {
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Username
        formPanel.add(createLabel("Username *:"));
        usernameField = createTextField();
        formPanel.add(usernameField);
        
        // Password
        formPanel.add(createLabel("Mật khẩu" + (user == null ? " *:" : ":")));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(passwordField);
        
        // Fullname
        formPanel.add(createLabel("Họ tên *:"));
        fullnameField = createTextField();
        formPanel.add(fullnameField);
        
        // Role
        formPanel.add(createLabel("Vai trò *:"));
        roleCombo = new JComboBox<>();
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        roleCombo.addItem("Quản trị viên");
        roleCombo.addItem("Nhân viên");
        formPanel.add(roleCombo);
        
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
        // Validation
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập username!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        if (user == null && passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        
        if (fullnameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            fullnameField.requestFocus();
            return;
        }
        
        try {
            String roleLabel = (String) roleCombo.getSelectedItem();
            String roleCode = "Quản trị viên".equals(roleLabel) ? "admin" : "staff";
            
            if (user == null) {
                // Check if username exists
                String checkQuery = "SELECT COUNT(*) as count FROM users WHERE username = ?";
                ResultSet rs = Database.executeQuery(checkQuery, usernameField.getText().trim());
                if (rs != null && rs.next() && rs.getInt("count") > 0) {
                    JOptionPane.showMessageDialog(this, "Username đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    usernameField.requestFocus();
                    return;
                }
                
                // Hash password
                String password = new String(passwordField.getPassword());
                String hashedPassword = md5Hash(password);
                
                // Insert
                String query = "INSERT INTO users (username, password, fullname, role) VALUES (?, ?, ?, ?)";
                
                int result = Database.executeUpdate(query,
                    usernameField.getText().trim(),
                    hashedPassword,
                    fullnameField.getText().trim(),
                    roleCode
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update (without password)
                String query = "UPDATE users SET username = ?, fullname = ?, role = ? WHERE user_id = ?";
                
                int result = Database.executeUpdate(query,
                    usernameField.getText().trim(),
                    fullnameField.getText().trim(),
                    roleCode,
                    user.getId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công!", "Thành công", 
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
    
    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
