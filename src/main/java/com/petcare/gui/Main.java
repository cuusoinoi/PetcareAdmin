package com.petcare.gui;

import com.petcare.util.ThemeManager;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Petcare Admin Application
 */
public class Main {
    public static void main(String[] args) {
        // Initialize theme (load saved preference)
        ThemeManager.initializeTheme();
        
        // Create and show login frame on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
