package com.hms.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NurseSchedule {

    private int scheduleId;
    private String nurseId;
    private LocalDate shiftDate;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private String wardAssigned;
    private String createdBy;
    private LocalDateTime createdAt;

    public NurseSchedule() {}

    public NurseSchedule(int scheduleId, String nurseId, LocalDate shiftDate,
                         LocalTime shiftStart, LocalTime shiftEnd, String wardAssigned,
                         String createdBy, LocalDateTime createdAt) {
        this.scheduleId = scheduleId;
        this.nurseId = nurseId;
        this.shiftDate = shiftDate;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.wardAssigned = wardAssigned;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public String getNurseId() { return nurseId; }
    public void setNurseId(String nurseId) { this.nurseId = nurseId; }

    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }

    public LocalTime getShiftStart() { return shiftStart; }
    public void setShiftStart(LocalTime shiftStart) { this.shiftStart = shiftStart; }

    public LocalTime getShiftEnd() { return shiftEnd; }
    public void setShiftEnd(LocalTime shiftEnd) { this.shiftEnd = shiftEnd; }

    public String getWardAssigned() { return wardAssigned; }
    public void setWardAssigned(String wardAssigned) { this.wardAssigned = wardAssigned; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "NurseSchedule{" +
                "scheduleId=" + scheduleId +
                ", nurseId='" + nurseId + '\'' +
                ", shiftDate=" + shiftDate +
                ", wardAssigned='" + wardAssigned + '\'' +
                '}';
    }
}
