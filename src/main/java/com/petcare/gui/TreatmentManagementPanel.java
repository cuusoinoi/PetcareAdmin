package com.petcare.gui;

import com.petcare.model.Database;
import com.petcare.model.TreatmentCourse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Treatment Course Management Panel with CRUD operations
 */
public class TreatmentManagementPanel extends JPanel {
    private JTable treatmentTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton completeButton;
    private JButton viewSessionsButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public TreatmentManagementPanel() {
        initComponents();
        loadTreatmentCourses();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Quản lý Liệu trình điều trị");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        
        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddTreatmentDialog());
        buttonPanel.add(addButton);
        
        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditTreatmentDialog());
        buttonPanel.add(editButton);
        
        viewSessionsButton = new JButton("📋 Xem buổi điều trị");
        viewSessionsButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewSessionsButton.addActionListener(e -> showTreatmentSessions());
        buttonPanel.add(viewSessionsButton);
        
        completeButton = new JButton("✅ Kết thúc");
        completeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        completeButton.setBackground(new Color(40, 167, 69));
        completeButton.setForeground(Color.WHITE);
        completeButton.addActionListener(e -> completeTreatment());
        buttonPanel.add(completeButton);
        
        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteTreatment());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Ngày bắt đầu", "Ngày kết thúc", "Khách hàng", "Thú cưng", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        treatmentTable = new JTable(tableModel);
        treatmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        treatmentTable.setRowHeight(30);
        treatmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        treatmentTable.setSelectionBackground(new Color(139, 69, 19));
        treatmentTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(treatmentTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        loadTreatmentCourses();
    }
    
    private void loadTreatmentCourses() {
        tableModel.setRowCount(0);
        
        try {
            String query = "SELECT tc.treatment_course_id, tc.start_date, tc.end_date, " +
                          "c.customer_name, p.pet_name, tc.status " +
                          "FROM treatment_courses tc " +
                          "INNER JOIN customers c ON tc.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON tc.pet_id = p.pet_id " +
                          "ORDER BY tc.treatment_course_id DESC";
            
            ResultSet rs = Database.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs != null && rs.next()) {
                String startDate = "";
                if (rs.getDate("start_date") != null) {
                    startDate = sdf.format(rs.getDate("start_date"));
                }
                
                String endDate = "";
                if (rs.getDate("end_date") != null) {
                    endDate = sdf.format(rs.getDate("end_date"));
                }
                
                String statusLabel = "1".equals(rs.getString("status")) ? "Đang điều trị" : "Kết thúc";
                
                Object[] row = {
                    rs.getInt("treatment_course_id"),
                    startDate,
                    endDate,
                    rs.getString("customer_name"),
                    rs.getString("pet_name"),
                    statusLabel
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showAddTreatmentDialog() {
        AddEditTreatmentDialog dialog = new AddEditTreatmentDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditTreatmentDialog() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn liệu trình cần sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        TreatmentCourse course = getTreatmentCourseById(courseId);
        
        if (course != null) {
            AddEditTreatmentDialog dialog = new AddEditTreatmentDialog(null, course);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }
    
    private void showTreatmentSessions() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn liệu trình cần xem!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        TreatmentSessionsDialog dialog = new TreatmentSessionsDialog(null, courseId);
        dialog.setVisible(true);
    }
    
    private void completeTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn liệu trình cần kết thúc!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        if ("Kết thúc".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "Liệu trình này đã kết thúc rồi!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn kết thúc liệu trình này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "UPDATE treatment_courses SET end_date = CURDATE(), status = '0' " +
                              "WHERE treatment_course_id = ?";
                int result = Database.executeUpdate(query, courseId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Kết thúc liệu trình thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void deleteTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn liệu trình cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa liệu trình này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM treatment_courses WHERE treatment_course_id = ?";
                int result = Database.executeUpdate(query, courseId);
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa liệu trình thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa liệu trình.", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private TreatmentCourse getTreatmentCourseById(int courseId) {
        try {
            String query = "SELECT * FROM treatment_courses WHERE treatment_course_id = ?";
            ResultSet rs = Database.executeQuery(query, courseId);
            
            if (rs != null && rs.next()) {
                TreatmentCourse course = new TreatmentCourse();
                course.setTreatmentCourseId(rs.getInt("treatment_course_id"));
                course.setCustomerId(rs.getInt("customer_id"));
                course.setPetId(rs.getInt("pet_id"));
                
                if (rs.getDate("start_date") != null) {
                    course.setStartDate(new java.util.Date(
                        rs.getDate("start_date").getTime()));
                }
                
                if (rs.getDate("end_date") != null) {
                    course.setEndDate(new java.util.Date(
                        rs.getDate("end_date").getTime()));
                }
                
                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    course.setStatus(TreatmentCourse.Status.fromCode(statusStr));
                }
                
                return course;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
