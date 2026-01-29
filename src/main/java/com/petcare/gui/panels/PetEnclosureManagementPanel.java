package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditPetEnclosureDialog;
import com.petcare.gui.dialogs.CheckoutDialog;
import com.petcare.model.entity.PetEnclosureListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.PetEnclosureService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<PetEnclosureListDto> list = PetEnclosureService.getInstance().getEnclosuresForList();
            for (PetEnclosureListDto dto : list) {
                String checkInStr = dto.getCheckInDate() != null ? sdf.format(dto.getCheckInDate()) : "";
                String checkOutStr = dto.getCheckOutDate() != null ? dateSdf.format(dto.getCheckOutDate()) : "";
                tableModel.addRow(new Object[]{
                    dto.getPetEnclosureId(),
                    dto.getPetEnclosureNumber(),
                    dto.getCustomerName() != null ? dto.getCustomerName() : "",
                    dto.getPetName() != null ? dto.getPetName() : "",
                    checkInStr,
                    checkOutStr,
                    dto.getDailyRate(),
                    dto.getDeposit(),
                    dto.getPetEnclosureStatus() != null ? dto.getPetEnclosureStatus() : ""
                });
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int enclosureId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            com.petcare.model.domain.PetEnclosure enclosure = PetEnclosureService.getInstance().getEnclosureById(enclosureId);
            if (enclosure != null) {
                AddEditPetEnclosureDialog dialog = new AddEditPetEnclosureDialog(null, enclosure);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCheckoutDialog() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng cần checkout!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int enclosureId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 8);
        if ("Check Out".equals(status)) {
            JOptionPane.showMessageDialog(this, "Lưu chuồng này đã được checkout rồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            com.petcare.model.domain.PetEnclosure enclosure = PetEnclosureService.getInstance().getEnclosureById(enclosureId);
            if (enclosure != null) {
                CheckoutDialog dialog = new CheckoutDialog(null, enclosure);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEnclosure() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int enclosureId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lưu chuồng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PetEnclosureService.getInstance().deleteEnclosure(enclosureId);
                JOptionPane.showMessageDialog(this, "Xóa lưu chuồng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
