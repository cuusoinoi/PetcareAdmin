package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditPetDialog;
import com.petcare.model.domain.Customer;
import com.petcare.model.domain.Pet;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.CustomerService;
import com.petcare.service.PetService;
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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

/**
 * Pet Management Panel with CRUD operations
 */
public class PetManagementPanel extends JPanel {
    private JTable petTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel searchPanel;
    private JPanel sideButtonPanel;
    private JLabel titleLabel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private PetService petService;
    private CustomerService customerService;
    
    public PetManagementPanel() {
        this.petService = PetService.getInstance();
        this.customerService = CustomerService.getInstance();
        initComponents();
        loadPets();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getContentBackground());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.getHeaderBackground());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        titleLabel = new JLabel("Quản lý Thú cưng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);

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
        ThemeManager.applyTableTheme(petTable);
        
        JScrollPane scrollPane = new JScrollPane(petTable);
        scrollPane.setBorder(null);

        searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(ThemeManager.getContentBackground());
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo tên thú cưng, khách hàng, loài, giống...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
            @Override
            public void removeUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
            @Override
            public void changedUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
        });
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);

        java.awt.Color iconColor = ThemeManager.isDarkMode() ? new Color(0xc0c0c0) : new Color(60, 60, 60);
        sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
        sideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideButtonPanel.setMinimumSize(new java.awt.Dimension(175, 0));
        sideButtonPanel.setPreferredSize(new java.awt.Dimension(175, 0));
        addButton = new JButton("Thêm");
        addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showAddPetDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditPetDialog());
        sideButtonPanel.add(editButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deletePet());
        sideButtonPanel.add(deleteButton);
        refreshButton = new JButton("Làm mới");
        refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
        refreshButton.addActionListener(e -> refreshData());
        sideButtonPanel.add(refreshButton);
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.getContentBackground());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        paginationPanel = new TablePaginationPanel(petTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }
    
    public void refreshData() {
        loadPets();
    }

    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        if (searchPanel != null) searchPanel.setBackground(ThemeManager.getContentBackground());
        if (headerPanel != null) {
            headerPanel.setBackground(ThemeManager.getHeaderBackground());
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
        }
        if (titleLabel != null) titleLabel.setForeground(ThemeManager.getTitleForeground());
        if (centerPanel != null) centerPanel.setBackground(ThemeManager.getContentBackground());
        if (sideButtonPanel != null) {
            sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
            for (java.awt.Component c : sideButtonPanel.getComponents()) {
                if (c instanceof JButton) {
                    ((JButton) c).setBackground(ThemeManager.getButtonBackground());
                    ((JButton) c).setForeground(ThemeManager.getButtonForeground());
                }
            }
            java.awt.Color iconColor = ThemeManager.getIconColor();
            addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
            editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
            deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
            refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        }
        if (searchField != null) {
            searchField.setBackground(ThemeManager.getTextFieldBackground());
            searchField.setForeground(ThemeManager.getTextFieldForeground());
        }
        if (searchPanel != null) {
            for (java.awt.Component c : searchPanel.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(ThemeManager.getTitleForeground());
            }
        }
        if (paginationPanel != null) paginationPanel.updateTheme();
        ThemeManager.applyTableTheme(petTable);
    }

    private void loadPets() {
        tableModel.setRowCount(0);
        
        try {
            List<Pet> pets = petService.getAllPets();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (Pet pet : pets) {
                // Get customer name
                String customerName = "";
                try {
                    Customer customer = customerService.getCustomerById(pet.getCustomerId());
                    if (customer != null) {
                        customerName = customer.getCustomerName();
                    }
                } catch (PetcareException ex) {
                    customerName = "N/A";
                }
                
                // Format gender
                String gender = "";
                if (pet.getPetGender() != null) {
                    gender = pet.getPetGender().equals("0") ? "Đực" : "Cái";
                }
                
                // Format sterilization
                String sterilization = "";
                if (pet.getPetSterilization() != null) {
                    sterilization = pet.getPetSterilization().equals("1") ? "Đã triệt sản" : "Chưa triệt sản";
                }
                
                // Format DOB
                String dob = "";
                if (pet.getPetDob() != null) {
                    dob = sdf.format(pet.getPetDob());
                }
                
                // Format weight
                String weight = "";
                if (pet.getPetWeight() != null && pet.getPetWeight() > 0) {
                    weight = String.valueOf(pet.getPetWeight());
                }
                
                Object[] row = {
                    pet.getPetId(),
                    pet.getPetName(),
                    customerName,
                    pet.getPetSpecies() != null ? pet.getPetSpecies() : "",
                    gender,
                    dob,
                    weight,
                    sterilization
                };
                tableModel.addRow(row);
            }
            if (paginationPanel != null) paginationPanel.refresh();
        } catch (PetcareException ex) {
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
        int modelRow = petTable.convertRowIndexToModel(selectedRow);
        int petId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        try {
            Pet pet = petService.getPetById(petId);
            if (pet != null) {
                AddEditPetDialog dialog = new AddEditPetDialog(null, pet);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thú cưng với ID: " + petId, 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải thông tin thú cưng: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
        int modelRow = petTable.convertRowIndexToModel(selectedRow);
        int petId = (Integer) tableModel.getValueAt(modelRow, 0);
        String petName = (String) tableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa thú cưng: " + petName + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                petService.deletePet(petId);
                JOptionPane.showMessageDialog(this, 
                    "Xóa thú cưng thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
