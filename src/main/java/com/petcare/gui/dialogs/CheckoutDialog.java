package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.ThemeManager;
import com.petcare.model.domain.InvoiceDetailItem;
import com.petcare.model.domain.PetEnclosure;
import com.petcare.service.CustomerService;
import com.petcare.service.GeneralSettingService;
import com.petcare.service.InvoiceService;
import com.petcare.service.PetEnclosureService;
import com.petcare.service.PetService;
import com.petcare.model.domain.ServiceType;
import com.petcare.service.ServiceTypeService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog for checkout pet enclosure and create invoice
 */
public class CheckoutDialog extends JDialog {
    private PetEnclosure enclosure;
    private JLabel customerLabel;
    private JLabel petLabel;
    private JLabel daysLabel;
    private JLabel enclosureFeeLabel;
    private JLabel overtimeFeeLabel;
    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;
    private JComboBox<String> serviceCombo;
    private JSpinner quantitySpinner;
    private JButton addServiceButton;
    private JTextField discountField;
    private JLabel subtotalLabel;
    private JLabel totalLabel;
    private JButton checkoutButton;
    private JButton cancelButton;
    private boolean saved = false;
    private List<ServiceType> serviceTypes; // domain ServiceType from ServiceTypeService
    private int checkoutHour = 18; // Default 18:00
    private int overtimeFeePerHour = 25000; // Default
    
    public CheckoutDialog(JDialog parent, PetEnclosure enclosure) {
        super(parent, true);
        this.enclosure = enclosure;
        loadSettings();
        initComponents();
        calculateFees();
        loadServiceTypes();
    }
    
    private void loadSettings() {
        try {
            checkoutHour = GeneralSettingService.getInstance().getCheckoutHour();
            overtimeFeePerHour = GeneralSettingService.getInstance().getOvertimeFeePerHour();
        } catch (com.petcare.model.exception.PetcareException ex) {
            ex.printStackTrace();
        }
    }
    
