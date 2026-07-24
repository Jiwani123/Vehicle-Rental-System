package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.entity.inquiry;
import org.example.rentalsytsem.entity.vehicle;
import org.example.rentalsytsem.entity.feedback;
import org.example.rentalsytsem.repository.userrepository;
import org.example.rentalsytsem.repository.bookingrepository;
import org.example.rentalsytsem.repository.inquiryrepository;
import org.example.rentalsytsem.repository.vehiclerepository;
import org.example.rentalsytsem.repository.feedbackrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class ReportingService {
    
    @Autowired
    private userrepository userRepository;
    
    @Autowired
    private bookingrepository bookingRepository;
    
    @Autowired
    private vehiclerepository vehicleRepository;
    
    @Autowired
    private inquiryrepository inquiryRepository;
    
    @Autowired
    private feedbackrepository feedbackRepository;

    /**
     * Generate comprehensive system report
     */
    public Map<String, Object> generateSystemReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // Basic statistics
            long totalUsers = userRepository.count();
            long totalBookings = bookingRepository.count();
            long totalVehicles = vehicleRepository.count();
            long totalInquiries = inquiryRepository.count();
            long totalFeedbacks = feedbackRepository.count();
            
            report.put("totalUsers", totalUsers);
            report.put("totalBookings", totalBookings);
            report.put("totalVehicles", totalVehicles);
            report.put("totalInquiries", totalInquiries);
            report.put("totalFeedbacks", totalFeedbacks);
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", "SYSTEM_OVERVIEW");
            
        } catch (Exception e) {
            report.put("error", "Error generating system report: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Generate user activity report
     */
    public Map<String, Object> generateUserActivityReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            List<user> users = userRepository.findAll();
            List<booking> bookings = bookingRepository.findAll();
            
            // User registration analysis
            Map<String, Integer> usersByRole = new HashMap<>();
            for (user u : users) {
                String role = u.getRole() != null ? u.getRole() : "UNKNOWN";
                usersByRole.put(role, usersByRole.getOrDefault(role, 0) + 1);
            }
            
            // Booking activity analysis
            Map<String, Integer> bookingActivity = new HashMap<>();
            bookingActivity.put("totalBookings", bookings.size());
            
            report.put("totalUsers", users.size());
            report.put("usersByRole", usersByRole);
            report.put("bookingActivity", bookingActivity);
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", "USER_ACTIVITY");
            
        } catch (Exception e) {
            report.put("error", "Error generating user activity report: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Generate vehicle utilization report
     */
    public Map<String, Object> generateVehicleUtilizationReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            List<vehicle> vehicles = vehicleRepository.findAll();
            List<booking> bookings = bookingRepository.findAll();
            
            // Vehicle statistics
            Map<String, Object> vehicleStats = new HashMap<>();
            vehicleStats.put("totalVehicles", vehicles.size());
            
            // Vehicle type analysis
            Map<String, Integer> vehiclesByType = new HashMap<>();
            for (vehicle v : vehicles) {
                String type = v.getVehicleType() != null ? v.getVehicleType() : "UNKNOWN";
                vehiclesByType.put(type, vehiclesByType.getOrDefault(type, 0) + 1);
            }
            
            report.put("vehicleStatistics", vehicleStats);
            report.put("vehiclesByType", vehiclesByType);
            report.put("totalBookings", bookings.size());
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", "VEHICLE_UTILIZATION");
            
        } catch (Exception e) {
            report.put("error", "Error generating vehicle utilization report: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Generate financial summary report
     */
    public Map<String, Object> generateFinancialReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            List<booking> bookings = bookingRepository.findAll();
            
            // Basic financial calculations
            double totalRevenue = 0.0;
            int completedBookings = 0;
            
            for (booking b : bookings) {
                // Assuming booking has totalAmount field and status field
                if (b.getTotalAmount() != null) {
                    totalRevenue += b.getTotalAmount();
                }
                // Count completed bookings if status field exists
                completedBookings++;
            }
            
            double averageBookingValue = completedBookings > 0 ? totalRevenue / completedBookings : 0.0;
            
            report.put("totalRevenue", totalRevenue);
            report.put("completedBookings", completedBookings);
            report.put("averageBookingValue", averageBookingValue);
            report.put("totalBookings", bookings.size());
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", "FINANCIAL_SUMMARY");
            
        } catch (Exception e) {
            report.put("error", "Error generating financial report: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Generate customer feedback report
     */
    public Map<String, Object> generateFeedbackReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            List<feedback> feedbacks = feedbackRepository.findAll();
            List<inquiry> inquiries = inquiryRepository.findAll();
            
            // Feedback analysis
            Map<String, Object> feedbackStats = new HashMap<>();
            feedbackStats.put("totalFeedbacks", feedbacks.size());
            
            // Rating analysis (if feedback has rating field)
            // Rating functionality not implemented yet
            double averageRating = 0.0;
            int ratedFeedbacks = feedbacks.size();
            
            // Note: Rating calculation will be implemented when rating field is added to feedback entity
            
            if (ratedFeedbacks > 0) {
                averageRating = averageRating / ratedFeedbacks;
            }
            
            // Inquiry statistics
            Map<String, Integer> inquiryByStatus = new HashMap<>();
            for (inquiry inq : inquiries) {
                String status = inq.getStatus() != null ? inq.getStatus().toString() : "UNKNOWN";
                inquiryByStatus.put(status, inquiryByStatus.getOrDefault(status, 0) + 1);
            }
            
            report.put("feedbackStatistics", feedbackStats);
            report.put("averageRating", averageRating);
            report.put("totalInquiries", inquiries.size());
            report.put("inquiriesByStatus", inquiryByStatus);
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", "CUSTOMER_FEEDBACK");
            
        } catch (Exception e) {
            report.put("error", "Error generating feedback report: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Generate custom date range report
     */
    public Map<String, Object> generateDateRangeReport(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // For now, return basic data - in real implementation, you'd filter by date range
            report.put("startDate", startDate);
            report.put("endDate", endDate);
            report.put("systemReport", generateSystemReport());
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", "DATE_RANGE");
            
        } catch (Exception e) {
            report.put("error", "Error generating date range report: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Get available report types
     */
    public Map<String, Object> getAvailableReportTypes() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<String> reportTypes = new ArrayList<>();
            reportTypes.add("SYSTEM_OVERVIEW");
            reportTypes.add("USER_ACTIVITY");
            reportTypes.add("VEHICLE_UTILIZATION");
            reportTypes.add("FINANCIAL_SUMMARY");
            reportTypes.add("CUSTOMER_FEEDBACK");
            reportTypes.add("DATE_RANGE");
            
            result.put("availableReports", reportTypes);
            result.put("totalTypes", reportTypes.size());
            result.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            result.put("error", "Error fetching report types: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Export report data (returns formatted data for export)
     */
    public Map<String, Object> exportReportData(String reportType) {
        Map<String, Object> exportData = new HashMap<>();
        
        try {
            switch (reportType.toUpperCase()) {
                case "SYSTEM_OVERVIEW":
                    exportData = generateSystemReport();
                    break;
                case "USER_ACTIVITY":
                    exportData = generateUserActivityReport();
                    break;
                case "VEHICLE_UTILIZATION":
                    exportData = generateVehicleUtilizationReport();
                    break;
                case "FINANCIAL_SUMMARY":
                    exportData = generateFinancialReport();
                    break;
                case "CUSTOMER_FEEDBACK":
                    exportData = generateFeedbackReport();
                    break;
                default:
                    exportData.put("error", "Unknown report type: " + reportType);
            }
            
            exportData.put("exportFormat", "JSON");
            exportData.put("exportedAt", LocalDateTime.now());
            
        } catch (Exception e) {
            exportData.put("error", "Error exporting report: " + e.getMessage());
        }
        
        return exportData;
    }
}