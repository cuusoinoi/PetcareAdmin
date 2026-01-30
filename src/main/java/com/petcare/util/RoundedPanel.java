package com.petcare.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel có nền và viền bo góc (mềm mại, hiện đại).
 * Dùng cho card thống kê, khung biểu đồ, v.v.
 */
public class RoundedPanel extends JPanel {
    private final int radius;
    private final Color borderColor;
    private final float borderThickness;

    public RoundedPanel(int radius) {
        this(radius, ThemeManager.getBorderColor(), 1f);
    }

    public RoundedPanel(int radius, Color borderColor, float borderThickness) {
        this.radius = radius;
        this.borderColor = borderColor != null ? borderColor : ThemeManager.getBorderColor();
        this.borderThickness = borderThickness <= 0 ? 1f : borderThickness;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        int r = Math.min(radius, Math.min(w, h) / 2);
        // Nền bo góc
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, r * 2f, r * 2f));
        // Viền bo góc (mảnh, mềm)
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.draw(new RoundRectangle2D.Float(borderThickness / 2, borderThickness / 2,
                w - borderThickness, h - borderThickness, r * 2f, r * 2f));
        g2.dispose();
        super.paintComponent(g);
    }
}
