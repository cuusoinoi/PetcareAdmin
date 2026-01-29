package com.petcare.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Helper to display emoji in Swing components.
 * - createEmojiIcon(emoji): use with setIcon() + setText() for icon and text on one line (e.g. sidebar).
 * - withEmoji(emoji, text): HTML for JButton/JLabel when HTML is acceptable.
 */
public final class EmojiFontHelper {

    private static final String EMOJI_FONT_WIN = "Segoe UI Emoji";
    private static final String EMOJI_FONT_MAC = "Apple Color Emoji";
    private static final String TEXT_FONT = "Segoe UI";
    /**
     * Kích thước hiển thị icon (Swing dùng làm kích thước nút).
     */
    private static final int EMOJI_ICON_SIZE = 24;
    /**
     * Canvas vẽ lớn hơn một chút để emoji không bị cắt, sau đó scale về EMOJI_ICON_SIZE.
     */
    private static final int CANVAS_SIZE = 32;
    private static final int EMOJI_FONT_SIZE = 20;

    /**
     * Tạo ImageIcon từ emoji để dùng setIcon() + setText() — icon và chữ luôn cùng một dòng.
     * Emoji được vẽ căn giữa trên canvas có padding, rồi scale về kích thước chuẩn để không bị cắt.
     */
    public static ImageIcon createEmojiIcon(String emoji) {
        if (emoji == null || emoji.isEmpty()) return null;
        String fontName = System.getProperty("os.name", "").toLowerCase().contains("mac") ? EMOJI_FONT_MAC : EMOJI_FONT_WIN;
        Font font = new Font(fontName, Font.PLAIN, EMOJI_FONT_SIZE);
        BufferedImage img = new BufferedImage(CANVAS_SIZE, CANVAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(emoji, g);
        // Căn giữa: baseline y = phần dưới canvas trừ descent, rồi lui lên cho đủ height
        int x = (int) Math.round((CANVAS_SIZE - bounds.getWidth()) / 2.0);
        int y = (int) Math.round((CANVAS_SIZE - bounds.getHeight()) / 2.0 + fm.getAscent());
        g.drawString(emoji, x, y);
        g.dispose();
        // Scale xuống EMOJI_ICON_SIZE để kích thước nút đồng đều, vẫn đủ padding không cắt
        BufferedImage out = new BufferedImage(EMOJI_ICON_SIZE, EMOJI_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, EMOJI_ICON_SIZE, EMOJI_ICON_SIZE, null);
        g2.dispose();
        return new ImageIcon(out);
    }

    /**
     * Tạo ImageIcon emoji với kích thước tùy chỉnh (dùng cho nút cột bên: icon to, chữ bên dưới hoặc cạnh).
     */
    public static ImageIcon createEmojiIcon(String emoji, int sizePx) {
        if (emoji == null || emoji.isEmpty() || sizePx <= 0) return null;
        if (sizePx == EMOJI_ICON_SIZE) return createEmojiIcon(emoji);
        String fontName = System.getProperty("os.name", "").toLowerCase().contains("mac") ? EMOJI_FONT_MAC : EMOJI_FONT_WIN;
        int canvas = Math.max(sizePx + 10, 40);
        int fontSz = (int) (sizePx * 0.85);
        Font font = new Font(fontName, Font.PLAIN, fontSz);
        BufferedImage img = new BufferedImage(canvas, canvas, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(emoji, g);
        int x = (int) Math.round((canvas - bounds.getWidth()) / 2.0);
        int y = (int) Math.round((canvas - bounds.getHeight()) / 2.0 + fm.getAscent());
        g.drawString(emoji, x, y);
        g.dispose();
        BufferedImage out = new BufferedImage(sizePx, sizePx, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, sizePx, sizePx, null);
        g2.dispose();
        return new ImageIcon(out);
    }

    /**
     * Tạo ImageIcon emoji với màu chữ (dùng cho nút nền sáng: Color.BLACK để icon thấy rõ).
     * setIcon(createEmojiIcon(emoji, Color.BLACK)) + setText("Xóa") giữ icon và chữ cùng một dòng.
     */
    public static ImageIcon createEmojiIcon(String emoji, Color fgColor) {
        if (emoji == null || emoji.isEmpty()) return null;
        if (fgColor == null) fgColor = Color.WHITE;
        String fontName = System.getProperty("os.name", "").toLowerCase().contains("mac") ? EMOJI_FONT_MAC : EMOJI_FONT_WIN;
        Font font = new Font(fontName, Font.PLAIN, EMOJI_FONT_SIZE);
        BufferedImage img = new BufferedImage(CANVAS_SIZE, CANVAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(fgColor);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(emoji, g);
        int x = (int) Math.round((CANVAS_SIZE - bounds.getWidth()) / 2.0);
        int y = (int) Math.round((CANVAS_SIZE - bounds.getHeight()) / 2.0 + fm.getAscent());
        g.drawString(emoji, x, y);
        g.dispose();
        BufferedImage out = new BufferedImage(EMOJI_ICON_SIZE, EMOJI_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, EMOJI_ICON_SIZE, EMOJI_ICON_SIZE, null);
        g2.dispose();
        return new ImageIcon(out);
    }

    private static String emojiFont() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("mac") ? EMOJI_FONT_MAC : EMOJI_FONT_WIN;
    }

    private static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Returns HTML string so that the emoji uses an emoji font and the rest uses the normal font.
     * Use with JButton, JToggleButton, JLabel: setText(EmojiFontHelper.withEmoji(emoji, text)).
     * Spaces in text are replaced with &nbsp; to reduce unwanted line breaks in narrow layouts (e.g. sidebar).
     */
    public static String withEmoji(String emoji, String text) {
        if (emoji == null) emoji = "";
        if (text == null) text = "";
        String ef = emojiFont();
        String safeText = htmlEscape(text).replace(" ", "&nbsp;");
        return "<html><body style='white-space:nowrap'><nobr>"
                + "<span style='font-family:\"" + ef + "\"'>" + emoji + "</span>&nbsp;"
                + "<span style='font-family:\"" + TEXT_FONT + "\"'>" + safeText + "</span>"
                + "</nobr></body></html>";
    }

    /**
     * Same as withEmoji but when the full label is "emoji + space + text" in one string.
     * Splits on first space: "📊 Dashboard" -> emoji "📊", text "Dashboard".
     */
    public static String withEmoji(String labelWithEmoji) {
        if (labelWithEmoji == null || labelWithEmoji.isEmpty()) return "";
        String s = labelWithEmoji.trim();
        int space = s.indexOf(' ');
        if (space <= 0) return htmlEscape(s);
        return withEmoji(s.substring(0, space).trim(), s.substring(space).trim());
    }
}
