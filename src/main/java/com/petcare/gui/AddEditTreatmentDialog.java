package com.petcare.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.Database;
import com.petcare.model.TreatmentCourse;
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
import javax.swing.JTextField;

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
        statusCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        statusCombo.addItem("Đang điều trị");
        statusCombo.addItem("Kết thúc");
        formPanel.add(statusCombo);
        
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
        saveButton.addActionListener(e -> saveTreatment());
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
    
    private void loadPetsByCustomer() {
        petCombo.removeAllItems();
        petCombo.addItem("-- Chọn thú cưng --");
        
        if (customerCombo.getSelectedIndex() == 0) {
            return;
        }
        
        try {
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            
            String query = "SELECT pet_id, pet_name FROM pets WHERE customer_id = ? ORDER BY pet_name";
            ResultSet rs = Database.executeQuery(query, customerId);
            
            while (rs != null && rs.next()) {
                String display = rs.getInt("pet_id") + " - " + rs.getString("pet_name");
                petCombo.addItem(display);
            }
        } catch (SQLException ex) {
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
            String statusCode = "Đang điều trị".equals(statusLabel) ? "1" : "0";
            
            if (course == null) {
                // Insert
                String query = "INSERT INTO treatment_courses (customer_id, pet_id, start_date, end_date, status) " +
                              "VALUES (?, ?, ?, ?, ?)";
                
                java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
                java.sql.Date sqlEndDate = endDate != null ? new java.sql.Date(endDate.getTime()) : null;
                
                int result = Database.executeUpdate(query,
                    customerId,
                    petId,
                    sqlStartDate,
                    sqlEndDate,
                    statusCode
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm liệu trình thành công!", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
            } else {
                // Update
                String query = "UPDATE treatment_courses SET customer_id = ?, pet_id = ?, start_date = ?, " +
                              "end_date = ?, status = ? WHERE treatment_course_id = ?";
                
                java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
                java.sql.Date sqlEndDate = endDate != null ? new java.sql.Date(endDate.getTime()) : null;
                
                int result = Database.executeUpdate(query,
                    customerId,
                    petId,
                    sqlStartDate,
                    sqlEndDate,
                    statusCode,
                    course.getTreatmentCourseId()
                );
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật liệu trình thành công!", "Thành công", 
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
