package com.petcare;

import com.petcare.gui.LoginFrame;
import com.petcare.persistence.DatabaseConnection;
import com.petcare.util.ThemeManager;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ThemeManager.initializeTheme();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
