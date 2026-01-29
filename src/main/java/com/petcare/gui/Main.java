package com.petcare.gui;

import com.petcare.persistence.DatabaseConnection;
import com.petcare.util.ThemeManager;

import javax.swing.*;

/**
 * Main entry point for Petcare Admin Application
 */
public class Main {
    public static void main(String[] args) {
        // Khởi tạo DB trước (H2: chạy schema + data nếu bảng trống)
        try {
            DatabaseConnection.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Initialize theme (load saved preference)
        ThemeManager.initializeTheme();
        // Create and show login frame on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
