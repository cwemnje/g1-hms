package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceItem {

    private int itemId;
    private String invoiceId;
    private String description;
    private String chargeType;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public InvoiceItem() {}

    public InvoiceItem(int itemId, String invoiceId, String description,
                       String chargeType, BigDecimal amount, LocalDateTime createdAt) {
        this.itemId = itemId;
        this.invoiceId = invoiceId;
        this.description = description;
        this.chargeType = chargeType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getChargeType() { return chargeType; }
    public void setChargeType(String chargeType) { this.chargeType = chargeType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "InvoiceItem{" +
                "itemId=" + itemId +
                ", invoiceId='" + invoiceId + '\'' +
                ", description='" + description + '\'' +
                ", chargeType='" + chargeType + '\'' +
                ", amount=" + amount +
                '}';
    }
}