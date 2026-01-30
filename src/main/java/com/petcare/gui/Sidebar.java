package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.LogoHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {
    private DashboardFrame dashboard;
    private ButtonGroup buttonGroup;
    public JToggleButton dashboardBtn;
    public JToggleButton customerBtn;
    public JToggleButton petBtn;
    public JToggleButton doctorBtn;
    public JToggleButton medicalRecordBtn;
    public JToggleButton vaccinationBtn;
    public JToggleButton treatmentBtn;
    public JToggleButton enclosureBtn;
    public JToggleButton appointmentBtn;
    public JToggleButton invoiceBtn;
    public JToggleButton printingTemplateBtn;
    public JToggleButton serviceTypeBtn;
    public JToggleButton medicineBtn;
    public JToggleButton vaccineTypeBtn;
    public JToggleButton userBtn;
    public JToggleButton settingsBtn;

    public Sidebar(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        this.buttonGroup = new ButtonGroup();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(139, 69, 19));
        setPreferredSize(new Dimension(300, 0));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 69, 19));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel logoLabel = new JLabel("UIT PETCARE", JLabel.CENTER);
        logoLabel.setIcon(LogoHelper.createLogoIcon(36));
        logoLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
        logoLabel.setIconTextGap(10);
        logoLabel.setFont(ThemeManager.getSemiboldFont(20));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(139, 69, 19));
        menuPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dashboardBtn = createMenuButton("📊", "Dashboard", true);
        dashboardBtn.addActionListener(e -> dashboard.showDashboard());
        menuPanel.add(dashboardBtn);
        menuPanel.add(createSeparator("QUẢN LÝ CHÍNH"));
        customerBtn = createMenuButton("👥", "Khách hàng", false);
        customerBtn.addActionListener(e -> dashboard.showCustomerManagement());
        menuPanel.add(customerBtn);
        petBtn = createMenuButton("🐾", "Thú cưng", false);
        petBtn.addActionListener(e -> dashboard.showPetManagement());
        menuPanel.add(petBtn);
        doctorBtn = createMenuButton("👨‍⚕️", "Bác sĩ", false);
        doctorBtn.addActionListener(e -> dashboard.showDoctorManagement());
        menuPanel.add(doctorBtn);
        menuPanel.add(createSeparator("KHÁM & ĐIỀU TRỊ"));
        medicalRecordBtn = createMenuButton("📋", "Hồ sơ khám bệnh", false);
        medicalRecordBtn.addActionListener(e -> dashboard.showMedicalRecordManagement());
        menuPanel.add(medicalRecordBtn);
        vaccinationBtn = createMenuButton("💉", "Tiêm chủng", false);
        vaccinationBtn.addActionListener(e -> dashboard.showVaccinationManagement());
        menuPanel.add(vaccinationBtn);
        treatmentBtn = createMenuButton("🏥", "Liệu trình điều trị", false);
        treatmentBtn.addActionListener(e -> dashboard.showTreatmentManagement());
        menuPanel.add(treatmentBtn);
        menuPanel.add(createSeparator("DỊCH VỤ"));
        enclosureBtn = createMenuButton("🏠", "Lưu chuồng", false);
        enclosureBtn.addActionListener(e -> dashboard.showEnclosureManagement());
        menuPanel.add(enclosureBtn);
        appointmentBtn = createMenuButton("📅", "Lịch hẹn", false);
        appointmentBtn.addActionListener(e -> dashboard.showAppointmentManagement());
        menuPanel.add(appointmentBtn);
        invoiceBtn = createMenuButton("🧾", "Hóa đơn", false);
        invoiceBtn.addActionListener(e -> dashboard.showInvoiceManagement());
        menuPanel.add(invoiceBtn);
        printingTemplateBtn = createMenuButton("📄", "Mẫu in lưu chuồng", false);
        printingTemplateBtn.addActionListener(e -> dashboard.showPrintingTemplate());
        menuPanel.add(printingTemplateBtn);
        menuPanel.add(createSeparator("DANH MỤC"));
        serviceTypeBtn = createMenuButton("🛎️", "Dịch vụ", false);
        serviceTypeBtn.addActionListener(e -> dashboard.showServiceTypeManagement());
        menuPanel.add(serviceTypeBtn);
        medicineBtn = createMenuButton("💊", "Thuốc", false);
        medicineBtn.addActionListener(e -> dashboard.showMedicineManagement());
        menuPanel.add(medicineBtn);
        vaccineTypeBtn = createMenuButton("💉", "Vaccine", false);
        vaccineTypeBtn.addActionListener(e -> dashboard.showVaccineTypeManagement());
        menuPanel.add(vaccineTypeBtn);
        menuPanel.add(createSeparator("HỆ THỐNG"));
        userBtn = createMenuButton("👤", "Người dùng", false);
        userBtn.addActionListener(e -> dashboard.showUserManagement());
        menuPanel.add(userBtn);
        settingsBtn = createMenuButton("⚙️", "Cài đặt", false);
        settingsBtn.addActionListener(e -> dashboard.showSettingsManagement());
        menuPanel.add(settingsBtn);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        javax.swing.JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(16);
        verticalBar.setPreferredSize(new Dimension(12, 0));
        verticalBar.putClientProperty(FlatClientProperties.STYLE, ""
                + "track:#5C3A0F;"
                + "thumb:#D4C4A8;"
                + "trackArc:6;"
                + "thumbArc:6;"
                + "width:12");
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        footerPanel.setBackground(new Color(139, 69, 19));
        footerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton themeToggleBtn = new JButton("Giao diện tối");
        themeToggleBtn.setIcon(EmojiFontHelper.createEmojiIcon("🌙"));
        themeToggleBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        themeToggleBtn.setIconTextGap(8);
        themeToggleBtn.setFont(ThemeManager.getSemiboldFont(13));
        themeToggleBtn.setBackground(new Color(108, 117, 125));
        themeToggleBtn.setForeground(Color.WHITE);
        themeToggleBtn.setBorderPainted(false);
        themeToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        themeToggleBtn.addActionListener(e -> {
            com.petcare.util.ThemeManager.toggleTheme();
            updateThemeButton(themeToggleBtn);
            dashboard.refreshTheme();
        });
        updateThemeButton(themeToggleBtn);
        footerPanel.add(themeToggleBtn);
        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setIcon(EmojiFontHelper.createEmojiIcon("🚪"));
        logoutBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        logoutBtn.setIconTextGap(8);
        logoutBtn.setFont(ThemeManager.getSemiboldFont(14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        logoutBtn.addActionListener(e -> dashboard.logout());
        footerPanel.add(logoutBtn);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JToggleButton createMenuButton(String emoji, String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text);
        btn.setIcon(EmojiFontHelper.createEmojiIcon(emoji));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(8);
        btn.setFont(ThemeManager.getSemiboldFont(14));
        btn.setBackground(selected ? new Color(160, 90, 30) : new Color(139, 69, 19));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setSelected(selected);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        btn.setMinimumSize(new Dimension(270, 40));
        btn.setPreferredSize(new Dimension(270, 40));
        btn.setMaximumSize(new Dimension(270, 40));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.isSelected()) {
                    btn.setBackground(new Color(160, 90, 30));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.isSelected()) {
                    btn.setBackground(new Color(139, 69, 19));
                }
            }
        });

        buttonGroup.add(btn);
        return btn;
    }

    private JLabel createSeparator(String text) {
        JLabel separator = new JLabel(text);
        separator.setFont(ThemeManager.getSemiboldFont(11));
        separator.setForeground(new Color(200, 200, 200));
        separator.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));
        return separator;
    }

    public void setSelectedButton(JToggleButton button) {
        resetAllButtons();
        if (button != null) {
            button.setSelected(true);
            button.setBackground(new Color(160, 90, 30));
        }
    }

    private void resetAllButtons() {
        dashboardBtn.setSelected(false);
        dashboardBtn.setBackground(new Color(139, 69, 19));
        customerBtn.setSelected(false);
        customerBtn.setBackground(new Color(139, 69, 19));
        petBtn.setSelected(false);
        petBtn.setBackground(new Color(139, 69, 19));
        doctorBtn.setSelected(false);
        doctorBtn.setBackground(new Color(139, 69, 19));
        medicalRecordBtn.setSelected(false);
        medicalRecordBtn.setBackground(new Color(139, 69, 19));
        vaccinationBtn.setSelected(false);
        vaccinationBtn.setBackground(new Color(139, 69, 19));
        treatmentBtn.setSelected(false);
        treatmentBtn.setBackground(new Color(139, 69, 19));
        enclosureBtn.setSelected(false);
        enclosureBtn.setBackground(new Color(139, 69, 19));
        appointmentBtn.setSelected(false);
        appointmentBtn.setBackground(new Color(139, 69, 19));
        invoiceBtn.setSelected(false);
        invoiceBtn.setBackground(new Color(139, 69, 19));
        printingTemplateBtn.setSelected(false);
        printingTemplateBtn.setBackground(new Color(139, 69, 19));
        serviceTypeBtn.setSelected(false);
        serviceTypeBtn.setBackground(new Color(139, 69, 19));
        medicineBtn.setSelected(false);
        medicineBtn.setBackground(new Color(139, 69, 19));
        vaccineTypeBtn.setSelected(false);
        vaccineTypeBtn.setBackground(new Color(139, 69, 19));
        userBtn.setSelected(false);
        userBtn.setBackground(new Color(139, 69, 19));
        settingsBtn.setSelected(false);
        settingsBtn.setBackground(new Color(139, 69, 19));
    }

    private void updateThemeButton(JButton btn) {
        boolean isDark = com.petcare.util.ThemeManager.isDarkMode();
        if (isDark) {
            btn.setIcon(EmojiFontHelper.createEmojiIcon("☀️"));
            btn.setText("Giao diện sáng");
        } else {
            btn.setIcon(EmojiFontHelper.createEmojiIcon("🌙"));
            btn.setText("Giao diện tối");
        }
    }
}
