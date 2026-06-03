package com.hms.models;

import java.time.LocalDateTime;

public class Appointment {

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String receptionistId;
    private LocalDateTime dateTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;

    // Default constructor
    public Appointment() {}

    // Parameterized constructor
    public Appointment(String appointmentId, String patientId, String doctorId,
                       String receptionistId, LocalDateTime dateTime, String status,
                       String notes, LocalDateTime createdAt) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.receptionistId = receptionistId;
        this.dateTime = dateTime;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getReceptionistId() { return receptionistId; }
    public void setReceptionistId(String receptionistId) { this.receptionistId = receptionistId; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // toString
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", receptionistId='" + receptionistId + '\'' +
                ", dateTime=" + dateTime +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}