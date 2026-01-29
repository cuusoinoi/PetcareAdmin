package com.petcare.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
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

    /** Màu nền tối sâu cho giao diện dark */
    private static final Color DARK_BG = new Color(0x1a1a1a);
    private static final Color DARK_HEADER = new Color(0x252525);
    private static final Color DARK_BORDER = new Color(0x404040);
    private static final Color LIGHT_CONTENT = new Color(245, 245, 245);
    private static final Color LIGHT_BORDER = new Color(220, 220, 220);

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
                applyDarkUiDefaults();
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            prefs.put(THEME_PREF_KEY, theme);
        } catch (Exception ex) {
            System.err.println("Failed to set theme: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Áp dụng màu nền đen sâu cho toàn bộ UI khi dark mode */
    private static void applyDarkUiDefaults() {
        UIManager.put("Panel.background", DARK_BG);
        UIManager.put("Table.background", DARK_BG);
        UIManager.put("Table.foreground", new Color(0xe0e0e0));
        UIManager.put("TableHeader.background", DARK_HEADER);
        UIManager.put("TableHeader.foreground", new Color(0xe0e0e0));
        UIManager.put("Viewport.background", DARK_BG);
        UIManager.put("ScrollPane.background", DARK_BG);
        UIManager.put("TextField.background", new Color(0x2d2d2d));
        UIManager.put("TextField.foreground", new Color(0xe0e0e0));
        UIManager.put("ComboBox.background", new Color(0x2d2d2d));
        UIManager.put("ComboBox.foreground", new Color(0xe0e0e0));
        UIManager.put("Label.foreground", new Color(0xe0e0e0));
        UIManager.put("Button.background", new Color(0x3d3d3d));
        UIManager.put("Button.foreground", new Color(0xe0e0e0));
    }

    public static Color getHeaderBackground() {
        return isDarkMode() ? DARK_HEADER : Color.WHITE;
    }

    public static Color getContentBackground() {
        return isDarkMode() ? DARK_BG : LIGHT_CONTENT;
    }

    public static Color getSideButtonPanelBackground() {
        return isDarkMode() ? DARK_BG : LIGHT_CONTENT;
    }

    public static Color getBorderColor() {
        return isDarkMode() ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color getFormBackground() {
        return isDarkMode() ? DARK_HEADER : Color.WHITE;
    }

    public static Color getTitleForeground() {
        return isDarkMode() ? new Color(0xe0e0e0) : Color.BLACK;
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
