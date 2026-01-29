package com.petcare.gui.panels;

import com.petcare.util.DashboardService;
import com.petcare.util.ThemeManager;
import com.petcare.util.DashboardService.ServiceRevenue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Dashboard Panel with stat cards and charts
 */
public class DashboardPanel extends JPanel {
    
    public DashboardPanel() {
        initComponents();
        loadData();
    }
    
    /** Gọi khi bấm menu Dashboard để cập nhật lại thẻ thống kê và biểu đồ. */
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

        // Top panel with stat cards
        JPanel cardsPanel = createStatCardsPanel();
        add(cardsPanel, BorderLayout.NORTH);

        // Charts panel (will be added in loadData)
    }

    private JPanel createStatCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ThemeManager.getContentBackground());
        
        // Cards will be created in loadData()
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
        // Get data
        int customerCount = DashboardService.getCustomerCount();
        int petCount = DashboardService.getPetCount();
        int medicalCount = DashboardService.getMedicalRecordCountThisMonth();
        int enclosureCount = DashboardService.getPetEnclosureCountThisMonth();
        long revenue = DashboardService.getInvoiceRevenueThisYear();
        
        // Calculate percentage changes
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
        
        // Create stat cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
        
        // Remove old cards panel and add new one
        removeAll();
        add(cardsPanel, BorderLayout.NORTH);

        // Create charts panel (bắt lỗi từng chart để không vỡ cả màn hình)
        JPanel chartsPanel = createChartsPanel();
        add(chartsPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createStatCard(String title, String value, Color color, Double percentChange) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(ThemeManager.getFormBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(200, 120));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(ThemeManager.isDarkMode() ? new Color(0xb0b0b0) : new Color(100, 100, 100));
        card.add(titleLabel, BorderLayout.NORTH);
        
        // Value and percent change
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel, BorderLayout.CENTER);
        
        if (percentChange != null) {
            JLabel percentLabel = new JLabel();
            percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panel.setBackground(ThemeManager.getContentBackground());

        try {
            Map<String, Integer> medicalData = DashboardService.getMedicalRecordsByDay(7);
            panel.add(createLineChart("Lượt khám (7 ngày gần nhất)", medicalData));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(createPlaceholderPanel("Lượt khám"));
        }
        try {
            Map<String, Map<String, Integer>> checkinCheckoutData = DashboardService.getCheckinCheckoutStats(7);
            panel.add(createBarChart("Check-in / Check-out (7 ngày gần nhất)", checkinCheckoutData));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(createPlaceholderPanel("Check-in / Check-out"));
        }
        try {
            Map<String, Long> monthlyRevenue = DashboardService.getMonthlyRevenueStats();
            panel.add(createRevenueLineChart("Doanh thu theo tháng (12 tháng)", monthlyRevenue));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(createPlaceholderPanel("Doanh thu theo tháng"));
        }
        try {
            List<ServiceRevenue> serviceRevenue = DashboardService.getRevenueByServiceType();
            panel.add(createDoughnutChart("Tỷ trọng doanh thu theo loại dịch vụ", serviceRevenue));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(createPlaceholderPanel("Tỷ trọng doanh thu"));
        }

        return panel;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ThemeManager.getFormBackground());
        p.add(new JLabel(title + " – Không thể tải", JLabel.CENTER), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(600, 300));
        return p;
    }
    
    private ChartPanel createLineChart(String title, Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");
        
        // Sort by date so chart shows chronological order
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
        
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.getRenderer().setSeriesPaint(0, new Color(220, 53, 69));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
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
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(ThemeManager.getFormBackground());
        
        return chartPanel;
    }
    
    private ChartPanel createBarChart(String title, Map<String, Map<String, Integer>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");
        
        // Sort by date so chart shows chronological order
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
        
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.getRenderer().setSeriesPaint(0, new Color(255, 193, 7));
        plot.getRenderer().setSeriesPaint(1, new Color(40, 167, 69));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
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
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(ThemeManager.getFormBackground());
        
        return chartPanel;
    }
    
    private ChartPanel createRevenueLineChart(String title, Map<String, Long> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/yyyy");
        
        // Sort by month so chart shows chronological order
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
        
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.getRenderer().setSeriesPaint(0, new Color(23, 162, 184));
        // Trục tháng (domain): hiển thị đúng nhãn MM/yyyy (01/2025, 02/2025...)
        CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        domainAxis.setMaximumCategoryLabelLines(2);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        // Trục doanh thu (range): hiển thị số thập phân (triệu VNĐ) đúng
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0.0"));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
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
        
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.setBackgroundPaint(Color.WHITE);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        try {
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        } catch (Exception ignored) {
            // Giữ mặc định nếu format lỗi
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
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
