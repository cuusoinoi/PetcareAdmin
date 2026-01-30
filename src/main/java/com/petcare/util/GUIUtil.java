package com.petcare.util;

import javax.swing.*;
import java.awt.*;

public final class GUIUtil {
    public static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(155, 36);
    public static final Dimension SIDEBAR_BUTTON_SIZE = new Dimension(145, 36);
    public static final int TEXT_FIELD_COLUMNS = 32;

    public static void setToolbarButtonSize(JButton button) {
        if (button != null) {
            button.setPreferredSize(TOOLBAR_BUTTON_SIZE);
            button.setMinimumSize(TOOLBAR_BUTTON_SIZE);
            button.setMaximumSize(TOOLBAR_BUTTON_SIZE);
        }
    }

    public static void setSidebarButtonSize(JButton button) {
        if (button != null) {
            button.setPreferredSize(SIDEBAR_BUTTON_SIZE);
            button.setMinimumSize(SIDEBAR_BUTTON_SIZE);
            button.setMaximumSize(SIDEBAR_BUTTON_SIZE);
        }
    }

    private GUIUtil() {
    }
}
