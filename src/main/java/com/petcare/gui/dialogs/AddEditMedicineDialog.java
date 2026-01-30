package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.Medicine;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.IMedicineService;
import com.petcare.service.MedicineService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding/editing medicine - uses MedicineService and domain Medicine only
 */
public class AddEditMedicineDialog extends JDialog {
    private JTextField medicineNameField;
    private JComboBox<String> routeCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private Medicine medicine;
    private final User currentUser;
    private final IMedicineService medicineService = MedicineService.getInstance();

    public AddEditMedicineDialog(JDialog parent, Medicine medicine, User currentUser) {
        super(parent, true);
        this.medicine = medicine;
        this.currentUser = currentUser;
        initComponents();
        if (medicine != null) {
            loadMedicineData();
            setTitle("Sửa thuốc");
        } else {
            setTitle("Thêm thuốc mới");
        }
    }

    private void initComponents() {
        setSize(520, 220);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getContentBackground());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

        formPanel.add(createLabel("Tên thuốc *:"));
        medicineNameField = createTextField();
        formPanel.add(medicineNameField);

        formPanel.add(createLabel("Đường dùng *:"));
        routeCombo = new JComboBox<>();
        routeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        routeCombo.setBackground(ThemeManager.getTextFieldBackground());
        routeCombo.setForeground(ThemeManager.getTextFieldForeground());
        routeCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        routeCombo.addItem("Uống");
        routeCombo.addItem("Tiêm bắp");
        routeCombo.addItem("Tiêm tĩnh mạch");
        routeCombo.addItem("Tiêm dưới da");
        formPanel.add(routeCombo);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());

        saveButton = new JButton(EmojiFontHelper.withEmoji("💾", "Lưu"));
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveMedicine());
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
        JTextField field = new JTextField(GUIUtil.TEXT_FIELD_COLUMNS);
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

    private void loadMedicineData() {
        if (medicine != null) {
            medicineNameField.setText(medicine.getMedicineName());
            if (medicine.getMedicineRoute() != null) {
                routeCombo.setSelectedItem(medicine.getMedicineRoute().getLabel());
            }
        }
    }

    private void saveMedicine() {
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
        String routeLabel = (String) routeCombo.getSelectedItem();
        Medicine.Route route = getRouteByLabel(routeLabel);
        try {
            if (medicine == null) {
                Medicine newMedicine = new Medicine();
                newMedicine.setMedicineName(medicineNameField.getText().trim());
                newMedicine.setMedicineRoute(route);
                medicineService.createMedicine(newMedicine, currentUser);
                JOptionPane.showMessageDialog(this, "Thêm thuốc thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                medicine.setMedicineName(medicineNameField.getText().trim());
                medicine.setMedicineRoute(route);
                medicineService.updateMedicine(medicine, currentUser);
                JOptionPane.showMessageDialog(this, "Cập nhật thuốc thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Medicine.Route getRouteByLabel(String label) {
        for (Medicine.Route r : Medicine.Route.values()) {
            if (r.getLabel().equals(label)) {
                return r;
            }
        }
        return Medicine.Route.PO;
    }

    public boolean isSaved() {
        return saved;
    }
}