    private void initComponents() {
        setSize(800, 650);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Check-out Lưu chuồng");
        getContentPane().setBackground(ThemeManager.getContentBackground());
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.setBackground(ThemeManager.getContentBackground());
        
        infoPanel.add(createLabel("Khách hàng:"));
        customerLabel = new JLabel();
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerLabel.setForeground(ThemeManager.getTableForeground());
        infoPanel.add(customerLabel);
        
        infoPanel.add(createLabel("Thú cưng:"));
        petLabel = new JLabel();
        petLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petLabel.setForeground(ThemeManager.getTableForeground());
        infoPanel.add(petLabel);
        
        infoPanel.add(createLabel("Số ngày lưu chuồng:"));
        daysLabel = new JLabel();
        daysLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        daysLabel.setForeground(ThemeManager.getTableForeground());
        infoPanel.add(daysLabel);
        
        infoPanel.add(createLabel("Phí lưu chuồng:"));
        enclosureFeeLabel = new JLabel();
        enclosureFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        enclosureFeeLabel.setForeground(ThemeManager.getTableForeground());
        infoPanel.add(enclosureFeeLabel);
        
        infoPanel.add(createLabel("Phí trễ giờ:"));
        overtimeFeeLabel = new JLabel();
        overtimeFeeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        overtimeFeeLabel.setForeground(ThemeManager.getTableForeground());
        infoPanel.add(overtimeFeeLabel);
        
        // Load customer and pet info
        loadCustomerAndPetInfo();
        
        // Services panel
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Dịch vụ bổ sung"));
        servicesPanel.setBackground(ThemeManager.getContentBackground());
        
        // Add service panel
        JPanel addServicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addServicePanel.setBackground(ThemeManager.getContentBackground());
        
        serviceCombo = new JComboBox<>();
        serviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        serviceCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        serviceCombo.setPreferredSize(new java.awt.Dimension(300, 30));
        addServicePanel.add(new JLabel("Dịch vụ:"));
        addServicePanel.add(serviceCombo);
        
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addServicePanel.add(new JLabel("Số lượng:"));
        addServicePanel.add(quantitySpinner);
        
        addServiceButton = new JButton(EmojiFontHelper.withEmoji("➕", "Thêm"));
        addServiceButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addServiceButton.addActionListener(e -> addService());
        addServicePanel.add(addServiceButton);
        
        servicesPanel.add(addServicePanel, BorderLayout.NORTH);
        
        // Service table
        String[] columns = {"Dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"};
        serviceTableModel = new DefaultTableModel(columns, 0) {
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
        
        // Remove service button
        JButton removeServiceButton = new JButton(EmojiFontHelper.withEmoji("➖", "Xóa dịch vụ đã chọn"));
        removeServiceButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeServiceButton.addActionListener(e -> {
            int selectedRow = serviceTable.getSelectedRow();
            if (selectedRow >= 0) {
                serviceTableModel.removeRow(selectedRow);
                updateTotals();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableButtonPanel.setBackground(ThemeManager.getContentBackground());
        tableButtonPanel.add(removeServiceButton);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ThemeManager.getContentBackground());
        tablePanel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        servicesPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        summaryPanel.setBackground(ThemeManager.getContentBackground());
        
        summaryPanel.add(createLabel("Giảm giá (VNĐ):"));
        discountField = new JTextField("0");
        discountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discountField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        discountField.addActionListener(e -> updateTotals());
        summaryPanel.add(discountField);
        
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
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.getContentBackground());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(servicesPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());
        
        checkoutButton = new JButton(EmojiFontHelper.withEmoji("✅", "Check-out & Tạo hóa đơn"));
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(40, 167, 69));
        checkoutButton.setForeground(Color.WHITE); // nút Check-out màu xanh, chữ trắng
        checkoutButton.setBorderPainted(false);
        checkoutButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        checkoutButton.addActionListener(e -> processCheckout());
        buttonPanel.add(checkoutButton);
        
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
    
    private void loadCustomerAndPetInfo() {
        try {
            com.petcare.model.domain.Customer c = CustomerService.getInstance().getCustomerById(enclosure.getCustomerId());
            com.petcare.model.domain.Pet p = PetService.getInstance().getPetById(enclosure.getPetId());
            if (c != null) customerLabel.setText(c.getCustomerName());
            if (p != null) petLabel.setText(p.getPetName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void calculateFees() {
        if (enclosure.getCheckInDate() == null) {
            return;
        }
        
        Date checkInDate = enclosure.getCheckInDate();
        Date checkOutDate = new Date();
        
        // Calculate days
        long diffInMillis = checkOutDate.getTime() - checkInDate.getTime();
        long days = diffInMillis / (1000 * 60 * 60 * 24) + 1; // +1 to include both days
        
        int enclosureFee = (int) (days * enclosure.getDailyRate());
        
        // Calculate overtime fee
        Calendar cal = Calendar.getInstance();
        cal.setTime(checkOutDate);
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int overtimeFee = 0;
        
        if (currentHour >= checkoutHour) {
            int overtimeHours = currentHour - checkoutHour;
            overtimeFee = overtimeHours * overtimeFeePerHour;
        }
        
        daysLabel.setText(String.valueOf(days) + " ngày");
        enclosureFeeLabel.setText(formatCurrency(enclosureFee));
        overtimeFeeLabel.setText(formatCurrency(overtimeFee));
        
        // Add enclosure fee as first service
        if (serviceTableModel.getRowCount() == 0) {
            serviceTableModel.addRow(new Object[]{
                "Lưu chuồng (" + days + " ngày)",
                (int)days,
                enclosure.getDailyRate(),
                enclosureFee,
                "🗑️"
            });
        }
        
        // Add overtime fee if exists
        if (overtimeFee > 0 && serviceTableModel.getRowCount() == 1) {
            serviceTableModel.addRow(new Object[]{
                "Phụ thu trễ giờ",
                1,
                overtimeFee,
                overtimeFee
            });
        }
        
        updateTotals();
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
        } catch (com.petcare.model.exception.PetcareException ex) {
            ex.printStackTrace();
        }
    }
    
    private void addService() {
        if (serviceCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!", "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedIndex = serviceCombo.getSelectedIndex() - 1;
        ServiceType service = serviceTypes.get(selectedIndex);
        int quantity = (Integer) quantitySpinner.getValue();
        int unitPrice = (int) service.getPrice();
        int totalPrice = quantity * unitPrice;
        
        serviceTableModel.addRow(new Object[]{
            service.getServiceName(),
            quantity,
            unitPrice,
            totalPrice,
            "🗑️"
        });
        
        updateTotals();
        serviceCombo.setSelectedIndex(0);
        quantitySpinner.setValue(1);
    }
    
    private void updateTotals() {
        int subtotal = 0;
        
        for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
            subtotal += (Integer) serviceTableModel.getValueAt(i, 3);
        }
        
        int discount = 0;
        try {
            discount = Integer.parseInt(discountField.getText().trim());
        } catch (NumberFormatException ex) {
            discount = 0;
        }
        
        int total = subtotal - discount - enclosure.getDeposit();
        if (total < 0) total = 0;
        
        subtotalLabel.setText(formatCurrency(subtotal));
        totalLabel.setText(formatCurrency(total));
    }
    
    private void processCheckout() {
        try {
            // Calculate totals
            int subtotal = 0;
            for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
                subtotal += (Integer) serviceTableModel.getValueAt(i, 3);
            }
            
            int discount = 0;
            try {
                discount = Integer.parseInt(discountField.getText().trim());
            } catch (NumberFormatException ex) {
                discount = 0;
            }
            
            int totalAmount = subtotal - discount - enclosure.getDeposit();
            if (totalAmount < 0) totalAmount = 0;
            
            // Update enclosure status
            Date checkOutDate = new Date();
            PetEnclosureService.getInstance().updateCheckOut(enclosure.getPetEnclosureId(), checkOutDate);

            // Build invoice details
            List<InvoiceDetailItem> details = new ArrayList<>();
            for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
                String serviceName = (String) serviceTableModel.getValueAt(i, 0);
                int quantity = (Integer) serviceTableModel.getValueAt(i, 1);
                int unitPrice = (Integer) serviceTableModel.getValueAt(i, 2);
                int totalPrice = (Integer) serviceTableModel.getValueAt(i, 3);
                int serviceTypeId = findServiceTypeIdByName(serviceName);
                if (serviceTypeId > 0) {
                    InvoiceDetailItem item = new InvoiceDetailItem();
                    item.setServiceTypeId(serviceTypeId);
                    item.setQuantity(quantity);
                    item.setUnitPrice(unitPrice);
                    item.setTotalPrice(totalPrice);
                    details.add(item);
                }
            }
            int invoiceId = InvoiceService.getInstance().createInvoice(
                enclosure.getCustomerId(), enclosure.getPetId(), enclosure.getPetEnclosureId(),
                checkOutDate, discount, subtotal, enclosure.getDeposit(), totalAmount, details);

            JOptionPane.showMessageDialog(this, 
                "Check-out thành công! Hóa đơn #" + invoiceId + " đã được tạo.", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private int findServiceTypeIdByName(String serviceName) {
        try {
            ServiceTypeService svc = ServiceTypeService.getInstance();
            ServiceType s = svc.getServiceTypeByName(serviceName);
            if (s == null && serviceName.startsWith("Lưu chuồng")) {
                s = svc.getServiceTypeByNamePrefix("Lưu chuồng");
            } else if (s == null && serviceName.startsWith("Phụ thu trễ giờ")) {
                s = svc.getServiceTypeByNamePrefix("Phụ thu trễ giờ");
            }
            return s != null ? s.getServiceTypeId() : 0;
        } catch (com.petcare.model.exception.PetcareException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
    
    public boolean isSaved() {
        return saved;
    }
}
