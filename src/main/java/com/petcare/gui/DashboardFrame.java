package com.petcare.gui;

import com.petcare.model.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

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
        contentPanel.add(appointmentPanel, "APPOINTMENT");
        contentPanel.add(serviceTypePanel, "SERVICE_TYPE");
        contentPanel.add(medicinePanel, "MEDICINE");
        contentPanel.add(vaccineTypePanel, "VACCINE_TYPE");
        contentPanel.add(userPanel, "USER");
        contentPanel.add(settingsPanel, "SETTINGS");
        
        // Set default selection
        sidebar.setSelectedButton(sidebar.dashboardBtn);
        
        // Add components
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        // Show dashboard by default
        showDashboard();
    }
    
    // Navigation methods
    public void showDashboard() {
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
        cardLayout.show(contentPanel, "USER");
        userPanel.refreshData();
        sidebar.setSelectedButton(sidebar.userBtn);
    }
    
    public void showSettingsManagement() {
        cardLayout.show(contentPanel, "SETTINGS");
        settingsPanel.refreshData();
        sidebar.setSelectedButton(sidebar.settingsBtn);
    }
    
    public void refreshTheme() {
        // Update all UI components to reflect theme change
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.updateComponentTreeUI(this);
            pack();
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
