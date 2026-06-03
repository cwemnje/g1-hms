package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.Medication;
import com.hms.models.ReorderRequest;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PharmacyService {

    private Connection connection;

    public PharmacyService() {
        this.connection = DatabaseConnection.getConnection();
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
            System.err.println("PharmacyService getAllMedications error: " + e.getMessage());
        }

        return medications;
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
            System.err.println("PharmacyService getMedicationById error: " + e.getMessage());
        }

        return null;
    }

    // Get medications below reorder threshold
    public List<Medication> getLowStockMedications() {
        List<Medication> medications = new ArrayList<>();
        String query = "SELECT * FROM medication WHERE stock_level <= reorder_threshold " +
                       "ORDER BY stock_level ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                medications.add(mapResultSetToMedication(rs));
            }
        } catch (SQLException e) {
            System.err.println("PharmacyService getLowStockMedications error: " + e.getMessage());
        }

        return medications;
    }

    // Update medication stock level
    public boolean updateStockLevel(int medicationId, int newStockLevel) {
        String query = "UPDATE medication SET stock_level = ? WHERE medication_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newStockLevel);
            stmt.setInt(2, medicationId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Stock updated for medication ID: " + medicationId);
                return true;
            } else {
                System.err.println("Stock update failed: medication not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("PharmacyService updateStockLevel error: " + e.getMessage());
            return false;
        }
    }

    // Update medication details
    public boolean updateMedication(Medication medication) {
        String query = "UPDATE medication SET medication_name = ?, reorder_threshold = ?, " +
                       "unit_price = ?, expiry_date = ? WHERE medication_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, medication.getMedicationName());
            stmt.setInt(2, medication.getReorderThreshold());
            stmt.setBigDecimal(3, medication.getUnitPrice());
            stmt.setDate(4, medication.getExpiryDate() != null ?
                    Date.valueOf(medication.getExpiryDate()) : null);
            stmt.setInt(5, medication.getMedicationId());
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("PharmacyService updateMedication error: " + e.getMessage());
            return false;
        }
    }

    // Delete a medication
    public boolean deleteMedication(int medicationId) {
        String query = "DELETE FROM medication WHERE medication_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, medicationId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("PharmacyService deleteMedication error: " + e.getMessage());
            return false;
        }
    }

    // Raise a reorder request
    public boolean raiseReorderRequest(ReorderRequest request) {
        String query = "INSERT INTO reorder_request (medication_id, pharmacist_id, " +
                       "quantity_requested, justification, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, request.getMedicationId());
            stmt.setString(2, request.getPharmacistId());
            stmt.setInt(3, request.getQuantityRequested());
            stmt.setString(4, request.getJustification());
            stmt.setString(5, "pending");
            stmt.executeUpdate();

            System.out.println("Reorder request raised for medication ID: " + request.getMedicationId());
            return true;

        } catch (SQLException e) {
            System.err.println("PharmacyService raiseReorderRequest error: " + e.getMessage());
            return false;
        }
    }

    // Get all pending reorder requests
    public List<ReorderRequest> getPendingReorderRequests() {
        List<ReorderRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM reorder_request WHERE status = 'pending' ORDER BY created_at ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToReorderRequest(rs));
            }
        } catch (SQLException e) {
            System.err.println("PharmacyService getPendingReorderRequests error: " + e.getMessage());
        }

        return requests;
    }

    // Approve a reorder request
    public boolean approveReorderRequest(int requestId, String reviewedBy, int quantityToAdd) {
        // Get the request
        String getQuery = "SELECT * FROM reorder_request WHERE request_id = ?";

        try (PreparedStatement getStmt = connection.prepareStatement(getQuery)) {
            getStmt.setInt(1, requestId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                int medicationId = rs.getInt("medication_id");

                // Update stock level
                String stockQuery = "UPDATE medication SET stock_level = stock_level + ? " +
                                    "WHERE medication_id = ?";
                try (PreparedStatement stockStmt = connection.prepareStatement(stockQuery)) {
                    stockStmt.setInt(1, quantityToAdd);
                    stockStmt.setInt(2, medicationId);
                    stockStmt.executeUpdate();
                }

                // Update request status
                String updateQuery = "UPDATE reorder_request SET status = 'approved', " +
                                     "reviewed_by = ? WHERE request_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, reviewedBy);
                    updateStmt.setInt(2, requestId);
                    updateStmt.executeUpdate();
                }

                System.out.println("Reorder request approved: " + requestId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("PharmacyService approveReorderRequest error: " + e.getMessage());
        }

        return false;
    }

    // Reject a reorder request
    public boolean rejectReorderRequest(int requestId, String reviewedBy) {
        String query = "UPDATE reorder_request SET status = 'rejected', " +
                       "reviewed_by = ? WHERE request_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reviewedBy);
            stmt.setInt(2, requestId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Reorder request rejected: " + requestId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("PharmacyService rejectReorderRequest error: " + e.getMessage());
            return false;
        }
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

    // Map ResultSet to ReorderRequest
    private ReorderRequest mapResultSetToReorderRequest(ResultSet rs) throws SQLException {
        ReorderRequest request = new ReorderRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setMedicationId(rs.getInt("medication_id"));
        request.setPharmacistId(rs.getString("pharmacist_id"));
        request.setQuantityRequested(rs.getInt("quantity_requested"));
        request.setJustification(rs.getString("justification"));
        request.setStatus(rs.getString("status"));
        request.setReviewedBy(rs.getString("reviewed_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) request.setCreatedAt(createdAt.toLocalDateTime());

        return request;
    }
}