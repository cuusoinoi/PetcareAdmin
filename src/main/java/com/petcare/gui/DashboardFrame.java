package com.petcare.gui;

import com.petcare.gui.panels.AppointmentManagementPanel;
import com.petcare.gui.panels.CustomerManagementPanel;
import com.petcare.gui.panels.DashboardPanel;
import com.petcare.gui.panels.DoctorManagementPanel;
import com.petcare.gui.panels.InvoiceManagementPanel;
import com.petcare.gui.panels.MedicalRecordManagementPanel;
import com.petcare.gui.panels.MedicineManagementPanel;
import com.petcare.gui.panels.PetEnclosureManagementPanel;
import com.petcare.gui.panels.PetManagementPanel;
import com.petcare.gui.panels.PrintingTemplatePanel;
import com.petcare.gui.panels.ServiceTypeManagementPanel;
import com.petcare.gui.panels.SettingsManagementPanel;
import com.petcare.gui.panels.TreatmentManagementPanel;
import com.petcare.gui.panels.UserManagementPanel;
import com.petcare.gui.panels.VaccinationManagementPanel;
import com.petcare.gui.panels.VaccineTypeManagementPanel;
import com.petcare.model.domain.User;

import javax.swing.*;
import java.awt.*;

/**
 * Main Dashboard Frame
 * This will contain the sidebar navigation and content panels
 */
public class DashboardFrame extends JFrame {
    private User currentUser;
    private Sidebar sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panels
    private DashboardPanel dashboardPanel;
    private CustomerManagementPanel customerPanel;
    private PetManagementPanel petPanel;
    private DoctorManagementPanel doctorPanel;
    private MedicalRecordManagementPanel medicalRecordPanel;
    private VaccinationManagementPanel vaccinationPanel;
    private TreatmentManagementPanel treatmentPanel;
    private PetEnclosureManagementPanel enclosurePanel;
    private InvoiceManagementPanel invoicePanel;
    private PrintingTemplatePanel printingTemplatePanel;
    private AppointmentManagementPanel appointmentPanel;
    private ServiceTypeManagementPanel serviceTypePanel;
    private MedicineManagementPanel medicinePanel;
    private VaccineTypeManagementPanel vaccineTypePanel;
    private UserManagementPanel userPanel;
    private SettingsManagementPanel settingsPanel;

    public DashboardFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void initComponents() {
        setTitle("UIT Petcare - " + currentUser.getFullname());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Create sidebar
        sidebar = new Sidebar(this);

        // Create content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(com.petcare.util.ThemeManager.getContentBackground());

        // Create panels
        dashboardPanel = new DashboardPanel();
        customerPanel = new CustomerManagementPanel();
        petPanel = new PetManagementPanel();
        doctorPanel = new DoctorManagementPanel();
        medicalRecordPanel = new MedicalRecordManagementPanel();
        vaccinationPanel = new VaccinationManagementPanel();
        treatmentPanel = new TreatmentManagementPanel();
        enclosurePanel = new PetEnclosureManagementPanel();
        invoicePanel = new InvoiceManagementPanel();
        printingTemplatePanel = new PrintingTemplatePanel();
        appointmentPanel = new AppointmentManagementPanel();
        serviceTypePanel = new ServiceTypeManagementPanel();
        medicinePanel = new MedicineManagementPanel();
        vaccineTypePanel = new VaccineTypeManagementPanel();
        userPanel = new UserManagementPanel();
        settingsPanel = new SettingsManagementPanel();

        // Add panels to card layout
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(customerPanel, "CUSTOMER");
        contentPanel.add(petPanel, "PET");
        contentPanel.add(doctorPanel, "DOCTOR");
        contentPanel.add(medicalRecordPanel, "MEDICAL_RECORD");
        contentPanel.add(vaccinationPanel, "VACCINATION");
        contentPanel.add(treatmentPanel, "TREATMENT");
        contentPanel.add(enclosurePanel, "ENCLOSURE");
        contentPanel.add(invoicePanel, "INVOICE");
        contentPanel.add(printingTemplatePanel, "PRINTING_TEMPLATE");
        contentPanel.add(appointmentPanel, "APPOINTMENT");
        contentPanel.add(serviceTypePanel, "SERVICE_TYPE");
        contentPanel.add(medicinePanel, "MEDICINE");
        contentPanel.add(vaccineTypePanel, "VACCINE_TYPE");
        contentPanel.add(userPanel, "USER");
        contentPanel.add(settingsPanel, "SETTINGS");

        // Set default selection và màn hình mặc định theo quyền
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        if (currentUser.getRole() == User.Role.ADMIN) {
            sidebar.setSelectedButton(sidebar.dashboardBtn);
            showDashboard();
        } else {
            sidebar.setSelectedButton(sidebar.customerBtn);
            showCustomerManagement();
        }
    }

