package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.DischargeSummary;
import com.hms.models.PatientStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DischargeService {

    private Connection connection;

    public DischargeService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Create a discharge summary draft
    public boolean createDischargeSummary(DischargeSummary summary) {
        if (!canDischarge(summary.getPatientId(), summary.getInvoiceId())) {
            System.err.println("Discharge failed: unpaid bills or pending procedures.");
            return false;
        }

        String query = "INSERT INTO discharge_summary (patient_id, doctor_id, record_id, " +
                       "invoice_id, diagnosis_summary, treatment_summary, medications_summary, " +
                       "followup_instructions, patient_condition, status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, summary.getPatientId());
            stmt.setString(2, summary.getDoctorId());
            stmt.setString(3, summary.getRecordId());
            stmt.setString(4, summary.getInvoiceId());
            stmt.setString(5, summary.getDiagnosisSummary());
            stmt.setString(6, summary.getTreatmentSummary());
            stmt.setString(7, summary.getMedicationsSummary());
            stmt.setString(8, summary.getFollowupInstructions());
            stmt.setString(9, summary.getPatientCondition());
            stmt.setString(10, "draft");
            stmt.executeUpdate();

            System.out.println("Discharge summary created for patient: " + summary.getPatientId());
            return true;

        } catch (SQLException e) {
            System.err.println("DischargeService createDischargeSummary error: " + e.getMessage());
            return false;
        }
    }

    // Finalize a discharge summary
    public boolean finalizeDischargeSummary(int dischargeId) {
        String query = "UPDATE discharge_summary SET status = 'finalized', " +
                       "discharged_at = ? WHERE discharge_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, dischargeId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Discharge summary finalized: " + dischargeId);
                return true;
            } else {
                System.err.println("Finalize failed: discharge summary not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("DischargeService finalizeDischargeSummary error: " + e.getMessage());
            return false;
        }
    }

    // Get discharge summary by patient ID
    public DischargeSummary getDischargeSummaryByPatient(String patientId) {
        String query = "SELECT * FROM discharge_summary WHERE patient_id = ? " +
                       "ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDischargeSummary(rs);
            }
        } catch (SQLException e) {
            System.err.println("DischargeService getDischargeSummaryByPatient error: " + e.getMessage());
        }

        return null;
    }

    // Get discharge summary by ID
    public DischargeSummary getDischargeSummaryById(int dischargeId) {
        String query = "SELECT * FROM discharge_summary WHERE discharge_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, dischargeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDischargeSummary(rs);
            }
        } catch (SQLException e) {
            System.err.println("DischargeService getDischargeSummaryById error: " + e.getMessage());
        }

        return null;
    }

    // Update discharge summary
    public boolean updateDischargeSummary(DischargeSummary summary) {
        String query = "UPDATE discharge_summary SET diagnosis_summary = ?, " +
                       "treatment_summary = ?, medications_summary = ?, " +
                       "followup_instructions = ?, patient_condition = ? " +
                       "WHERE discharge_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, summary.getDiagnosisSummary());
            stmt.setString(2, summary.getTreatmentSummary());
            stmt.setString(3, summary.getMedicationsSummary());
            stmt.setString(4, summary.getFollowupInstructions());
            stmt.setString(5, summary.getPatientCondition());
            stmt.setInt(6, summary.getDischargeId());
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("DischargeService updateDischargeSummary error: " + e.getMessage());
            return false;
        }
    }

    // Update patient status
    public boolean updatePatientStatus(String patientId, String status) {
        // Check if status record exists
        String checkQuery = "SELECT COUNT(*) FROM patient_status WHERE patient_id = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, patientId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Update existing
                String updateQuery = "UPDATE patient_status SET current_status = ?, " +
                                     "updated_at = CURRENT_TIMESTAMP WHERE patient_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, status);
                    updateStmt.setString(2, patientId);
                    updateStmt.executeUpdate();
                }
            } else {
                // Insert new
                String insertQuery = "INSERT INTO patient_status (patient_id, current_status) " +
                                     "VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, patientId);
                    insertStmt.setString(2, status);
                    insertStmt.executeUpdate();
                }
            }

            System.out.println("Patient " + patientId + " status → " + status);
            return true;

        } catch (SQLException e) {
            System.err.println("DischargeService updatePatientStatus error: " + e.getMessage());
            return false;
        }
    }

    // Get patient status
    public PatientStatus getPatientStatus(String patientId) {
        String query = "SELECT * FROM patient_status WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PatientStatus status = new PatientStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setPatientId(rs.getString("patient_id"));
                status.setCurrentStatus(rs.getString("current_status"));
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) status.setUpdatedAt(updatedAt.toLocalDateTime());
                return status;
            }
        } catch (SQLException e) {
            System.err.println("DischargeService getPatientStatus error: " + e.getMessage());
        }

        return null;
    }

    // Check if patient can be discharged — private helper
    private boolean canDischarge(String patientId, String invoiceId) {
        String query = "SELECT status FROM invoice WHERE invoice_id = ? AND patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoiceId);
            stmt.setString(2, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return status.equals("settled");
            }
        } catch (SQLException e) {
            System.err.println("DischargeService canDischarge error: " + e.getMessage());
        }

        return false;
    }

    // Map ResultSet to DischargeSummary
    private DischargeSummary mapResultSetToDischargeSummary(ResultSet rs) throws SQLException {
        DischargeSummary summary = new DischargeSummary();
        summary.setDischargeId(rs.getInt("discharge_id"));
        summary.setPatientId(rs.getString("patient_id"));
        summary.setDoctorId(rs.getString("doctor_id"));
        summary.setRecordId(rs.getString("record_id"));
        summary.setInvoiceId(rs.getString("invoice_id"));
        summary.setDiagnosisSummary(rs.getString("diagnosis_summary"));
        summary.setTreatmentSummary(rs.getString("treatment_summary"));
        summary.setMedicationsSummary(rs.getString("medications_summary"));
        summary.setFollowupInstructions(rs.getString("followup_instructions"));
        summary.setPatientCondition(rs.getString("patient_condition"));
        summary.setStatus(rs.getString("status"));

        Timestamp dischargedAt = rs.getTimestamp("discharged_at");
        if (dischargedAt != null) summary.setDischargedAt(dischargedAt.toLocalDateTime());

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) summary.setCreatedAt(createdAt.toLocalDateTime());

        return summary;
    }
}