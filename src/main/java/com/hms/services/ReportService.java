package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.FinancialReport;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private Connection connection;

    public ReportService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Save a financial report record
    public boolean saveReport(FinancialReport report) {
        String query = "INSERT INTO financial_report (generated_by, report_type, date_from, " +
                       "date_to, department, payment_status_filter) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, report.getGeneratedBy());
            stmt.setString(2, report.getReportType());
            stmt.setDate(3, Date.valueOf(report.getDateFrom()));
            stmt.setDate(4, Date.valueOf(report.getDateTo()));
            stmt.setString(5, report.getDepartment());
            stmt.setString(6, report.getPaymentStatusFilter());
            stmt.executeUpdate();

            System.out.println("Financial report saved.");
            return true;

        } catch (SQLException e) {
            System.err.println("ReportService saveReport error: " + e.getMessage());
            return false;
        }
    }

    // Get total revenue between two dates
    public BigDecimal getTotalRevenue(LocalDate from, LocalDate to) {
        String query = "SELECT COALESCE(SUM(amount_paid), 0) FROM payment " +
                       "WHERE payment_status = 'successful' " +
                       "AND DATE(paid_at) BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            System.err.println("ReportService getTotalRevenue error: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    // Get total outstanding amount
    public BigDecimal getTotalOutstanding() {
        String query = "SELECT COALESCE(SUM(amount_due), 0) FROM invoice " +
                       "WHERE status IN ('generated', 'partially_paid')";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            System.err.println("ReportService getTotalOutstanding error: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    // Get payment count by method between two dates
    public Map<String, Integer> getPaymentsByMethod(LocalDate from, LocalDate to) {
        Map<String, Integer> result = new HashMap<>();
        String query = "SELECT payment_method, COUNT(*) as count FROM payment " +
                       "WHERE payment_status = 'successful' " +
                       "AND DATE(paid_at) BETWEEN ? AND ? " +
                       "GROUP BY payment_method";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("payment_method"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("ReportService getPaymentsByMethod error: " + e.getMessage());
        }

        return result;
    }

    // Get invoice summary by status
    public Map<String, Integer> getInvoiceSummaryByStatus() {
        Map<String, Integer> result = new HashMap<>();
        String query = "SELECT status, COUNT(*) as count FROM invoice GROUP BY status";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("ReportService getInvoiceSummaryByStatus error: " + e.getMessage());
        }

        return result;
    }

    // Get total patients registered between two dates
    public int getTotalPatientsRegistered(LocalDate from, LocalDate to) {
        String query = "SELECT COUNT(*) FROM patient WHERE DATE(created_at) BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("ReportService getTotalPatientsRegistered error: " + e.getMessage());
        }

        return 0;
    }

    // Get total appointments between two dates
    public int getTotalAppointments(LocalDate from, LocalDate to) {
        String query = "SELECT COUNT(*) FROM appointment WHERE DATE(created_at) BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("ReportService getTotalAppointments error: " + e.getMessage());
        }

        return 0;
    }

    // Get total lab orders between two dates
    public int getTotalLabOrders(LocalDate from, LocalDate to) {
        String query = "SELECT COUNT(*) FROM lab_order WHERE DATE(created_at) BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("ReportService getTotalLabOrders error: " + e.getMessage());
        }

        return 0;
    }

    // Get all saved reports
    public List<FinancialReport> getAllReports() {
        List<FinancialReport> reports = new ArrayList<>();
        String query = "SELECT * FROM financial_report ORDER BY generated_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            System.err.println("ReportService getAllReports error: " + e.getMessage());
        }

        return reports;
    }

    // Map ResultSet to FinancialReport
    private FinancialReport mapResultSetToReport(ResultSet rs) throws SQLException {
        FinancialReport report = new FinancialReport();
        report.setReportId(rs.getInt("report_id"));
        report.setGeneratedBy(rs.getString("generated_by"));
        report.setReportType(rs.getString("report_type"));
        report.setDateFrom(rs.getDate("date_from").toLocalDate());
        report.setDateTo(rs.getDate("date_to").toLocalDate());
        report.setDepartment(rs.getString("department"));
        report.setPaymentStatusFilter(rs.getString("payment_status_filter"));

        Timestamp generatedAt = rs.getTimestamp("generated_at");
        if (generatedAt != null) report.setGeneratedAt(generatedAt.toLocalDateTime());

        return report;
    }
}