package com.hms.models;

import java.time.LocalDateTime;

public class TestResult {

    private int resultId;
    private String orderId;
    private String labStaffId;
    private String recordId;
    private String resultValue;
    private String notes;
    private LocalDateTime enteredAt;

    public TestResult() {}

    public TestResult(int resultId, String orderId, String labStaffId, String recordId,
                      String resultValue, String notes, LocalDateTime enteredAt) {
        this.resultId = resultId;
        this.orderId = orderId;
        this.labStaffId = labStaffId;
        this.recordId = recordId;
        this.resultValue = resultValue;
        this.notes = notes;
        this.enteredAt = enteredAt;
    }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getLabStaffId() { return labStaffId; }
    public void setLabStaffId(String labStaffId) { this.labStaffId = labStaffId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getResultValue() { return resultValue; }
    public void setResultValue(String resultValue) { this.resultValue = resultValue; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getEnteredAt() { return enteredAt; }
    public void setEnteredAt(LocalDateTime enteredAt) { this.enteredAt = enteredAt; }

    @Override
    public String toString() {
        return "TestResult{" +
                "resultId=" + resultId +
                ", orderId='" + orderId + '\'' +
                ", resultValue='" + resultValue + '\'' +
                '}';
    }
}