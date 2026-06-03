package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.DispensingRecord;
import com.hms.models.Medication;
import com.hms.models.Prescription;
import com.hms.models.PrescriptionItem;
import com.hms.utils.IDGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {

    private Connection connection;

    public PrescriptionService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Create a new prescription
    public boolean createPrescription(Prescription prescription) {
        String prescriptionId = IDGenerator.generateGenericId("PRSC", "prescription", connection);
        if (prescriptionId == null) {
            System.err.println("Prescription failed: could not generate prescription ID.");
            return false;
        }

        prescription.setPrescriptionId(prescriptionId);

        String query = "INSERT INTO prescription (prescription_id, patient_id, doctor_id, " +
                       "record_id, status, allergy_checked) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, prescription.getPrescriptionId());
            stmt.setString(2, prescription.getPatientId());
            stmt.setString(3, prescription.getDoctorId());
            stmt.setString(4, prescription.getRecordId());
            stmt.setString(5, "pending");
            stmt.setBoolean(6, prescription.isAllergyChecked());
            stmt.executeUpdate();

            System.out.println("Prescription created: " + prescriptionId);
            return true;

        } catch (SQLException e) {
            System.err.println("PrescriptionService createPrescription error: " + e.getMessage());
            return false;
        }
    }

    // Add item to a prescription
    public boolean addPrescriptionItem(PrescriptionItem item) {
        String query = "INSERT INTO prescription_item (prescription_id, medication_id, " +
                       "dosage, instructions, quantity) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, item.getPrescriptionId());
            stmt.setInt(2, item.getMedicationId());
            stmt.setString(3, item.getDosage());
            stmt.setString(4, item.getInstructions());
            stmt.setInt(5, item.getQuantity());
            stmt.executeUpdate();

            System.out.println("Prescription item added to: " + item.getPrescriptionId());
            return true;

        } catch (SQLException e) {
            System.err.println("PrescriptionService addPrescriptionItem error: " + e.getMessage());
            return false;
        }
    }

    // Get prescription by ID
    public Prescription getPrescriptionById(String prescriptionId) {
        String query = "SELECT * FROM prescription WHERE prescription_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, prescriptionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPrescription(rs);
            }
        } catch (SQLException e) {
            System.err.println("PrescriptionService getPrescriptionById error: " + e.getMessage());
        }

        return null;
    }

    // Get all prescriptions for a patient
    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String query = "SELECT * FROM prescription WHERE patient_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        } catch (SQLException e) {
            System.err.println("PrescriptionService getPrescriptionsByPatient error: " + e.getMessage());
        }

        return prescriptions;
    }

    // Get all pending prescriptions
    public List<Prescription> getPendingPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        String query = "SELECT * FROM prescription WHERE status = 'pending' ORDER BY created_at ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        } catch (SQLException e) {
            System.err.println("PrescriptionService getPendingPrescriptions error: " + e.getMessage());
        }

        return prescriptions;
    }

    // Get all items for a prescription
    public List<PrescriptionItem> getPrescriptionItems(String prescriptionId) {
        List<PrescriptionItem> items = new ArrayList<>();
        String query = "SELECT * FROM prescription_item WHERE prescription_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, prescriptionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToPrescriptionItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("PrescriptionService getPrescriptionItems error: " + e.getMessage());
        }

        return items;
    }

    // Dispense a prescription
    public boolean dispensePrescription(DispensingRecord record) {
        // Check prescription exists and is pending
        Prescription prescription = getPrescriptionById(record.getPrescriptionId());
        if (prescription == null) {
            System.err.println("Dispense failed: prescription not found.");
            return false;
        }

        if (!prescription.getStatus().equals("pending")) {
            System.err.println("Dispense failed: prescription is not pending.");
            return false;
        }

        // Deduct stock for each item
        List<PrescriptionItem> items = getPrescriptionItems(record.getPrescriptionId());
        for (PrescriptionItem item : items) {
            boolean deducted = deductStock(item.getMedicationId(), item.getQuantity());
            if (!deducted) {
                System.err.println("Dispense failed: insufficient stock for medication ID " + item.getMedicationId());
                return false;
            }
        }

        // Insert dispensing record
        String query = "INSERT INTO dispensing_record (prescription_id, pharmacist_id, notes) " +
                       "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, record.getPrescriptionId());
            stmt.setString(2, record.getPharmacistId());
            stmt.setString(3, record.getNotes());
            stmt.executeUpdate();

            // Update prescription status
            updatePrescriptionStatus(record.getPrescriptionId(), "dispensed");

            System.out.println("Prescription dispensed: " + record.getPrescriptionId());
            return true;

        } catch (SQLException e) {
            System.err.println("PrescriptionService dispensePrescription error: " + e.getMessage());
            return false;
        }
    }

    // Add a medication to inventory
    public boolean addMedication(Medication medication) {
        String query = "INSERT INTO medication (medication_name, stock_level, " +
                       "reorder_threshold, unit_price, expiry_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, medication.getMedicationName());
            stmt.setInt(2, medication.getStockLevel());
            stmt.setInt(3, medication.getReorderThreshold());
            stmt.setBigDecimal(4, medication.getUnitPrice());
            stmt.setDate(5, medication.getExpiryDate() != null ?
                    Date.valueOf(medication.getExpiryDate()) : null);
            stmt.executeUpdate();

            System.out.println("Medication added: " + medication.getMedicationName());
            return true;

        } catch (SQLException e) {
            System.err.println("PrescriptionService addMedication error: " + e.getMessage());
            return false;
        }
    }

    // Get medication by ID
    public Medication getMedicationById(int medicationId) {
        String query = "SELECT * FROM medication WHERE medication_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, medicationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMedication(rs);
            }
        } catch (SQLException e) {
            System.err.println("PrescriptionService getMedicationById error: " + e.getMessage());
        }

        return null;
    }

    // Get all medications
    public List<Medication> getAllMedications() {
        List<Medication> medications = new ArrayList<>();
        String query = "SELECT * FROM medication ORDER BY medication_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                medications.add(mapResultSetToMedication(rs));
            }
        } catch (SQLException e) {
            System.err.println("PrescriptionService getAllMedications error: " + e.getMessage());
        }

        return medications;
    }

    // Update prescription status
    public boolean updatePrescriptionStatus(String prescriptionId, String status) {
        String query = "UPDATE prescription SET status = ? WHERE prescription_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, prescriptionId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("PrescriptionService updatePrescriptionStatus error: " + e.getMessage());
            return false;
        }
    }

    // Deduct stock after dispensing
    private boolean deductStock(int medicationId, int quantity) {
        String query = "UPDATE medication SET stock_level = stock_level - ? " +
                       "WHERE medication_id = ? AND stock_level >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, medicationId);
            stmt.setInt(3, quantity);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                System.err.println("Stock deduction failed: insufficient stock.");
                return false;
            }
            return true;

        } catch (SQLException e) {
            System.err.println("PrescriptionService deductStock error: " + e.getMessage());
            return false;
        }
    }

    // Map ResultSet to Prescription
    private Prescription mapResultSetToPrescription(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(rs.getString("prescription_id"));
        prescription.setPatientId(rs.getString("patient_id"));
        prescription.setDoctorId(rs.getString("doctor_id"));
        prescription.setRecordId(rs.getString("record_id"));
        prescription.setStatus(rs.getString("status"));
        prescription.setAllergyChecked(rs.getBoolean("allergy_checked"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) prescription.setCreatedAt(createdAt.toLocalDateTime());

        return prescription;
    }

    // Map ResultSet to PrescriptionItem
    private PrescriptionItem mapResultSetToPrescriptionItem(ResultSet rs) throws SQLException {
        PrescriptionItem item = new PrescriptionItem();
        item.setItemId(rs.getInt("item_id"));
        item.setPrescriptionId(rs.getString("prescription_id"));
        item.setMedicationId(rs.getInt("medication_id"));
        item.setDosage(rs.getString("dosage"));
        item.setInstructions(rs.getString("instructions"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }

    // Map ResultSet to Medication
    private Medication mapResultSetToMedication(ResultSet rs) throws SQLException {
        Medication medication = new Medication();
        medication.setMedicationId(rs.getInt("medication_id"));
        medication.setMedicationName(rs.getString("medication_name"));
        medication.setStockLevel(rs.getInt("stock_level"));
        medication.setReorderThreshold(rs.getInt("reorder_threshold"));
        medication.setUnitPrice(rs.getBigDecimal("unit_price"));

        Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) medication.setExpiryDate(expiryDate.toLocalDate());

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) medication.setCreatedAt(createdAt.toLocalDateTime());

        return medication;
    }
}