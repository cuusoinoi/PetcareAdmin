package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.PetEnclosure;
import com.petcare.service.CustomerService;
import com.petcare.service.PetEnclosureService;
import com.petcare.service.PetService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for adding/editing pet enclosure (Check-in)
 */
public class AddEditPetEnclosureDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JTextField enclosureNumberField;
    private JTextField checkInDateField;
    private JTextField dailyRateField;
    private JTextField depositField;
    private JTextField emergencyLimitField;
    private JTextArea noteArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private PetEnclosure enclosure;
    
    public AddEditPetEnclosureDialog(JDialog parent, PetEnclosure enclosure) {
        super(parent, true);
        this.enclosure = enclosure;
        initComponents();
        loadCustomers();
        
        if (enclosure != null) {
            loadEnclosureData();
            setTitle("Sửa lưu chuồng");
        } else {
            setTitle("Check-in thú cưng");
            // Set default values
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            checkInDateField.setText(sdf.format(new Date()));
            dailyRateField.setText("80000");
        }
    }
    
    private void initComponents() {
        setSize(600, 500);
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
        customerCombo.addActionListener(e -> loadPetsByCustomer());
        formPanel.add(customerCombo);
        
        // Pet
        formPanel.add(createLabel("Thú cưng *:"));
        petCombo = new JComboBox<>();
        petCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(petCombo);
        
        // Enclosure Number
        formPanel.add(createLabel("Số chuồng *:"));
        enclosureNumberField = createTextField();
        formPanel.add(enclosureNumberField);
        
        // Check-in Date
        formPanel.add(createLabel("Ngày Check-in * (dd/MM/yyyy HH:mm):"));
        checkInDateField = createTextField();
        checkInDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy HH:mm");
        formPanel.add(checkInDateField);
        
        // Daily Rate
        formPanel.add(createLabel("Phí/ngày (VNĐ) *:"));
        dailyRateField = createTextField();
        formPanel.add(dailyRateField);
        
        // Deposit
        formPanel.add(createLabel("Đặt cọc (VNĐ):"));
        depositField = createTextField();
        depositField.setText("0");
        formPanel.add(depositField);
        
        // Emergency Limit
        formPanel.add(createLabel("Hạn mức khẩn cấp (VNĐ):"));
        emergencyLimitField = createTextField();
        emergencyLimitField.setText("0");
        formPanel.add(emergencyLimitField);
        
        // Note
        formPanel.add(createLabel("Ghi chú:"));
        noteArea = new JTextArea(3, 20);
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noteArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        noteArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(noteArea);
        
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
        saveButton.addActionListener(e -> saveEnclosure());
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
            CustomerService.getInstance().getAllCustomers().forEach(c -> {
                customerCombo.addItem(c.getCustomerId() + " - " + c.getCustomerName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPetsByCustomer() {
        petCombo.removeAllItems();
        petCombo.addItem("-- Chọn thú cưng --");
        if (customerCombo.getSelectedIndex() == 0) return;
        try {
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            PetService.getInstance().getPetsByCustomerId(customerId).forEach(p -> {
                petCombo.addItem(p.getPetId() + " - " + p.getPetName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadEnclosureData() {
        if (enclosure != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(enclosure.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    loadPetsByCustomer();
                    break;
                }
            }
            
            // Set pet
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                String item = petCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(enclosure.getPetId()))) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            enclosureNumberField.setText(String.valueOf(enclosure.getPetEnclosureNumber()));
            
            if (enclosure.getCheckInDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                checkInDateField.setText(sdf.format(enclosure.getCheckInDate()));
            }
            
            dailyRateField.setText(String.valueOf(enclosure.getDailyRate()));
            depositField.setText(String.valueOf(enclosure.getDeposit()));
            emergencyLimitField.setText(String.valueOf(enclosure.getEmergencyLimit()));
            noteArea.setText(enclosure.getPetEnclosureNote() != null ? enclosure.getPetEnclosureNote() : "");
        }
    }
    
    private void saveEnclosure() {
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
        
        if (enclosureNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số chuồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            enclosureNumberField.requestFocus();
            return;
        }
        
        if (checkInDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày check-in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            checkInDateField.requestFocus();
            return;
        }
        
        if (dailyRateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập phí/ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dailyRateField.requestFocus();
            return;
        }
        
        try {
            // Get IDs
            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);
            
            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);
            
            int enclosureNumber = Integer.parseInt(enclosureNumberField.getText().trim());
            int dailyRate = Integer.parseInt(dailyRateField.getText().trim());
            int deposit = depositField.getText().trim().isEmpty() ? 0 : 
                         Integer.parseInt(depositField.getText().trim());
            int emergencyLimit = emergencyLimitField.getText().trim().isEmpty() ? 0 : 
                                Integer.parseInt(emergencyLimitField.getText().trim());
            
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date checkInDate;
            try {
                checkInDate = sdf.parse(checkInDateField.getText().trim());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày check-in không đúng định dạng (dd/MM/yyyy HH:mm)!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                checkInDateField.requestFocus();
                return;
            }
            
            PetEnclosureService service = PetEnclosureService.getInstance();
            if (enclosure == null) {
                PetEnclosure newEnclosure = new PetEnclosure();
                newEnclosure.setCustomerId(customerId);
                newEnclosure.setPetId(petId);
                newEnclosure.setPetEnclosureNumber(enclosureNumber);
                newEnclosure.setCheckInDate(checkInDate);
                newEnclosure.setDailyRate(dailyRate);
                newEnclosure.setDeposit(deposit);
                newEnclosure.setEmergencyLimit(emergencyLimit);
                newEnclosure.setPetEnclosureNote(noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim());
                service.createEnclosure(newEnclosure);
                JOptionPane.showMessageDialog(this, "Check-in thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                enclosure.setCustomerId(customerId);
                enclosure.setPetId(petId);
                enclosure.setPetEnclosureNumber(enclosureNumber);
                enclosure.setCheckInDate(checkInDate);
                enclosure.setDailyRate(dailyRate);
                enclosure.setDeposit(deposit);
                enclosure.setEmergencyLimit(emergencyLimit);
                enclosure.setPetEnclosureNote(noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim());
                service.updateEnclosure(enclosure);
                JOptionPane.showMessageDialog(this, "Cập nhật lưu chuồng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho phí/ngày, đặt cọc, hạn mức!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
