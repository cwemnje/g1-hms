package com.hms.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IDGenerator {

    // Private constructor — utility class, no instantiation needed
    private IDGenerator() {}

    // Generate ID for staff users
    public static String generateUserId(String role, Connection connection) {
        String prefix = switch (role.toLowerCase()) {
            case "admin"          -> "HMSA";
            case "doctor"         -> "HMSD";
            case "nurse"          -> "HMSN";
            case "receptionist"   -> "HMSR";
            case "pharmacist"     -> "HMSP";
            case "lab_technician" -> "HMSL";
            default               -> "HMSX";
        };

        String query = "SELECT COUNT(*) FROM users " +
                       "WHERE role_id = (SELECT role_id FROM roles WHERE role_name = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, role.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                int nextNumber = count + 1;
                return prefix + String.format("%03d", nextNumber);
            }
        } catch (SQLException e) {
            System.err.println("IDGenerator error (user): " + e.getMessage());
        }

        return null;
    }

    // Generate ID for patients
    public static String generatePatientId(Connection connection) {
        String query = "SELECT COUNT(*) FROM patient";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                int nextNumber = count + 1;
                return "PTNT" + String.format("%03d", nextNumber);
            }
        } catch (SQLException e) {
            System.err.println("IDGenerator error (patient): " + e.getMessage());
        }

        return null;
    }

    // Generic ID generator for other entities
    public static String generateGenericId(String prefix, String tableName, Connection connection) {
        String query = "SELECT COUNT(*) FROM " + tableName;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                int nextNumber = count + 1;
                return prefix + String.format("%03d", nextNumber);
            }
        } catch (SQLException e) {
            System.err.println("IDGenerator error (" + tableName + "): " + e.getMessage());
        }

        return null;
    }
}