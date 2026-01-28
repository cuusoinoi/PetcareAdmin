package com.petcare.gui;

import com.petcare.model.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog for viewing treatment sessions of a treatment course
 */
public class TreatmentSessionsDialog extends JDialog {
    
    public TreatmentSessionsDialog(JDialog parent, int courseId) {
        super(parent, true);
        initComponents(courseId);
    }
    
    private void initComponents(int courseId) {
        setSize(900, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setTitle("Buổi điều trị - Liệu trình #" + courseId);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Info panel
        JPanel infoPanel = new JPanel(new java.awt.GridLayout(0, 2, 15, 10));
        infoPanel.setBackground(Color.WHITE);
        
        try {
            String query = "SELECT tc.treatment_course_id, tc.start_date, c.customer_name, p.pet_name " +
                          "FROM treatment_courses tc " +
                          "INNER JOIN customers c ON tc.customer_id = c.customer_id " +
                          "INNER JOIN pets p ON tc.pet_id = p.pet_id " +
                          "WHERE tc.treatment_course_id = ?";
            
            ResultSet rs = Database.executeQuery(query, courseId);
            
            if (rs != null && rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String startDate = "";
                if (rs.getDate("start_date") != null) {
                    startDate = sdf.format(rs.getDate("start_date"));
                }
                
                infoPanel.add(createInfoLabel("Khách hàng:"));
                infoPanel.add(createInfoValue(rs.getString("customer_name")));
                
                infoPanel.add(createInfoLabel("Thú cưng:"));
                infoPanel.add(createInfoValue(rs.getString("pet_name")));
                
                infoPanel.add(createInfoLabel("Ngày bắt đầu:"));
                infoPanel.add(createInfoValue(startDate));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Sessions table
        String[] columns = {"ID", "Ngày giờ", "Bác sĩ", "Nhiệt độ", "Cân nặng", "Nhịp tim", "Nhịp thở"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try {
            String query = "SELECT ts.treatment_session_id, ts.treatment_session_datetime, " +
                          "d.doctor_name, ts.temperature, ts.weight, ts.pulse_rate, ts.respiratory_rate " +
                          "FROM treatment_sessions ts " +
                          "INNER JOIN doctors d ON ts.doctor_id = d.doctor_id " +
                          "WHERE ts.treatment_course_id = ? " +
                          "ORDER BY ts.treatment_session_datetime DESC";
            
            ResultSet rs = Database.executeQuery(query, courseId);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            while (rs != null && rs.next()) {
                String datetime = "";
                if (rs.getTimestamp("treatment_session_datetime") != null) {
                    datetime = sdf.format(rs.getTimestamp("treatment_session_datetime"));
                }
                
                Object[] row = {
                    rs.getInt("treatment_session_id"),
                    datetime,
                    rs.getString("doctor_name"),
                    rs.getDouble("temperature") + "°C",
                    rs.getDouble("weight") + " kg",
                    rs.getInt("pulse_rate") != 0 ? String.valueOf(rs.getInt("pulse_rate")) : "",
                    rs.getInt("respiratory_rate") != 0 ? String.valueOf(rs.getInt("respiratory_rate")) : ""
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JTable sessionsTable = new JTable(tableModel);
        sessionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sessionsTable.setRowHeight(30);
        sessionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(sessionsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách buổi điều trị"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        javax.swing.JButton closeButton = new javax.swing.JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }
    
    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text != null ? text : "");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
}
