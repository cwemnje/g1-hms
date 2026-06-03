package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Medication {

    private int medicationId;
    private String medicationName;
    private int stockLevel;
    private int reorderThreshold;
    private BigDecimal unitPrice;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;

    public Medication() {}

    public Medication(int medicationId, String medicationName, int stockLevel,
                      int reorderThreshold, BigDecimal unitPrice, LocalDate expiryDate,
                      LocalDateTime createdAt) {
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.stockLevel = stockLevel;
        this.reorderThreshold = reorderThreshold;
        this.unitPrice = unitPrice;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    public int getMedicationId() { return medicationId; }
    public void setMedicationId(int medicationId) { this.medicationId = medicationId; }

    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

    public int getStockLevel() { return stockLevel; }
    public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }

    public int getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(int reorderThreshold) { this.reorderThreshold = reorderThreshold; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Medication{" +
                "medicationId=" + medicationId +
                ", medicationName='" + medicationName + '\'' +
                ", stockLevel=" + stockLevel +
                ", unitPrice=" + unitPrice +
                '}';
    }
}