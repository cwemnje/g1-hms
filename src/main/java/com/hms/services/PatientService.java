package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.Patient;
import com.hms.utils.IDGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    private Connection connection;

    public PatientService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Register a new patient
    public boolean registerPatient(Patient patient) {
        if (patientExists(patient.getPatientId())) {
            System.err.println("Registration failed: patient ID already exists.");
            return false;
        }

        String patientId = IDGenerator.generatePatientId(connection);
        if (patientId == null) {
            System.err.println("Registration failed: could not generate patient ID.");
            return false;
        }

        patient.setPatientId(patientId);

        String query = "INSERT INTO patient (patient_id, first_name, last_name, date_of_birth, " +
                       "gender, phone, email, address, insurance_details, " +
                       "outstanding_bill_amount, portal_access, portal_credentials) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patient.getPatientId());
            stmt.setString(2, patient.getFirstName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, patient.getDateOfBirth() != null ?
                    Date.valueOf(patient.getDateOfBirth()) : null);
            stmt.setString(5, patient.getGender());
            stmt.setString(6, patient.getPhone());
            stmt.setString(7, patient.getEmail());
            stmt.setString(8, patient.getAddress());
            stmt.setString(9, patient.getInsuranceDetails());
            stmt.setBigDecimal(10, patient.getOutstandingBillAmount() != null ?
                    patient.getOutstandingBillAmount() : BigDecimal.ZERO);
            stmt.setBoolean(11, patient.isPortalAccess());
            stmt.setString(12, patient.getPortalCredentials());
            stmt.executeUpdate();

            System.out.println("Patient registered successfully: " + patientId);
            return true;

        } catch (SQLException e) {
            System.err.println("PatientService registerPatient error: " + e.getMessage());
            return false;
        }
    }

    // Search patient by ID
    public Patient searchPatientById(String patientId) {
        String query = "SELECT * FROM patient WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPatient(rs);
            } else {
                System.err.println("Patient not found: " + patientId);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("PatientService searchPatientById error: " + e.getMessage());
            return null;
        }
    }

    // Search patients by name (first or last)
    public List<Patient> searchPatientByName(String name) {
        List<Patient> results = new ArrayList<>();
        String query = "SELECT * FROM patient WHERE " +
                       "LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchTerm = "%" + name + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToPatient(rs));
            }

        } catch (SQLException e) {
            System.err.println("PatientService searchPatientByName error: " + e.getMessage());
        }

        return results;
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patient ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }

        } catch (SQLException e) {
            System.err.println("PatientService getAllPatients error: " + e.getMessage());
        }

        return patients;
    }

    // Update patient details
    public boolean updatePatient(Patient patient) {
        String query = "UPDATE patient SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                       "gender = ?, phone = ?, email = ?, address = ?, " +
                       "insurance_details = ?, portal_access = ?, portal_credentials = ? " +
                       "WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, patient.getDateOfBirth() != null ?
                    Date.valueOf(patient.getDateOfBirth()) : null);
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getInsuranceDetails());
            stmt.setBoolean(9, patient.isPortalAccess());
            stmt.setString(10, patient.getPortalCredentials());
            stmt.setString(11, patient.getPatientId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Patient updated successfully: " + patient.getPatientId());
                return true;
            } else {
                System.err.println("Update failed: patient not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("PatientService updatePatient error: " + e.getMessage());
            return false;
        }
    }

    // Delete a patient
    public boolean deletePatient(String patientId) {
        String query = "DELETE FROM patient WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Patient deleted successfully: " + patientId);
                return true;
            } else {
                System.err.println("Delete failed: patient not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("PatientService deletePatient error: " + e.getMessage());
            return false;
        }
    }

    // Check if patient exists
    public boolean patientExists(String patientId) {
        String query = "SELECT COUNT(*) FROM patient WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("PatientService patientExists error: " + e.getMessage());
        }

        return false;
    }

    // Map ResultSet row to Patient object
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getString("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            patient.setDateOfBirth(dob.toLocalDate());
        }

        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setInsuranceDetails(rs.getString("insurance_details"));
        patient.setOutstandingBillAmount(rs.getBigDecimal("outstanding_bill_amount"));
        patient.setPortalAccess(rs.getBoolean("portal_access"));
        patient.setPortalCredentials(rs.getString("portal_credentials"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            patient.setCreatedAt(createdAt.toLocalDateTime());
        }

        return patient;
    }
}