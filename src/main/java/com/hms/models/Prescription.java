package com.hms.models;

import java.time.LocalDateTime;

public class Prescription {

    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private String recordId;
    private String status;
    private boolean allergyChecked;
    private LocalDateTime createdAt;

    public Prescription() {}

    public Prescription(String prescriptionId, String patientId, String doctorId,
                        String recordId, String status, boolean allergyChecked,
                        LocalDateTime createdAt) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordId = recordId;
        this.status = status;
        this.allergyChecked = allergyChecked;
        this.createdAt = createdAt;
    }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAllergyChecked() { return allergyChecked; }
    public void setAllergyChecked(boolean allergyChecked) { this.allergyChecked = allergyChecked; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Prescription{" +
                "prescriptionId='" + prescriptionId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}