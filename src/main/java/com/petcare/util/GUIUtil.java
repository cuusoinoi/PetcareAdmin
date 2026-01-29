package com.petcare.util;

import java.awt.Dimension;
import javax.swing.JButton;

/**
 * Hằng số và helper cho giao diện.
 */
public final class GUIUtil {

    /** Kích thước chuẩn cho nút toolbar (Thêm, Sửa, Xóa, ...) để đồng bộ giữa các trang. */
    public static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(155, 36);

    /** Nút cột bên: chiều cao bằng toolbar (36), rộng vừa icon to + chữ. */
    public static final Dimension SIDEBAR_BUTTON_SIZE = new Dimension(145, 36);

    /**
     * Đặt kích thước chuẩn cho nút toolbar để các nút đồng bộ (không có nút dài quá như "Cập nhật trạng thái").
     */
    public static void setToolbarButtonSize(JButton button) {
        if (button != null) {
            button.setPreferredSize(TOOLBAR_BUTTON_SIZE);
            button.setMinimumSize(TOOLBAR_BUTTON_SIZE);
            button.setMaximumSize(TOOLBAR_BUTTON_SIZE);
        }
    }

    /**
     * Đặt kích thước nhỏ cho nút cột bên (sidebar), rộng đủ hiển thị đủ chữ "Cập nhật trạng thái".
     */
    public static void setSidebarButtonSize(JButton button) {
        if (button != null) {
            button.setPreferredSize(SIDEBAR_BUTTON_SIZE);
            button.setMinimumSize(SIDEBAR_BUTTON_SIZE);
            button.setMaximumSize(SIDEBAR_BUTTON_SIZE);
        }
    }

    private GUIUtil() {}
}
