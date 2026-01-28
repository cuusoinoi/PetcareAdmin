package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditPetEnclosureDialog;
import com.petcare.gui.dialogs.CheckoutDialog;
import com.petcare.model.Database;
import com.petcare.model.PetEnclosure;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Pet Enclosure Management Panel with Check-in/Check-out
 */
public class PetEnclosureManagementPanel extends JPanel {
    private JTable enclosureTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton checkoutButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public PetEnclosureManagementPanel() {
        initComponents();
        loadEnclosures();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Quản lý Lưu chuồng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        
        addButton = new JButton("➕ Check-in");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showCheckInDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditEnclosureDialog());
        buttonPanel.add(editButton);
        
        checkoutButton = new JButton("✅ Check-out");
        checkoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        checkoutButton.setBackground(new Color(40, 167, 69));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.addActionListener(e -> showCheckoutDialog());
        buttonPanel.add(checkoutButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteEnclosure());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Số chuồng", "Khách hàng", "Thú cưng", "Ngày Check-in", 
                           "Ngày Check-out", "Phí/ngày", "Đặt cọc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        enclosureTable = new JTable(tableModel);
        enclosureTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        enclosureTable.setRowHeight(30);
        enclosureTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        enclosureTable.setSelectionBackground(new Color(139, 69, 19));
        enclosureTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(enclosureTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadEnclosures();
    }
    
    private void loadEnclosures() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT pe.pet_enclosure_id, pe.pet_enclosure_number, c.customer_name, " +
                          "p.pet_name, pe.check_in_date, pe.check_out_date, pe.daily_rate, " +
                          "pe.deposit, pe.pet_enclosure_status " +
                          "FROM pet_enclosures pe " +
                          "INNER JOIN customers c ON pe.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON pe.pet_id = p.pet_id " +
                          "ORDER BY pe.pet_enclosure_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs != null && rs.next()) {
                String checkIn = "";
                if (rs.getTimestamp("check_in_date") != null) {
                    checkIn = sdf.format(rs.getTimestamp("check_in_date"));
                }
                
                String checkOut = "";
                if (rs.getTimestamp("check_out_date") != null) {
                    checkOut = dateSdf.format(rs.getTimestamp("check_out_date"));
                }
                
                Object[] row = {
                    rs.getInt("pet_enclosure_id"),
                    rs.getInt("pet_enclosure_number"),
                    rs.getString("customer_name"),
                    rs.getString("pet_name"),
                    checkIn,
                    checkOut,
                    rs.getInt("daily_rate"),
                    rs.getInt("deposit"),
                    rs.getString("pet_enclosure_status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showCheckInDialog() {
        AddEditPetEnclosureDialog dialog = new AddEditPetEnclosureDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditEnclosureDialog() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn lưu chuồng cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int enclosureId = (Integer) tableModel.getValueAt(selectedRow, 0);
        PetEnclosure enclosure = getEnclosureById(enclosureId);
        
        if (enclosure != null) {
            AddEditPetEnclosureDialog dialog = new AddEditPetEnclosureDialog(null, enclosure);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void showCheckoutDialog() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn lưu chuồng cần checkout!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int enclosureId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 8);
        
        if ("Check Out".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "Lưu chuồng này đã được checkout rồi!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PetEnclosure enclosure = getEnclosureById(enclosureId);
        
        if (enclosure != null) {
            CheckoutDialog dialog = new CheckoutDialog(null, enclosure);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void deleteEnclosure() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn lưu chuồng cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int enclosureId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa lưu chuồng này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM pet_enclosures WHERE pet_enclosure_id = ?";
                int result = Database.executeUpdate(query, enclosureId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa lưu chuồng thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa lưu chuồng.", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private PetEnclosure getEnclosureById(int enclosureId) {
        try {
            String query = "SELECT * FROM pet_enclosures WHERE pet_enclosure_id = ?";
            ResultSet rs = Database.executeQuery(query, enclosureId);
            
            if (rs != null && rs.next()) {
                PetEnclosure enclosure = new PetEnclosure();
                enclosure.setPetEnclosureId(rs.getInt("pet_enclosure_id"));
                enclosure.setCustomerId(rs.getInt("customer_id"));
                enclosure.setPetId(rs.getInt("pet_id"));
                enclosure.setPetEnclosureNumber(rs.getInt("pet_enclosure_number"));
                
                if (rs.getTimestamp("check_in_date") != null) {
                    enclosure.setCheckInDate(new java.util.Date(
                        rs.getTimestamp("check_in_date").getTime()));
                }
                
                if (rs.getTimestamp("check_out_date") != null) {
                    enclosure.setCheckOutDate(new java.util.Date(
                        rs.getTimestamp("check_out_date").getTime()));
                }
                
                enclosure.setDailyRate(rs.getInt("daily_rate"));
                enclosure.setDeposit(rs.getInt("deposit"));
                enclosure.setEmergencyLimit(rs.getInt("emergency_limit"));
                enclosure.setPetEnclosureNote(rs.getString("pet_enclosure_note"));
                
                String statusStr = rs.getString("pet_enclosure_status");
                if (statusStr != null) {
                    enclosure.setPetEnclosureStatus(PetEnclosure.Status.fromLabel(statusStr));
                }
                
                return enclosure;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
