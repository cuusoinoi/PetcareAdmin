package com.petcare.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.prefs.Preferences;
import javax.swing.UIManager;

/**
 * Theme Manager for switching between Light and Dark mode
 */
public class ThemeManager {
    private static final String THEME_PREF_KEY = "theme";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    /**
     * Get current theme
     */
    public static String getCurrentTheme() {
        return prefs.get(THEME_PREF_KEY, THEME_LIGHT);
    }
    
    /**
     * Set theme and save preference
     */
    public static void setTheme(String theme) {
        try {
            if (THEME_DARK.equals(theme)) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            prefs.put(THEME_PREF_KEY, theme);
        } catch (Exception ex) {
            System.err.println("Failed to set theme: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Toggle between light and dark theme
     */
    public static void toggleTheme() {
        String current = getCurrentTheme();
        setTheme(THEME_LIGHT.equals(current) ? THEME_DARK : THEME_LIGHT);
    }
    
    /**
     * Check if dark mode is enabled
     */
    public static boolean isDarkMode() {
        return THEME_DARK.equals(getCurrentTheme());
    }
    
    /**
     * Initialize theme on application start
     */
    public static void initializeTheme() {
        String theme = getCurrentTheme();
        setTheme(theme);
    }
}
