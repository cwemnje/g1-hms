package com.hms.models;

import java.time.LocalDateTime;

public class Theatre {

    private int theatreId;
    private String theatreName;
    private String status;
    private LocalDateTime createdAt;

    public Theatre() {}

    public Theatre(int theatreId, String theatreName, String status, LocalDateTime createdAt) {
        this.theatreId = theatreId;
        this.theatreName = theatreName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getTheatreId() { return theatreId; }
    public void setTheatreId(int theatreId) { this.theatreId = theatreId; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Theatre{" +
                "theatreId=" + theatreId +
                ", theatreName='" + theatreName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}