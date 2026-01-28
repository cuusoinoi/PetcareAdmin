package com.petcare.gui;

import com.petcare.model.Database;
import com.petcare.model.Pet;
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
 * Pet Management Panel with CRUD operations
 */
public class PetManagementPanel extends JPanel {
    private JTable petTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public PetManagementPanel() {
        initComponents();
        loadPets();
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
        
        JLabel titleLabel = new JLabel("Quản lý Thú cưng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddPetDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditPetDialog());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deletePet());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Tên thú cưng", "Khách hàng", "Loài/Giống", "Giới tính", 
                           "Ngày sinh", "Cân nặng (kg)", "Triệt sản"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        petTable = new JTable(tableModel);
        petTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petTable.setRowHeight(30);
        petTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        petTable.setSelectionBackground(new Color(139, 69, 19));
        petTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(petTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadPets();
    }
    
    private void loadPets() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT p.pet_id, p.pet_name, c.customer_name, p.pet_species, " +
                          "p.pet_gender, p.pet_dob, p.pet_weight, p.pet_sterilization " +
                          "FROM pets p " +
                          "INNER JOIN customers c ON p.customer_id = c.customer_id " +
                          "ORDER BY p.pet_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs != null && rs.next()) {
                String gender = "";
                if (rs.getString("pet_gender") != null) {
                    gender = rs.getString("pet_gender").equals("0") ? "Đực" : "Cái";
                }
                
                String sterilization = "";
                if (rs.getString("pet_sterilization") != null) {
                    sterilization = rs.getString("pet_sterilization").equals("1") ? "Đã triệt sản" : "Chưa triệt sản";
                }
                
                String dob = "";
                if (rs.getDate("pet_dob") != null) {
                    dob = sdf.format(rs.getDate("pet_dob"));
                }
                
                String weight = "";
                if (rs.getDouble("pet_weight") > 0) {
                    weight = String.valueOf(rs.getDouble("pet_weight"));
                }
                
                Object[] row = {
                    rs.getInt("pet_id"),
                    rs.getString("pet_name"),
                    rs.getString("customer_name"),
                    rs.getString("pet_species") != null ? rs.getString("pet_species") : "",
                    gender,
                    dob,
                    weight,
                    sterilization
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
    
    private void showAddPetDialog() {
        AddEditPetDialog dialog = new AddEditPetDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditPetDialog() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn thú cưng cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int petId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Pet pet = getPetById(petId);
        
        if (pet != null) {
            AddEditPetDialog dialog = new AddEditPetDialog(null, pet);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void deletePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn thú cưng cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int petId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String petName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa thú cưng: " + petName + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM pets WHERE pet_id = ?";
                int result = Database.executeUpdate(query, petId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa thú cưng thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa thú cưng. Có thể thú cưng đang có dữ liệu liên quan.", 
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
    
    private Pet getPetById(int petId) {
        try {
            String query = "SELECT * FROM pets WHERE pet_id = ?";
            ResultSet rs = Database.executeQuery(query, petId);
            
            if (rs != null && rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("pet_id"));
                pet.setCustomerId(rs.getInt("customer_id"));
                pet.setPetName(rs.getString("pet_name"));
                pet.setPetSpecies(rs.getString("pet_species"));
                
                String genderCode = rs.getString("pet_gender");
                if (genderCode != null) {
                    pet.setPetGender(Pet.Gender.fromCode(genderCode));
                }
                
                if (rs.getDate("pet_dob") != null) {
                    pet.setPetDob(new java.util.Date(rs.getDate("pet_dob").getTime()));
                }
                
                if (rs.getDouble("pet_weight") > 0) {
                    pet.setPetWeight(rs.getDouble("pet_weight"));
                }
                
                String sterilizationCode = rs.getString("pet_sterilization");
                if (sterilizationCode != null) {
                    pet.setPetSterilization(sterilizationCode.equals("1"));
                }
                
                pet.setPetCharacteristic(rs.getString("pet_characteristic"));
                pet.setPetDrugAllergy(rs.getString("pet_drug_allergy"));
                
                return pet;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
