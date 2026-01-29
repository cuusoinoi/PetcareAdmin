package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.TreatmentCourse;
import com.petcare.service.CustomerService;
import com.petcare.service.PetService;
import com.petcare.service.TreatmentCourseService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dialog for adding/editing treatment course
 */
public class AddEditTreatmentDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> statusCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private TreatmentCourse course;

    public AddEditTreatmentDialog(JDialog parent, TreatmentCourse course) {
        super(parent, true);
        this.course = course;
        initComponents();
        loadCustomers();

        if (course != null) {
            loadCourseData();
            setTitle("Sửa liệu trình điều trị");
        } else {
            setTitle("Thêm liệu trình điều trị mới");
            statusCombo.setSelectedIndex(0); // Default: Đang điều trị
        }
    }

    private void initComponents() {
        setSize(500, 300);
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

        // Start Date
        formPanel.add(createLabel("Ngày bắt đầu * (dd/MM/yyyy):"));
        startDateField = createTextField();
        startDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(startDateField);

        // End Date
        formPanel.add(createLabel("Ngày kết thúc (dd/MM/yyyy):"));
        endDateField = createTextField();
        endDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        formPanel.add(endDateField);

        // Status
        formPanel.add(createLabel("Trạng thái:"));
        statusCombo = new JComboBox<>();
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setBackground(ThemeManager.getTextFieldBackground());
        statusCombo.setForeground(ThemeManager.getTextFieldForeground());
        statusCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        statusCombo.addItem("Đang điều trị");
        statusCombo.addItem("Kết thúc");
        formPanel.add(statusCombo);

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
        saveButton.addActionListener(e -> saveTreatment());
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

    private void loadCourseData() {
        if (course != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(course.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    loadPetsByCustomer();
                    break;
                }
            }

            // Set pet
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                String item = petCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(course.getPetId()))) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set dates
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (course.getStartDate() != null) {
                startDateField.setText(sdf.format(course.getStartDate()));
            }
            if (course.getEndDate() != null) {
                endDateField.setText(sdf.format(course.getEndDate()));
            }

            // Set status
            if (course.getStatus() != null) {
                statusCombo.setSelectedItem(course.getStatus().getLabel());
            }
        }
    }

    private void saveTreatment() {
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

        if (startDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            startDateField.requestFocus();
            return;
        }

        try {
            // Get IDs
            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);

            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);

            // Parse dates
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate;
            try {
                startDate = sdf.parse(startDateField.getText().trim());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không đúng định dạng (dd/MM/yyyy)!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                startDateField.requestFocus();
                return;
            }

            Date endDate = null;
            if (!endDateField.getText().trim().isEmpty()) {
                try {
                    endDate = sdf.parse(endDateField.getText().trim());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ngày kết thúc không đúng định dạng (dd/MM/yyyy)!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    endDateField.requestFocus();
                    return;
                }
            }

            String statusLabel = (String) statusCombo.getSelectedItem();
            TreatmentCourse.Status status = "Đang điều trị".equals(statusLabel) ? TreatmentCourse.Status.ACTIVE : TreatmentCourse.Status.COMPLETED;
            TreatmentCourseService service = TreatmentCourseService.getInstance();
            if (course == null) {
                TreatmentCourse newCourse = new TreatmentCourse();
                newCourse.setCustomerId(customerId);
                newCourse.setPetId(petId);
                newCourse.setStartDate(startDate);
                newCourse.setEndDate(endDate);
                newCourse.setStatus(status);
                service.createCourse(newCourse);
                JOptionPane.showMessageDialog(this, "Thêm liệu trình thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                course.setCustomerId(customerId);
                course.setPetId(petId);
                course.setStartDate(startDate);
                course.setEndDate(endDate);
                course.setStatus(status);
                service.updateCourse(course);
                JOptionPane.showMessageDialog(this, "Cập nhật liệu trình thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
