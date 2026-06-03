package com.hms.models;

import java.time.LocalDateTime;

public class ConsultationNote {

    private int noteId;
    private String recordId;
    private String doctorId;
    private String appointmentId;
    private String note;
    private LocalDateTime createdAt;

    public ConsultationNote() {}

    public ConsultationNote(int noteId, String recordId, String doctorId,
                            String appointmentId, String note, LocalDateTime createdAt) {
        this.noteId = noteId;
        this.recordId = recordId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.note = note;
        this.createdAt = createdAt;
    }

    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ConsultationNote{" +
                "noteId=" + noteId +
                ", recordId='" + recordId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}