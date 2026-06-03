package com.hms.models;

import java.time.LocalDateTime;

public class DispensingRecord {

    private int dispenseId;
    private String prescriptionId;
    private String pharmacistId;
    private LocalDateTime dispensedAt;
    private String notes;

    public DispensingRecord() {}

    public DispensingRecord(int dispenseId, String prescriptionId, String pharmacistId,
                            LocalDateTime dispensedAt, String notes) {
        this.dispenseId = dispenseId;
        this.prescriptionId = prescriptionId;
        this.pharmacistId = pharmacistId;
        this.dispensedAt = dispensedAt;
        this.notes = notes;
    }

    public int getDispenseId() { return dispenseId; }
    public void setDispenseId(int dispenseId) { this.dispenseId = dispenseId; }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(String pharmacistId) { this.pharmacistId = pharmacistId; }

    public LocalDateTime getDispensedAt() { return dispensedAt; }
    public void setDispensedAt(LocalDateTime dispensedAt) { this.dispensedAt = dispensedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "DispensingRecord{" +
                "dispenseId=" + dispenseId +
                ", prescriptionId='" + prescriptionId + '\'' +
                ", pharmacistId='" + pharmacistId + '\'' +
                '}';
    }
}