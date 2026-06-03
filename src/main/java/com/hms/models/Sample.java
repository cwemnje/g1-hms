package com.hms.models;

import java.time.LocalDateTime;

public class Sample {

    private int sampleId;
    private String orderId;
    private String labStaffId;
    private LocalDateTime collectedAt;
    private String sampleStatus;

    public Sample() {}

    public Sample(int sampleId, String orderId, String labStaffId,
                  LocalDateTime collectedAt, String sampleStatus) {
        this.sampleId = sampleId;
        this.orderId = orderId;
        this.labStaffId = labStaffId;
        this.collectedAt = collectedAt;
        this.sampleStatus = sampleStatus;
    }

    public int getSampleId() { return sampleId; }
    public void setSampleId(int sampleId) { this.sampleId = sampleId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getLabStaffId() { return labStaffId; }
    public void setLabStaffId(String labStaffId) { this.labStaffId = labStaffId; }

    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }

    public String getSampleStatus() { return sampleStatus; }
    public void setSampleStatus(String sampleStatus) { this.sampleStatus = sampleStatus; }

    @Override
    public String toString() {
        return "Sample{" +
                "sampleId=" + sampleId +
                ", orderId='" + orderId + '\'' +
                ", sampleStatus='" + sampleStatus + '\'' +
                '}';
    }
}