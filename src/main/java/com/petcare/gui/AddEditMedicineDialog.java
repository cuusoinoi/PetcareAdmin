package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.Medicine;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for adding/editing medicine
 */
public class AddEditMedicineDialog extends JDialog {
    private JTextField medicineNameField;
    private JComboBox<String> routeCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Medicine medicine;
    
    public AddEditMedicineDialog(JDialog parent, Medicine medicine) {
        super(parent, true);
        this.medicine = medicine;
        initComponents();
        
        if (medicine != null) {
            loadMedicineData();
            setTitle("Sửa thuốc");
        } else {
            setTitle("Thêm thuốc mới");
        }
    }
    
    private void initComponents() {
        setSize(450, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Medicine Name
        formPanel.add(createLabel("Tên thuốc *:"));
        medicineNameField = createTextField();
        formPanel.add(medicineNameField);
        
        // Route
        formPanel.add(createLabel("Đường dùng *:"));
        routeCombo = new JComboBox<>();
        routeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        routeCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        routeCombo.addItem("Uống");
        routeCombo.addItem("Tiêm bắp");
        routeCombo.addItem("Tiêm tĩnh mạch");
        routeCombo.addItem("Tiêm dưới da");
        formPanel.add(routeCombo);
        
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
        saveButton.addActionListener(e -> saveMedicine());
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
    
    private void loadMedicineData() {
        if (medicine != null) {
            medicineNameField.setText(medicine.getMedicineName());
            if (medicine.getMedicineRoute() != null) {
                routeCombo.setSelectedItem(medicine.getMedicineRoute().getLabel());
            }
        }
    }
    
    private void saveMedicine() {
        // Validation
        if (medicineNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thuốc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            medicineNameField.requestFocus();
            return;
        }
        
        if (routeCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đường dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            routeCombo.requestFocus();
            return;
        }
        
        try {
            String routeLabel = (String) routeCombo.getSelectedItem();
            String routeCode = getRouteCode(routeLabel);
            
            if (medicine == null) {
                // Insert
                String query = "INSERT INTO medicines (medicine_name, medicine_route) VALUES (?, ?)";
                
                int result = Database.executeUpdate(query,
                    medicineNameField.getText().trim(),
                    routeCode
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm thuốc thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE medicines SET medicine_name = ?, medicine_route = ? " +
                              "WHERE medicine_id = ?";
                
                int result = Database.executeUpdate(query,
                    medicineNameField.getText().trim(),
                    routeCode,
                    medicine.getMedicineId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thuốc thành công!", "Thành công", 
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
    
    private String getRouteCode(String label) {
        for (Medicine.Route route : Medicine.Route.values()) {
            if (route.getLabel().equals(label)) {
                return route.getCode();
            }
        }
        return "PO"; // Default
    }
    
    public boolean isSaved() {
        return saved;
    }
}
