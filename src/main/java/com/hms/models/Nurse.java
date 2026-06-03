package com.hms.models;

public class Nurse {

    private String nurseId;
    private String wardAssignment;
    private String vitalsHistory;

    // Default constructor
    public Nurse() {}

    // Parameterized constructor
    public Nurse(String nurseId, String wardAssignment, String vitalsHistory) {
        this.nurseId = nurseId;
        this.wardAssignment = wardAssignment;
        this.vitalsHistory = vitalsHistory;
    }

    // Getters and Setters
    public String getNurseId() { return nurseId; }
    public void setNurseId(String nurseId) { this.nurseId = nurseId; }

    public String getWardAssignment() { return wardAssignment; }
    public void setWardAssignment(String wardAssignment) { this.wardAssignment = wardAssignment; }

    public String getVitalsHistory() { return vitalsHistory; }
    public void setVitalsHistory(String vitalsHistory) { this.vitalsHistory = vitalsHistory; }

    // toString
    @Override
    public String toString() {
        return "Nurse{" +
                "nurseId='" + nurseId + '\'' +
                ", wardAssignment='" + wardAssignment + '\'' +
                ", vitalsHistory='" + vitalsHistory + '\'' +
                '}';
    }
}