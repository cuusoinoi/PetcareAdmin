package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.entity.TreatmentCourseInfoDto;
import com.petcare.model.entity.TreatmentSessionListDto;
import com.petcare.service.TreatmentCourseService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
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
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            TreatmentCourseInfoDto courseInfo = TreatmentCourseService.getInstance().getCourseInfoForSessions(courseId);
            if (courseInfo != null) {
                String startDateStr = courseInfo.getStartDate() != null ? sdfDate.format(courseInfo.getStartDate()) : "";
                infoPanel.add(createInfoLabel("Khách hàng:"));
                infoPanel.add(createInfoValue(courseInfo.getCustomerName()));
                infoPanel.add(createInfoLabel("Thú cưng:"));
                infoPanel.add(createInfoValue(courseInfo.getPetName()));
                infoPanel.add(createInfoLabel("Ngày bắt đầu:"));
                infoPanel.add(createInfoValue(startDateStr));
            }
        } catch (Exception ex) {
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
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            List<TreatmentSessionListDto> sessions = TreatmentCourseService.getInstance().getSessionsByCourseId(courseId);
            for (TreatmentSessionListDto dto : sessions) {
                String datetimeStr = dto.getTreatmentSessionDatetime() != null ? sdfDateTime.format(dto.getTreatmentSessionDatetime()) : "";
                tableModel.addRow(new Object[]{
                    dto.getTreatmentSessionId(),
                    datetimeStr,
                    dto.getDoctorName() != null ? dto.getDoctorName() : "",
                    dto.getTemperature() + "°C",
                    dto.getWeight() + " kg",
                    dto.getPulseRate() != null && dto.getPulseRate() != 0 ? String.valueOf(dto.getPulseRate()) : "",
                    dto.getRespiratoryRate() != null && dto.getRespiratoryRate() != 0 ? String.valueOf(dto.getRespiratoryRate()) : ""
                });
            }
        } catch (Exception ex) {
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
