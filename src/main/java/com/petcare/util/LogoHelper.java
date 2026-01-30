package com.petcare.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public final class LogoHelper {
    private static final String LOGO_PATH = "/images/logo.png";

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
