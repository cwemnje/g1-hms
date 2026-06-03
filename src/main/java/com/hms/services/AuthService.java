package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.User;
import com.hms.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private Connection connection;

    public AuthService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Login — returns User object if successful, null if not
    public User login(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND is_active = TRUE";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Verify the password against the stored hash
                if (PasswordHasher.verifyPassword(password, storedHash)) {
                    return mapResultSetToUser(rs);
                } else {
                    System.err.println("Login failed: incorrect password.");
                    return null;
                }
            } else {
                System.err.println("Login failed: user not found.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("AuthService login error: " + e.getMessage());
            return null;
        }
    }

    // Register a new staff user
    public boolean registerUser(User user, String plainPassword) {
        // Check if email already exists
        if (emailExists(user.getEmail())) {
            System.err.println("Registration failed: email already in use.");
            return false;
        }

        // Hash the password before saving
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        if (hashedPassword == null) {
            System.err.println("Registration failed: password hashing error.");
            return false;
        }

        String query = "INSERT INTO users (user_id, first_name, last_name, email, phone, " +
                       "password_hash, role_id, shift_schedule, access_permissions, is_active) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, hashedPassword);
            stmt.setInt(7, user.getRoleId());
            stmt.setString(8, user.getShiftSchedule());
            stmt.setBoolean(9, user.isAccessPermissions());
            stmt.setBoolean(10, user.isActive());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("AuthService register error: " + e.getMessage());
            return false;
        }
    }

    // Deactivate a user account
    public boolean deactivateUser(String userId) {
        String query = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("AuthService deactivate error: " + e.getMessage());
            return false;
        }
    }

    // Reactivate a user account
    public boolean reactivateUser(String userId) {
        String query = "UPDATE users SET is_active = TRUE WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("AuthService reactivate error: " + e.getMessage());
            return false;
        }
    }

    // Change password
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        // First verify the old password
        String fetchQuery = "SELECT password_hash FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(fetchQuery)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                if (!PasswordHasher.verifyPassword(oldPassword, storedHash)) {
                    System.err.println("Password change failed: old password incorrect.");
                    return false;
                }

                // Hash and save the new password
                String newHash = PasswordHasher.hashPassword(newPassword);
                String updateQuery = "UPDATE users SET password_hash = ? WHERE user_id = ?";

                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newHash);
                    updateStmt.setString(2, userId);
                    updateStmt.executeUpdate();
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("AuthService changePassword error: " + e.getMessage());
        }

        return false;
    }

    // Check if email already exists
    private boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("AuthService emailExists error: " + e.getMessage());
        }

        return false;
    }

    // Map a ResultSet row to a User object
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
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

   public User loginById(String userId, String password) {
    String query = "SELECT * FROM users WHERE user_id = ? AND is_active = TRUE";

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password_hash");
            if (PasswordHasher.verifyPassword(password, storedHash)) {
                return mapResultSetToUser(rs);
            } else {
                System.err.println("Login failed: incorrect password.");
                return null;
            }
        } else {
            System.err.println("Login failed: user not found.");
            return null;
        }

    } catch (SQLException e) {
        System.err.println("AuthService loginById error: " + e.getMessage());
        return null;
    }
}
}