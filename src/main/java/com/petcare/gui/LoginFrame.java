package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Login Frame for Petcare Admin
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginFrame() {
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        setTitle("UIT Petcare - Đăng nhập Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 69, 19)); // Brown color
        headerPanel.setPreferredSize(new Dimension(0, 120));
        
        JLabel titleLabel = new JLabel("UIT PETCARE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Login form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(0, 400));
        
        // Username label
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setBounds(50, 50, 300, 25);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameLabel);
        
        // Username field
        usernameField = new JTextField();
        usernameField.setBounds(50, 80, 350, 40);
        usernameField.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 10; " +
            "placeholderText: Nhập tên đăng nhập");
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameField);
        
        // Password label
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setBounds(50, 140, 300, 25);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordLabel);
        
        // Password field
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 170, 350, 40);
        passwordField.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 10; " +
            "placeholderText: Nhập mật khẩu");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordField);
        
        // Login button
        loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(50, 250, 350, 45);
        loginButton.setBackground(new Color(139, 69, 19));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton);
        
        // Info label
        JLabel infoLabel = new JLabel("<html><center>Mặc định: admin / 123456</center></html>", 
            SwingConstants.CENTER);
        infoLabel.setBounds(50, 310, 350, 40);
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Set default values for testing
        usernameField.setText("admin");
        passwordField.setText("123456");
    }
    
    private void setupUI() {
        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> performLogin());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên đăng nhập!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập mật khẩu!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        
        // Authenticate user
        User user = authenticateUser(username, password);
        
        if (user != null) {
            // Close login frame
            this.dispose();
            
            // Open dashboard
            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame(user);
                dashboard.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                "Sai tên đăng nhập hoặc mật khẩu!", 
                "Lỗi đăng nhập", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    private User authenticateUser(String username, String password) {
        try {
            // Hash password with MD5 (matching PHP implementation)
            String hashedPassword = md5(password);
            
            // Query with PreparedStatement (safe from SQL Injection)
            String query = "SELECT id, username, fullname, avatar, role " +
                          "FROM users " +
                          "WHERE username = ? AND password = ? AND role IN ('admin', 'staff')";
            
            ResultSet rs = Database.executeQuery(query, username, hashedPassword);
            
            if (rs != null && rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullname(rs.getString("fullname"));
                user.setAvatar(rs.getString("avatar"));
                
                String roleStr = rs.getString("role");
                if ("admin".equalsIgnoreCase(roleStr)) {
                    user.setRole(User.Role.ADMIN);
                } else {
                    user.setRole(User.Role.STAFF);
                }
                
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi kết nối database: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    /**
     * MD5 hash function (matching PHP md5())
     */
    private String md5(String input) {
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
}
