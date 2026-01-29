package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditPetEnclosureDialog;
import com.petcare.gui.dialogs.CheckoutDialog;
import com.petcare.model.entity.PetEnclosureListDto;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.PetEnclosureService;
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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

/**
 * Pet Enclosure Management Panel with Check-in/Check-out
 */
public class PetEnclosureManagementPanel extends JPanel {
    private JTable enclosureTable;
    private DefaultTableModel tableModel;
    private TablePaginationPanel paginationPanel;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel searchPanel;
    private JPanel sideButtonPanel;
    private JLabel titleLabel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton checkoutButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public PetEnclosureManagementPanel() {
        initComponents();
        loadEnclosures();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getContentBackground());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.getHeaderBackground());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        titleLabel = new JLabel("Quản lý Lưu chuồng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Số chuồng", "Khách hàng", "Thú cưng", "Ngày Check-in",
                           "Ngày Check-out", "Phí/ngày", "Đặt cọc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enclosureTable = new JTable(tableModel);
        enclosureTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        enclosureTable.setRowHeight(30);
        enclosureTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        enclosureTable.setSelectionBackground(new Color(139, 69, 19));
        enclosureTable.setSelectionForeground(Color.WHITE);
        ThemeManager.applyTableTheme(enclosureTable);

        JScrollPane scrollPane = new JScrollPane(enclosureTable);
        scrollPane.setBorder(null);

        searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(ThemeManager.getContentBackground());
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo số chuồng, khách hàng, thú cưng...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
            @Override
            public void removeUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
            @Override
            public void changedUpdate(DocumentEvent e) { paginationPanel.setSearchText(searchField.getText().trim()); }
        });
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);

        java.awt.Color iconColor = ThemeManager.isDarkMode() ? new Color(0xc0c0c0) : new Color(60, 60, 60);
        sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
        sideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideButtonPanel.setMinimumSize(new java.awt.Dimension(175, 0));
        sideButtonPanel.setPreferredSize(new java.awt.Dimension(175, 0));
        addButton = new JButton("Check-in");
        addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showCheckInDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditEnclosureDialog());
        sideButtonPanel.add(editButton);
        checkoutButton = new JButton("Check-out");
        checkoutButton.setIcon(EmojiFontHelper.createEmojiIcon("✅", Color.WHITE));
        checkoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(checkoutButton);
        checkoutButton.setBackground(new Color(40, 167, 69));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.addActionListener(e -> showCheckoutDialog());
        sideButtonPanel.add(checkoutButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteEnclosure());
        sideButtonPanel.add(deleteButton);
        refreshButton = new JButton("Làm mới");
        refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(refreshButton);
        refreshButton.addActionListener(e -> refreshData());
        sideButtonPanel.add(refreshButton);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.getContentBackground());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        paginationPanel = new TablePaginationPanel(enclosureTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }
    
    public void refreshData() {
        loadEnclosures();
    }

    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        if (searchPanel != null) searchPanel.setBackground(ThemeManager.getContentBackground());
        if (headerPanel != null) {
            headerPanel.setBackground(ThemeManager.getHeaderBackground());
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
        }
        if (titleLabel != null) titleLabel.setForeground(ThemeManager.getTitleForeground());
        if (centerPanel != null) centerPanel.setBackground(ThemeManager.getContentBackground());
        if (sideButtonPanel != null) {
            sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
            for (java.awt.Component c : sideButtonPanel.getComponents()) {
                if (c instanceof JButton) {
                    ((JButton) c).setBackground(ThemeManager.getButtonBackground());
                    ((JButton) c).setForeground(ThemeManager.getButtonForeground());
                }
            }
            java.awt.Color iconColor = ThemeManager.getIconColor();
            addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
            editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
            checkoutButton.setIcon(EmojiFontHelper.createEmojiIcon("✅", Color.WHITE));
            checkoutButton.setBackground(new Color(40, 167, 69));
            checkoutButton.setForeground(Color.WHITE);
            deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
            refreshButton.setIcon(EmojiFontHelper.createEmojiIcon("🔄", iconColor));
        }
        if (searchField != null) {
            searchField.setBackground(ThemeManager.getTextFieldBackground());
            searchField.setForeground(ThemeManager.getTextFieldForeground());
        }
        if (searchPanel != null) {
            for (java.awt.Component c : searchPanel.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(ThemeManager.getTitleForeground());
            }
        }
        if (paginationPanel != null) paginationPanel.updateTheme();
        ThemeManager.applyTableTheme(enclosureTable);
    }


    private void loadEnclosures() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<PetEnclosureListDto> list = PetEnclosureService.getInstance().getEnclosuresForList();
            for (PetEnclosureListDto dto : list) {
                String checkInStr = dto.getCheckInDate() != null ? sdf.format(dto.getCheckInDate()) : "";
                String checkOutStr = dto.getCheckOutDate() != null ? dateSdf.format(dto.getCheckOutDate()) : "";
                tableModel.addRow(new Object[]{
                    dto.getPetEnclosureId(),
                    dto.getPetEnclosureNumber(),
                    dto.getCustomerName() != null ? dto.getCustomerName() : "",
                    dto.getPetName() != null ? dto.getPetName() : "",
                    checkInStr,
                    checkOutStr,
                    dto.getDailyRate(),
                    dto.getDeposit(),
                    dto.getPetEnclosureStatus() != null ? dto.getPetEnclosureStatus() : ""
                });
            }
            if (paginationPanel != null) paginationPanel.refresh();
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCheckInDialog() {
        AddEditPetEnclosureDialog dialog = new AddEditPetEnclosureDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditEnclosureDialog() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = enclosureTable.convertRowIndexToModel(selectedRow);
        int enclosureId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            com.petcare.model.domain.PetEnclosure enclosure = PetEnclosureService.getInstance().getEnclosureById(enclosureId);
            if (enclosure != null) {
                AddEditPetEnclosureDialog dialog = new AddEditPetEnclosureDialog(null, enclosure);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCheckoutDialog() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng cần checkout!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = enclosureTable.convertRowIndexToModel(selectedRow);
        int enclosureId = (Integer) tableModel.getValueAt(modelRow, 0);
        String status = (String) tableModel.getValueAt(modelRow, 8);
        if ("Check Out".equals(status)) {
            JOptionPane.showMessageDialog(this, "Lưu chuồng này đã được checkout rồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            com.petcare.model.domain.PetEnclosure enclosure = PetEnclosureService.getInstance().getEnclosureById(enclosureId);
            if (enclosure != null) {
                CheckoutDialog dialog = new CheckoutDialog(null, enclosure);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEnclosure() {
        int selectedRow = enclosureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lưu chuồng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = enclosureTable.convertRowIndexToModel(selectedRow);
        int enclosureId = (Integer) tableModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lưu chuồng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PetEnclosureService.getInstance().deleteEnclosure(enclosureId);
                JOptionPane.showMessageDialog(this, "Xóa lưu chuồng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
