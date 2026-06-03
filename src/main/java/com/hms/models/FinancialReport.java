package com.hms.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinancialReport {

    private int reportId;
    private String generatedBy;
    private String reportType;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String department;
    private String paymentStatusFilter;
    private LocalDateTime generatedAt;

    public FinancialReport() {}

    public FinancialReport(int reportId, String generatedBy, String reportType,
                           LocalDate dateFrom, LocalDate dateTo, String department,
                           String paymentStatusFilter, LocalDateTime generatedAt) {
        this.reportId = reportId;
        this.generatedBy = generatedBy;
        this.reportType = reportType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.department = department;
        this.paymentStatusFilter = paymentStatusFilter;
        this.generatedAt = generatedAt;
    }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }

    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPaymentStatusFilter() { return paymentStatusFilter; }
    public void setPaymentStatusFilter(String paymentStatusFilter) { this.paymentStatusFilter = paymentStatusFilter; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    @Override
    public String toString() {
        return "FinancialReport{" +
                "reportId=" + reportId +
                ", reportType='" + reportType + '\'' +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", department='" + department + '\'' +
                '}';
    }
}