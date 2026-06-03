package com.hms.models;

import java.time.LocalDateTime;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passwordHash;
    private int roleId;
    private String shiftSchedule;
    private boolean accessPermissions;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(String userId, String firstName, String lastName, String email, String phone,
                String passwordHash, int roleId, String shiftSchedule, boolean accessPermissions, boolean isActive,
                LocalDateTime createdAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.shiftSchedule = shiftSchedule;
        this.roleId = roleId;
        this.accessPermissions = accessPermissions;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getShiftSchedule() { return shiftSchedule; }
    public void setShiftSchedule(String shiftSchedule) { this.shiftSchedule = shiftSchedule; }

    public boolean isAccessPermissions() { return accessPermissions; }
    public void setAccessPermissions(boolean accessPermissions) { this.accessPermissions = accessPermissions; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // toString
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roleId=" + roleId +
                ", shiftSchedule='" + shiftSchedule + '\'' +
                ", accessPermissions=" + accessPermissions +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
