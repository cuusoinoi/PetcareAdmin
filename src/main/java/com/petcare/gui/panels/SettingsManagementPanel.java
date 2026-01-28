package com.petcare.gui.panels;

import com.petcare.model.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Settings Management Panel
 */
public class SettingsManagementPanel extends JPanel {
    private JTextField clinicNameField;
    private JTextField clinicAddress1Field;
    private JTextField clinicAddress2Field;
    private JTextField phoneNumber1Field;
    private JTextField phoneNumber2Field;
    private JTextField representativeNameField;
    private JTextField checkoutHourField;
    private JTextField overtimeFeePerHourField;
    private JTextField defaultDailyRateField;
    private JTextField signingPlaceField;
    private JButton saveButton;
    private JButton refreshButton;
    
    public SettingsManagementPanel() {
        initComponents();
        loadSettings();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Cài đặt Hệ thống");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadSettings());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(Color.WHITE);
        
        // Clinic Name
        formPanel.add(createLabel("Tên phòng khám *:"));
        clinicNameField = createTextField();
        formPanel.add(clinicNameField);
        
        // Clinic Address 1
        formPanel.add(createLabel("Địa chỉ 1 *:"));
        clinicAddress1Field = createTextField();
        formPanel.add(clinicAddress1Field);
        
        // Clinic Address 2
        formPanel.add(createLabel("Địa chỉ 2:"));
        clinicAddress2Field = createTextField();
        formPanel.add(clinicAddress2Field);
        
        // Phone Number 1
        formPanel.add(createLabel("Số điện thoại 1 *:"));
        phoneNumber1Field = createTextField();
        formPanel.add(phoneNumber1Field);
        
        // Phone Number 2
        formPanel.add(createLabel("Số điện thoại 2:"));
        phoneNumber2Field = createTextField();
        formPanel.add(phoneNumber2Field);
        
        // Representative Name
        formPanel.add(createLabel("Tên người đại diện *:"));
        representativeNameField = createTextField();
        formPanel.add(representativeNameField);
        
        // Default Daily Rate
        formPanel.add(createLabel("Phí lưu chuồng mặc định/ngày (VNĐ):"));
        defaultDailyRateField = createTextField();
        defaultDailyRateField.putClientProperty("JTextField.placeholderText", "80000");
        formPanel.add(defaultDailyRateField);
        
        // Checkout Hour
        formPanel.add(createLabel("Giờ checkout mặc định (HH:mm):"));
        checkoutHourField = createTextField();
        checkoutHourField.putClientProperty("JTextField.placeholderText", "18:00");
        formPanel.add(checkoutHourField);
        
        // Overtime Fee Per Hour
        formPanel.add(createLabel("Phí trễ giờ/giờ (VNĐ):"));
        overtimeFeePerHourField = createTextField();
        overtimeFeePerHourField.putClientProperty("JTextField.placeholderText", "25000");
        formPanel.add(overtimeFeePerHourField);
        
        // Signing Place
        formPanel.add(createLabel("Nơi ký *:"));
        signingPlaceField = createTextField();
        formPanel.add(signingPlaceField);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Save button panel
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        savePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        savePanel.setBackground(Color.WHITE);
        
        saveButton = new JButton("💾 Lưu cài đặt");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new java.awt.Dimension(200, 40));
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveSettings());
        savePanel.add(saveButton);
        
        add(savePanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        return field;
    }
    
    private void loadSettings() {
        try {
            String query = "SELECT * FROM general_settings LIMIT 1";
            ResultSet rs = Database.executeQuery(query);
            
            if (rs != null && rs.next()) {
                clinicNameField.setText(rs.getString("clinic_name") != null ? rs.getString("clinic_name") : "");
                clinicAddress1Field.setText(rs.getString("clinic_address_1") != null ? rs.getString("clinic_address_1") : "");
                clinicAddress2Field.setText(rs.getString("clinic_address_2") != null ? rs.getString("clinic_address_2") : "");
                phoneNumber1Field.setText(rs.getString("phone_number_1") != null ? rs.getString("phone_number_1") : "");
                phoneNumber2Field.setText(rs.getString("phone_number_2") != null ? rs.getString("phone_number_2") : "");
                representativeNameField.setText(rs.getString("representative_name") != null ? rs.getString("representative_name") : "");
                
                String checkoutHour = rs.getString("checkout_hour");
                if (checkoutHour != null && checkoutHour.length() >= 5) {
                    checkoutHourField.setText(checkoutHour.substring(0, 5)); // Extract HH:mm from TIME format
                } else {
                    checkoutHourField.setText("18:00");
                }
                
                overtimeFeePerHourField.setText(rs.getInt("overtime_fee_per_hour") > 0 ? 
                    String.valueOf(rs.getInt("overtime_fee_per_hour")) : "25000");
                defaultDailyRateField.setText(rs.getInt("default_daily_rate") > 0 ? 
                    String.valueOf(rs.getInt("default_daily_rate")) : "80000");
                signingPlaceField.setText(rs.getString("signing_place") != null ? rs.getString("signing_place") : "");
            } else {
                // Initialize with defaults if no settings exist
                checkoutHourField.setText("18:00");
                overtimeFeePerHourField.setText("25000");
                defaultDailyRateField.setText("80000");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải cài đặt: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void saveSettings() {
        // Validation
        if (clinicNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng khám!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            clinicNameField.requestFocus();
            return;
        }
        
        if (clinicAddress1Field.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ 1!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            clinicAddress1Field.requestFocus();
            return;
        }
        
        if (phoneNumber1Field.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại 1!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            phoneNumber1Field.requestFocus();
            return;
        }
        
        if (representativeNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người đại diện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            representativeNameField.requestFocus();
            return;
        }
        
        if (signingPlaceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nơi ký!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            signingPlaceField.requestFocus();
            return;
        }
        
        try {
            int overtimeFee = 25000;
            if (!overtimeFeePerHourField.getText().trim().isEmpty()) {
                overtimeFee = Integer.parseInt(overtimeFeePerHourField.getText().trim());
            }
            
            int defaultDailyRate = 80000;
            if (!defaultDailyRateField.getText().trim().isEmpty()) {
                defaultDailyRate = Integer.parseInt(defaultDailyRateField.getText().trim());
            }
            
            String checkoutHour = checkoutHourField.getText().trim().isEmpty() ? "18:00:00" : 
                                 checkoutHourField.getText().trim() + ":00";
            
            // Check if settings exist
            String checkQuery = "SELECT COUNT(*) as count FROM general_settings";
            ResultSet rs = Database.executeQuery(checkQuery);
            boolean settingsExist = false;
            if (rs != null && rs.next()) {
                settingsExist = rs.getInt("count") > 0;
            }
            
            if (settingsExist) {
                // Update
                String query = "UPDATE general_settings SET clinic_name = ?, clinic_address_1 = ?, " +
                              "clinic_address_2 = ?, phone_number_1 = ?, phone_number_2 = ?, " +
                              "representative_name = ?, checkout_hour = ?, overtime_fee_per_hour = ?, " +
                              "default_daily_rate = ?, signing_place = ?";
                
                int result = Database.executeUpdate(query,
                    clinicNameField.getText().trim(),
                    clinicAddress1Field.getText().trim(),
                    clinicAddress2Field.getText().trim().isEmpty() ? null : clinicAddress2Field.getText().trim(),
                    phoneNumber1Field.getText().trim(),
                    phoneNumber2Field.getText().trim().isEmpty() ? null : phoneNumber2Field.getText().trim(),
                    representativeNameField.getText().trim(),
                    checkoutHour,
                    overtimeFee,
                    defaultDailyRate,
                    signingPlaceField.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Cập nhật cài đặt thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Insert
                String query = "INSERT INTO general_settings (clinic_name, clinic_address_1, " +
                              "clinic_address_2, phone_number_1, phone_number_2, representative_name, " +
                              "checkout_hour, overtime_fee_per_hour, default_daily_rate, signing_place) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                int result = Database.executeUpdate(query,
                    clinicNameField.getText().trim(),
                    clinicAddress1Field.getText().trim(),
                    clinicAddress2Field.getText().trim().isEmpty() ? null : clinicAddress2Field.getText().trim(),
                    phoneNumber1Field.getText().trim(),
                    phoneNumber2Field.getText().trim().isEmpty() ? null : phoneNumber2Field.getText().trim(),
                    representativeNameField.getText().trim(),
                    checkoutHour,
                    overtimeFee,
                    defaultDailyRate,
                    signingPlaceField.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Lưu cài đặt thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Phí trễ giờ hoặc phí lưu chuồng không hợp lệ!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public void refreshData() {
        loadSettings();
    }
}
