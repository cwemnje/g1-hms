package com.hms.models;

import java.time.LocalDateTime;

public class AuditTrail {

    private int auditId;
    private String performedBy;
    private String actionType;
    private String tableAffected;
    private String recordAffected;
    private String description;
    private LocalDateTime performedAt;

    public AuditTrail() {}

    public AuditTrail(int auditId, String performedBy, String actionType,
                      String tableAffected, String recordAffected, String description,
                      LocalDateTime performedAt) {
        this.auditId = auditId;
        this.performedBy = performedBy;
        this.actionType = actionType;
        this.tableAffected = tableAffected;
        this.recordAffected = recordAffected;
        this.description = description;
        this.performedAt = performedAt;
    }

    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTableAffected() { return tableAffected; }
    public void setTableAffected(String tableAffected) { this.tableAffected = tableAffected; }

    public String getRecordAffected() { return recordAffected; }
    public void setRecordAffected(String recordAffected) { this.recordAffected = recordAffected; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "auditId=" + auditId +
                ", performedBy='" + performedBy + '\'' +
                ", actionType='" + actionType + '\'' +
                ", tableAffected='" + tableAffected + '\'' +
                '}';
    }
}