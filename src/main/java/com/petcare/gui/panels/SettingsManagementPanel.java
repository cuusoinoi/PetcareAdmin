package com.petcare.gui.panels;

import com.petcare.model.domain.GeneralSetting;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.GeneralSettingService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Settings Management Panel - uses GeneralSettingService only
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
    private final GeneralSettingService settingService = GeneralSettingService.getInstance();

    public SettingsManagementPanel() {
        initComponents();
        loadSettings();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Cài đặt Hệ thống");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadSettings());
        buttonPanel.add(refreshButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createLabel("Tên phòng khám *:"));
        clinicNameField = createTextField();
        formPanel.add(clinicNameField);

        formPanel.add(createLabel("Địa chỉ 1 *:"));
        clinicAddress1Field = createTextField();
        formPanel.add(clinicAddress1Field);

        formPanel.add(createLabel("Địa chỉ 2:"));
        clinicAddress2Field = createTextField();
        formPanel.add(clinicAddress2Field);

        formPanel.add(createLabel("Số điện thoại 1 *:"));
        phoneNumber1Field = createTextField();
        formPanel.add(phoneNumber1Field);

        formPanel.add(createLabel("Số điện thoại 2:"));
        phoneNumber2Field = createTextField();
        formPanel.add(phoneNumber2Field);

        formPanel.add(createLabel("Tên người đại diện *:"));
        representativeNameField = createTextField();
        formPanel.add(representativeNameField);

        formPanel.add(createLabel("Phí lưu chuồng mặc định/ngày (VNĐ):"));
        defaultDailyRateField = createTextField();
        defaultDailyRateField.putClientProperty("JTextField.placeholderText", "80000");
        formPanel.add(defaultDailyRateField);

        formPanel.add(createLabel("Giờ checkout mặc định (HH:mm):"));
        checkoutHourField = createTextField();
        checkoutHourField.putClientProperty("JTextField.placeholderText", "18:00");
        formPanel.add(checkoutHourField);

        formPanel.add(createLabel("Phí trễ giờ/giờ (VNĐ):"));
        overtimeFeePerHourField = createTextField();
        overtimeFeePerHourField.putClientProperty("JTextField.placeholderText", "25000");
        formPanel.add(overtimeFeePerHourField);

        formPanel.add(createLabel("Nơi ký *:"));
        signingPlaceField = createTextField();
        formPanel.add(signingPlaceField);

        add(formPanel, BorderLayout.CENTER);

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        savePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        savePanel.setBackground(Color.WHITE);
        saveButton = new JButton("Lưu cài đặt");
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
        return field;
    }

    private void loadSettings() {
        try {
            GeneralSetting s = settingService.getSettings();
            if (s != null) {
                clinicNameField.setText(s.getClinicName() != null ? s.getClinicName() : "");
                clinicAddress1Field.setText(s.getClinicAddress1() != null ? s.getClinicAddress1() : "");
                clinicAddress2Field.setText(s.getClinicAddress2() != null ? s.getClinicAddress2() : "");
                phoneNumber1Field.setText(s.getPhoneNumber1() != null ? s.getPhoneNumber1() : "");
                phoneNumber2Field.setText(s.getPhoneNumber2() != null ? s.getPhoneNumber2() : "");
                representativeNameField.setText(s.getRepresentativeName() != null ? s.getRepresentativeName() : "");
                checkoutHourField.setText(s.getCheckoutHour() != null ? s.getCheckoutHour() : "18:00");
                overtimeFeePerHourField.setText(s.getOvertimeFeePerHour() > 0 ? String.valueOf(s.getOvertimeFeePerHour()) : "25000");
                defaultDailyRateField.setText(s.getDefaultDailyRate() > 0 ? String.valueOf(s.getDefaultDailyRate()) : "80000");
                signingPlaceField.setText(s.getSigningPlace() != null ? s.getSigningPlace() : "");
            } else {
                checkoutHourField.setText("18:00");
                overtimeFeePerHourField.setText("25000");
                defaultDailyRateField.setText("80000");
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải cài đặt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSettings() {
        try {
            int overtimeFee = 25000;
            if (!overtimeFeePerHourField.getText().trim().isEmpty()) {
                overtimeFee = Integer.parseInt(overtimeFeePerHourField.getText().trim());
            }
            int defaultDailyRate = 80000;
            if (!defaultDailyRateField.getText().trim().isEmpty()) {
                defaultDailyRate = Integer.parseInt(defaultDailyRateField.getText().trim());
            }
            String checkoutHour = checkoutHourField.getText().trim().isEmpty() ? "18:00" : checkoutHourField.getText().trim();

            GeneralSetting s = new GeneralSetting();
            s.setClinicName(clinicNameField.getText().trim());
            s.setClinicAddress1(clinicAddress1Field.getText().trim());
            s.setClinicAddress2(clinicAddress2Field.getText().trim().isEmpty() ? null : clinicAddress2Field.getText().trim());
            s.setPhoneNumber1(phoneNumber1Field.getText().trim());
            s.setPhoneNumber2(phoneNumber2Field.getText().trim().isEmpty() ? null : phoneNumber2Field.getText().trim());
            s.setRepresentativeName(representativeNameField.getText().trim());
            s.setCheckoutHour(checkoutHour);
            s.setOvertimeFeePerHour(overtimeFee);
            s.setDefaultDailyRate(defaultDailyRate);
            s.setSigningPlace(signingPlaceField.getText().trim());

            settingService.saveSettings(s);
            JOptionPane.showMessageDialog(this, "Lưu cài đặt thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Phí trễ giờ hoặc phí lưu chuồng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        loadSettings();
    }
}
