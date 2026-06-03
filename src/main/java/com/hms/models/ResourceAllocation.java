package com.hms.models;

import java.time.LocalDateTime;

public class ResourceAllocation {

    private int allocationId;
    private String patientId;
    private String doctorId;
    private String approvedBy;
    private Integer bedId;
    private Integer theatreId;
    private String allocationType;
    private boolean isEmergency;
    private LocalDateTime allocatedAt;
    private LocalDateTime releasedAt;

    public ResourceAllocation() {}

    public ResourceAllocation(int allocationId, String patientId, String doctorId,
                               String approvedBy, Integer bedId, Integer theatreId,
                               String allocationType, boolean isEmergency,
                               LocalDateTime allocatedAt, LocalDateTime releasedAt) {
        this.allocationId = allocationId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.approvedBy = approvedBy;
        this.bedId = bedId;
        this.theatreId = theatreId;
        this.allocationType = allocationType;
        this.isEmergency = isEmergency;
        this.allocatedAt = allocatedAt;
        this.releasedAt = releasedAt;
    }

    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }

    public Integer getTheatreId() { return theatreId; }
    public void setTheatreId(Integer theatreId) { this.theatreId = theatreId; }

    public String getAllocationType() { return allocationType; }
    public void setAllocationType(String allocationType) { this.allocationType = allocationType; }

    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean isEmergency) { this.isEmergency = isEmergency; }

    public LocalDateTime getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(LocalDateTime allocatedAt) { this.allocatedAt = allocatedAt; }

    public LocalDateTime getReleasedAt() { return releasedAt; }
    public void setReleasedAt(LocalDateTime releasedAt) { this.releasedAt = releasedAt; }

    @Override
    public String toString() {
        return "ResourceAllocation{" +
                "allocationId=" + allocationId +
                ", patientId='" + patientId + '\'' +
                ", allocationType='" + allocationType + '\'' +
                ", isEmergency=" + isEmergency +
                '}';
    }
}