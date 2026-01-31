package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.InvoiceDetailItem;
import com.petcare.model.domain.InvoiceMedicineItem;
import com.petcare.model.domain.InvoiceVaccinationItem;
import com.petcare.model.domain.MedicalRecord;
import com.petcare.model.domain.ServiceType;
import com.petcare.model.entity.MedicalRecordListDto;
import com.petcare.model.entity.MedicalRecordMedicineListDto;
import com.petcare.model.entity.MedicalRecordServiceItemListDto;
import com.petcare.model.entity.VaccinationForInvoiceDto;
import com.petcare.service.CustomerService;
import com.petcare.service.InvoiceService;
import com.petcare.service.MedicalRecordMedicineService;
import com.petcare.service.MedicalRecordService;
import com.petcare.service.MedicalRecordServiceItemService;
import com.petcare.service.PetService;
import com.petcare.service.PetVaccinationService;
import com.petcare.service.ServiceTypeService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dialog: Xuất hóa đơn từ lượt khám. Chọn mã lượt khám → tự điền khách/thú cưng và thuốc đã kê; thêm dịch vụ thủ công.
 */
public class AddInvoiceFromVisitDialog extends JDialog {
    private JComboBox<String> medicalRecordCombo;
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;
    private JComboBox<String> serviceCombo;
    private JSpinner quantitySpinner;
    private JTable medicineTable;
    private DefaultTableModel medicineTableModel;
    private JTable vaccineTable;
    private DefaultTableModel vaccineTableModel;
    private JTextField discountField;
    private JTextField depositField;
    private JLabel subtotalLabel;
    private JLabel totalLabel;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private List<ServiceType> serviceTypes;
    private List<MedicalRecordMedicineListDto> medicineDetailsFromVisit = new ArrayList<>();
    private List<VaccinationForInvoiceDto> vaccinationDetailsFromVisit = new ArrayList<>();

    public AddInvoiceFromVisitDialog(Window owner) {
        this(owner, null);
    }

    public AddInvoiceFromVisitDialog(Window owner, Integer preselectedMedicalRecordId) {
        super(owner, "Xuất hóa đơn từ lượt khám");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        loadMedicalRecords();
        loadCustomers();
        loadServiceTypes();
        if (preselectedMedicalRecordId != null) {
            for (int i = 0; i < medicalRecordCombo.getItemCount(); i++) {
                String item = medicalRecordCombo.getItemAt(i);
                if (item.startsWith(preselectedMedicalRecordId + " - ")) {
                    medicalRecordCombo.setSelectedIndex(i);
                    onMedicalRecordSelected();
                    break;
                }
            }
        }
    }

