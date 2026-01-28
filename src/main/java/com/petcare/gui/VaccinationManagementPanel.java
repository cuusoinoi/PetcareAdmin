package com.petcare.gui;

import com.petcare.model.Database;
import com.petcare.model.PetVaccination;
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
        
        try {
            String query = "SELECT pv.pet_vaccination_id, pv.vaccination_date, pv.next_vaccination_date, " +
                          "v.vaccine_name, c.customer_name, p.pet_name, d.doctor_name " +
                          "FROM pet_vaccinations pv " +
                          "INNER JOIN vaccines v ON pv.vaccine_id = v.vaccine_id " +
                          "INNER JOIN customers c ON pv.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON pv.pet_id = p.pet_id " +
                          "INNER JOIN doctors d ON pv.doctor_id = d.doctor_id " +
                          "ORDER BY pv.pet_vaccination_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs != null && rs.next()) {
                String vaccinationDate = "";
                if (rs.getDate("vaccination_date") != null) {
                    vaccinationDate = sdf.format(rs.getDate("vaccination_date"));
                }
                
                String nextDate = "";
                if (rs.getDate("next_vaccination_date") != null) {
                    nextDate = sdf.format(rs.getDate("next_vaccination_date"));
                }
                
                Object[] row = {
                    rs.getInt("pet_vaccination_id"),
                    vaccinationDate,
                    rs.getString("vaccine_name"),
                    rs.getString("customer_name"),
                    rs.getString("pet_name"),
                    rs.getString("doctor_name"),
                    nextDate
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
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn bản ghi tiêm chủng cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vaccinationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        PetVaccination vaccination = getVaccinationById(vaccinationId);
        
        if (vaccination != null) {
            AddEditVaccinationDialog dialog = new AddEditVaccinationDialog(null, vaccination);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
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
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa bản ghi tiêm chủng này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM pet_vaccinations WHERE pet_vaccination_id = ?";
                int result = Database.executeUpdate(query, vaccinationId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa bản ghi tiêm chủng thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa bản ghi tiêm chủng.", 
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
    
    private PetVaccination getVaccinationById(int vaccinationId) {
        try {
            String query = "SELECT * FROM pet_vaccinations WHERE pet_vaccination_id = ?";
            ResultSet rs = Database.executeQuery(query, vaccinationId);
            
            if (rs != null && rs.next()) {
                PetVaccination vaccination = new PetVaccination();
                vaccination.setPetVaccinationId(rs.getInt("pet_vaccination_id"));
                vaccination.setVaccineId(rs.getInt("vaccine_id"));
                vaccination.setCustomerId(rs.getInt("customer_id"));
                vaccination.setPetId(rs.getInt("pet_id"));
                vaccination.setDoctorId(rs.getInt("doctor_id"));
                
                if (rs.getDate("vaccination_date") != null) {
                    vaccination.setVaccinationDate(new java.util.Date(
                        rs.getDate("vaccination_date").getTime()));
                }
                
                if (rs.getDate("next_vaccination_date") != null) {
                    vaccination.setNextVaccinationDate(new java.util.Date(
                        rs.getDate("next_vaccination_date").getTime()));
                }
                
                vaccination.setNotes(rs.getString("notes"));
                
                return vaccination;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
