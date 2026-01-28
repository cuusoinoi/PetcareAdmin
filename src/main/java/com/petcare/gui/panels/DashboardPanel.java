package com.petcare.gui.panels;

import com.petcare.util.DashboardService;
import com.petcare.util.DashboardService.ServiceRevenue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
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
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Top panel with stat cards
        JPanel cardsPanel = createStatCardsPanel();
        add(cardsPanel, BorderLayout.NORTH);
        
        // Charts panel (will be added in loadData)
    }
    
    private JPanel createStatCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));
        
        // Cards will be created in loadData()
        return panel;
    }
    
    private void loadData() {
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
        cardsPanel.setBackground(new Color(245, 245, 245));
        
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
        
        // Create charts panel
        JPanel chartsPanel = createChartsPanel();
        add(chartsPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    private JPanel createStatCard(String title, String value, Color color, Double percentChange) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(200, 120));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 100, 100));
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
        panel.setBackground(new Color(245, 245, 245));
        
        // Chart 1: Medical Records (7 days)
        Map<String, Integer> medicalData = DashboardService.getMedicalRecordsByDay(7);
        ChartPanel medicalChart = createLineChart(
            "Lượt khám (7 ngày gần nhất)",
            medicalData
        );
        panel.add(medicalChart);
        
        // Chart 2: Check-in/Check-out (7 days)
        Map<String, Map<String, Integer>> checkinCheckoutData = DashboardService.getCheckinCheckoutStats(7);
        ChartPanel checkinCheckoutChart = createBarChart(
            "Check-in / Check-out (7 ngày gần nhất)",
            checkinCheckoutData
        );
        panel.add(checkinCheckoutChart);
        
        // Chart 3: Monthly Revenue (12 months)
        Map<String, Long> monthlyRevenue = DashboardService.getMonthlyRevenueStats();
        ChartPanel revenueChart = createRevenueLineChart(
            "Doanh thu theo tháng (12 tháng)",
            monthlyRevenue
        );
        panel.add(revenueChart);
        
        // Chart 4: Revenue by Service Type
        List<ServiceRevenue> serviceRevenue = DashboardService.getRevenueByServiceType();
        ChartPanel serviceChart = createDoughnutChart(
            "Tỷ trọng doanh thu theo loại dịch vụ",
            serviceRevenue
        );
        panel.add(serviceChart);
        
        return panel;
    }
    
    private ChartPanel createLineChart(String title, Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            try {
                Date date = inputFormat.parse(entry.getKey());
                String label = outputFormat.format(date);
                dataset.addValue(entry.getValue(), "Lượt khám", label);
            } catch (Exception ex) {
                dataset.addValue(entry.getValue(), "Lượt khám", entry.getKey());
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
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(Color.WHITE);
        
        return chartPanel;
    }
    
    private ChartPanel createBarChart(String title, Map<String, Map<String, Integer>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");
        
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            try {
                Date date = inputFormat.parse(entry.getKey());
                String label = outputFormat.format(date);
                Map<String, Integer> dayData = entry.getValue();
                dataset.addValue(dayData.get("checkin"), "Check-in", label);
                dataset.addValue(dayData.get("checkout"), "Check-out", label);
            } catch (Exception ex) {
                String label = entry.getKey();
                Map<String, Integer> dayData = entry.getValue();
                dataset.addValue(dayData.get("checkin"), "Check-in", label);
                dataset.addValue(dayData.get("checkout"), "Check-out", label);
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
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(Color.WHITE);
        
        return chartPanel;
    }
    
    private ChartPanel createRevenueLineChart(String title, Map<String, Long> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/yyyy");
        
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            try {
                Date date = inputFormat.parse(entry.getKey());
                String label = outputFormat.format(date);
                dataset.addValue(entry.getValue() / 1000000.0, "Doanh thu (triệu VNĐ)", label);
            } catch (Exception ex) {
                dataset.addValue(entry.getValue() / 1000000.0, "Doanh thu (triệu VNĐ)", entry.getKey());
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
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(Color.WHITE);
        
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
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(Color.WHITE);
        
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
