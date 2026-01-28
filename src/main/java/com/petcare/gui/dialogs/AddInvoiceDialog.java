package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
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
 * Dialog for adding invoice
 */
public class AddInvoiceDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;
    private JComboBox<String> serviceCombo;
    private JSpinner quantitySpinner;
    private JButton addServiceButton;
    private JButton removeServiceButton;
    private JTextField discountField;
    private JTextField depositField;
    private JLabel subtotalLabel;
    private JLabel totalLabel;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private List<ServiceType> serviceTypes;
    
    public AddInvoiceDialog(JDialog parent) {
        super(parent, true);
        initComponents();
        loadCustomers();
        loadServiceTypes();
    }
    
    private void initComponents() {
        setSize(700, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Thêm hóa đơn mới");
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Customer
        formPanel.add(createLabel("Khách hàng *:"));
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        customerCombo.addActionListener(e -> loadPetsByCustomer());
        formPanel.add(customerCombo);
        
        // Pet
        formPanel.add(createLabel("Thú cưng *:"));
        petCombo = new JComboBox<>();
        petCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(petCombo);
        
        // Services section
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Dịch vụ"));
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
        
        removeServiceButton = new JButton("➖ Xóa dịch vụ đã chọn");
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
        
        summaryPanel.add(createLabel("Đặt cọc (VNĐ):"));
        depositField = new JTextField("0");
        depositField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        depositField.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        depositField.addActionListener(e -> updateTotals());
        summaryPanel.add(depositField);
        
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
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(servicesPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        saveButton = new JButton("💾 Lưu");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveInvoice());
        buttonPanel.add(saveButton);
        
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
    
    private void loadCustomers() {
        customerCombo.removeAllItems();
        customerCombo.addItem("-- Chọn khách hàng --");
        
        try {
            String query = "SELECT customer_id, customer_name FROM customers ORDER BY customer_name";
            ResultSet rs = Database.executeQuery(query);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("customer_id") + " - " + rs.getString("customer_name");
                customerCombo.addItem(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadPetsByCustomer() {
        petCombo.removeAllItems();
        petCombo.addItem("-- Chọn thú cưng --");
        
        if (customerCombo.getSelectedIndex() == 0) {
            return;
        }
        
        try {
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            
            String query = "SELECT pet_id, pet_name FROM pets WHERE customer_id = ? ORDER BY pet_name";
            ResultSet rs = Database.executeQuery(query, customerId);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("pet_id") + " - " + rs.getString("pet_name");
                petCombo.addItem(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
            totalPrice
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
        
        int deposit = 0;
        try {
            deposit = Integer.parseInt(depositField.getText().trim());
        } catch (NumberFormatException ex) {
            deposit = 0;
        }
        
        int total = subtotal - discount - deposit;
        if (total < 0) total = 0;
        
        subtotalLabel.setText(formatCurrency(subtotal));
        totalLabel.setText(formatCurrency(total));
    }
    
    private void saveInvoice() {
        // Validation
        if (customerCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerCombo.requestFocus();
            return;
        }
        
        if (petCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thú cưng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            petCombo.requestFocus();
            return;
        }
        
        if (serviceTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một dịch vụ!", "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Get IDs
            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);
            
            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);
            
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
            
            int deposit = 0;
            try {
                deposit = Integer.parseInt(depositField.getText().trim());
            } catch (NumberFormatException ex) {
                deposit = 0;
            }
            
            int totalAmount = subtotal - discount - deposit;
            if (totalAmount < 0) totalAmount = 0;
            
            // Create invoice
            Date invoiceDate = new Date();
            String invoiceQuery = "INSERT INTO invoices (customer_id, pet_id, invoice_date, " +
                                "discount, subtotal, deposit, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            java.sql.Timestamp sqlDate = new java.sql.Timestamp(invoiceDate.getTime());
            
            // Get generated invoice ID
            java.sql.Connection conn = Database.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(invoiceQuery, 
                java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);
            ps.setInt(2, petId);
            ps.setTimestamp(3, sqlDate);
            ps.setInt(4, discount);
            ps.setInt(5, subtotal);
            ps.setInt(6, deposit);
            ps.setInt(7, totalAmount);
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
                "Tạo hóa đơn thành công! Hóa đơn #" + invoiceId, 
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
            String query = "SELECT service_type_id FROM service_types WHERE service_name = ? LIMIT 1";
            ResultSet rs = Database.executeQuery(query, serviceName);
            if (rs != null && rs.next()) {
                return rs.getInt("service_type_id");
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
