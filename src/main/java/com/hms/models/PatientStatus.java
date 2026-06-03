package com.hms.models;

import java.time.LocalDateTime;

public class PatientStatus {

    private int statusId;
    private String patientId;
    private String currentStatus;
    private LocalDateTime updatedAt;

    public PatientStatus() {}

    public PatientStatus(int statusId, String patientId, String currentStatus,
                         LocalDateTime updatedAt) {
        this.statusId = statusId;
        this.patientId = patientId;
        this.currentStatus = currentStatus;
        this.updatedAt = updatedAt;
    }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PatientStatus{" +
                "statusId=" + statusId +
                ", patientId='" + patientId + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                '}';
    }
}