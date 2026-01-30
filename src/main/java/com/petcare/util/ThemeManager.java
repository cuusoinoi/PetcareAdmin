package com.petcare.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Theme Manager for switching between Light and Dark mode
 */
public class ThemeManager {
    private static final String THEME_PREF_KEY = "theme";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    /**
     * Màu nền tối sâu cho giao diện dark
     */
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
                applyLightUiDefaults();
            }
            applyModernStyle();
            prefs.put(THEME_PREF_KEY, theme);
            prefs.flush();
        } catch (Exception ex) {
            System.err.println("Failed to set theme: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Bo góc, font hiện đại, scrollbar bo góc – áp dụng cho toàn bộ UI
     */
    private static void applyModernStyle() {
        // Bo góc (arc): nút, ô nhập, combo, checkbox, progress bar
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("ProgressBar.arc", 8);
        // Scrollbar bo góc
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.trackArc", 8);
        UIManager.put("ScrollBar.width", 12);
        // Font giống Restaurant: size 14, Segoe UI Semibold cho header/nút
        Font baseFont = createModernFont(14);
        Font semiboldFont = createSemiboldFont(14);
        Font headerFont = createSemiboldFont(16);
        UIManager.put("Label.font", baseFont);
        UIManager.put("Button.font", semiboldFont);
        UIManager.put("ToggleButton.font", semiboldFont);
        UIManager.put("TextField.font", baseFont);
        UIManager.put("ComboBox.font", baseFont);
        UIManager.put("Table.font", baseFont);
        UIManager.put("TableHeader.font", headerFont);
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("TableHeader.height", 34);
    }

    private static Font createModernFont(int size) {
        String[] families = { "Segoe UI", "SF Pro Display", "Inter", "Roboto", "Helvetica Neue", Font.SANS_SERIF };
        for (String name : families) {
            Font f = new Font(name, Font.PLAIN, size);
            if (name.equals(f.getFamily())) {
                return f;
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }

    /** Segoe UI Semibold (đẹp như Restaurant), fallback BOLD nếu không có. */
    private static Font createSemiboldFont(int size) {
        Font f = new Font("Segoe UI Semibold", Font.PLAIN, size);
        if ("Segoe UI Semibold".equals(f.getFamily()) || "Segoe UI".equals(f.getFamily())) {
            return f;
        }
        return createModernFont(size).deriveFont(Font.BOLD);
    }

    /** Font hiện đại dùng chung (Label, Button, TextField, ...). */
    public static Font getModernFont(int size) {
        return createModernFont(size);
    }

    /** Font Semibold cho tiêu đề, nút (giống Restaurant). */
    public static Font getSemiboldFont(int size) {
        return createSemiboldFont(size);
    }

    /**
     * Áp dụng màu sáng cho các key đã ghi đè khi dark, để chuyển lại light đúng
     */
    private static void applyLightUiDefaults() {
        UIManager.put("Panel.background", LIGHT_CONTENT);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("TableHeader.background", new Color(0xf0f0f0));
        UIManager.put("TableHeader.foreground", Color.BLACK);
        UIManager.put("Viewport.background", Color.WHITE);
        UIManager.put("ScrollPane.background", LIGHT_CONTENT);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Button.background", new Color(0xf0f0f0));
        UIManager.put("Button.foreground", Color.BLACK);
    }

    /**
     * Áp dụng màu nền đen sâu cho toàn bộ UI khi dark mode
     */
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
     * Màu nền và chữ của bảng (JTable) theo theme
     */
    public static Color getTableBackground() {
        return isDarkMode() ? DARK_BG : Color.WHITE;
    }

    public static Color getTableForeground() {
        return isDarkMode() ? new Color(0xe0e0e0) : Color.BLACK;
    }

    public static Color getTableHeaderBackground() {
        return isDarkMode() ? DARK_HEADER : new Color(0xf0f0f0);
    }

    public static Color getTableHeaderForeground() {
        return isDarkMode() ? new Color(0xe0e0e0) : Color.BLACK;
    }

    /**
     * Màu ô nhập (JTextField) theo theme
     */
    public static Color getTextFieldBackground() {
        return isDarkMode() ? new Color(0x2d2d2d) : Color.WHITE;
    }

    public static Color getTextFieldForeground() {
        return isDarkMode() ? new Color(0xe0e0e0) : Color.BLACK;
    }

    /**
     * Màu nút thường (JButton) theo theme
     */
    public static Color getButtonBackground() {
        return isDarkMode() ? new Color(0x3d3d3d) : new Color(0xf0f0f0);
    }

    public static Color getButtonForeground() {
        return isDarkMode() ? new Color(0xe0e0e0) : Color.BLACK;
    }

    /**
     * Màu icon trên nút (giao diện tối = sáng để thấy, giao diện sáng = tối)
     */
    public static Color getIconColor() {
        return isDarkMode() ? new Color(0xc0c0c0) : new Color(60, 60, 60);
    }

    /**
     * Áp dụng màu theme cho bảng và header (gọi trong initComponents và updateTheme)
     */
    public static void applyTableTheme(JTable table) {
        if (table == null) return;
        table.setBackground(getTableBackground());
        table.setForeground(getTableForeground());
        table.getTableHeader().setBackground(getTableHeaderBackground());
        table.getTableHeader().setForeground(getTableHeaderForeground());
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
