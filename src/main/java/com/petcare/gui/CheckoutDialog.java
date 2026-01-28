package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.PetEnclosure;
import com.petcare.model.ServiceType;
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
    private List<ServiceType> serviceTypes;
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
            String query = "SELECT checkout_hour, overtime_fee_per_hour FROM general_settings LIMIT 1";
            ResultSet rs = Database.executeQuery(query);
            
            if (rs != null && rs.next()) {
                String checkoutHourStr = rs.getString("checkout_hour");
                if (checkoutHourStr != null) {
                    checkoutHour = Integer.parseInt(checkoutHourStr.split(":")[0]);
                }
                overtimeFeePerHour = rs.getInt("overtime_fee_per_hour");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void initComponents() {
        setSize(800, 650);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Check-out Lưu chuồng");
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.setBackground(Color.WHITE);
        
        infoPanel.add(createLabel("Khách hàng:"));
        customerLabel = new JLabel();
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(customerLabel);
        
        infoPanel.add(createLabel("Thú cưng:"));
        petLabel = new JLabel();
        petLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(petLabel);
        
        infoPanel.add(createLabel("Số ngày lưu chuồng:"));
        daysLabel = new JLabel();
        daysLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(daysLabel);
        
        infoPanel.add(createLabel("Phí lưu chuồng:"));
        enclosureFeeLabel = new JLabel();
        enclosureFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(enclosureFeeLabel);
        
        infoPanel.add(createLabel("Phí trễ giờ:"));
        overtimeFeeLabel = new JLabel();
        overtimeFeeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(overtimeFeeLabel);
        
        // Load customer and pet info
        loadCustomerAndPetInfo();
        
        // Services panel
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Dịch vụ bổ sung"));
        servicesPanel.setBackground(Color.WHITE);
        
        // Add service panel
        JPanel addServicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addServicePanel.setBackground(Color.WHITE);
        
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
        
        addServiceButton = new JButton("➕ Thêm");
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
        
        // Remove service button
        JButton removeServiceButton = new JButton("➖ Xóa dịch vụ đã chọn");
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
        tableButtonPanel.setBackground(Color.WHITE);
        tableButtonPanel.add(removeServiceButton);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        servicesPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        summaryPanel.setBackground(Color.WHITE);
        
        summaryPanel.add(createLabel("Giảm giá (VNĐ):"));
        discountField = new JTextField("0");
        discountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discountField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        discountField.addActionListener(e -> updateTotals());
        summaryPanel.add(discountField);
        
        summaryPanel.add(createLabel("Tổng tiền (VNĐ):"));
        subtotalLabel = new JLabel("0");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryPanel.add(subtotalLabel);
        
        summaryPanel.add(createLabel("Thành tiền (VNĐ):"));
        totalLabel = new JLabel("0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(139, 69, 19));
        summaryPanel.add(totalLabel);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(servicesPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        checkoutButton = new JButton("✅ Check-out & Tạo hóa đơn");
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(40, 167, 69));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setBorderPainted(false);
        checkoutButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        checkoutButton.addActionListener(e -> processCheckout());
        buttonPanel.add(checkoutButton);
        
        cancelButton = new JButton("❌ Hủy");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
    
    private void loadCustomerAndPetInfo() {
        try {
            String query = "SELECT c.customer_name, p.pet_name " +
                          "FROM customers c, pets p " +
                          "WHERE c.customer_id = ? AND p.pet_id = ?";
            ResultSet rs = Database.executeQuery(query, 
                enclosure.getCustomerId(), enclosure.getPetId());
            
            if (rs != null && rs.next()) {
                customerLabel.setText(rs.getString("customer_name"));
                petLabel.setText(rs.getString("pet_name"));
            }
        } catch (SQLException ex) {
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
            String query = "SELECT service_type_id, service_name, price FROM service_types ORDER BY service_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                ServiceType st = new ServiceType();
                st.setServiceTypeId(rs.getInt("service_type_id"));
                st.setServiceName(rs.getString("service_name"));
                st.setPrice(rs.getDouble("price"));
                serviceTypes.add(st);
                
                serviceCombo.addItem(st.getServiceName() + " - " + formatCurrency((int)st.getPrice()));
            }
        } catch (SQLException ex) {
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
            String updateQuery = "UPDATE pet_enclosures SET check_out_date = ?, pet_enclosure_status = 'Check Out' " +
                               "WHERE pet_enclosure_id = ?";
            Database.executeUpdate(updateQuery, new java.sql.Timestamp(checkOutDate.getTime()), 
                enclosure.getPetEnclosureId());
            
            // Create invoice
            String invoiceQuery = "INSERT INTO invoices (customer_id, pet_id, pet_enclosure_id, invoice_date, " +
                                "discount, subtotal, deposit, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            java.sql.Timestamp invoiceDate = new java.sql.Timestamp(checkOutDate.getTime());
            
            // Get generated invoice ID
            java.sql.Connection conn = Database.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(invoiceQuery, 
                java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, enclosure.getCustomerId());
            ps.setInt(2, enclosure.getPetId());
            ps.setInt(3, enclosure.getPetEnclosureId());
            ps.setTimestamp(4, invoiceDate);
            ps.setInt(5, discount);
            ps.setInt(6, subtotal);
            ps.setInt(7, enclosure.getDeposit());
            ps.setInt(8, totalAmount);
            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int invoiceId = 0;
            if (generatedKeys.next()) {
                invoiceId = generatedKeys.getInt(1);
            }
            
            // Add invoice details
            for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
                String serviceName = (String) serviceTableModel.getValueAt(i, 0);
                int quantity = (Integer) serviceTableModel.getValueAt(i, 1);
                int unitPrice = (Integer) serviceTableModel.getValueAt(i, 2);
                int totalPrice = (Integer) serviceTableModel.getValueAt(i, 3);
                
                // Find service_type_id by name
                int serviceTypeId = findServiceTypeIdByName(serviceName);
                if (serviceTypeId > 0) {
                    String detailQuery = "INSERT INTO invoice_details (invoice_id, service_type_id, quantity, " +
                                       "unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
                    Database.executeUpdate(detailQuery, invoiceId, serviceTypeId, quantity, unitPrice, totalPrice);
                }
            }
            
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
            // Handle special service names
            if (serviceName.startsWith("Lưu chuồng")) {
                String query = "SELECT service_type_id FROM service_types WHERE service_name LIKE 'Lưu chuồng%' LIMIT 1";
                ResultSet rs = Database.executeQuery(query);
                if (rs != null && rs.next()) {
                    return rs.getInt("service_type_id");
                }
            } else if (serviceName.startsWith("Phụ thu trễ giờ")) {
                String query = "SELECT service_type_id FROM service_types WHERE service_name LIKE 'Phụ thu trễ giờ%' LIMIT 1";
                ResultSet rs = Database.executeQuery(query);
                if (rs != null && rs.next()) {
                    return rs.getInt("service_type_id");
                }
            } else {
                String query = "SELECT service_type_id FROM service_types WHERE service_name = ? LIMIT 1";
                ResultSet rs = Database.executeQuery(query, serviceName);
                if (rs != null && rs.next()) {
                    return rs.getInt("service_type_id");
                }
            }
        } catch (SQLException ex) {
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
