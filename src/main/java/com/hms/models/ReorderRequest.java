package com.hms.models;

import java.time.LocalDateTime;

public class ReorderRequest {

    private int requestId;
    private int medicationId;
    private String pharmacistId;
    private int quantityRequested;
    private String justification;
    private String status;
    private String reviewedBy;
    private LocalDateTime createdAt;

    public ReorderRequest() {}

    public ReorderRequest(int requestId, int medicationId, String pharmacistId,
                          int quantityRequested, String justification, String status,
                          String reviewedBy, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.medicationId = medicationId;
        this.pharmacistId = pharmacistId;
        this.quantityRequested = quantityRequested;
        this.justification = justification;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.createdAt = createdAt;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getMedicationId() { return medicationId; }
    public void setMedicationId(int medicationId) { this.medicationId = medicationId; }

    public String getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(String pharmacistId) { this.pharmacistId = pharmacistId; }

    public int getQuantityRequested() { return quantityRequested; }
    public void setQuantityRequested(int quantityRequested) { this.quantityRequested = quantityRequested; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ReorderRequest{" +
                "requestId=" + requestId +
                ", medicationId=" + medicationId +
                ", status='" + status + '\'' +
                '}';
    }
}