package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Receipt {

    private int receiptId;
    private int paymentId;
    private String invoiceId;
    private BigDecimal amountPaid;
    private LocalDateTime issuedAt;

    public Receipt() {}

    public Receipt(int receiptId, int paymentId, String invoiceId,
                   BigDecimal amountPaid, LocalDateTime issuedAt) {
        this.receiptId = receiptId;
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amountPaid = amountPaid;
        this.issuedAt = issuedAt;
    }

    public int getReceiptId() { return receiptId; }
    public void setReceiptId(int receiptId) { this.receiptId = receiptId; }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    @Override
    public String toString() {
        return "Receipt{" +
                "receiptId=" + receiptId +
                ", paymentId=" + paymentId +
                ", invoiceId='" + invoiceId + '\'' +
                ", amountPaid=" + amountPaid +
                '}';
    }
}