package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.LabOrder;
import com.hms.models.Sample;
import com.hms.models.TestResult;
import com.hms.utils.IDGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabService {

    private Connection connection;

    public LabService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Order a lab test
    public boolean orderLabTest(LabOrder labOrder) {
        String orderId = IDGenerator.generateGenericId("LABO", "lab_order", connection);
        if (orderId == null) {
            System.err.println("Lab order failed: could not generate order ID.");
            return false;
        }

        labOrder.setOrderId(orderId);

        String query = "INSERT INTO lab_order (order_id, patient_id, doctor_id, " +
                       "test_type, status, is_urgent) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, labOrder.getOrderId());
            stmt.setString(2, labOrder.getPatientId());
            stmt.setString(3, labOrder.getDoctorId());
            stmt.setString(4, labOrder.getTestType());
            stmt.setString(5, "pending");
            stmt.setBoolean(6, labOrder.isUrgent());
            stmt.executeUpdate();

            System.out.println("Lab order created: " + orderId);
            return true;

        } catch (SQLException e) {
            System.err.println("LabService orderLabTest error: " + e.getMessage());
            return false;
        }
    }

    // Get lab order by ID
    public LabOrder getLabOrderById(String orderId) {
        String query = "SELECT * FROM lab_order WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLabOrder(rs);
            }
        } catch (SQLException e) {
            System.err.println("LabService getLabOrderById error: " + e.getMessage());
        }

        return null;
    }

    // Get all lab orders for a patient
    public List<LabOrder> getLabOrdersByPatient(String patientId) {
        List<LabOrder> orders = new ArrayList<>();
        String query = "SELECT * FROM lab_order WHERE patient_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToLabOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("LabService getLabOrdersByPatient error: " + e.getMessage());
        }

        return orders;
    }

    // Get all pending lab orders
    public List<LabOrder> getPendingLabOrders() {
        List<LabOrder> orders = new ArrayList<>();
        String query = "SELECT * FROM lab_order WHERE status = 'pending' ORDER BY is_urgent DESC, created_at ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToLabOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("LabService getPendingLabOrders error: " + e.getMessage());
        }

        return orders;
    }

    // Update lab order status
    public boolean updateLabOrderStatus(String orderId, String status) {
        String query = "UPDATE lab_order SET status = ? WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Lab order " + orderId + " status → " + status);
                return true;
            } else {
                System.err.println("Status update failed: lab order not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("LabService updateLabOrderStatus error: " + e.getMessage());
            return false;
        }
    }

    // Record sample collection
    public boolean collectSample(Sample sample) {
        String query = "INSERT INTO sample (order_id, lab_staff_id, sample_status) " +
                       "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sample.getOrderId());
            stmt.setString(2, sample.getLabStaffId());
            stmt.setString(3, "received");
            stmt.executeUpdate();

            // Update lab order status
            updateLabOrderStatus(sample.getOrderId(), "sample_collected");

            System.out.println("Sample collected for order: " + sample.getOrderId());
            return true;

        } catch (SQLException e) {
            System.err.println("LabService collectSample error: " + e.getMessage());
            return false;
        }
    }

    // Update sample status
    public boolean updateSampleStatus(int sampleId, String status) {
        String query = "UPDATE sample SET sample_status = ? WHERE sample_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, sampleId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("LabService updateSampleStatus error: " + e.getMessage());
            return false;
        }
    }

    // Enter test results
    public boolean enterTestResults(TestResult result) {
        String query = "INSERT INTO test_result (order_id, lab_staff_id, record_id, " +
                       "result_value, notes) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, result.getOrderId());
            stmt.setString(2, result.getLabStaffId());
            stmt.setString(3, result.getRecordId());
            stmt.setString(4, result.getResultValue());
            stmt.setString(5, result.getNotes());
            stmt.executeUpdate();

            // Update lab order status to completed
            updateLabOrderStatus(result.getOrderId(), "completed");

            System.out.println("Test results entered for order: " + result.getOrderId());
            return true;

        } catch (SQLException e) {
            System.err.println("LabService enterTestResults error: " + e.getMessage());
            return false;
        }
    }

    // Get test results for a lab order
    public List<TestResult> getTestResults(String orderId) {
        List<TestResult> results = new ArrayList<>();
        String query = "SELECT * FROM test_result WHERE order_id = ? ORDER BY entered_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToTestResult(rs));
            }
        } catch (SQLException e) {
            System.err.println("LabService getTestResults error: " + e.getMessage());
        }

        return results;
    }

    // Get all test results for a patient via medical record
    public List<TestResult> getTestResultsByRecord(String recordId) {
        List<TestResult> results = new ArrayList<>();
        String query = "SELECT * FROM test_result WHERE record_id = ? ORDER BY entered_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, recordId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToTestResult(rs));
            }
        } catch (SQLException e) {
            System.err.println("LabService getTestResultsByRecord error: " + e.getMessage());
        }

        return results;
    }

    // Map ResultSet to LabOrder
    private LabOrder mapResultSetToLabOrder(ResultSet rs) throws SQLException {
        LabOrder order = new LabOrder();
        order.setOrderId(rs.getString("order_id"));
        order.setPatientId(rs.getString("patient_id"));
        order.setDoctorId(rs.getString("doctor_id"));
        order.setTestType(rs.getString("test_type"));
        order.setStatus(rs.getString("status"));
        order.setUrgent(rs.getBoolean("is_urgent"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) order.setCreatedAt(createdAt.toLocalDateTime());

        return order;
    }

    // Map ResultSet to Sample
    private Sample mapResultSetToSample(ResultSet rs) throws SQLException {
        Sample sample = new Sample();
        sample.setSampleId(rs.getInt("sample_id"));
        sample.setOrderId(rs.getString("order_id"));
        sample.setLabStaffId(rs.getString("lab_staff_id"));
        sample.setSampleStatus(rs.getString("sample_status"));

        Timestamp collectedAt = rs.getTimestamp("collected_at");
        if (collectedAt != null) sample.setCollectedAt(collectedAt.toLocalDateTime());

        return sample;
    }

    // Map ResultSet to TestResult
    private TestResult mapResultSetToTestResult(ResultSet rs) throws SQLException {
        TestResult result = new TestResult();
        result.setResultId(rs.getInt("result_id"));
        result.setOrderId(rs.getString("order_id"));
        result.setLabStaffId(rs.getString("lab_staff_id"));
        result.setRecordId(rs.getString("record_id"));
        result.setResultValue(rs.getString("result_value"));
        result.setNotes(rs.getString("notes"));

        Timestamp enteredAt = rs.getTimestamp("entered_at");
        if (enteredAt != null) result.setEnteredAt(enteredAt.toLocalDateTime());

        return result;
    }
}