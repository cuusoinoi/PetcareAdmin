package com.petcare.gui.panels;

import com.petcare.util.DashboardService;
import com.petcare.util.DashboardService.ServiceRevenue;
import com.petcare.util.ThemeManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        initComponents();
        loadData();
    }

    public void refreshData() {
        loadData();
    }

    public void updateTheme() {
        setBackground(ThemeManager.getContentBackground());
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getContentBackground());

        JPanel cardsPanel = createStatCardsPanel();
        add(cardsPanel, BorderLayout.NORTH);
    }

    private JPanel createStatCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.setBackground(ThemeManager.getContentBackground());
        return panel;
    }

    private void loadData() {
        try {
            loadDataInternal();
        } catch (Exception e) {
            e.printStackTrace();
            removeAll();
            add(new JLabel("Không thể tải Dashboard. Vui lòng thử lại.", JLabel.CENTER), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void loadDataInternal() {
        int customerCount = DashboardService.getCustomerCount();
        int petCount = DashboardService.getPetCount();
        int medicalCount = DashboardService.getMedicalRecordCountThisMonth();
        int enclosureCount = DashboardService.getPetEnclosureCountThisMonth();
        long revenue = DashboardService.getInvoiceRevenueThisYear();

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = cal.getTime();

        int medicalYesterday = DashboardService.getMedicalRecordCountByDate(yesterday);
        int medicalToday = DashboardService.getMedicalRecordCountByDate(today);
        double medicalPercentChange = DashboardService.calculatePercentChange(medicalYesterday, medicalToday);

        int enclosureYesterday = DashboardService.getPetEnclosureCountByDate(yesterday);
        int enclosureToday = DashboardService.getPetEnclosureCountByDate(today);
        double enclosurePercentChange = DashboardService.calculatePercentChange(enclosureYesterday, enclosureToday);

        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        int lastMonth = cal.get(Calendar.MONTH) + 1;
        int lastMonthYear = cal.get(Calendar.YEAR);
        long lastMonthRevenue = DashboardService.getInvoiceRevenueByMonth(lastMonthYear, lastMonth);
        cal.setTime(new Date());
        long thisMonthRevenue = DashboardService.getInvoiceRevenueByMonth(
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        double revenuePercentChange = DashboardService.calculatePercentChange(lastMonthRevenue, thisMonthRevenue);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 20, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        cardsPanel.setBackground(ThemeManager.getContentBackground());

        cardsPanel.add(createStatCard("Tổng khách hàng", String.valueOf(customerCount),
                new Color(52, 152, 219), null));
        cardsPanel.add(createStatCard("Tổng thú cưng", String.valueOf(petCount),
                new Color(155, 89, 182), null));
        cardsPanel.add(createStatCard("Lượt khám", String.valueOf(medicalCount),
                new Color(231, 76, 60), medicalPercentChange));
        cardsPanel.add(createStatCard("Lượt lưu chuồng", String.valueOf(enclosureCount),
                new Color(241, 196, 15), enclosurePercentChange));
        cardsPanel.add(createStatCard("Doanh thu", formatCurrency(revenue),
                new Color(46, 204, 113), revenuePercentChange));

        removeAll();
        add(cardsPanel, BorderLayout.NORTH);
        JPanel chartsPanel = createChartsPanel();
        add(chartsPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createStatCard(String title, String value, Color color, Double percentChange) {
        Color borderColor = ThemeManager.isDarkMode()
                ? new Color(0x404040)
                : new Color(0xe0e0e0);
        JPanel card = new com.petcare.util.RoundedPanel(16, borderColor, 1f);
        card.setLayout(new BorderLayout());
        card.setBackground(ThemeManager.getFormBackground());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(220, 128));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.getModernFont(13));
        titleLabel.setForeground(ThemeManager.isDarkMode() ? new Color(0xb0b0b0) : new Color(100, 100, 100));
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(ThemeManager.getSemiboldFont(24));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel, BorderLayout.CENTER);

        if (percentChange != null) {
            JLabel percentLabel = new JLabel();
            percentLabel.setFont(ThemeManager.getModernFont(11));
            if (percentChange >= 0) {
                percentLabel.setText(String.format("↑ %.1f%%", percentChange));
                percentLabel.setForeground(new Color(46, 204, 113));
            } else {
                percentLabel.setText(String.format("↓ %.1f%%", Math.abs(percentChange)));
                percentLabel.setForeground(new Color(231, 76, 60));
            }
            valuePanel.add(percentLabel, BorderLayout.EAST);
        }

        card.add(valuePanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        panel.setBackground(ThemeManager.getContentBackground());

        Color chartBorderColor = ThemeManager.isDarkMode() ? new Color(0x404040) : new Color(0xe0e0e0);
        try {
            Map<String, Integer> medicalData = DashboardService.getMedicalRecordsByDay(7);
            panel.add(wrapChartInRounded(createLineChart("Lượt khám (7 ngày gần nhất)", medicalData), chartBorderColor));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(wrapChartInRounded(createPlaceholderPanel("Lượt khám"), chartBorderColor));
        }
        try {
            Map<String, Map<String, Integer>> checkinCheckoutData = DashboardService.getCheckinCheckoutStats(7);
            panel.add(wrapChartInRounded(createBarChart("Check-in / Check-out (7 ngày gần nhất)", checkinCheckoutData), chartBorderColor));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(wrapChartInRounded(createPlaceholderPanel("Check-in / Check-out"), chartBorderColor));
        }
        try {
            Map<String, Long> monthlyRevenue = DashboardService.getMonthlyRevenueStats();
            panel.add(wrapChartInRounded(createRevenueLineChart("Doanh thu theo tháng (12 tháng)", monthlyRevenue), chartBorderColor));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(wrapChartInRounded(createPlaceholderPanel("Doanh thu theo tháng"), chartBorderColor));
        }
        try {
            List<ServiceRevenue> serviceRevenue = DashboardService.getRevenueByServiceType();
            panel.add(wrapChartInRounded(createDoughnutChart("Tỷ trọng doanh thu theo loại dịch vụ", serviceRevenue), chartBorderColor));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(wrapChartInRounded(createPlaceholderPanel("Tỷ trọng doanh thu"), chartBorderColor));
        }

        return panel;
    }

    private JPanel wrapChartInRounded(JComponent chart, Color borderColor) {
        com.petcare.util.RoundedPanel wrapper = new com.petcare.util.RoundedPanel(16, borderColor, 1f);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(ThemeManager.getFormBackground());
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        wrapper.add(chart, BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(600, 320));
        return wrapper;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(title + " – Không thể tải", JLabel.CENTER);
        lbl.setFont(ThemeManager.getModernFont(14));
        p.add(lbl, BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(600, 300));
        return p;
    }

    private void applyChartLegendTheme(JFreeChart chart, Color textColor) {
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(ThemeManager.getFormBackground());
            chart.getLegend().setItemPaint(textColor);
        }
    }

    private ChartPanel createLineChart(String title, Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");

        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);
        for (String dateKey : sortedKeys) {
            Integer value = data.get(dateKey);
            if (value == null) value = 0;
            try {
                Date date = inputFormat.parse(dateKey);
                String label = outputFormat.format(date);
                dataset.addValue(value, "Lượt khám", label);
            } catch (Exception ex) {
                dataset.addValue(value, "Lượt khám", dateKey);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                "Ngày",
                "Số lượt",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        Color textColor = ThemeManager.getTitleForeground();
        chart.getTitle().setFont(ThemeManager.getSemiboldFont(16));
        chart.getTitle().setPaint(textColor);
        chart.setBackgroundPaint(ThemeManager.getFormBackground());

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(ThemeManager.getFormBackground());
        plot.setRangeGridlinePaint(ThemeManager.isDarkMode() ? new Color(0x404040) : new Color(220, 220, 220));
        plot.getRenderer().setSeriesPaint(0, new Color(220, 53, 69));
        plot.getDomainAxis().setTickLabelPaint(textColor);
        plot.getDomainAxis().setLabelPaint(textColor);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(textColor);
        rangeAxis.setLabelPaint(textColor);
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        if (dataset.getRowCount() > 0 && dataset.getColumnCount() > 0) {
            double maxVal = 0;
            for (int r = 0; r < dataset.getRowCount(); r++)
                for (int c = 0; c < dataset.getColumnCount(); c++)
                    maxVal = Math.max(maxVal, dataset.getValue(r, c).doubleValue());
            if (maxVal <= 0) rangeAxis.setUpperBound(5);
        }
        applyChartLegendTheme(chart, textColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setOpaque(false);
        chartPanel.setBackground(ThemeManager.getFormBackground());
        return chartPanel;
    }

    private ChartPanel createBarChart(String title, Map<String, Map<String, Integer>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");

        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);
        for (String dateKey : sortedKeys) {
            Map<String, Integer> dayData = data.get(dateKey);
            int checkin = dayData != null ? dayData.getOrDefault("checkin", 0) : 0;
            int checkout = dayData != null ? dayData.getOrDefault("checkout", 0) : 0;
            try {
                Date date = inputFormat.parse(dateKey);
                String label = outputFormat.format(date);
                dataset.addValue(checkin, "Check-in", label);
                dataset.addValue(checkout, "Check-out", label);
            } catch (Exception ex) {
                dataset.addValue(checkin, "Check-in", dateKey);
                dataset.addValue(checkout, "Check-out", dateKey);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Ngày",
                "Số lượt",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        Color textColor = ThemeManager.getTitleForeground();
        chart.getTitle().setFont(ThemeManager.getSemiboldFont(16));
        chart.getTitle().setPaint(textColor);
        chart.setBackgroundPaint(ThemeManager.getFormBackground());

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(ThemeManager.getFormBackground());
        plot.setRangeGridlinePaint(ThemeManager.isDarkMode() ? new Color(0x404040) : new Color(220, 220, 220));
        plot.getRenderer().setSeriesPaint(0, new Color(255, 193, 7));
        plot.getRenderer().setSeriesPaint(1, new Color(40, 167, 69));
        plot.getDomainAxis().setTickLabelPaint(textColor);
        plot.getDomainAxis().setLabelPaint(textColor);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(textColor);
        rangeAxis.setLabelPaint(textColor);
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        if (dataset.getRowCount() > 0 && dataset.getColumnCount() > 0) {
            double maxVal = 0;
            for (int r = 0; r < dataset.getRowCount(); r++)
                for (int c = 0; c < dataset.getColumnCount(); c++)
                    maxVal = Math.max(maxVal, dataset.getValue(r, c).doubleValue());
            if (maxVal <= 0) rangeAxis.setUpperBound(5);
        }
        applyChartLegendTheme(chart, textColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setOpaque(false);
        chartPanel.setBackground(ThemeManager.getFormBackground());
        return chartPanel;
    }

    private ChartPanel createRevenueLineChart(String title, Map<String, Long> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/yyyy");

        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        for (String monthKey : sortedKeys) {
            Long value = data.get(monthKey);
            if (value == null) value = 0L;
            double millions = value / 1000000.0;
            try {
                Date date = inputFormat.parse(monthKey);
                String label = outputFormat.format(date);
                dataset.addValue(millions, "Doanh thu (triệu VNĐ)", label);
            } catch (Exception ex) {
                dataset.addValue(millions, "Doanh thu (triệu VNĐ)", monthKey);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                "Tháng",
                "Doanh thu (triệu VNĐ)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        Color textColor = ThemeManager.getTitleForeground();
        chart.getTitle().setFont(ThemeManager.getSemiboldFont(16));
        chart.getTitle().setPaint(textColor);
        chart.setBackgroundPaint(ThemeManager.getFormBackground());

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(ThemeManager.getFormBackground());
        plot.setRangeGridlinePaint(ThemeManager.isDarkMode() ? new Color(0x404040) : new Color(220, 220, 220));
        plot.getRenderer().setSeriesPaint(0, new Color(23, 162, 184));
        CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(ThemeManager.getModernFont(11));
        domainAxis.setTickLabelPaint(textColor);
        domainAxis.setLabelPaint(textColor);
        domainAxis.setMaximumCategoryLabelLines(2);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(textColor);
        rangeAxis.setLabelPaint(textColor);
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0.0"));
        applyChartLegendTheme(chart, textColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setOpaque(false);
        chartPanel.setBackground(ThemeManager.getFormBackground());
        return chartPanel;
    }

    private ChartPanel createDoughnutChart(String title, List<ServiceRevenue> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (ServiceRevenue sr : data) {
            dataset.setValue(sr.serviceName, sr.revenue);
        }

        JFreeChart chart = ChartFactory.createRingChart(
                title,
                dataset,
                true,
                true,
                false
        );

        Color textColor = ThemeManager.getTitleForeground();
        chart.getTitle().setFont(ThemeManager.getSemiboldFont(16));
        chart.getTitle().setPaint(textColor);
        chart.setBackgroundPaint(ThemeManager.getFormBackground());

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(ThemeManager.getFormBackground());
        plot.setLabelFont(ThemeManager.getModernFont(11));
        plot.setLabelPaint(textColor);
        try {
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        } catch (Exception ignored) {
        }
        applyChartLegendTheme(chart, textColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setOpaque(false);
        chartPanel.setBackground(ThemeManager.getFormBackground());
        return chartPanel;
    }

    private String formatCurrency(long amount) {
        if (amount >= 1000000000) {
            return String.format("%.1fB", amount / 1000000000.0);
        } else if (amount >= 1000000) {
            return String.format("%.1fM", amount / 1000000.0);
        } else if (amount >= 1000) {
            return String.format("%.1fK", amount / 1000.0);
        }
        return String.valueOf(amount);
    }
}
