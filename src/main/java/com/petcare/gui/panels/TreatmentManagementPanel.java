package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditTreatmentDialog;
import com.petcare.gui.dialogs.TreatmentSessionsDialog;
import com.petcare.model.entity.TreatmentCourseListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.TreatmentCourseService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;

/**
 * Treatment Course Management Panel with CRUD operations
 */
public class TreatmentManagementPanel extends JPanel {
    private JTable treatmentTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
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
        add(headerPanel, BorderLayout.NORTH);

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

        java.awt.Color iconColor = new Color(60, 60, 60);
        JPanel sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(new Color(245, 245, 245));
        sideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideButtonPanel.setMinimumSize(new java.awt.Dimension(175, 0));
        sideButtonPanel.setPreferredSize(new java.awt.Dimension(175, 0));
        addButton = new JButton("Thêm");
        addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showAddTreatmentDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditTreatmentDialog());
        sideButtonPanel.add(editButton);
        viewSessionsButton = new JButton("Xem buổi điều trị");
        viewSessionsButton.setIcon(EmojiFontHelper.createEmojiIcon("📋", iconColor));
        viewSessionsButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(viewSessionsButton);
        viewSessionsButton.addActionListener(e -> showTreatmentSessions());
        sideButtonPanel.add(viewSessionsButton);
        completeButton = new JButton("Kết thúc");
        completeButton.setIcon(EmojiFontHelper.createEmojiIcon("✅", Color.WHITE));
        completeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(completeButton);
        completeButton.setBackground(new Color(40, 167, 69));
        completeButton.setForeground(Color.WHITE);
        completeButton.addActionListener(e -> completeTreatment());
        sideButtonPanel.add(completeButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteTreatment());
        sideButtonPanel.add(deleteButton);
        refreshButton = new JButton("Làm mới");
        refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
        refreshButton.addActionListener(e -> refreshData());
        sideButtonPanel.add(refreshButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        paginationPanel = new TablePaginationPanel(treatmentTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }
    
    public void refreshData() {
        loadTreatmentCourses();
    }
    
    private void loadTreatmentCourses() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<TreatmentCourseListDto> list = TreatmentCourseService.getInstance().getCoursesForList();
            for (TreatmentCourseListDto dto : list) {
                String startDateStr = dto.getStartDate() != null ? sdf.format(dto.getStartDate()) : "";
                String endDateStr = dto.getEndDate() != null ? sdf.format(dto.getEndDate()) : "";
                String statusLabel = TreatmentCourseService.statusCodeToLabel(dto.getStatus());
                tableModel.addRow(new Object[]{
                    dto.getTreatmentCourseId(),
                    startDateStr,
                    endDateStr,
                    dto.getCustomerName() != null ? dto.getCustomerName() : "",
                    dto.getPetName() != null ? dto.getPetName() : "",
                    statusLabel
                });
            }
            if (paginationPanel != null) paginationPanel.refresh();
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn liệu trình cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = treatmentTable.convertRowIndexToModel(selectedRow);
        int courseId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            com.petcare.model.domain.TreatmentCourse course = TreatmentCourseService.getInstance().getCourseById(courseId);
            if (course != null) {
                AddEditTreatmentDialog dialog = new AddEditTreatmentDialog(null, course);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        int modelRow = treatmentTable.convertRowIndexToModel(selectedRow);
        int courseId = (Integer) tableModel.getValueAt(modelRow, 0);
        TreatmentSessionsDialog dialog = new TreatmentSessionsDialog(null, courseId);
        dialog.setVisible(true);
    }
    
    private void completeTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn liệu trình cần kết thúc!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = treatmentTable.convertRowIndexToModel(selectedRow);
        int courseId = (Integer) tableModel.getValueAt(modelRow, 0);
        String status = (String) tableModel.getValueAt(modelRow, 5);
        if ("Kết thúc".equals(status)) {
            JOptionPane.showMessageDialog(this, "Liệu trình này đã kết thúc rồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn kết thúc liệu trình này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                TreatmentCourseService.getInstance().completeCourse(courseId);
                JOptionPane.showMessageDialog(this, "Kết thúc liệu trình thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn liệu trình cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = treatmentTable.convertRowIndexToModel(selectedRow);
        int courseId = (Integer) tableModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa liệu trình này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                TreatmentCourseService.getInstance().deleteCourse(courseId);
                JOptionPane.showMessageDialog(this, "Xóa liệu trình thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
