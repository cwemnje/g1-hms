package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.DoctorSchedule;
import com.hms.models.NurseSchedule;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleService {

    private Connection connection;

    public ScheduleService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ══════════════════════════════════════════════════════
    // DOCTOR SCHEDULES
    // ══════════════════════════════════════════════════════

    // Create a doctor schedule
    public boolean createDoctorSchedule(DoctorSchedule schedule) {
        if (doctorScheduleConflict(schedule.getDoctorId(), schedule.getShiftDate(),
        schedule.getShiftStart().toString() + ":00", schedule.getShiftEnd().toString() + ":00")) {
            System.err.println("Schedule failed: doctor has a conflicting schedule.");
            return false;
        }

        String query = "INSERT INTO doctor_schedule (doctor_id, shift_date, shift_start, " +
                       "shift_end, duty_type, created_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, schedule.getDoctorId());
            stmt.setDate(2, Date.valueOf(schedule.getShiftDate()));
            stmt.setTime(3, Time.valueOf(schedule.getShiftStart()));
            stmt.setTime(4, Time.valueOf(schedule.getShiftEnd()));
            stmt.setString(5, schedule.getDutyType());
            stmt.setString(6, schedule.getCreatedBy());
            stmt.executeUpdate();

            System.out.println("Doctor schedule created for: " + schedule.getDoctorId());
            return true;

        } catch (SQLException e) {
            System.err.println("ScheduleService createDoctorSchedule error: " + e.getMessage());
            return false;
        }
    }

    // Get all schedules for a doctor
    public List<DoctorSchedule> getDoctorSchedules(String doctorId) {
        List<DoctorSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM doctor_schedule WHERE doctor_id = ? ORDER BY shift_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                schedules.add(mapResultSetToDoctorSchedule(rs));
            }
        } catch (SQLException e) {
            System.err.println("ScheduleService getDoctorSchedules error: " + e.getMessage());
        }

        return schedules;
    }

    // Get doctor schedules for a specific date
    public List<DoctorSchedule> getDoctorSchedulesByDate(LocalDate date) {
        List<DoctorSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM doctor_schedule WHERE shift_date = ? ORDER BY shift_start ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                schedules.add(mapResultSetToDoctorSchedule(rs));
            }
        } catch (SQLException e) {
            System.err.println("ScheduleService getDoctorSchedulesByDate error: " + e.getMessage());
        }

        return schedules;
    }

    // Update a doctor schedule
    public boolean updateDoctorSchedule(DoctorSchedule schedule) {
        String query = "UPDATE doctor_schedule SET shift_date = ?, shift_start = ?, " +
                       "shift_end = ?, duty_type = ? WHERE schedule_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(schedule.getShiftDate()));
            stmt.setTime(2, Time.valueOf(schedule.getShiftStart()));
            stmt.setTime(3, Time.valueOf(schedule.getShiftEnd()));
            stmt.setString(4, schedule.getDutyType());
            stmt.setInt(5, schedule.getScheduleId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Doctor schedule updated: " + schedule.getScheduleId());
                return true;
            } else {
                System.err.println("Update failed: doctor schedule not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("ScheduleService updateDoctorSchedule error: " + e.getMessage());
            return false;
        }
    }

    // Delete a doctor schedule
    public boolean deleteDoctorSchedule(int scheduleId) {
        String query = "DELETE FROM doctor_schedule WHERE schedule_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("ScheduleService deleteDoctorSchedule error: " + e.getMessage());
            return false;
        }
    }

    // Check for doctor schedule conflicts
    public boolean doctorScheduleConflict(String doctorId, LocalDate date,
                                           String shiftStart, String shiftEnd) {
        String query = "SELECT COUNT(*) FROM doctor_schedule WHERE doctor_id = ? " +
                       "AND shift_date = ? AND NOT (shift_end <= ? OR shift_start >= ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(shiftStart));
            stmt.setTime(4, Time.valueOf(shiftEnd));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("ScheduleService doctorScheduleConflict error: " + e.getMessage());
        }

        return false;
    }

    // ══════════════════════════════════════════════════════
    // NURSE SCHEDULES
    // ══════════════════════════════════════════════════════

    // Create a nurse schedule
    public boolean createNurseSchedule(NurseSchedule schedule) {
if (nurseScheduleConflict(schedule.getNurseId(), schedule.getShiftDate(),
        schedule.getShiftStart().toString() + ":00", schedule.getShiftEnd().toString() + ":00")) {
            System.err.println("Schedule failed: nurse has a conflicting schedule.");
            return false;
        }

        String query = "INSERT INTO nurse_schedule (nurse_id, shift_date, shift_start, " +
                       "shift_end, ward_assigned, created_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, schedule.getNurseId());
            stmt.setDate(2, Date.valueOf(schedule.getShiftDate()));
            stmt.setTime(3, Time.valueOf(schedule.getShiftStart()));
            stmt.setTime(4, Time.valueOf(schedule.getShiftEnd()));
            stmt.setString(5, schedule.getWardAssigned());
            stmt.setString(6, schedule.getCreatedBy());
            stmt.executeUpdate();

            System.out.println("Nurse schedule created for: " + schedule.getNurseId());
            return true;

        } catch (SQLException e) {
            System.err.println("ScheduleService createNurseSchedule error: " + e.getMessage());
            return false;
        }
    }

    // Get all schedules for a nurse
    public List<NurseSchedule> getNurseSchedules(String nurseId) {
        List<NurseSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM nurse_schedule WHERE nurse_id = ? ORDER BY shift_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nurseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                schedules.add(mapResultSetToNurseSchedule(rs));
            }
        } catch (SQLException e) {
            System.err.println("ScheduleService getNurseSchedules error: " + e.getMessage());
        }

        return schedules;
    }

    // Get nurse schedules for a specific date
    public List<NurseSchedule> getNurseSchedulesByDate(LocalDate date) {
        List<NurseSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM nurse_schedule WHERE shift_date = ? ORDER BY shift_start ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                schedules.add(mapResultSetToNurseSchedule(rs));
            }
        } catch (SQLException e) {
            System.err.println("ScheduleService getNurseSchedulesByDate error: " + e.getMessage());
        }

        return schedules;
    }

    // Update a nurse schedule
    public boolean updateNurseSchedule(NurseSchedule schedule) {
        String query = "UPDATE nurse_schedule SET shift_date = ?, shift_start = ?, " +
                       "shift_end = ?, ward_assigned = ? WHERE schedule_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(schedule.getShiftDate()));
            stmt.setTime(2, Time.valueOf(schedule.getShiftStart()));
            stmt.setTime(3, Time.valueOf(schedule.getShiftEnd()));
            stmt.setString(4, schedule.getWardAssigned());
            stmt.setInt(5, schedule.getScheduleId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Nurse schedule updated: " + schedule.getScheduleId());
                return true;
            } else {
                System.err.println("Update failed: nurse schedule not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("ScheduleService updateNurseSchedule error: " + e.getMessage());
            return false;
        }
    }

    // Delete a nurse schedule
    public boolean deleteNurseSchedule(int scheduleId) {
        String query = "DELETE FROM nurse_schedule WHERE schedule_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("ScheduleService deleteNurseSchedule error: " + e.getMessage());
            return false;
        }
    }

    // Check for nurse schedule conflicts
    public boolean nurseScheduleConflict(String nurseId, LocalDate date,
                                          String shiftStart, String shiftEnd) {
        String query = "SELECT COUNT(*) FROM nurse_schedule WHERE nurse_id = ? " +
                       "AND shift_date = ? AND NOT (shift_end <= ? OR shift_start >= ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nurseId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(shiftStart));
            stmt.setTime(4, Time.valueOf(shiftEnd));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("ScheduleService nurseScheduleConflict error: " + e.getMessage());
        }

        return false;
    }

    // Map ResultSet to DoctorSchedule
    private DoctorSchedule mapResultSetToDoctorSchedule(ResultSet rs) throws SQLException {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setDoctorId(rs.getString("doctor_id"));
        schedule.setShiftDate(rs.getDate("shift_date").toLocalDate());
        schedule.setShiftStart(rs.getTime("shift_start").toLocalTime());
        schedule.setShiftEnd(rs.getTime("shift_end").toLocalTime());
        schedule.setDutyType(rs.getString("duty_type"));
        schedule.setCreatedBy(rs.getString("created_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) schedule.setCreatedAt(createdAt.toLocalDateTime());

        return schedule;
    }

    // Map ResultSet to NurseSchedule
    private NurseSchedule mapResultSetToNurseSchedule(ResultSet rs) throws SQLException {
        NurseSchedule schedule = new NurseSchedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setNurseId(rs.getString("nurse_id"));
        schedule.setShiftDate(rs.getDate("shift_date").toLocalDate());
        schedule.setShiftStart(rs.getTime("shift_start").toLocalTime());
        schedule.setShiftEnd(rs.getTime("shift_end").toLocalTime());
        schedule.setWardAssigned(rs.getString("ward_assigned"));
        schedule.setCreatedBy(rs.getString("created_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) schedule.setCreatedAt(createdAt.toLocalDateTime());

        return schedule;
    }
}