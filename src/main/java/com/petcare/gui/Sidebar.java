package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

/**
 * Sidebar navigation for Petcare Admin
 */
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
        setBackground(new Color(139, 69, 19)); // Brown color
        setPreferredSize(new Dimension(260, 0));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 69, 19));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel logoLabel = new JLabel("UIT PETCARE", JLabel.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Menu items panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1, 0, 5));
        menuPanel.setBackground(new Color(139, 69, 19));
        menuPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Dashboard
        dashboardBtn = createMenuButton("📊 Dashboard", true);
        dashboardBtn.addActionListener(e -> dashboard.showDashboard());
        menuPanel.add(dashboardBtn);
        
        // Separator
        menuPanel.add(createSeparator("QUẢN LÝ CHÍNH"));
        
        // Customer
        customerBtn = createMenuButton("👥 Khách hàng", false);
        customerBtn.addActionListener(e -> dashboard.showCustomerManagement());
        menuPanel.add(customerBtn);
        
        // Pet
        petBtn = createMenuButton("🐾 Thú cưng", false);
        petBtn.addActionListener(e -> dashboard.showPetManagement());
        menuPanel.add(petBtn);
        
        // Doctor
        doctorBtn = createMenuButton("👨‍⚕️ Bác sĩ", false);
        doctorBtn.addActionListener(e -> dashboard.showDoctorManagement());
        menuPanel.add(doctorBtn);
        
        // Separator
        menuPanel.add(createSeparator("KHÁM & ĐIỀU TRỊ"));
        
        // Medical Record
        medicalRecordBtn = createMenuButton("📋 Hồ sơ khám bệnh", false);
        medicalRecordBtn.addActionListener(e -> dashboard.showMedicalRecordManagement());
        menuPanel.add(medicalRecordBtn);
        
        // Vaccination
        vaccinationBtn = createMenuButton("💉 Tiêm chủng", false);
        vaccinationBtn.addActionListener(e -> dashboard.showVaccinationManagement());
        menuPanel.add(vaccinationBtn);
        
        // Treatment
        treatmentBtn = createMenuButton("🏥 Liệu trình điều trị", false);
        treatmentBtn.addActionListener(e -> dashboard.showTreatmentManagement());
        menuPanel.add(treatmentBtn);
        
        // Separator
        menuPanel.add(createSeparator("DỊCH VỤ"));
        
        // Pet Enclosure
        enclosureBtn = createMenuButton("🏠 Lưu chuồng", false);
        enclosureBtn.addActionListener(e -> dashboard.showEnclosureManagement());
        menuPanel.add(enclosureBtn);
        
        // Appointment
        appointmentBtn = createMenuButton("📅 Lịch hẹn", false);
        appointmentBtn.addActionListener(e -> dashboard.showAppointmentManagement());
        menuPanel.add(appointmentBtn);
        
        // Invoice
        invoiceBtn = createMenuButton("🧾 Hóa đơn", false);
        invoiceBtn.addActionListener(e -> dashboard.showInvoiceManagement());
        menuPanel.add(invoiceBtn);
        
        // Separator
        menuPanel.add(createSeparator("DANH MỤC"));
        
        // Service Types
        serviceTypeBtn = createMenuButton("🛎️ Dịch vụ", false);
        serviceTypeBtn.addActionListener(e -> dashboard.showServiceTypeManagement());
        menuPanel.add(serviceTypeBtn);
        
        // Medicines
        medicineBtn = createMenuButton("💊 Thuốc", false);
        medicineBtn.addActionListener(e -> dashboard.showMedicineManagement());
        menuPanel.add(medicineBtn);
        
        // Vaccine Types
        vaccineTypeBtn = createMenuButton("💉 Vaccine", false);
        vaccineTypeBtn.addActionListener(e -> dashboard.showVaccineTypeManagement());
        menuPanel.add(vaccineTypeBtn);
        
        // Separator
        menuPanel.add(createSeparator("HỆ THỐNG"));
        
        // Users
        userBtn = createMenuButton("👤 Người dùng", false);
        userBtn.addActionListener(e -> dashboard.showUserManagement());
        menuPanel.add(userBtn);
        
        // Settings
        settingsBtn = createMenuButton("⚙️ Cài đặt", false);
        settingsBtn.addActionListener(e -> dashboard.showSettingsManagement());
        menuPanel.add(settingsBtn);
        
        // Scroll pane for menu
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer with theme toggle and logout
        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        footerPanel.setBackground(new Color(139, 69, 19));
        footerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Theme toggle button
        JButton themeToggleBtn = new JButton("🌙 Giao diện tối");
        themeToggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        themeToggleBtn.setBackground(new Color(108, 117, 125));
        themeToggleBtn.setForeground(Color.WHITE);
        themeToggleBtn.setBorderPainted(false);
        themeToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        themeToggleBtn.addActionListener(e -> {
            com.petcare.util.ThemeManager.toggleTheme();
            updateThemeButton(themeToggleBtn);
            // Refresh UI
            dashboard.refreshTheme();
        });
        updateThemeButton(themeToggleBtn);
        footerPanel.add(themeToggleBtn);
        
        // Logout button
        JButton logoutBtn = new JButton("🚪 Đăng xuất");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        logoutBtn.addActionListener(e -> dashboard.logout());
        footerPanel.add(logoutBtn);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JToggleButton createMenuButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(selected ? new Color(160, 90, 30) : new Color(139, 69, 19));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(JToggleButton.LEFT);
        btn.setSelected(selected);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        
        // Hover effect
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
        separator.setFont(new Font("Segoe UI", Font.BOLD, 11));
        separator.setForeground(new Color(200, 200, 200));
        separator.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));
        return separator;
    }
    
    public void setSelectedButton(JToggleButton button) {
        // Reset all buttons
        resetAllButtons();
        
        // Set selected button
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
            btn.setText("☀️ Giao diện sáng");
        } else {
            btn.setText("🌙 Giao diện tối");
        }
    }
}
