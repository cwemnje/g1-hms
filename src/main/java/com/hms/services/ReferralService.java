package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.ExternalFacility;
import com.hms.models.Referral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReferralService {

    private Connection connection;

    public ReferralService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ══════════════════════════════════════════════════════
    // EXTERNAL FACILITIES
    // ══════════════════════════════════════════════════════

    // Add an external facility
    public boolean addExternalFacility(ExternalFacility facility) {
        String query = "INSERT INTO external_facility (facility_name, location, contact, " +
                       "specialization, added_by) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, facility.getFacilityName());
            stmt.setString(2, facility.getLocation());
            stmt.setString(3, facility.getContact());
            stmt.setString(4, facility.getSpecialization());
            stmt.setString(5, facility.getAddedBy());
            stmt.executeUpdate();

            System.out.println("External facility added: " + facility.getFacilityName());
            return true;

        } catch (SQLException e) {
            System.err.println("ReferralService addExternalFacility error: " + e.getMessage());
            return false;
        }
    }

    // Get all external facilities
    public List<ExternalFacility> getAllFacilities() {
        List<ExternalFacility> facilities = new ArrayList<>();
        String query = "SELECT * FROM external_facility ORDER BY facility_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                facilities.add(mapResultSetToFacility(rs));
            }
        } catch (SQLException e) {
            System.err.println("ReferralService getAllFacilities error: " + e.getMessage());
        }

        return facilities;
    }

    // Get facility by ID
    public ExternalFacility getFacilityById(int facilityId) {
        String query = "SELECT * FROM external_facility WHERE facility_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, facilityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFacility(rs);
            }
        } catch (SQLException e) {
            System.err.println("ReferralService getFacilityById error: " + e.getMessage());
        }

        return null;
    }

    // Update an external facility
    public boolean updateExternalFacility(ExternalFacility facility) {
        String query = "UPDATE external_facility SET facility_name = ?, location = ?, " +
                       "contact = ?, specialization = ? WHERE facility_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, facility.getFacilityName());
            stmt.setString(2, facility.getLocation());
            stmt.setString(3, facility.getContact());
            stmt.setString(4, facility.getSpecialization());
            stmt.setInt(5, facility.getFacilityId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("External facility updated: " + facility.getFacilityId());
                return true;
            } else {
                System.err.println("Update failed: facility not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("ReferralService updateExternalFacility error: " + e.getMessage());
            return false;
        }
    }

    // Delete an external facility
    public boolean deleteExternalFacility(int facilityId) {
        String query = "DELETE FROM external_facility WHERE facility_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, facilityId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("ReferralService deleteExternalFacility error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════
    // REFERRALS
    // ══════════════════════════════════════════════════════

    // Create a referral
    public boolean createReferral(Referral referral) {
        String query = "INSERT INTO referral (patient_id, doctor_id, facility_id, " +
                       "referral_details, justification, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, referral.getPatientId());
            stmt.setString(2, referral.getDoctorId());
            stmt.setInt(3, referral.getFacilityId());
            stmt.setString(4, referral.getReferralDetails());
            stmt.setString(5, referral.getJustification());
            stmt.setString(6, "pending");
            stmt.executeUpdate();

            System.out.println("Referral created for patient: " + referral.getPatientId());
            return true;

        } catch (SQLException e) {
            System.err.println("ReferralService createReferral error: " + e.getMessage());
            return false;
        }
    }

    // Get referral by ID
    public Referral getReferralById(int referralId) {
        String query = "SELECT * FROM referral WHERE referral_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, referralId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToReferral(rs);
            }
        } catch (SQLException e) {
            System.err.println("ReferralService getReferralById error: " + e.getMessage());
        }

        return null;
    }

    // Get all referrals for a patient
    public List<Referral> getReferralsByPatient(String patientId) {
        List<Referral> referrals = new ArrayList<>();
        String query = "SELECT * FROM referral WHERE patient_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                referrals.add(mapResultSetToReferral(rs));
            }
        } catch (SQLException e) {
            System.err.println("ReferralService getReferralsByPatient error: " + e.getMessage());
        }

        return referrals;
    }

    // Get all referrals by a doctor
    public List<Referral> getReferralsByDoctor(String doctorId) {
        List<Referral> referrals = new ArrayList<>();
        String query = "SELECT * FROM referral WHERE doctor_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                referrals.add(mapResultSetToReferral(rs));
            }
        } catch (SQLException e) {
            System.err.println("ReferralService getReferralsByDoctor error: " + e.getMessage());
        }

        return referrals;
    }

    // Get all pending referrals
    public List<Referral> getPendingReferrals() {
        List<Referral> referrals = new ArrayList<>();
        String query = "SELECT * FROM referral WHERE status = 'pending' ORDER BY created_at ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                referrals.add(mapResultSetToReferral(rs));
            }
        } catch (SQLException e) {
            System.err.println("ReferralService getPendingReferrals error: " + e.getMessage());
        }

        return referrals;
    }

    // Update referral status
    public boolean updateReferralStatus(int referralId, String status) {
        String query = "UPDATE referral SET status = ? WHERE referral_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, referralId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Referral " + referralId + " status → " + status);
                return true;
            } else {
                System.err.println("Status update failed: referral not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("ReferralService updateReferralStatus error: " + e.getMessage());
            return false;
        }
    }

    // Map ResultSet to ExternalFacility
    private ExternalFacility mapResultSetToFacility(ResultSet rs) throws SQLException {
        ExternalFacility facility = new ExternalFacility();
        facility.setFacilityId(rs.getInt("facility_id"));
        facility.setFacilityName(rs.getString("facility_name"));
        facility.setLocation(rs.getString("location"));
        facility.setContact(rs.getString("contact"));
        facility.setSpecialization(rs.getString("specialization"));
        facility.setAddedBy(rs.getString("added_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) facility.setCreatedAt(createdAt.toLocalDateTime());

        return facility;
    }

    // Map ResultSet to Referral
    private Referral mapResultSetToReferral(ResultSet rs) throws SQLException {
        Referral referral = new Referral();
        referral.setReferralId(rs.getInt("referral_id"));
        referral.setPatientId(rs.getString("patient_id"));
        referral.setDoctorId(rs.getString("doctor_id"));
        referral.setFacilityId(rs.getInt("facility_id"));
        referral.setReferralDetails(rs.getString("referral_details"));
        referral.setJustification(rs.getString("justification"));
        referral.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) referral.setCreatedAt(createdAt.toLocalDateTime());

        return referral;
    }
}