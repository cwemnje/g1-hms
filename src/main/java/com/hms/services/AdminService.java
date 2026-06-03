package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.Bed;
import com.hms.models.ResourceAllocation;
import com.hms.models.Theatre;
import com.hms.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private Connection connection;

    public AdminService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ══════════════════════════════════════════════════════
    // USER MANAGEMENT
    // ══════════════════════════════════════════════════════

    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getAllUsers error: " + e.getMessage());
        }

        return users;
    }

    // Get user by ID
    public User getUserById(String userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("AdminService getUserById error: " + e.getMessage());
        }

        return null;
    }

    // Get all users by role
    public List<User> getUsersByRole(int roleId) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role_id = ? AND is_active = TRUE";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getUsersByRole error: " + e.getMessage());
        }

        return users;
    }

    // Update user access permissions
    public boolean updateAccessPermissions(String userId, boolean access) {
        String query = "UPDATE users SET access_permissions = ? WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, access);
            stmt.setString(2, userId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Access permissions updated for: " + userId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("AdminService updateAccessPermissions error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════
    // BED MANAGEMENT
    // ══════════════════════════════════════════════════════

    // Add a bed
    public boolean addBed(Bed bed) {
        String query = "INSERT INTO bed (ward_name, bed_number, status) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bed.getWardName());
            stmt.setString(2, bed.getBedNumber());
            stmt.setString(3, "available");
            stmt.executeUpdate();

            System.out.println("Bed added: " + bed.getBedNumber());
            return true;

        } catch (SQLException e) {
            System.err.println("AdminService addBed error: " + e.getMessage());
            return false;
        }
    }

    // Get all available beds
    public List<Bed> getAvailableBeds() {
        List<Bed> beds = new ArrayList<>();
        String query = "SELECT * FROM bed WHERE status = 'available' ORDER BY ward_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                beds.add(mapResultSetToBed(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getAvailableBeds error: " + e.getMessage());
        }

        return beds;
    }

    // Get all beds
    public List<Bed> getAllBeds() {
        List<Bed> beds = new ArrayList<>();
        String query = "SELECT * FROM bed ORDER BY ward_name ASC, bed_number ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                beds.add(mapResultSetToBed(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getAllBeds error: " + e.getMessage());
        }

        return beds;
    }

    // Update bed status
    public boolean updateBedStatus(int bedId, String status) {
        String query = "UPDATE bed SET status = ? WHERE bed_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, bedId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("AdminService updateBedStatus error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════
    // THEATRE MANAGEMENT
    // ══════════════════════════════════════════════════════

    // Add a theatre
    public boolean addTheatre(Theatre theatre) {
        String query = "INSERT INTO theatre (theatre_name, status) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, theatre.getTheatreName());
            stmt.setString(2, "available");
            stmt.executeUpdate();

            System.out.println("Theatre added: " + theatre.getTheatreName());
            return true;

        } catch (SQLException e) {
            System.err.println("AdminService addTheatre error: " + e.getMessage());
            return false;
        }
    }

    // Get all available theatres
    public List<Theatre> getAvailableTheatres() {
        List<Theatre> theatres = new ArrayList<>();
        String query = "SELECT * FROM theatre WHERE status = 'available'";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                theatres.add(mapResultSetToTheatre(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getAvailableTheatres error: " + e.getMessage());
        }

        return theatres;
    }

    // Get all theatres
    public List<Theatre> getAllTheatres() {
        List<Theatre> theatres = new ArrayList<>();
        String query = "SELECT * FROM theatre ORDER BY theatre_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                theatres.add(mapResultSetToTheatre(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getAllTheatres error: " + e.getMessage());
        }

        return theatres;
    }

    // Update theatre status
    public boolean updateTheatreStatus(int theatreId, String status) {
        String query = "UPDATE theatre SET status = ? WHERE theatre_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, theatreId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("AdminService updateTheatreStatus error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════
    // RESOURCE ALLOCATION
    // ══════════════════════════════════════════════════════

    // Allocate a resource to a patient
    public boolean allocateResource(ResourceAllocation allocation) {
        String query = "INSERT INTO resource_allocation (patient_id, doctor_id, approved_by, " +
                       "bed_id, theatre_id, allocation_type, is_emergency) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, allocation.getPatientId());
            stmt.setString(2, allocation.getDoctorId());
            stmt.setString(3, allocation.getApprovedBy());
            if (allocation.getBedId() != null) {
                stmt.setInt(4, allocation.getBedId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (allocation.getTheatreId() != null) {
                stmt.setInt(5, allocation.getTheatreId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, allocation.getAllocationType());
            stmt.setBoolean(7, allocation.isEmergency());
            stmt.executeUpdate();

            // Update resource status
            if (allocation.getAllocationType().equals("bed") && allocation.getBedId() != null) {
                updateBedStatus(allocation.getBedId(), "occupied");
            } else if (allocation.getAllocationType().equals("theatre") && allocation.getTheatreId() != null) {
                updateTheatreStatus(allocation.getTheatreId(), "occupied");
            }

            System.out.println("Resource allocated to patient: " + allocation.getPatientId());
            return true;

        } catch (SQLException e) {
            System.err.println("AdminService allocateResource error: " + e.getMessage());
            return false;
        }
    }

    // Release a resource
    public boolean releaseResource(int allocationId) {
        // Get allocation details first
        String getQuery = "SELECT * FROM resource_allocation WHERE allocation_id = ?";

        try (PreparedStatement getStmt = connection.prepareStatement(getQuery)) {
            getStmt.setInt(1, allocationId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("allocation_type");
                int bedId = rs.getInt("bed_id");
                int theatreId = rs.getInt("theatre_id");

                // Update resource status back to available
                if (type.equals("bed")) {
                    updateBedStatus(bedId, "available");
                } else if (type.equals("theatre")) {
                    updateTheatreStatus(theatreId, "available");
                }

                // Update allocation released_at
                String updateQuery = "UPDATE resource_allocation SET released_at = ? " +
                                     "WHERE allocation_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                    updateStmt.setInt(2, allocationId);
                    updateStmt.executeUpdate();
                }

                System.out.println("Resource released: allocation ID " + allocationId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("AdminService releaseResource error: " + e.getMessage());
        }

        return false;
    }

    // Get all active allocations
    public List<ResourceAllocation> getActiveAllocations() {
        List<ResourceAllocation> allocations = new ArrayList<>();
        String query = "SELECT * FROM resource_allocation WHERE released_at IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                allocations.add(mapResultSetToAllocation(rs));
            }
        } catch (SQLException e) {
            System.err.println("AdminService getActiveAllocations error: " + e.getMessage());
        }

        return allocations;
    }

    // Map ResultSet to User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRoleId(rs.getInt("role_id"));
        user.setShiftSchedule(rs.getString("shift_schedule"));
        user.setAccessPermissions(rs.getBoolean("access_permissions"));
        user.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());

        return user;
    }

    // Map ResultSet to Bed
    private Bed mapResultSetToBed(ResultSet rs) throws SQLException {
        Bed bed = new Bed();
        bed.setBedId(rs.getInt("bed_id"));
        bed.setWardName(rs.getString("ward_name"));
        bed.setBedNumber(rs.getString("bed_number"));
        bed.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) bed.setCreatedAt(createdAt.toLocalDateTime());

        return bed;
    }

    // Map ResultSet to Theatre
    private Theatre mapResultSetToTheatre(ResultSet rs) throws SQLException {
        Theatre theatre = new Theatre();
        theatre.setTheatreId(rs.getInt("theatre_id"));
        theatre.setTheatreName(rs.getString("theatre_name"));
        theatre.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) theatre.setCreatedAt(createdAt.toLocalDateTime());

        return theatre;
    }

    // Map ResultSet to ResourceAllocation
    private ResourceAllocation mapResultSetToAllocation(ResultSet rs) throws SQLException {
        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAllocationId(rs.getInt("allocation_id"));
        allocation.setPatientId(rs.getString("patient_id"));
        allocation.setDoctorId(rs.getString("doctor_id"));
        allocation.setApprovedBy(rs.getString("approved_by"));

        int bedId = rs.getInt("bed_id");
        if (!rs.wasNull()) allocation.setBedId(bedId);

        int theatreId = rs.getInt("theatre_id");
        if (!rs.wasNull()) allocation.setTheatreId(theatreId);

        allocation.setAllocationType(rs.getString("allocation_type"));
        allocation.setEmergency(rs.getBoolean("is_emergency"));

        Timestamp allocatedAt = rs.getTimestamp("allocated_at");
        if (allocatedAt != null) allocation.setAllocatedAt(allocatedAt.toLocalDateTime());

        Timestamp releasedAt = rs.getTimestamp("released_at");
        if (releasedAt != null) allocation.setReleasedAt(releasedAt.toLocalDateTime());

        return allocation;
    }
}