    private void initComponents() {
        setSize(750, 650);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setTitle("Xuất hóa đơn từ lượt khám");
        getContentPane().setBackground(ThemeManager.getContentBackground());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

        formPanel.add(createLabel("Lượt khám *:"));
        medicalRecordCombo = new JComboBox<>();
        medicalRecordCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medicalRecordCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        medicalRecordCombo.addActionListener(e -> onMedicalRecordSelected());
        formPanel.add(medicalRecordCombo);

        formPanel.add(createLabel("Khách hàng:"));
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        customerCombo.addActionListener(e -> loadPetsByCustomer());
        formPanel.add(customerCombo);

        formPanel.add(createLabel("Thú cưng:"));
        petCombo = new JComboBox<>();
        petCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(petCombo);

        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Dịch vụ (từ lượt khám + thêm thủ công)"));
        servicesPanel.setBackground(ThemeManager.getContentBackground());

        JPanel addServicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addServicePanel.setBackground(ThemeManager.getContentBackground());
        serviceCombo = new JComboBox<>();
        serviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        serviceCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        serviceCombo.setPreferredSize(new Dimension(300, 30));
        addServicePanel.add(new JLabel("Dịch vụ:"));
        addServicePanel.add(serviceCombo);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addServicePanel.add(new JLabel("Số lượng:"));
        addServicePanel.add(quantitySpinner);
        JButton addServiceBtn = new JButton(EmojiFontHelper.withEmoji("➕", "Thêm"));
        addServiceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addServiceBtn.addActionListener(e -> addService());
        addServicePanel.add(addServiceBtn);
        JButton removeServiceBtn = new JButton(EmojiFontHelper.withEmoji("➖", "Xóa dịch vụ đã chọn"));
        removeServiceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeServiceBtn.addActionListener(e -> {
            int row = serviceTable.getSelectedRow();
            if (row >= 0) {
                serviceTableModel.removeRow(row);
                updateTotals();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        addServicePanel.add(removeServiceBtn);
        servicesPanel.add(addServicePanel, BorderLayout.NORTH);

        String[] serviceColumns = {"Dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"};
        serviceTableModel = new DefaultTableModel(serviceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        serviceTable = new JTable(serviceTableModel);
        serviceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        serviceTable.setRowHeight(25);
        serviceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ThemeManager.applyTableTheme(serviceTable);
        servicesPanel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        JPanel medicinePanel = new JPanel(new BorderLayout());
        medicinePanel.setBorder(BorderFactory.createTitledBorder("Thuốc (từ lượt khám)"));
        medicinePanel.setBackground(ThemeManager.getContentBackground());
        String[] medColumns = {"Thuốc", "Số lượng", "Đơn giá", "Thành tiền"};
        medicineTableModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineTable.setRowHeight(25);
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ThemeManager.applyTableTheme(medicineTable);
        medicinePanel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);

        JPanel vaccinePanel = new JPanel(new BorderLayout());
        vaccinePanel.setBorder(BorderFactory.createTitledBorder("Tiêm chủng (từ lượt khám)"));
        vaccinePanel.setBackground(ThemeManager.getContentBackground());
        String[] vacColumns = {"Vaccine", "Số lượng", "Đơn giá", "Thành tiền"};
        vaccineTableModel = new DefaultTableModel(vacColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vaccineTable = new JTable(vaccineTableModel);
        vaccineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vaccineTable.setRowHeight(25);
        vaccineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ThemeManager.applyTableTheme(vaccineTable);
        vaccinePanel.add(new JScrollPane(vaccineTable), BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        summaryPanel.setBackground(ThemeManager.getContentBackground());
        summaryPanel.add(createLabel("Giảm giá (VNĐ):"));
        discountField = new JTextField("0");
        discountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discountField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        discountField.addActionListener(e -> updateTotals());
        summaryPanel.add(discountField);
        summaryPanel.add(createLabel("Đặt cọc (VNĐ):"));
        depositField = new JTextField("0");
        depositField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        depositField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        depositField.addActionListener(e -> updateTotals());
        summaryPanel.add(depositField);
        summaryPanel.add(createLabel("Tổng tiền (VNĐ):"));
        subtotalLabel = new JLabel("0");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalLabel.setForeground(ThemeManager.getTableForeground());
        summaryPanel.add(subtotalLabel);
        summaryPanel.add(createLabel("Thành tiền (VNĐ):"));
        totalLabel = new JLabel("0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(ThemeManager.isDarkMode() ? new Color(255, 180, 100) : new Color(139, 69, 19));
        summaryPanel.add(totalLabel);

        JPanel centerContent = new JPanel(new BorderLayout(0, 10));
        centerContent.setBackground(ThemeManager.getContentBackground());
        centerContent.add(formPanel, BorderLayout.NORTH);
        JPanel tablesPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        tablesPanel.setBackground(ThemeManager.getContentBackground());
        tablesPanel.add(servicesPanel);
        tablesPanel.add(vaccinePanel);
        tablesPanel.add(medicinePanel);
        centerContent.add(tablesPanel, BorderLayout.CENTER);
        centerContent.add(summaryPanel, BorderLayout.SOUTH);
        JScrollPane scrollPane = new JScrollPane(centerContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ThemeManager.getContentBackground());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());
        saveButton = new JButton("Lưu");
        saveButton.setIcon(EmojiFontHelper.createEmojiIcon("💾", Color.WHITE));
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveInvoice());
        buttonPanel.add(saveButton);
        cancelButton = new JButton(EmojiFontHelper.withEmoji("❌", "Hủy"));
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(ThemeManager.getButtonBackground());
        cancelButton.setForeground(ThemeManager.getButtonForeground());
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.getTitleForeground());
        return label;
    }

    private void loadMedicalRecords() {
        medicalRecordCombo.removeAllItems();
        medicalRecordCombo.addItem("-- Chọn lượt khám --");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (MedicalRecordListDto dto : MedicalRecordService.getInstance().getRecordsForList()) {
                String dateStr = dto.getVisitDate() != null ? sdf.format(dto.getVisitDate()) : "";
                medicalRecordCombo.addItem(dto.getMedicalRecordId() + " - " + dateStr + " - " + (dto.getCustomerName() != null ? dto.getCustomerName() : "") + " - " + (dto.getPetName() != null ? dto.getPetName() : ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onMedicalRecordSelected() {
        if (medicalRecordCombo.getSelectedIndex() <= 0) {
            if (customerCombo.getItemCount() > 0) {
                customerCombo.setSelectedIndex(0);
            }
            petCombo.removeAllItems();
            petCombo.addItem("-- Chọn thú cưng --");
            serviceTableModel.setRowCount(0);
            medicineDetailsFromVisit.clear();
            medicineTableModel.setRowCount(0);
            vaccinationDetailsFromVisit.clear();
            vaccineTableModel.setRowCount(0);
            updateTotals();
            return;
        }
        String sel = (String) medicalRecordCombo.getSelectedItem();
        int medicalRecordId = Integer.parseInt(sel.split(" - ")[0]);
        try {
            MedicalRecord record = MedicalRecordService.getInstance().getRecordById(medicalRecordId);
            if (record != null) {
                for (int i = 0; i < customerCombo.getItemCount(); i++) {
                    String item = customerCombo.getItemAt(i);
                    if (item.startsWith(record.getCustomerId() + " - ")) {
                        customerCombo.setSelectedIndex(i);
                        loadPetsByCustomer();
                        break;
                    }
                }
                for (int i = 0; i < petCombo.getItemCount(); i++) {
                    String item = petCombo.getItemAt(i);
                    if (item.startsWith(record.getPetId() + " - ")) {
                        petCombo.setSelectedIndex(i);
                        break;
                    }
                }
                // Dịch vụ đã thêm trong hồ sơ khám
                serviceTableModel.setRowCount(0);
                for (MedicalRecordServiceItemListDto dto : MedicalRecordServiceItemService.getInstance().getServicesByMedicalRecordId(medicalRecordId)) {
                    serviceTableModel.addRow(new Object[]{
                            dto.getServiceName(),
                            dto.getQuantity(),
                            dto.getUnitPrice(),
                            dto.getTotalPrice()
                    });
                }
                // Thuốc đã kê trong lượt khám
                medicineDetailsFromVisit = MedicalRecordMedicineService.getInstance().getMedicinesByMedicalRecordId(medicalRecordId);
                medicineTableModel.setRowCount(0);
                for (MedicalRecordMedicineListDto dto : medicineDetailsFromVisit) {
                    medicineTableModel.addRow(new Object[]{
                            dto.getMedicineName(),
                            dto.getQuantity(),
                            dto.getUnitPrice(),
                            dto.getTotalPrice()
                    });
                }
                // Tiêm chủng gắn với lượt khám (mỗi mũi = 1, đơn giá từ vaccine)
                vaccinationDetailsFromVisit = PetVaccinationService.getInstance().getVaccinationsByMedicalRecordId(medicalRecordId);
                vaccineTableModel.setRowCount(0);
                for (VaccinationForInvoiceDto dto : vaccinationDetailsFromVisit) {
                    int qty = 1;
                    int total = dto.getUnitPrice() * qty;
                    vaccineTableModel.addRow(new Object[]{
                            dto.getVaccineName(),
                            qty,
                            dto.getUnitPrice(),
                            total
                    });
                }
            }
            updateTotals();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCustomers() {
        customerCombo.removeAllItems();
        customerCombo.addItem("-- Chọn khách hàng --");
        try {
            CustomerService.getInstance().getAllCustomers().forEach(c -> {
                customerCombo.addItem(c.getCustomerId() + " - " + c.getCustomerName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPetsByCustomer() {
        petCombo.removeAllItems();
        petCombo.addItem("-- Chọn thú cưng --");
        if (customerCombo.getSelectedIndex() == 0) return;
        try {
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            PetService.getInstance().getPetsByCustomerId(customerId).forEach(p -> {
                petCombo.addItem(p.getPetId() + " - " + p.getPetName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadServiceTypes() {
        serviceCombo.removeAllItems();
        serviceCombo.addItem("-- Chọn dịch vụ --");
        serviceTypes = new ArrayList<>();
        try {
            for (ServiceType st : ServiceTypeService.getInstance().getAllServiceTypes()) {
                serviceTypes.add(st);
                serviceCombo.addItem(st.getServiceName() + " - " + formatCurrency((int) st.getPrice()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addService() {
        if (serviceCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idx = serviceCombo.getSelectedIndex() - 1;
        ServiceType st = serviceTypes.get(idx);
        int quantity = (Integer) quantitySpinner.getValue();
        int unitPrice = (int) st.getPrice();
        int totalPrice = quantity * unitPrice;
        serviceTableModel.addRow(new Object[]{st.getServiceName(), quantity, unitPrice, totalPrice});
        updateTotals();
        serviceCombo.setSelectedIndex(0);
        quantitySpinner.setValue(1);
    }

    private void updateTotals() {
        int servicesTotal = 0;
        for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
            servicesTotal += (Integer) serviceTableModel.getValueAt(i, 3);
        }
        int vaccinesTotal = 0;
        for (int i = 0; i < vaccineTableModel.getRowCount(); i++) {
            vaccinesTotal += (Integer) vaccineTableModel.getValueAt(i, 3);
        }
        int medicinesTotal = 0;
        for (int i = 0; i < medicineTableModel.getRowCount(); i++) {
            medicinesTotal += (Integer) medicineTableModel.getValueAt(i, 3);
        }
        int subtotal = servicesTotal + vaccinesTotal + medicinesTotal;
        int discount = 0;
        try {
            discount = Integer.parseInt(discountField.getText().trim());
        } catch (NumberFormatException e) {
            discount = 0;
        }
        int deposit = 0;
        try {
            deposit = Integer.parseInt(depositField.getText().trim());
        } catch (NumberFormatException e) {
            deposit = 0;
        }
        int total = subtotal - discount - deposit;
        if (total < 0) total = 0;
        subtotalLabel.setText(formatCurrency(subtotal));
        totalLabel.setText(formatCurrency(total));
    }

    private void saveInvoice() {
        if (medicalRecordCombo.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lượt khám!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            medicalRecordCombo.requestFocus();
            return;
        }
        if (customerCombo.getSelectedIndex() == 0 || petCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Khách hàng và thú cưng phải được điền từ lượt khám.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (serviceTableModel.getRowCount() == 0 && medicineDetailsFromVisit.isEmpty() && vaccinationDetailsFromVisit.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cần ít nhất một dịch vụ, thuốc hoặc tiêm chủng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String mrSel = (String) medicalRecordCombo.getSelectedItem();
            int medicalRecordId = Integer.parseInt(mrSel.split(" - ")[0]);
            String customerSel = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSel.split(" - ")[0]);
            String petSel = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSel.split(" - ")[0]);

            int subtotal = 0;
            for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
                subtotal += (Integer) serviceTableModel.getValueAt(i, 3);
            }
            for (VaccinationForInvoiceDto dto : vaccinationDetailsFromVisit) {
                subtotal += dto.getUnitPrice() * 1;
            }
            for (MedicalRecordMedicineListDto dto : medicineDetailsFromVisit) {
                subtotal += dto.getTotalPrice();
            }
            int discount = 0;
            try {
                discount = Integer.parseInt(discountField.getText().trim());
            } catch (NumberFormatException e) {
                discount = 0;
            }
            int deposit = 0;
            try {
                deposit = Integer.parseInt(depositField.getText().trim());
            } catch (NumberFormatException e) {
                deposit = 0;
            }
            int totalAmount = subtotal - discount - deposit;
            if (totalAmount < 0) totalAmount = 0;

            List<InvoiceDetailItem> details = new ArrayList<>();
            for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
                String serviceName = (String) serviceTableModel.getValueAt(i, 0);
                int quantity = (Integer) serviceTableModel.getValueAt(i, 1);
                int unitPrice = (Integer) serviceTableModel.getValueAt(i, 2);
                int totalPrice = (Integer) serviceTableModel.getValueAt(i, 3);
                ServiceType st = ServiceTypeService.getInstance().getServiceTypeByName(serviceName);
                if (st != null) {
                    InvoiceDetailItem item = new InvoiceDetailItem();
                    item.setServiceTypeId(st.getServiceTypeId());
                    item.setQuantity(quantity);
                    item.setUnitPrice(unitPrice);
                    item.setTotalPrice(totalPrice);
                    details.add(item);
                }
            }
            List<InvoiceMedicineItem> medicineDetails = new ArrayList<>();
            for (MedicalRecordMedicineListDto dto : medicineDetailsFromVisit) {
                InvoiceMedicineItem item = new InvoiceMedicineItem();
                item.setMedicineId(dto.getMedicineId());
                item.setQuantity(dto.getQuantity());
                item.setUnitPrice(dto.getUnitPrice());
                item.setTotalPrice(dto.getTotalPrice());
                medicineDetails.add(item);
            }
            List<InvoiceVaccinationItem> vaccinationDetails = new ArrayList<>();
            for (VaccinationForInvoiceDto dto : vaccinationDetailsFromVisit) {
                InvoiceVaccinationItem item = new InvoiceVaccinationItem();
                item.setVaccineId(dto.getVaccineId());
                item.setQuantity(1);
                item.setUnitPrice(dto.getUnitPrice());
                item.setTotalPrice(dto.getUnitPrice() * 1);
                vaccinationDetails.add(item);
            }

            int invoiceId = InvoiceService.getInstance().createInvoiceFromVisit(
                    customerId, petId, medicalRecordId, new Date(),
                    discount, subtotal, deposit, totalAmount,
                    details.isEmpty() ? null : details,
                    medicineDetails.isEmpty() ? null : medicineDetails,
                    vaccinationDetails.isEmpty() ? null : vaccinationDetails);
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công! Hóa đơn #" + invoiceId, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }

    public boolean isSaved() {
        return saved;
    }
}
