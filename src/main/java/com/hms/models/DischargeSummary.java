package com.hms.models;

import java.time.LocalDateTime;

public class DischargeSummary {

    private int dischargeId;
    private String patientId;
    private String doctorId;
    private String recordId;
    private String invoiceId;
    private String diagnosisSummary;
    private String treatmentSummary;
    private String medicationsSummary;
    private String followupInstructions;
    private String patientCondition;
    private String status;
    private LocalDateTime dischargedAt;
    private LocalDateTime createdAt;

    public DischargeSummary() {}

    public DischargeSummary(int dischargeId, String patientId, String doctorId,
                            String recordId, String invoiceId, String diagnosisSummary,
                            String treatmentSummary, String medicationsSummary,
                            String followupInstructions, String patientCondition,
                            String status, LocalDateTime dischargedAt, LocalDateTime createdAt) {
        this.dischargeId = dischargeId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordId = recordId;
        this.invoiceId = invoiceId;
        this.diagnosisSummary = diagnosisSummary;
        this.treatmentSummary = treatmentSummary;
        this.medicationsSummary = medicationsSummary;
        this.followupInstructions = followupInstructions;
        this.patientCondition = patientCondition;
        this.status = status;
        this.dischargedAt = dischargedAt;
        this.createdAt = createdAt;
    }

    public int getDischargeId() { return dischargeId; }
    public void setDischargeId(int dischargeId) { this.dischargeId = dischargeId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getDiagnosisSummary() { return diagnosisSummary; }
    public void setDiagnosisSummary(String diagnosisSummary) { this.diagnosisSummary = diagnosisSummary; }

    public String getTreatmentSummary() { return treatmentSummary; }
    public void setTreatmentSummary(String treatmentSummary) { this.treatmentSummary = treatmentSummary; }

    public String getMedicationsSummary() { return medicationsSummary; }
    public void setMedicationsSummary(String medicationsSummary) { this.medicationsSummary = medicationsSummary; }

    public String getFollowupInstructions() { return followupInstructions; }
    public void setFollowupInstructions(String followupInstructions) { this.followupInstructions = followupInstructions; }

    public String getPatientCondition() { return patientCondition; }
    public void setPatientCondition(String patientCondition) { this.patientCondition = patientCondition; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDischargedAt() { return dischargedAt; }
    public void setDischargedAt(LocalDateTime dischargedAt) { this.dischargedAt = dischargedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DischargeSummary{" +
                "dischargeId=" + dischargeId +
                ", patientId='" + patientId + '\'' +
                ", status='" + status + '\'' +
                ", patientCondition='" + patientCondition + '\'' +
                '}';
    }
}