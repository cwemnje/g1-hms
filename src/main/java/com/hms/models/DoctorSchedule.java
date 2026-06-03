package com.hms.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DoctorSchedule {

    private int scheduleId;
    private String doctorId;
    private LocalDate shiftDate;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private String dutyType;
    private String createdBy;
    private LocalDateTime createdAt;

    public DoctorSchedule() {}

    public DoctorSchedule(int scheduleId, String doctorId, LocalDate shiftDate,
                          LocalTime shiftStart, LocalTime shiftEnd, String dutyType,
                          String createdBy, LocalDateTime createdAt) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.shiftDate = shiftDate;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.dutyType = dutyType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }

    public LocalTime getShiftStart() { return shiftStart; }
    public void setShiftStart(LocalTime shiftStart) { this.shiftStart = shiftStart; }

    public LocalTime getShiftEnd() { return shiftEnd; }
    public void setShiftEnd(LocalTime shiftEnd) { this.shiftEnd = shiftEnd; }

    public String getDutyType() { return dutyType; }
    public void setDutyType(String dutyType) { this.dutyType = dutyType; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DoctorSchedule{" +
                "scheduleId=" + scheduleId +
                ", doctorId='" + doctorId + '\'' +
                ", shiftDate=" + shiftDate +
                ", shiftStart=" + shiftStart +
                ", shiftEnd=" + shiftEnd +
                ", dutyType='" + dutyType + '\'' +
                '}';
    }
}