package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private int paymentId;
    private String invoiceId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String paymentStatus;
    private String processedBy;
    private LocalDateTime paidAt;

    public Payment() {}

    public Payment(int paymentId, String invoiceId, BigDecimal amountPaid,
                   String paymentMethod, String paymentStatus,
                   String processedBy, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.processedBy = processedBy;
        this.paidAt = paidAt;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", invoiceId='" + invoiceId + '\'' +
                ", amountPaid=" + amountPaid +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}