package com.hms.models;

import java.time.LocalDateTime;

public class LabOrder {

    private String orderId;
    private String patientId;
    private String doctorId;
    private String testType;
    private String status;
    private boolean isUrgent;
    private LocalDateTime createdAt;

    public LabOrder() {}

    public LabOrder(String orderId, String patientId, String doctorId, String testType,
                    String status, boolean isUrgent, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.testType = testType;
        this.status = status;
        this.isUrgent = isUrgent;
        this.createdAt = createdAt;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isUrgent() { return isUrgent; }
    public void setUrgent(boolean isUrgent) { this.isUrgent = isUrgent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "LabOrder{" +
                "orderId='" + orderId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", testType='" + testType + '\'' +
                ", status='" + status + '\'' +
                ", isUrgent=" + isUrgent +
                '}';
    }
}