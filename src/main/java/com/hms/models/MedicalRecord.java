package com.hms.models;

import java.time.LocalDateTime;

public class MedicalRecord {

    private String recordId;
    private String patientId;
    private String diagnosis;
    private String treatmentHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicalRecord() {}

    public MedicalRecord(String recordId, String patientId, String diagnosis,
                         String treatmentHistory, LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatmentHistory = treatmentHistory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatmentHistory() { return treatmentHistory; }
    public void setTreatmentHistory(String treatmentHistory) { this.treatmentHistory = treatmentHistory; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "recordId='" + recordId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                '}';
    }
}