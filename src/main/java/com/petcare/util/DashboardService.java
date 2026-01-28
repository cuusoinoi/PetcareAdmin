package com.petcare.util;

import com.petcare.model.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for Dashboard data
 */
public class DashboardService {
    
    /**
     * Get total customer count
     */
    public static int getCustomerCount() {
        try {
            ResultSet rs = Database.executeQuery("SELECT COUNT(*) as count FROM customers");
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get total pet count
     */
    public static int getPetCount() {
        try {
            ResultSet rs = Database.executeQuery("SELECT COUNT(*) as count FROM pets");
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get medical record count for current month
     */
    public static int getMedicalRecordCountThisMonth() {
        try {
            String query = "SELECT COUNT(*) as count FROM medical_records " +
                          "WHERE YEAR(medical_record_visit_date) = YEAR(CURDATE()) " +
                          "AND MONTH(medical_record_visit_date) = MONTH(CURDATE())";
            ResultSet rs = Database.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get medical record count for yesterday
     */
    public static int getMedicalRecordCountByDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            String query = "SELECT COUNT(*) as count FROM medical_records " +
                          "WHERE DATE(medical_record_visit_date) = ?";
            ResultSet rs = Database.executeQuery(query, dateStr);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get pet enclosure count for current month
     */
    public static int getPetEnclosureCountThisMonth() {
        try {
            String query = "SELECT COUNT(*) as count FROM pet_enclosures " +
                          "WHERE YEAR(check_in_date) = YEAR(CURDATE()) " +
                          "AND MONTH(check_in_date) = MONTH(CURDATE())";
            ResultSet rs = Database.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get pet enclosure count by date
     */
    public static int getPetEnclosureCountByDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            String query = "SELECT COUNT(*) as count FROM pet_enclosures " +
                          "WHERE DATE(check_in_date) = ?";
            ResultSet rs = Database.executeQuery(query, dateStr);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get total revenue for current year
     */
    public static long getInvoiceRevenueThisYear() {
        try {
            String query = "SELECT SUM(total_amount) as total FROM invoices " +
                          "WHERE YEAR(invoice_date) = YEAR(CURDATE())";
            ResultSet rs = Database.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getLong("total");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get revenue for a specific month
     */
    public static long getInvoiceRevenueByMonth(int year, int month) {
        try {
            String query = "SELECT SUM(total_amount) as total FROM invoices " +
                          "WHERE YEAR(invoice_date) = ? AND MONTH(invoice_date) = ?";
            ResultSet rs = Database.executeQuery(query, year, month);
            if (rs != null && rs.next()) {
                return rs.getLong("total");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get medical records count by day for last 7 days
     * Returns Map<date, count>
     */
    public static Map<String, Integer> getMedicalRecordsByDay(int days) {
        Map<String, Integer> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        for (int i = days - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = sdf.format(cal.getTime());
            
            try {
                String query = "SELECT COUNT(*) as count FROM medical_records " +
                              "WHERE DATE(medical_record_visit_date) = ?";
                ResultSet rs = Database.executeQuery(query, dateStr);
                if (rs != null && rs.next()) {
                    result.put(dateStr, rs.getInt("count"));
                } else {
                    result.put(dateStr, 0);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                result.put(dateStr, 0);
            }
        }
        
        return result;
    }
    
    /**
     * Get check-in/check-out stats for last 7 days
     */
    public static Map<String, Map<String, Integer>> getCheckinCheckoutStats(int days) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        for (int i = days - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = sdf.format(cal.getTime());
            
            try {
                // Check-in count
                String checkinQuery = "SELECT COUNT(*) as count FROM pet_enclosures " +
                                    "WHERE DATE(check_in_date) = ?";
                ResultSet rs = Database.executeQuery(checkinQuery, dateStr);
                int checkin = 0;
                if (rs != null && rs.next()) {
                    checkin = rs.getInt("count");
                }
                
                // Check-out count
                String checkoutQuery = "SELECT COUNT(*) as count FROM pet_enclosures " +
                                     "WHERE DATE(check_out_date) = ?";
                rs = Database.executeQuery(checkoutQuery, dateStr);
                int checkout = 0;
                if (rs != null && rs.next()) {
                    checkout = rs.getInt("count");
                }
                
                Map<String, Integer> dayStats = new HashMap<>();
                dayStats.put("checkin", checkin);
                dayStats.put("checkout", checkout);
                result.put(dateStr, dayStats);
            } catch (SQLException ex) {
                ex.printStackTrace();
                Map<String, Integer> dayStats = new HashMap<>();
                dayStats.put("checkin", 0);
                dayStats.put("checkout", 0);
                result.put(dateStr, dayStats);
            }
        }
        
        return result;
    }
    
    /**
     * Get monthly revenue stats for last 12 months
     */
    public static Map<String, Long> getMonthlyRevenueStats() {
        Map<String, Long> result = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (int i = 11; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            
            long revenue = getInvoiceRevenueByMonth(year, month);
            String key = String.format("%04d-%02d", year, month);
            result.put(key, revenue);
        }
        
        return result;
    }
    
    /**
     * Get revenue by service type for current year
     */
    public static List<ServiceRevenue> getRevenueByServiceType() {
        List<ServiceRevenue> result = new ArrayList<>();
        
        try {
            String query = "SELECT st.service_name, SUM(id.total_price) as total_revenue " +
                          "FROM invoice_details id " +
                          "INNER JOIN service_types st ON id.service_type_id = st.service_type_id " +
                          "INNER JOIN invoices i ON id.invoice_id = i.invoice_id " +
                          "WHERE YEAR(i.invoice_date) = YEAR(CURDATE()) " +
                          "GROUP BY st.service_type_id, st.service_name " +
                          "ORDER BY total_revenue DESC";
            
            ResultSet rs = Database.executeQuery(query);
            while (rs != null && rs.next()) {
                ServiceRevenue sr = new ServiceRevenue();
                sr.serviceName = rs.getString("service_name");
                sr.revenue = rs.getLong("total_revenue");
                result.add(sr);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Calculate percentage change
     */
    public static double calculatePercentChange(double oldValue, double newValue) {
        if (oldValue == 0 && newValue > 0) {
            return 100.0;
        } else if (oldValue > 0) {
            return ((newValue - oldValue) / oldValue) * 100.0;
        }
        return 0.0;
    }
    
    /**
     * Helper class for service revenue
     */
    public static class ServiceRevenue {
        public String serviceName;
        public long revenue;
    }
}
