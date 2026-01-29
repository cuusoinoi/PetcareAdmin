package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditVaccinationDialog;
import com.petcare.model.entity.PetVaccinationListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.PetVaccinationService;
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
 * Vaccination Management Panel with CRUD operations
 */
public class VaccinationManagementPanel extends JPanel {
    private JTable vaccinationTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public VaccinationManagementPanel() {
        initComponents();
        loadVaccinations();
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
        
        JLabel titleLabel = new JLabel("Quản lý Tiêm chủng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddVaccinationDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditVaccinationDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteVaccination());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Ngày tiêm", "Vaccine", "Khách hàng", "Thú cưng", "Bác sĩ", "Ngày tiêm tiếp theo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vaccinationTable = new JTable(tableModel);
        vaccinationTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vaccinationTable.setRowHeight(30);
        vaccinationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        vaccinationTable.setSelectionBackground(new Color(139, 69, 19));
        vaccinationTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(vaccinationTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadVaccinations();
    }
    
    private void loadVaccinations() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<PetVaccinationListDto> list = PetVaccinationService.getInstance().getVaccinationsForList();
            for (PetVaccinationListDto dto : list) {
                String vaccinationDateStr = dto.getVaccinationDate() != null ? sdf.format(dto.getVaccinationDate()) : "";
                String nextDateStr = dto.getNextVaccinationDate() != null ? sdf.format(dto.getNextVaccinationDate()) : "";
                tableModel.addRow(new Object[]{
                    dto.getPetVaccinationId(),
                    vaccinationDateStr,
                    dto.getVaccineName() != null ? dto.getVaccineName() : "",
                    dto.getCustomerName() != null ? dto.getCustomerName() : "",
                    dto.getPetName() != null ? dto.getPetName() : "",
                    dto.getDoctorName() != null ? dto.getDoctorName() : "",
                    nextDateStr
                });
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddVaccinationDialog() {
        AddEditVaccinationDialog dialog = new AddEditVaccinationDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditVaccinationDialog() {
        int selectedRow = vaccinationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi tiêm chủng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int vaccinationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            com.petcare.model.domain.PetVaccination vaccination = PetVaccinationService.getInstance().getVaccinationById(vaccinationId);
            if (vaccination != null) {
                AddEditVaccinationDialog dialog = new AddEditVaccinationDialog(null, vaccination);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteVaccination() {
        int selectedRow = vaccinationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn bản ghi tiêm chủng cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vaccinationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa bản ghi tiêm chủng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PetVaccinationService.getInstance().deleteVaccination(vaccinationId);
                JOptionPane.showMessageDialog(this, "Xóa bản ghi tiêm chủng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
