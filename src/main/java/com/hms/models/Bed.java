package com.hms.models;

import java.time.LocalDateTime;

public class Bed {

    private int bedId;
    private String wardName;
    private String bedNumber;
    private String status;
    private LocalDateTime createdAt;

    public Bed() {}

    public Bed(int bedId, String wardName, String bedNumber,
               String status, LocalDateTime createdAt) {
        this.bedId = bedId;
        this.wardName = wardName;
        this.bedNumber = bedNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getBedId() { return bedId; }
    public void setBedId(int bedId) { this.bedId = bedId; }

    public String getWardName() { return wardName; }
    public void setWardName(String wardName) { this.wardName = wardName; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Bed{" +
                "bedId=" + bedId +
                ", wardName='" + wardName + '\'' +
                ", bedNumber='" + bedNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}