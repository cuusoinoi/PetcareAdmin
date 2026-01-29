package com.petcare.gui.panels;

import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

/**
 * Thanh phân trang đặt dưới JTable: sắp xếp theo cột (click tiêu đề) và phân trang.
 * Dùng TableRowSorter + RowFilter để chỉ hiển thị dòng thuộc trang hiện tại.
 */
public class TablePaginationPanel extends JPanel {
    private static final int[] PAGE_SIZES = {10, 25, 50, 100};
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final JLabel infoLabel;
    private final JButton firstBtn;
    private final JButton prevBtn;
    private final JButton nextBtn;
    private final JButton lastBtn;
    private final JComboBox<Integer> pageSizeCombo;

    private final javax.swing.JTable table;
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;

    private int currentPage = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String searchText = "";

    public TablePaginationPanel(javax.swing.JTable table) {
        if (!(table.getModel() instanceof DefaultTableModel)) {
            throw new IllegalArgumentException("Table must use DefaultTableModel");
        }
        this.table = table;
        this.model = (DefaultTableModel) table.getModel();
        this.sorter = new TableRowSorter<>(this.model);
        sorter.setComparator(0, numericComparator());
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        table.setRowSorter(sorter);

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setFont(new Font("Segoe UI", Font.PLAIN, 12));

        infoLabel = new JLabel();
        firstBtn = new JButton("Đầu");
        prevBtn = new JButton("Trước");
        nextBtn = new JButton("Sau");
        lastBtn = new JButton("Cuối");
        pageSizeCombo = new JComboBox<>(java.util.Arrays.stream(PAGE_SIZES).boxed().toArray(Integer[]::new));
        pageSizeCombo.setSelectedItem(DEFAULT_PAGE_SIZE);
        pageSizeCombo.addActionListener(e -> {
            pageSize = (Integer) pageSizeCombo.getSelectedItem();
            currentPage = 1;
            applyPageFilter();
            updateButtonsAndLabel();
        });

        firstBtn.addActionListener(e -> {
            currentPage = 1;
            applyPageFilter();
            updateButtonsAndLabel();
        });
        prevBtn.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                applyPageFilter();
                updateButtonsAndLabel();
            }
        });
        nextBtn.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                applyPageFilter();
                updateButtonsAndLabel();
            }
        });
        lastBtn.addActionListener(e -> {
            currentPage = getTotalPages();
            if (currentPage < 1) currentPage = 1;
            applyPageFilter();
            updateButtonsAndLabel();
        });

        add(new JLabel("Hiển thị:"));
        add(pageSizeCombo);
        add(new JLabel("dòng"));
        add(infoLabel);
        add(firstBtn);
        add(prevBtn);
        add(nextBtn);
        add(lastBtn);

        updateButtonsAndLabel();
    }

    /**
     * Cập nhật màu theo theme (gọi từ panel cha khi đổi giao diện sáng/tối).
     */
    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        infoLabel.setForeground(ThemeManager.getTitleForeground());
        firstBtn.setBackground(ThemeManager.getButtonBackground());
        firstBtn.setForeground(ThemeManager.getButtonForeground());
        prevBtn.setBackground(ThemeManager.getButtonBackground());
        prevBtn.setForeground(ThemeManager.getButtonForeground());
        nextBtn.setBackground(ThemeManager.getButtonBackground());
        nextBtn.setForeground(ThemeManager.getButtonForeground());
        lastBtn.setBackground(ThemeManager.getButtonBackground());
        lastBtn.setForeground(ThemeManager.getButtonForeground());
        pageSizeCombo.setBackground(ThemeManager.getTextFieldBackground());
        pageSizeCombo.setForeground(ThemeManager.getTextFieldForeground());
        for (Component c : getComponents()) {
            if (c instanceof JLabel && c != infoLabel) {
                ((JLabel) c).setForeground(ThemeManager.getTitleForeground());
            }
        }
    }

    /**
     * Gọi sau khi load xong dữ liệu (model đã thay đổi) để về trang 1 và cập nhật filter.
     */
    public void refresh() {
        currentPage = 1;
        applyPageFilter();
        updateButtonsAndLabel();
    }

    /**
     * Lọc bảng theo từ khóa (tìm kiếm). Gọi khi user gõ vào ô tìm kiếm.
     */
    public void setSearchText(String text) {
        this.searchText = text != null ? text.trim() : "";
        currentPage = 1;
        applyPageFilter();
        updateButtonsAndLabel();
    }

    private int getTotalRows() {
        if (searchText != null && !searchText.isEmpty()) {
            try {
                return table.getRowCount();
            } catch (Exception e) {
                return model.getRowCount();
            }
        }
        return model.getRowCount();
    }

    private int getTotalPages() {
        if (searchText != null && !searchText.isEmpty()) {
            return 1;
        }
        int total = model.getRowCount();
        if (total <= 0 || pageSize <= 0) return 1;
        return (total + pageSize - 1) / pageSize;
    }

    private void applyPageFilter() {
        RowFilter<DefaultTableModel, Integer> searchFilter = null;
        if (searchText != null && !searchText.isEmpty()) {
            final String key = searchText.toLowerCase();
            searchFilter = new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        Object v = entry.getValue(i);
                        if (v != null && v.toString().toLowerCase().contains(key)) return true;
                    }
                    return false;
                }
            };
        }

        if (searchFilter != null) {
            sorter.setRowFilter(searchFilter);
            updateButtonsAndLabel();
            return;
        }

        int total = model.getRowCount();
        if (total == 0) {
            sorter.setRowFilter(null);
            updateButtonsAndLabel();
            return;
        }
        sorter.setRowFilter(null);
        int n = model.getRowCount();
        final int[] viewToModel = new int[n];
        for (int i = 0; i < n; i++) {
            viewToModel[i] = sorter.convertRowIndexToModel(i);
        }
        final int page = currentPage;
        final int size = pageSize;
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int modelRow = entry.getIdentifier();
                int viewIdx = -1;
                for (int i = 0; i < viewToModel.length; i++) {
                    if (viewToModel[i] == modelRow) {
                        viewIdx = i;
                        break;
                    }
                }
                if (viewIdx < 0) return false;
                return viewIdx >= (page - 1) * size && viewIdx < page * size;
            }
        });
    }

    private void updateButtonsAndLabel() {
        int total = model.getRowCount();
        boolean searching = searchText != null && !searchText.isEmpty();
        if (searching) {
            try {
                total = table.getRowCount();
            } catch (Exception ignored) {
            }
        }
        int totalPages = getTotalPages();
        if (totalPages < 1) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        int from = total == 0 ? 0 : (currentPage - 1) * pageSize + 1;
        int to = searching ? total : Math.min(currentPage * pageSize, total);
        if (searching) {
            infoLabel.setText(String.format("  Tìm thấy %d kết quả", total));
        } else {
            infoLabel.setText(String.format("  %d - %d của %d  |  Trang %d / %d", from, to, total, currentPage, totalPages));
        }

        firstBtn.setEnabled(!searching && currentPage > 1);
        prevBtn.setEnabled(!searching && currentPage > 1);
        nextBtn.setEnabled(!searching && currentPage < totalPages);
        lastBtn.setEnabled(!searching && currentPage < totalPages);
    }

    /**
     * So sánh theo số để cột ID (và cột số khác) sắp 1, 2, 3, ..., 9, 10, 11 chứ không phải 1, 10, 11, ..., 2.
     */
    private static Comparator<Object> numericComparator() {
        return (a, b) -> {
            int na = toInt(a);
            int nb = toInt(b);
            if (na != nb) return Integer.compare(na, nb);
            String sa = a != null ? a.toString() : "";
            String sb = b != null ? b.toString() : "";
            return sa.compareTo(sb);
        };
    }

    private static int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        if (o instanceof String) {
            String s = ((String) o).trim().replaceAll("[^0-9-]", "");
            if (!s.isEmpty()) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }
}
