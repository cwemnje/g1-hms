package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.Appointment;
import com.hms.utils.IDGenerator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {

    private Connection connection;

    public AppointmentService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Schedule a new appointment
    public boolean scheduleAppointment(Appointment appointment) {
        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDateTime())) {
            System.err.println("Scheduling failed: doctor not available at that time.");
            return false;
        }

        String appointmentId = IDGenerator.generateGenericId("APPT", "appointment", connection);
        if (appointmentId == null) {
            System.err.println("Scheduling failed: could not generate appointment ID.");
            return false;
        }

        appointment.setAppointmentId(appointmentId);

        String query = "INSERT INTO appointment (appointment_id, patient_id, doctor_id, " +
                       "receptionist_id, date_time, status, notes) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, appointment.getAppointmentId());
            stmt.setString(2, appointment.getPatientId());
            stmt.setString(3, appointment.getDoctorId());
            stmt.setString(4, appointment.getReceptionistId());
            stmt.setTimestamp(5, Timestamp.valueOf(appointment.getDateTime()));
            stmt.setString(6, "scheduled");
            stmt.setString(7, appointment.getNotes());
            stmt.executeUpdate();

            System.out.println("Appointment scheduled: " + appointmentId);
            return true;

        } catch (SQLException e) {
            System.err.println("AppointmentService scheduleAppointment error: " + e.getMessage());
            return false;
        }
    }

    // Check in a patient for their appointment
    public boolean checkInPatient(String appointmentId) {
        return updateStatus(appointmentId, "arrived");
    }

    // Complete an appointment
    public boolean completeAppointment(String appointmentId) {
        return updateStatus(appointmentId, "completed");
    }

    // Cancel an appointment
    public boolean cancelAppointment(String appointmentId) {
        return updateStatus(appointmentId, "cancelled");
    }

    // Reschedule an appointment
    public boolean rescheduleAppointment(String appointmentId, LocalDateTime newDateTime) {
        String doctorId = getDoctorIdForAppointment(appointmentId);
        if (doctorId == null) {
            System.err.println("Reschedule failed: appointment not found.");
            return false;
        }

        if (!isDoctorAvailable(doctorId, newDateTime)) {
            System.err.println("Reschedule failed: doctor not available at new time.");
            return false;
        }

        String query = "UPDATE appointment SET date_time = ?, status = 'scheduled' " +
                       "WHERE appointment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(newDateTime));
            stmt.setString(2, appointmentId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("AppointmentService rescheduleAppointment error: " + e.getMessage());
            return false;
        }
    }

    // Get appointment by ID
    public Appointment getAppointmentById(String appointmentId) {
        String query = "SELECT * FROM appointment WHERE appointment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
        } catch (SQLException e) {
            System.err.println("AppointmentService getAppointmentById error: " + e.getMessage());
        }

        return null;
    }

    // Get all appointments for a patient
    public List<Appointment> getAppointmentsByPatient(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointment WHERE patient_id = ? ORDER BY date_time DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("AppointmentService getAppointmentsByPatient error: " + e.getMessage());
        }

        return appointments;
    }

    // Get all appointments for a doctor
    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointment WHERE doctor_id = ? ORDER BY date_time ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("AppointmentService getAppointmentsByDoctor error: " + e.getMessage());
        }

        return appointments;
    }

    // Get all appointments for today
    public List<Appointment> getTodaysAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointment WHERE DATE(date_time) = CURRENT_DATE " +
                       "ORDER BY date_time ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("AppointmentService getTodaysAppointments error: " + e.getMessage());
        }

        return appointments;
    }

    // Check if doctor is available at a given time
    public boolean isDoctorAvailable(String doctorId, LocalDateTime dateTime) {
        String query = "SELECT COUNT(*) FROM appointment " +
                       "WHERE doctor_id = ? AND date_time = ? " +
                       "AND status NOT IN ('cancelled', 'completed')";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("AppointmentService isDoctorAvailable error: " + e.getMessage());
        }

        return false;
    }

    // Update appointment status — private helper
    private boolean updateStatus(String appointmentId, String status) {
        String query = "UPDATE appointment SET status = ? WHERE appointment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, appointmentId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Appointment " + appointmentId + " status → " + status);
                return true;
            } else {
                System.err.println("Status update failed: appointment not found.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("AppointmentService updateStatus error: " + e.getMessage());
            return false;
        }
    }

    // Get doctor ID for a given appointment — private helper
    private String getDoctorIdForAppointment(String appointmentId) {
        String query = "SELECT doctor_id FROM appointment WHERE appointment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("doctor_id");
            }
        } catch (SQLException e) {
            System.err.println("AppointmentService getDoctorId error: " + e.getMessage());
        }

        return null;
    }

    // Map ResultSet to Appointment object
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getString("appointment_id"));
        appointment.setPatientId(rs.getString("patient_id"));
        appointment.setDoctorId(rs.getString("doctor_id"));
        appointment.setReceptionistId(rs.getString("receptionist_id"));
        appointment.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        appointment.setStatus(rs.getString("status"));
        appointment.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            appointment.setCreatedAt(createdAt.toLocalDateTime());
        }

        return appointment;
    }
}