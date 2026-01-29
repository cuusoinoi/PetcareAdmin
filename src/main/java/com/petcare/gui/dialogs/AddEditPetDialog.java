package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.Customer;
import com.petcare.model.domain.Pet;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.CustomerService;
import com.petcare.service.PetService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private PetService petService;
    private CustomerService customerService;

    public AddEditPetDialog(JDialog parent, Pet pet) {
        super(parent, true);
        this.pet = pet;
        this.petService = PetService.getInstance();
        this.customerService = CustomerService.getInstance();
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
        getContentPane().setBackground(ThemeManager.getContentBackground());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

        // Customer
        formPanel.add(createLabel("Khách hàng *:"));
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.setBackground(ThemeManager.getTextFieldBackground());
        customerCombo.setForeground(ThemeManager.getTextFieldForeground());
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
        characteristicArea.setBackground(ThemeManager.getTextFieldBackground());
        characteristicArea.setForeground(ThemeManager.getTextFieldForeground());
        characteristicArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        characteristicArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(characteristicArea);

        // Allergy
        formPanel.add(createLabel("Dị ứng thuốc:"));
        allergyArea = new JTextArea(2, 20);
        allergyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        allergyArea.setBackground(ThemeManager.getTextFieldBackground());
        allergyArea.setForeground(ThemeManager.getTextFieldForeground());
        allergyArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        allergyArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(allergyArea);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());

        saveButton = new JButton(EmojiFontHelper.withEmoji("💾", "Lưu"));
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> savePet());
        buttonPanel.add(saveButton);

        cancelButton = new JButton(EmojiFontHelper.withEmoji("❌", "Hủy"));
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(ThemeManager.getButtonBackground());
        cancelButton.setForeground(ThemeManager.getButtonForeground());
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.getTitleForeground());
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(ThemeManager.getTextFieldBackground());
        field.setForeground(ThemeManager.getTextFieldForeground());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        return field;
    }

    private void loadCustomers() {
        customerCombo.removeAllItems();
        customerCombo.addItem("-- Chọn khách hàng --");

        try {
            List<Customer> customers = customerService.getAllCustomers();
            for (Customer customer : customers) {
                String display = customer.getCustomerId() + " - " + customer.getCustomerName();
                customerCombo.addItem(display);
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách khách hàng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
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

            // Set gender - pet.getPetGender() returns "0" or "1"
            if (pet.getPetGender() != null) {
                if (pet.getPetGender().equals("0")) {
                    maleRadio.setSelected(true);
                } else if (pet.getPetGender().equals("1")) {
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

            // Set sterilization - pet.getPetSterilization() returns "0" or "1"
            if (pet.getPetSterilization() != null) {
                if (pet.getPetSterilization().equals("1")) {
                    sterilizedYesRadio.setSelected(true);
                } else if (pet.getPetSterilization().equals("0")) {
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

            // Create Pet domain model - validation will happen in setters
            Pet petToSave;
            if (pet == null) {
                // Create new pet
                petToSave = new Pet(
                        customerId,
                        nameField.getText().trim(),
                        speciesField.getText().trim().isEmpty() ? null : speciesField.getText().trim(),
                        gender,
                        dob,
                        weight,
                        sterilization,
                        characteristicArea.getText().trim().isEmpty() ? null : characteristicArea.getText().trim(),
                        allergyArea.getText().trim().isEmpty() ? null : allergyArea.getText().trim()
                );

                // Save via service
                petService.createPet(petToSave);
                JOptionPane.showMessageDialog(this, "Thêm thú cưng thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                // Update existing pet
                pet.setCustomerId(customerId);
                pet.setPetName(nameField.getText().trim());
                pet.setPetSpecies(speciesField.getText().trim().isEmpty() ? null : speciesField.getText().trim());
                pet.setPetGender(gender);
                pet.setPetDob(dob);
                pet.setPetWeight(weight);
                pet.setPetSterilization(sterilization);
                pet.setPetCharacteristic(characteristicArea.getText().trim().isEmpty() ? null : characteristicArea.getText().trim());
                pet.setPetDrugAllergy(allergyArea.getText().trim().isEmpty() ? null : allergyArea.getText().trim());

                // Save via service
                petService.updatePet(pet);
                JOptionPane.showMessageDialog(this, "Cập nhật thú cưng thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
