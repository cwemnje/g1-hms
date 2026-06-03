package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.ConsultationNote;
import com.hms.models.MedicalRecord;
import com.hms.models.Vitals;
import com.hms.utils.IDGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordService {

    private Connection connection;

    public MedicalRecordService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Create a new medical record for a patient
    public boolean createMedicalRecord(MedicalRecord record) {
        if (recordExistsForPatient(record.getPatientId())) {
            System.err.println("Creation failed: medical record already exists for patient.");
            return false;
        }

        String recordId = IDGenerator.generateGenericId("MEDR", "medical_record", connection);
        if (recordId == null) {
            System.err.println("Creation failed: could not generate record ID.");
            return false;
        }

        record.setRecordId(recordId);

        String query = "INSERT INTO medical_record (record_id, patient_id, diagnosis, " +
                       "treatment_history) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, record.getRecordId());
            stmt.setString(2, record.getPatientId());
            stmt.setString(3, record.getDiagnosis());
            stmt.setString(4, record.getTreatmentHistory());
            stmt.executeUpdate();

            System.out.println("Medical record created: " + recordId);
            return true;

        } catch (SQLException e) {
            System.err.println("MedicalRecordService createMedicalRecord error: " + e.getMessage());
            return false;
        }
    }

    // Get medical record by patient ID
    public MedicalRecord getRecordByPatientId(String patientId) {
        String query = "SELECT * FROM medical_record WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRecord(rs);
            } else {
                System.err.println("No medical record found for patient: " + patientId);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("MedicalRecordService getRecordByPatientId error: " + e.getMessage());
            return null;
        }
    }

    // Get medical record by record ID
    public MedicalRecord getRecordById(String recordId) {
        String query = "SELECT * FROM medical_record WHERE record_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, recordId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRecord(rs);
            }
        } catch (SQLException e) {
            System.err.println("MedicalRecordService getRecordById error: " + e.getMessage());
        }

        return null;
    }

    // Update diagnosis and treatment history
    public boolean updateMedicalRecord(MedicalRecord record) {
        String query = "UPDATE medical_record SET diagnosis = ?, treatment_history = ?, " +
                       "updated_at = CURRENT_TIMESTAMP WHERE record_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, record.getDiagnosis());
            stmt.setString(2, record.getTreatmentHistory());
            stmt.setString(3, record.getRecordId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Medical record updated: " + record.getRecordId());
                return true;
            } else {
                System.err.println("Update failed: record not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("MedicalRecordService updateMedicalRecord error: " + e.getMessage());
            return false;
        }
    }

    // Add a consultation note
    public boolean addConsultationNote(ConsultationNote note) {
        String query = "INSERT INTO consultation_note (record_id, doctor_id, " +
                       "appointment_id, note) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, note.getRecordId());
            stmt.setString(2, note.getDoctorId());
            stmt.setString(3, note.getAppointmentId());
            stmt.setString(4, note.getNote());
            stmt.executeUpdate();

            System.out.println("Consultation note added for record: " + note.getRecordId());
            return true;

        } catch (SQLException e) {
            System.err.println("MedicalRecordService addConsultationNote error: " + e.getMessage());
            return false;
        }
    }

    // Get all consultation notes for a medical record
    public List<ConsultationNote> getConsultationNotes(String recordId) {
        List<ConsultationNote> notes = new ArrayList<>();
        String query = "SELECT * FROM consultation_note WHERE record_id = ? " +
                       "ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, recordId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            System.err.println("MedicalRecordService getConsultationNotes error: " + e.getMessage());
        }

        return notes;
    }

    // Record vitals for a patient
    public boolean recordVitals(Vitals vitals) {
        String query = "INSERT INTO vitals (record_id, nurse_id, blood_pressure, " +
                       "temperature, pulse_rate, respiratory_rate, weight, height) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, vitals.getRecordId());
            stmt.setString(2, vitals.getNurseId());
            stmt.setString(3, vitals.getBloodPressure());
            stmt.setBigDecimal(4, vitals.getTemperature());
            stmt.setInt(5, vitals.getPulseRate());
            stmt.setInt(6, vitals.getRespiratoryRate());
            stmt.setBigDecimal(7, vitals.getWeight());
            stmt.setBigDecimal(8, vitals.getHeight());
            stmt.executeUpdate();

            System.out.println("Vitals recorded for record: " + vitals.getRecordId());
            return true;

        } catch (SQLException e) {
            System.err.println("MedicalRecordService recordVitals error: " + e.getMessage());
            return false;
        }
    }

    // Get all vitals for a medical record
    public List<Vitals> getVitals(String recordId) {
        List<Vitals> vitalsList = new ArrayList<>();
        String query = "SELECT * FROM vitals WHERE record_id = ? ORDER BY recorded_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, recordId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vitalsList.add(mapResultSetToVitals(rs));
            }
        } catch (SQLException e) {
            System.err.println("MedicalRecordService getVitals error: " + e.getMessage());
        }

        return vitalsList;
    }

    // Check if a medical record exists for a patient
    public boolean recordExistsForPatient(String patientId) {
        String query = "SELECT COUNT(*) FROM medical_record WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("MedicalRecordService recordExistsForPatient error: " + e.getMessage());
        }

        return false;
    }

    // Map ResultSet to MedicalRecord
    private MedicalRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(rs.getString("record_id"));
        record.setPatientId(rs.getString("patient_id"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setTreatmentHistory(rs.getString("treatment_history"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) record.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) record.setUpdatedAt(updatedAt.toLocalDateTime());

        return record;
    }

    // Map ResultSet to ConsultationNote
    private ConsultationNote mapResultSetToNote(ResultSet rs) throws SQLException {
        ConsultationNote note = new ConsultationNote();
        note.setNoteId(rs.getInt("note_id"));
        note.setRecordId(rs.getString("record_id"));
        note.setDoctorId(rs.getString("doctor_id"));
        note.setAppointmentId(rs.getString("appointment_id"));
        note.setNote(rs.getString("note"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) note.setCreatedAt(createdAt.toLocalDateTime());

        return note;
    }

    // Map ResultSet to Vitals
    private Vitals mapResultSetToVitals(ResultSet rs) throws SQLException {
        Vitals vitals = new Vitals();
        vitals.setVitalsId(rs.getInt("vitals_id"));
        vitals.setRecordId(rs.getString("record_id"));
        vitals.setNurseId(rs.getString("nurse_id"));
        vitals.setBloodPressure(rs.getString("blood_pressure"));

        BigDecimal temp = rs.getBigDecimal("temperature");
        if (temp != null) vitals.setTemperature(temp);

        vitals.setPulseRate(rs.getInt("pulse_rate"));
        vitals.setRespiratoryRate(rs.getInt("respiratory_rate"));

        BigDecimal weight = rs.getBigDecimal("weight");
        if (weight != null) vitals.setWeight(weight);

        BigDecimal height = rs.getBigDecimal("height");
        if (height != null) vitals.setHeight(height);

        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        if (recordedAt != null) vitals.setRecordedAt(recordedAt.toLocalDateTime());

        return vitals;
    }
}