package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {
    private String patientId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String insuranceDetails;
    private BigDecimal outstandingBillAmount;
    private boolean portalAccess;
    private String portalCredentials;
    private LocalDateTime createdAt;

    // Default constructor
    public Patient() {}

    // Parameterized constructor
    public Patient(String patientId, String firstName, String lastName, LocalDate dateOfBirth, String gender,
                   String address, String phone, String email, String insuranceDetails, BigDecimal outstandingBillAmount,
                   boolean portalAccess, String portalCredentials, LocalDateTime createdAt) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.insuranceDetails = insuranceDetails;
        this.outstandingBillAmount = outstandingBillAmount;
        this.portalAccess = portalAccess;
        this.portalCredentials = portalCredentials;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getInsuranceDetails() { return insuranceDetails; }
    public void setInsuranceDetails(String insuranceDetails) { this.insuranceDetails = insuranceDetails; }

    public BigDecimal getOutstandingBillAmount() { return outstandingBillAmount; }
    public void setOutstandingBillAmount(BigDecimal outstandingBillAmount) { this.outstandingBillAmount = outstandingBillAmount; }

    public boolean isPortalAccess() { return portalAccess; }
    public void setPortalAccess(boolean portalAccess) { this.portalAccess = portalAccess; }

    public String getPortalCredentials() { return portalCredentials; }
    public void setPortalCredentials(String portalCredentials) { this.portalCredentials = portalCredentials; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
// toString - useful for debugging
    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth + '\'' +
                ", gender ='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", insuranceDetails='" + insuranceDetails + '\'' +  
                ", outstandingBillAmount='" + outstandingBillAmount + '\'' +
                ", portalAccess='" + portalAccess + '\'' +
                ", portalCredentials='" + portalCredentials + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

