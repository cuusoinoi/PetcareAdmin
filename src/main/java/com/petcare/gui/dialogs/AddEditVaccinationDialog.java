package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.PetVaccination;
import com.petcare.service.CustomerService;
import com.petcare.service.DoctorService;
import com.petcare.service.PetService;
import com.petcare.service.PetVaccinationService;
import com.petcare.service.VaccineTypeService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dialog for adding/editing pet vaccination
 */
public class AddEditVaccinationDialog extends JDialog {
    private JComboBox<String> vaccineCombo;
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JComboBox<String> doctorCombo;
    private JTextField vaccinationDateField;
    private JTextField nextVaccinationDateField;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private PetVaccination vaccination;

    public AddEditVaccinationDialog(JDialog parent, PetVaccination vaccination) {
        super(parent, true);
        this.vaccination = vaccination;
        initComponents();
        loadVaccines();
        loadCustomers();
        loadDoctors();

        if (vaccination != null) {
            loadVaccinationData();
            setTitle("Sửa bản ghi tiêm chủng");
        } else {
            setTitle("Thêm bản ghi tiêm chủng mới");
        }
    }

    private void initComponents() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getContentBackground());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());

        // Vaccine
        formPanel.add(createLabel("Vaccine *:"));
        vaccineCombo = new JComboBox<>();
        vaccineCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vaccineCombo.setBackground(ThemeManager.getTextFieldBackground());
        vaccineCombo.setForeground(ThemeManager.getTextFieldForeground());
        vaccineCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(vaccineCombo);

        // Customer
        formPanel.add(createLabel("Khách hàng *:"));
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.setBackground(ThemeManager.getTextFieldBackground());
        customerCombo.setForeground(ThemeManager.getTextFieldForeground());
        customerCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        customerCombo.addActionListener(e -> loadPetsByCustomer());
        formPanel.add(customerCombo);

        // Pet
        formPanel.add(createLabel("Thú cưng *:"));
        petCombo = new JComboBox<>();
        petCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petCombo.setBackground(ThemeManager.getTextFieldBackground());
        petCombo.setForeground(ThemeManager.getTextFieldForeground());
        petCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(petCombo);

        // Doctor
        formPanel.add(createLabel("Bác sĩ *:"));
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorCombo.setBackground(ThemeManager.getTextFieldBackground());
        doctorCombo.setForeground(ThemeManager.getTextFieldForeground());
        doctorCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(doctorCombo);

        // Vaccination Date
        formPanel.add(createLabel("Ngày tiêm * (dd/MM/yyyy):"));
        vaccinationDateField = createTextField();
        vaccinationDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(vaccinationDateField);

        // Next Vaccination Date
        formPanel.add(createLabel("Ngày tiêm tiếp theo (dd/MM/yyyy):"));
        nextVaccinationDateField = createTextField();
        nextVaccinationDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(nextVaccinationDateField);

        // Notes
        formPanel.add(createLabel("Ghi chú:"));
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setBackground(ThemeManager.getTextFieldBackground());
        notesArea.setForeground(ThemeManager.getTextFieldForeground());
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        notesArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        formPanel.add(notesArea);

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
        saveButton.addActionListener(e -> saveVaccination());
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

    private void loadVaccines() {
        vaccineCombo.removeAllItems();
        vaccineCombo.addItem("-- Chọn vaccine --");
        try {
            VaccineTypeService.getInstance().getAllVaccineTypes().forEach(v -> {
                vaccineCombo.addItem(v.getVaccineId() + " - " + v.getVaccineName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void loadDoctors() {
        doctorCombo.removeAllItems();
        doctorCombo.addItem("-- Chọn bác sĩ --");
        try {
            DoctorService.getInstance().getAllDoctors().forEach(d -> {
                doctorCombo.addItem(d.getDoctorId() + " - " + d.getDoctorName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadVaccinationData() {
        if (vaccination != null) {
            // Set vaccine
            for (int i = 0; i < vaccineCombo.getItemCount(); i++) {
                String item = vaccineCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(vaccination.getVaccineId()))) {
                    vaccineCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(vaccination.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    loadPetsByCustomer();
                    break;
                }
            }

            // Set pet
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                String item = petCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(vaccination.getPetId()))) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set doctor
            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                String item = doctorCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(vaccination.getDoctorId()))) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set dates
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (vaccination.getVaccinationDate() != null) {
                vaccinationDateField.setText(sdf.format(vaccination.getVaccinationDate()));
            }
            if (vaccination.getNextVaccinationDate() != null) {
                nextVaccinationDateField.setText(sdf.format(vaccination.getNextVaccinationDate()));
            }

            notesArea.setText(vaccination.getNotes() != null ? vaccination.getNotes() : "");
        }
    }

    private void saveVaccination() {
        // Validation
        if (vaccineCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vaccine!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            vaccineCombo.requestFocus();
            return;
        }

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

        if (doctorCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            doctorCombo.requestFocus();
            return;
        }

        if (vaccinationDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày tiêm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            vaccinationDateField.requestFocus();
            return;
        }

        try {
            // Get IDs
            String vaccineSelected = (String) vaccineCombo.getSelectedItem();
            int vaccineId = Integer.parseInt(vaccineSelected.split(" - ")[0]);

            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);

            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);

            String doctorSelected = (String) doctorCombo.getSelectedItem();
            int doctorId = Integer.parseInt(doctorSelected.split(" - ")[0]);

            // Parse dates
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date vaccinationDate;
            try {
                vaccinationDate = sdf.parse(vaccinationDateField.getText().trim());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày tiêm không đúng định dạng (dd/MM/yyyy)!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                vaccinationDateField.requestFocus();
                return;
            }

            Date nextVaccinationDate = null;
            if (!nextVaccinationDateField.getText().trim().isEmpty()) {
                try {
                    nextVaccinationDate = sdf.parse(nextVaccinationDateField.getText().trim());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ngày tiêm tiếp theo không đúng định dạng (dd/MM/yyyy)!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    nextVaccinationDateField.requestFocus();
                    return;
                }
            }

            PetVaccinationService service = PetVaccinationService.getInstance();
            if (vaccination == null) {
                PetVaccination newVaccination = new PetVaccination();
                newVaccination.setVaccineId(vaccineId);
                newVaccination.setCustomerId(customerId);
                newVaccination.setPetId(petId);
                newVaccination.setDoctorId(doctorId);
                newVaccination.setVaccinationDate(vaccinationDate);
                newVaccination.setNextVaccinationDate(nextVaccinationDate);
                newVaccination.setNotes(notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());
                service.createVaccination(newVaccination);
                JOptionPane.showMessageDialog(this, "Thêm bản ghi tiêm chủng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                vaccination.setVaccineId(vaccineId);
                vaccination.setCustomerId(customerId);
                vaccination.setPetId(petId);
                vaccination.setDoctorId(doctorId);
                vaccination.setVaccinationDate(vaccinationDate);
                vaccination.setNextVaccinationDate(nextVaccinationDate);
                vaccination.setNotes(notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());
                service.updateVaccination(vaccination);
                JOptionPane.showMessageDialog(this, "Cập nhật bản ghi tiêm chủng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
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
