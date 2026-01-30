package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditUserDialog;
import com.petcare.gui.dialogs.ChangePasswordDialog;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.IUserService;
import com.petcare.service.UserService;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * User Management Panel - uses UserService only (no direct Database)
 */
public class UserManagementPanel extends JPanel {
    private JTable userTable;
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
    private JButton changePasswordButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private final IUserService userService;
    private final User currentUser;

    public UserManagementPanel(User currentUser) {
        this.userService = UserService.getInstance();
        this.currentUser = currentUser;
        initComponents();
        loadUsers();
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

        titleLabel = new JLabel("Quản lý Người dùng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.getTitleForeground());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Họ tên", "Vai trò"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.setSelectionBackground(new Color(139, 69, 19));
        userTable.setSelectionForeground(Color.WHITE);
        ThemeManager.applyTableTheme(userTable);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(null);

        searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(ThemeManager.getContentBackground());
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo username, họ tên, vai trò...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                paginationPanel.setSearchText(searchField.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                paginationPanel.setSearchText(searchField.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                paginationPanel.setSearchText(searchField.getText().trim());
            }
        });
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);

        java.awt.Color iconColor = ThemeManager.isDarkMode() ? new Color(0xc0c0c0) : new Color(60, 60, 60);
        sideButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        sideButtonPanel.setBackground(ThemeManager.getSideButtonPanelBackground());
        sideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideButtonPanel.setMinimumSize(new java.awt.Dimension(175, 0));
        sideButtonPanel.setPreferredSize(new java.awt.Dimension(175, 0));
        addButton = new JButton("Thêm");
        addButton.setIcon(EmojiFontHelper.createEmojiIcon("➕", iconColor));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(addButton);
        addButton.addActionListener(e -> showAddUserDialog());
        sideButtonPanel.add(addButton);
        editButton = new JButton("Sửa");
        editButton.setIcon(EmojiFontHelper.createEmojiIcon("✏️", iconColor));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(editButton);
        editButton.addActionListener(e -> showEditUserDialog());
        sideButtonPanel.add(editButton);
        changePasswordButton = new JButton("Đổi mật khẩu");
        changePasswordButton.setIcon(EmojiFontHelper.createEmojiIcon("🔑", iconColor));
        changePasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(changePasswordButton);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        sideButtonPanel.add(changePasswordButton);
        deleteButton = new JButton("Xóa");
        deleteButton.setIcon(EmojiFontHelper.createEmojiIcon("🗑️", iconColor));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GUIUtil.setToolbarButtonSize(deleteButton);
        deleteButton.addActionListener(e -> deleteUser());
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
        paginationPanel = new TablePaginationPanel(userTable);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(sideButtonPanel, BorderLayout.EAST);
    }

    public void refreshData() {
        loadUsers();
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
            changePasswordButton.setIcon(EmojiFontHelper.createEmojiIcon("🔑", iconColor));
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
        ThemeManager.applyTableTheme(userTable);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            for (User user : userService.getAllUsers()) {
                String roleLabel = user.getRole() == User.Role.ADMIN ? "Quản trị viên" : "Nhân viên";
                tableModel.addRow(new Object[]{
                        user.getUserId(),
                        user.getUsername(),
                        user.getFullname(),
                        roleLabel
                });
            }
            if (paginationPanel != null) paginationPanel.refresh();
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserDialog() {
                AddEditUserDialog dialog = new AddEditUserDialog(null, null, currentUser);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn người dùng cần sửa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        int userId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                AddEditUserDialog dialog = new AddEditUserDialog(null, user, currentUser);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showChangePasswordDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn người dùng cần đổi mật khẩu!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        int userId = (Integer) tableModel.getValueAt(modelRow, 0);
        ChangePasswordDialog dialog = new ChangePasswordDialog(null, userId, currentUser);
        dialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn người dùng cần xóa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        int userId = (Integer) tableModel.getValueAt(modelRow, 0);
        String username = (String) tableModel.getValueAt(modelRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa người dùng \"" + username + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userService.deleteUser(userId, currentUser);
                JOptionPane.showMessageDialog(this,
                        "Xóa người dùng thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (PetcareException ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
