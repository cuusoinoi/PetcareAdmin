package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Customer;
import com.petcare.model.Database;
import com.petcare.model.Pet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for adding/editing pet
 */
public class AddEditPetDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JTextField nameField;
    private JTextField speciesField;
    private JRadioButton maleRadio;
    private JRadioButton femaleRadio;
    private JTextField dobField;
    private JTextField weightField;
    private JRadioButton sterilizedYesRadio;
    private JRadioButton sterilizedNoRadio;
    private JTextArea characteristicArea;
    private JTextArea allergyArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Pet pet;
    
    public AddEditPetDialog(JDialog parent, Pet pet) {
        super(parent, true);
        this.pet = pet;
        initComponents();
        loadCustomers();
        
        if (pet != null) {
            loadPetData();
            setTitle("Sửa thú cưng");
        } else {
            setTitle("Thêm thú cưng mới");
        }
    }
    
    private void initComponents() {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Customer
        formPanel.add(createLabel("Khách hàng *:"));
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(customerCombo);
        
        // Name
        formPanel.add(createLabel("Tên thú cưng *:"));
        nameField = createTextField();
        formPanel.add(nameField);
        
        // Species
        formPanel.add(createLabel("Loài/Giống:"));
        speciesField = createTextField();
        formPanel.add(speciesField);
        
        // Gender
        formPanel.add(createLabel("Giới tính:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setOpaque(false);
        maleRadio = new JRadioButton("Đực");
        maleRadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        femaleRadio = new JRadioButton("Cái");
        femaleRadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        javax.swing.ButtonGroup genderGroup = new javax.swing.ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        formPanel.add(genderPanel);
        
        // DOB
        formPanel.add(createLabel("Ngày sinh (dd/MM/yyyy):"));
        dobField = createTextField();
        dobField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(dobField);
        
        // Weight
        formPanel.add(createLabel("Cân nặng (kg):"));
        weightField = createTextField();
        formPanel.add(weightField);
        
        // Sterilization
        formPanel.add(createLabel("Triệt sản:"));
        JPanel sterilizationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sterilizationPanel.setOpaque(false);
        sterilizedYesRadio = new JRadioButton("Đã triệt sản");
        sterilizedYesRadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sterilizedNoRadio = new JRadioButton("Chưa triệt sản");
        sterilizedNoRadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        javax.swing.ButtonGroup sterilizationGroup = new javax.swing.ButtonGroup();
        sterilizationGroup.add(sterilizedYesRadio);
        sterilizationGroup.add(sterilizedNoRadio);
        sterilizationPanel.add(sterilizedYesRadio);
        sterilizationPanel.add(sterilizedNoRadio);
        formPanel.add(sterilizationPanel);
        
        // Characteristic
        formPanel.add(createLabel("Đặc điểm:"));
        characteristicArea = new JTextArea(2, 20);
        characteristicArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        characteristicArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        characteristicArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(characteristicArea);
        
        // Allergy
        formPanel.add(createLabel("Dị ứng thuốc:"));
        allergyArea = new JTextArea(2, 20);
        allergyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        allergyArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        allergyArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(allergyArea);
        
        add(formPanel, BorderLayout.CENTER);
        
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
        saveButton.addActionListener(e -> savePet());
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
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        return field;
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
    
    private void loadPetData() {
        if (pet != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(pet.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            nameField.setText(pet.getPetName());
            speciesField.setText(pet.getPetSpecies() != null ? pet.getPetSpecies() : "");
            
            if (pet.getPetGender() != null) {
                if (pet.getPetGender() == Pet.Gender.MALE) {
                    maleRadio.setSelected(true);
                } else {
                    femaleRadio.setSelected(true);
                }
            }
            
            if (pet.getPetDob() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dobField.setText(sdf.format(pet.getPetDob()));
            }
            
            if (pet.getPetWeight() != null) {
                weightField.setText(String.valueOf(pet.getPetWeight()));
            }
            
            if (pet.getPetSterilization() != null) {
                if (pet.getPetSterilization()) {
                    sterilizedYesRadio.setSelected(true);
                } else {
                    sterilizedNoRadio.setSelected(true);
                }
            }
            
            characteristicArea.setText(pet.getPetCharacteristic() != null ? pet.getPetCharacteristic() : "");
            allergyArea.setText(pet.getPetDrugAllergy() != null ? pet.getPetDrugAllergy() : "");
        }
    }
    
    private void savePet() {
        // Validation
        if (customerCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerCombo.requestFocus();
            return;
        }
        
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thú cưng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        try {
            // Get customer ID
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            
            // Parse date
            Date dob = null;
            if (!dobField.getText().trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    dob = sdf.parse(dobField.getText().trim());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    dobField.requestFocus();
                    return;
                }
            }
            
            // Parse weight
            Double weight = null;
            if (!weightField.getText().trim().isEmpty()) {
                try {
                    weight = Double.parseDouble(weightField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cân nặng phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    weightField.requestFocus();
                    return;
                }
            }
            
            // Get gender
            String gender = null;
            if (maleRadio.isSelected()) {
                gender = "0";
            } else if (femaleRadio.isSelected()) {
                gender = "1";
            }
            
            // Get sterilization
            String sterilization = null;
            if (sterilizedYesRadio.isSelected()) {
                sterilization = "1";
            } else if (sterilizedNoRadio.isSelected()) {
                sterilization = "0";
            }
            
            if (pet == null) {
                // Insert
                String query = "INSERT INTO pets (customer_id, pet_name, pet_species, pet_gender, " +
                              "pet_dob, pet_weight, pet_sterilization, pet_characteristic, pet_drug_allergy) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                java.sql.Date sqlDob = dob != null ? new java.sql.Date(dob.getTime()) : null;
                
                int result = Database.executeUpdate(query,
                    customerId,
                    nameField.getText().trim(),
                    speciesField.getText().trim().isEmpty() ? null : speciesField.getText().trim(),
                    gender,
                    sqlDob,
                    weight,
                    sterilization,
                    characteristicArea.getText().trim().isEmpty() ? null : characteristicArea.getText().trim(),
                    allergyArea.getText().trim().isEmpty() ? null : allergyArea.getText().trim()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm thú cưng thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE pets SET customer_id = ?, pet_name = ?, pet_species = ?, " +
                              "pet_gender = ?, pet_dob = ?, pet_weight = ?, pet_sterilization = ?, " +
                              "pet_characteristic = ?, pet_drug_allergy = ? WHERE pet_id = ?";
                
                java.sql.Date sqlDob = dob != null ? new java.sql.Date(dob.getTime()) : null;
                
                int result = Database.executeUpdate(query,
                    customerId,
                    nameField.getText().trim(),
                    speciesField.getText().trim().isEmpty() ? null : speciesField.getText().trim(),
                    gender,
                    sqlDob,
                    weight,
                    sterilization,
                    characteristicArea.getText().trim().isEmpty() ? null : characteristicArea.getText().trim(),
                    allergyArea.getText().trim().isEmpty() ? null : allergyArea.getText().trim(),
                    pet.getPetId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thú cưng thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
