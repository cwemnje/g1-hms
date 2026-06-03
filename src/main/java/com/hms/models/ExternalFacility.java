package com.hms.models;

import java.time.LocalDateTime;

public class ExternalFacility {

    private int facilityId;
    private String facilityName;
    private String location;
    private String contact;
    private String specialization;
    private String addedBy;
    private LocalDateTime createdAt;

    public ExternalFacility() {}

    public ExternalFacility(int facilityId, String facilityName, String location,
                            String contact, String specialization, String addedBy,
                            LocalDateTime createdAt) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.location = location;
        this.contact = contact;
        this.specialization = specialization;
        this.addedBy = addedBy;
        this.createdAt = createdAt;
    }

    public int getFacilityId() { return facilityId; }
    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ExternalFacility{" +
                "facilityId=" + facilityId +
                ", facilityName='" + facilityName + '\'' +
                ", location='" + location + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}