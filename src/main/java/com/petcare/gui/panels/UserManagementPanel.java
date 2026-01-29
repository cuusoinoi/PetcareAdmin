package com.petcare.gui.panels;

import com.petcare.gui.dialogs.AddEditUserDialog;
import com.petcare.gui.dialogs.ChangePasswordDialog;
import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;
import com.petcare.service.UserService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * User Management Panel - uses UserService only (no direct Database)
 */
public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton changePasswordButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private final UserService userService;

    public UserManagementPanel() {
        this.userService = UserService.getInstance();
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Quản lý Người dùng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));

        addButton = new JButton("➕ Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddUserDialog());
        buttonPanel.add(addButton);

        editButton = new JButton("✏️ Sửa");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> showEditUserDialog());
        buttonPanel.add(editButton);

        changePasswordButton = new JButton("🔑 Đổi mật khẩu");
        changePasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        buttonPanel.add(changePasswordButton);

        deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteUser());
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
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

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadUsers();
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
        } catch (PetcareException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserDialog() {
        AddEditUserDialog dialog = new AddEditUserDialog(null, null);
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
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                AddEditUserDialog dialog = new AddEditUserDialog(null, user);
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
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        ChangePasswordDialog dialog = new ChangePasswordDialog(null, userId);
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
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa người dùng \"" + username + "\"?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userService.deleteUser(userId);
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
