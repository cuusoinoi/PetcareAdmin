package com.petcare.util;

import com.petcare.persistence.Database;
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
            return com.petcare.service.MedicalRecordService.getInstance().getCountThisMonth();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Get medical record count by date
     */
    public static int getMedicalRecordCountByDate(Date date) {
        try {
            return com.petcare.service.MedicalRecordService.getInstance().getCountByDate(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get pet enclosure count for current month
     */
    public static int getPetEnclosureCountThisMonth() {
        try {
            return com.petcare.service.PetEnclosureService.getInstance().getCountThisMonth();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Get pet enclosure count by date
     */
    public static int getPetEnclosureCountByDate(Date date) {
        try {
            return com.petcare.service.PetEnclosureService.getInstance().getCountByDate(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get total revenue for current year
     */
    public static long getInvoiceRevenueThisYear() {
        try {
            return com.petcare.service.InvoiceService.getInstance().getRevenueThisYear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Get revenue for a specific month
     */
    public static long getInvoiceRevenueByMonth(int year, int month) {
        try {
            return com.petcare.service.InvoiceService.getInstance().getRevenueByMonth(year, month);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get medical records count by day for last N days
     * Returns Map<date, count>
     */
    public static Map<String, Integer> getMedicalRecordsByDay(int days) {
        try {
            return com.petcare.service.MedicalRecordService.getInstance().getCountByDay(days);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }
    
    /**
     * Get check-in/check-out stats for last N days
     */
    public static Map<String, Map<String, Integer>> getCheckinCheckoutStats(int days) {
        try {
            return com.petcare.service.PetEnclosureService.getInstance().getCheckinCheckoutStats(days);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }
    
    /**
     * Get monthly revenue stats for last 12 months
     */
    public static Map<String, Long> getMonthlyRevenueStats() {
        try {
            return com.petcare.service.InvoiceService.getInstance().getMonthlyRevenueStats();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * Get revenue by service type for current year
     */
    public static List<ServiceRevenue> getRevenueByServiceType() {
        List<ServiceRevenue> result = new ArrayList<>();
        try {
            for (com.petcare.model.entity.ServiceRevenueDto dto : com.petcare.service.InvoiceService.getInstance().getRevenueByServiceTypeThisYear()) {
                ServiceRevenue sr = new ServiceRevenue();
                sr.serviceName = dto.getServiceName();
                sr.revenue = dto.getRevenue();
                result.add(sr);
            }
        } catch (Exception ex) {
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
