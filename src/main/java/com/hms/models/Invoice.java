package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {

    private String invoiceId;
    private String patientId;
    private String receptionistId;
    private BigDecimal totalAmount;
    private BigDecimal insuranceDeduction;
    private BigDecimal amountDue;
    private String status;
    private LocalDateTime createdAt;

    public Invoice() {}

    public Invoice(String invoiceId, String patientId, String receptionistId,
                   BigDecimal totalAmount, BigDecimal insuranceDeduction,
                   BigDecimal amountDue, String status, LocalDateTime createdAt) {
        this.invoiceId = invoiceId;
        this.patientId = patientId;
        this.receptionistId = receptionistId;
        this.totalAmount = totalAmount;
        this.insuranceDeduction = insuranceDeduction;
        this.amountDue = amountDue;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReceptionistId() { return receptionistId; }
    public void setReceptionistId(String receptionistId) { this.receptionistId = receptionistId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getInsuranceDeduction() { return insuranceDeduction; }
    public void setInsuranceDeduction(BigDecimal insuranceDeduction) { this.insuranceDeduction = insuranceDeduction; }

    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", totalAmount=" + totalAmount +
                ", amountDue=" + amountDue +
                ", status='" + status + '\'' +
                '}';
    }
}