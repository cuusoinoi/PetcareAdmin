package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.UserService;
import com.petcare.util.LogoHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 69, 19));
        headerPanel.setPreferredSize(new Dimension(0, 120));

        JLabel titleLabel = new JLabel("UIT PETCARE", SwingConstants.CENTER);
        titleLabel.setIcon(LogoHelper.createLogoIcon(48));
        titleLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
        titleLabel.setIconTextGap(12);
        titleLabel.setFont(ThemeManager.getSemiboldFont(32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(0, 400));
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setBounds(50, 50, 300, 25);
        usernameLabel.setFont(ThemeManager.getModernFont(14));
        formPanel.add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setBounds(50, 80, 350, 40);
        usernameField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        usernameField.putClientProperty("JTextField.placeholderText", "Nhập tên đăng nhập");
        usernameField.setFont(ThemeManager.getModernFont(14));
        formPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setBounds(50, 140, 300, 25);
        passwordLabel.setFont(ThemeManager.getModernFont(14));
        formPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 170, 350, 40);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        passwordField.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu");
        passwordField.setFont(ThemeManager.getModernFont(14));
        formPanel.add(passwordField);
        loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(50, 250, 350, 45);
        loginButton.setBackground(new Color(139, 69, 19));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(ThemeManager.getSemiboldFont(16));
        loginButton.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton);
        JLabel infoLabel = new JLabel("<html><center>Mặc định: admin / 123456</center></html>",
                SwingConstants.CENTER);
        infoLabel.setBounds(50, 310, 350, 40);
        infoLabel.setFont(ThemeManager.getModernFont(12).deriveFont(Font.ITALIC));
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
        usernameField.setText("admin");
        passwordField.setText("123456");
    }

    private void setupUI() {
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

        try {
            User user = UserService.getInstance().authenticate(username, password);
            if (user != null) {
                this.dispose();
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
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
