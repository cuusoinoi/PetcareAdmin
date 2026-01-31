package com.petcare.util;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Hiển thị popup chọn ngày/giờ (click) và ghi vào JTextField.
 * Dùng cho các ô ngày giờ trong form thay vì chỉ nhập tay.
 */
public final class DatePickerHelper {

    private static final String FORMAT_DATE = "dd/MM/yyyy";
    private static final String FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm";

    private DatePickerHelper() {
    }

    /**
     * Hiển thị dialog chọn ngày (dd/MM/yyyy). Khi chọn OK sẽ ghi vào textField.
     */
    public static void showDatePicker(Window parent, JTextField textField) {
        showPicker(parent, textField, FORMAT_DATE, false);
    }

    /**
     * Hiển thị dialog chọn ngày + giờ (dd/MM/yyyy HH:mm). Khi chọn OK sẽ ghi vào textField.
     */
    public static void showDateTimePicker(Window parent, JTextField textField) {
        showPicker(parent, textField, FORMAT_DATE_TIME, true);
    }

    private static final String FORMAT_TIME = "HH:mm";

    /**
     * Hiển thị dialog chọn giờ (HH:mm). Khi chọn OK sẽ ghi vào textField.
     */
    public static void showTimePicker(Window parent, JTextField textField) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME);
        Date initial;
        try {
            String t = textField.getText() != null ? textField.getText().trim() : "";
            if (t.isEmpty()) {
                initial = new Date();
            } else {
                initial = sdf.parse(t);
            }
        } catch (Exception e) {
            initial = new Date();
        }

        JSpinner spinner = new JSpinner(new javax.swing.SpinnerDateModel(initial, null, null, Calendar.MINUTE));
        spinner.setEditor(new JSpinner.DateEditor(spinner, FORMAT_TIME));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(100, 28));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.setBackground(ThemeManager.getContentBackground());
        content.add(new JLabel("Chọn giờ:"), BorderLayout.NORTH);
        content.add(spinner, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(ThemeManager.getContentBackground());
        JButton okBtn = new JButton("Chọn");
        okBtn.setBackground(ThemeManager.getButtonBackground());
        okBtn.setForeground(ThemeManager.getButtonForeground());
        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setBackground(ThemeManager.getButtonBackground());
        cancelBtn.setForeground(ThemeManager.getButtonForeground());

        JDialog dialog = new JDialog(parent, "Chọn giờ", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setBackground(ThemeManager.getContentBackground());
        dialog.setLayout(new BorderLayout());
        dialog.add(content, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> {
            Date chosen = (Date) spinner.getValue();
            textField.setText(sdf.format(chosen));
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttons.add(okBtn);
        buttons.add(cancelBtn);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private static JButton createPickerButton(String emoji, String tooltip, Runnable action) {
        JButton btn = new JButton();
        btn.setIcon(EmojiFontHelper.createEmojiIcon(emoji, ThemeManager.getIconColor()));
        btn.setText("");
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(38, 28));
        btn.setMinimumSize(new Dimension(38, 28));
        btn.setMaximumSize(new Dimension(38, 28));
        btn.setBackground(ThemeManager.getButtonBackground());
        btn.setForeground(ThemeManager.getButtonForeground());
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private static void showPicker(Window parent, JTextField textField, String format, boolean includeTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date initial;
        try {
            String t = textField.getText() != null ? textField.getText().trim() : "";
            initial = t.isEmpty() ? new Date() : sdf.parse(t);
        } catch (Exception e) {
            initial = new Date();
        }

        JSpinner spinner;
        if (includeTime) {
            spinner = new JSpinner(new javax.swing.SpinnerDateModel(initial, null, null, Calendar.MINUTE));
            spinner.setEditor(new JSpinner.DateEditor(spinner, FORMAT_DATE_TIME));
        } else {
            spinner = new JSpinner(new javax.swing.SpinnerDateModel(initial, null, null, Calendar.DAY_OF_MONTH));
            spinner.setEditor(new JSpinner.DateEditor(spinner, FORMAT_DATE));
        }
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(180, 28));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.setBackground(ThemeManager.getContentBackground());
        content.add(new JLabel(includeTime ? "Chọn ngày giờ:" : "Chọn ngày:"), BorderLayout.NORTH);
        content.add(spinner, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(ThemeManager.getContentBackground());
        JButton okBtn = new JButton("Chọn");
        okBtn.setBackground(ThemeManager.getButtonBackground());
        okBtn.setForeground(ThemeManager.getButtonForeground());
        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setBackground(ThemeManager.getButtonBackground());
        cancelBtn.setForeground(ThemeManager.getButtonForeground());

        JDialog dialog = new JDialog(parent, includeTime ? "Chọn ngày giờ" : "Chọn ngày", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setBackground(ThemeManager.getContentBackground());
        dialog.setLayout(new BorderLayout());
        dialog.add(content, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> {
            Date chosen = (Date) spinner.getValue();
            textField.setText(sdf.format(chosen));
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttons.add(okBtn);
        buttons.add(cancelBtn);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    public static JPanel wrapDateField(Window parent, JTextField dateField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(ThemeManager.getContentBackground());
        panel.add(dateField, BorderLayout.CENTER);
        JButton btn = createPickerButton("📅", "Chọn ngày", () -> showDatePicker(parent, dateField));
        panel.add(btn, BorderLayout.EAST);
        return panel;
    }

    public static JPanel wrapDateTimeField(Window parent, JTextField dateTimeField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(ThemeManager.getContentBackground());
        panel.add(dateTimeField, BorderLayout.CENTER);
        JButton btn = createPickerButton("📅", "Chọn ngày giờ", () -> showDateTimePicker(parent, dateTimeField));
        panel.add(btn, BorderLayout.EAST);
        return panel;
    }

    public static JPanel wrapTimeField(Window parent, JTextField timeField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(ThemeManager.getContentBackground());
        panel.add(timeField, BorderLayout.CENTER);
        JButton btn = createPickerButton("🕐", "Chọn giờ", () -> showTimePicker(parent, timeField));
        panel.add(btn, BorderLayout.EAST);
        return panel;
    }
}
