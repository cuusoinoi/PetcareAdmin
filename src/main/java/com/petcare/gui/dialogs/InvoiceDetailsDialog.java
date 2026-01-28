package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog for viewing invoice details
 */
public class InvoiceDetailsDialog extends JDialog {
    
    public InvoiceDetailsDialog(JDialog parent, int invoiceId) {
        super(parent, true);
        initComponents(invoiceId);
    }
    
    private void initComponents(int invoiceId) {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Chi tiết Hóa đơn #" + invoiceId);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Invoice info
        JPanel infoPanel = new JPanel(new java.awt.GridLayout(0, 2, 15, 10));
        infoPanel.setBackground(Color.WHITE);
        
        try {
            String query = "SELECT i.*, c.customer_name, c.customer_phone_number, " +
                          "p.pet_name, pe.pet_enclosure_number " +
                          "FROM invoices i " +
                          "INNER JOIN customers c ON i.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON i.pet_id = p.pet_id " +
                          "LEFT JOIN pet_enclosures pe ON i.pet_enclosure_id = pe.pet_enclosure_id " +
                          "WHERE i.invoice_id = ?";
            
            ResultSet rs = Database.executeQuery(query, invoiceId);
            
            if (rs != null && rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String invoiceDate = "";
                if (rs.getTimestamp("invoice_date") != null) {
                    invoiceDate = sdf.format(rs.getTimestamp("invoice_date"));
                }
                
                infoPanel.add(createInfoLabel("Khách hàng:"));
                infoPanel.add(createInfoValue(rs.getString("customer_name")));
                
                infoPanel.add(createInfoLabel("Số điện thoại:"));
                infoPanel.add(createInfoValue(rs.getString("customer_phone_number")));
                
                infoPanel.add(createInfoLabel("Thú cưng:"));
                infoPanel.add(createInfoValue(rs.getString("pet_name")));
                
                if (rs.getInt("pet_enclosure_number") > 0) {
                    infoPanel.add(createInfoLabel("Số chuồng:"));
                    infoPanel.add(createInfoValue(String.valueOf(rs.getInt("pet_enclosure_number"))));
                }
                
                infoPanel.add(createInfoLabel("Ngày hóa đơn:"));
                infoPanel.add(createInfoValue(invoiceDate));
                
                infoPanel.add(createInfoLabel("Tổng tiền:"));
                infoPanel.add(createInfoValue(formatCurrency(rs.getInt("subtotal"))));
                
                infoPanel.add(createInfoLabel("Giảm giá:"));
                infoPanel.add(createInfoValue(formatCurrency(rs.getInt("discount"))));
                
                infoPanel.add(createInfoLabel("Đặt cọc:"));
                infoPanel.add(createInfoValue(formatCurrency(rs.getInt("deposit"))));
                
                infoPanel.add(createInfoLabel("Thành tiền:"));
                JLabel totalLabel = createInfoValue(formatCurrency(rs.getInt("total_amount")));
                totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                totalLabel.setForeground(new Color(139, 69, 19));
                infoPanel.add(totalLabel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Invoice details table
        String[] columns = {"Dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try {
            String query = "SELECT st.service_name, id.quantity, id.unit_price, id.total_price " +
                          "FROM invoice_details id " +
                          "INNER JOIN service_types st ON id.service_type_id = st.service_type_id " +
                          "WHERE id.invoice_id = ?";
            
            ResultSet rs = Database.executeQuery(query, invoiceId);
            
            while (rs != null && rs.next()) {
                Object[] row = {
                    rs.getString("service_name"),
                    rs.getInt("quantity"),
                    formatCurrency(rs.getInt("unit_price")),
                    formatCurrency(rs.getInt("total_price"))
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JTable detailsTable = new JTable(tableModel);
        detailsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsTable.setRowHeight(30);
        detailsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết dịch vụ"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        javax.swing.JButton closeButton = new javax.swing.JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }
    
    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text != null ? text : "");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
    
    private String formatCurrency(int amount) {
        return String.format("%,d", amount) + " VNĐ";
    }
}
