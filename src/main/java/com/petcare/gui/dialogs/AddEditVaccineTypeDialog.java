package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.User;
import com.petcare.model.domain.VaccineType;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.IVaccineTypeService;
import com.petcare.service.VaccineTypeService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding/editing vaccine type - uses VaccineTypeService and domain VaccineType only
 */
public class AddEditVaccineTypeDialog extends JDialog {
    private JTextField vaccineNameField;
    private JTextField unitPriceField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private VaccineType vaccine;
    private final User currentUser;
    private final IVaccineTypeService vaccineTypeService = VaccineTypeService.getInstance();

    public AddEditVaccineTypeDialog(JDialog parent, VaccineType vaccine, User currentUser) {
        super(parent, true);
        this.vaccine = vaccine;
        this.currentUser = currentUser;
        initComponents();
        if (vaccine != null) {
            loadVaccineData();
            setTitle("Sửa vaccine");
        } else {
            setTitle("Thêm vaccine mới");
        }
    }

    private void initComponents() {
        setSize(560, 320);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getContentBackground());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

        formPanel.add(createLabel("Tên vaccine *:"));
        vaccineNameField = createTextField();
        formPanel.add(vaccineNameField);

        formPanel.add(createLabel("Đơn giá (VNĐ) *:"));
        unitPriceField = createTextField();
        unitPriceField.setText("0");
        unitPriceField.putClientProperty("JTextField.placeholderText", "0");
        formPanel.add(unitPriceField);

        formPanel.add(createLabel("Mô tả:"));
        descriptionArea = new JTextArea(2, GUIUtil.TEXT_FIELD_COLUMNS);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBackground(ThemeManager.getTextFieldBackground());
        descriptionArea.setForeground(ThemeManager.getTextFieldForeground());
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        descriptionArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(descriptionArea);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());

        saveButton = new JButton("Lưu");
        saveButton.setIcon(EmojiFontHelper.createEmojiIcon("💾", Color.WHITE));
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveVaccine());
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

    private void loadVaccineData() {
        if (vaccine != null) {
            vaccineNameField.setText(vaccine.getVaccineName());
            unitPriceField.setText(String.valueOf(vaccine.getUnitPrice()));
            descriptionArea.setText(vaccine.getDescription() != null ? vaccine.getDescription() : "");
        }
    }

    private void saveVaccine() {
        if (vaccineNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên vaccine!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            vaccineNameField.requestFocus();
            return;
        }
        int unitPrice;
        try {
            unitPrice = Integer.parseInt(unitPriceField.getText().trim());
            if (unitPrice < 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                unitPriceField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            unitPriceField.requestFocus();
            return;
        }
        try {
            if (vaccine == null) {
                VaccineType newVaccine = new VaccineType();
                newVaccine.setVaccineName(vaccineNameField.getText().trim());
                newVaccine.setUnitPrice(unitPrice);
                newVaccine.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());
                vaccineTypeService.createVaccineType(newVaccine, currentUser);
                JOptionPane.showMessageDialog(this, "Thêm vaccine thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                vaccine.setVaccineName(vaccineNameField.getText().trim());
                vaccine.setUnitPrice(unitPrice);
                vaccine.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());
                vaccineTypeService.updateVaccineType(vaccine, currentUser);
                JOptionPane.showMessageDialog(this, "Cập nhật vaccine thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
