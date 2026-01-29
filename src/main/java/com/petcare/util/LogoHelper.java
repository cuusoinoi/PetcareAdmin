package com.petcare.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Tải logo UIT Petcare từ resources (images/logo.png) dùng cho màn hình đăng nhập và sidebar.
 */
public final class LogoHelper {

    private static final String LOGO_PATH = "/images/logo.png";

    /**
     * Tạo ImageIcon logo với kích thước cho trước (scale từ file logo.png).
     *
     * @param sizePx kích thước cạnh (px), ví dụ 48 cho login, 36 cho sidebar
     */
    public static ImageIcon createLogoIcon(int sizePx) {
        try {
            URL url = LogoHelper.class.getResource(LOGO_PATH);
            if (url == null) return null;
            BufferedImage original = ImageIO.read(url);
            if (original == null) return null;
            Image scaled = original.getScaledInstance(sizePx, sizePx, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }
}
