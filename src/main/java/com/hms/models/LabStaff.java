package com.hms.models;

public class LabStaff {
    private String labStaffId;
    private String maintenance_log;
    private String labAccessLevel;

    // Default constructor
    public LabStaff() {}

    // Parameterized constructor
    public LabStaff(String labStaffId, String maintenance_log, String labAccessLevel) {
        this.labStaffId = labStaffId;
        this.maintenance_log = maintenance_log;
        this.labAccessLevel = labAccessLevel;
    }

    // Getters and Setters
    public String getLabStaffId() { return labStaffId; }
    public void setLabStaffId(String labStaffId) { this.labStaffId = labStaffId; }  
    public String getMaintenance_log() { return maintenance_log; }
    public void setMaintenance_log(String maintenance_log) { this.maintenance_log = maintenance_log; }   
    public String getLabAccessLevel() { return labAccessLevel; }
    public void setLabAccessLevel(String labAccessLevel) { this.labAccessLevel = labAccessLevel; }

    // toString
    @Override
    public String toString() {
        return "LabStaff{" +
                "labStaffId='" + labStaffId + '\'' +
                ", maintenance_log='" + maintenance_log + '\'' +
                ", labAccessLevel='" + labAccessLevel + '\'' +
                '}';
    }

}
