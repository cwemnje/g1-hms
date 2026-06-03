package com.hms.models;

import java.time.LocalDateTime;

public class Referral {

    private int referralId;
    private String patientId;
    private String doctorId;
    private int facilityId;
    private String referralDetails;
    private String justification;
    private String status;
    private LocalDateTime createdAt;

    public Referral() {}

    public Referral(int referralId, String patientId, String doctorId, int facilityId,
                    String referralDetails, String justification, String status,
                    LocalDateTime createdAt) {
        this.referralId = referralId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.facilityId = facilityId;
        this.referralDetails = referralDetails;
        this.justification = justification;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getReferralId() { return referralId; }
    public void setReferralId(int referralId) { this.referralId = referralId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public int getFacilityId() { return facilityId; }
    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }

    public String getReferralDetails() { return referralDetails; }
    public void setReferralDetails(String referralDetails) { this.referralDetails = referralDetails; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Referral{" +
                "referralId=" + referralId +
                ", patientId='" + patientId + '\'' +
                ", facilityId=" + facilityId +
                ", status='" + status + '\'' +
                '}';
    }
}