    // Navigation methods
    public void showDashboard() {
        if (currentUser.getRole() != User.Role.ADMIN) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xem Dashboard.", "Phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        dashboardPanel.refreshData();
        cardLayout.show(contentPanel, "DASHBOARD");
        sidebar.setSelectedButton(sidebar.dashboardBtn);
    }

    public void showCustomerManagement() {
        cardLayout.show(contentPanel, "CUSTOMER");
        customerPanel.refreshData();
        sidebar.setSelectedButton(sidebar.customerBtn);
    }

    public void showPetManagement() {
        cardLayout.show(contentPanel, "PET");
        petPanel.refreshData();
        sidebar.setSelectedButton(sidebar.petBtn);
    }

    public void showDoctorManagement() {
        cardLayout.show(contentPanel, "DOCTOR");
        doctorPanel.refreshData();
        sidebar.setSelectedButton(sidebar.doctorBtn);
    }

    public void showMedicalRecordManagement() {
        cardLayout.show(contentPanel, "MEDICAL_RECORD");
        medicalRecordPanel.refreshData();
        sidebar.setSelectedButton(sidebar.medicalRecordBtn);
    }

    public void showVaccinationManagement() {
        cardLayout.show(contentPanel, "VACCINATION");
        vaccinationPanel.refreshData();
        sidebar.setSelectedButton(sidebar.vaccinationBtn);
    }

    public void showTreatmentManagement() {
        cardLayout.show(contentPanel, "TREATMENT");
        treatmentPanel.refreshData();
        sidebar.setSelectedButton(sidebar.treatmentBtn);
    }

    public void showEnclosureManagement() {
        cardLayout.show(contentPanel, "ENCLOSURE");
        enclosurePanel.refreshData();
        sidebar.setSelectedButton(sidebar.enclosureBtn);
    }

    public void showAppointmentManagement() {
        cardLayout.show(contentPanel, "APPOINTMENT");
        appointmentPanel.refreshData();
        sidebar.setSelectedButton(sidebar.appointmentBtn);
    }

    public void showInvoiceManagement() {
        cardLayout.show(contentPanel, "INVOICE");
        invoicePanel.refreshData();
        sidebar.setSelectedButton(sidebar.invoiceBtn);
    }

    public void showPrintingTemplate() {
        cardLayout.show(contentPanel, "PRINTING_TEMPLATE");
        printingTemplatePanel.refreshData();
        sidebar.setSelectedButton(sidebar.printingTemplateBtn);
    }

    public void showServiceTypeManagement() {
        cardLayout.show(contentPanel, "SERVICE_TYPE");
        serviceTypePanel.refreshData();
        sidebar.setSelectedButton(sidebar.serviceTypeBtn);
    }

    public void showMedicineManagement() {
        cardLayout.show(contentPanel, "MEDICINE");
        medicinePanel.refreshData();
        sidebar.setSelectedButton(sidebar.medicineBtn);
    }

    public void showVaccineTypeManagement() {
        cardLayout.show(contentPanel, "VACCINE_TYPE");
        vaccineTypePanel.refreshData();
        sidebar.setSelectedButton(sidebar.vaccineTypeBtn);
    }

    public void showUserManagement() {
        if (currentUser.getRole() != User.Role.ADMIN) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập mục Người dùng.", "Phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cardLayout.show(contentPanel, "USER");
        userPanel.refreshData();
        sidebar.setSelectedButton(sidebar.userBtn);
    }

    public void showSettingsManagement() {
        if (currentUser.getRole() != User.Role.ADMIN) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập mục Cài đặt.", "Phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cardLayout.show(contentPanel, "SETTINGS");
        settingsPanel.refreshData();
        sidebar.setSelectedButton(sidebar.settingsBtn);
    }

    public void refreshTheme() {
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.updateComponentTreeUI(this);
            contentPanel.setBackground(com.petcare.util.ThemeManager.getContentBackground());
            dashboardPanel.updateTheme();
            customerPanel.updateTheme();
            petPanel.updateTheme();
            doctorPanel.updateTheme();
            medicalRecordPanel.updateTheme();
            vaccinationPanel.updateTheme();
            treatmentPanel.updateTheme();
            enclosurePanel.updateTheme();
            invoicePanel.updateTheme();
            printingTemplatePanel.updateTheme();
            appointmentPanel.updateTheme();
            serviceTypePanel.updateTheme();
            medicinePanel.updateTheme();
            vaccineTypePanel.updateTheme();
            userPanel.updateTheme();
            settingsPanel.updateTheme();
            revalidate();
            repaint();
        });
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
