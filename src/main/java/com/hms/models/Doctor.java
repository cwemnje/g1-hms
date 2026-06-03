package com.hms.models;

public class Doctor {
    private String doctorId;
    private String specialization;
    private boolean personalAvailability;

    // Default constructor
    public Doctor() {}

    // Parameterized constructor
    public Doctor(String doctorId, String specialization, boolean personalAvailability) {
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.personalAvailability = personalAvailability;
    }

    // Getters and Setters
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public boolean isPersonalAvailability() { return personalAvailability; }
    public void setPersonalAvailability(boolean personalAvailability) { this.personalAvailability = personalAvailability; }

    // toString
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId='" + doctorId + '\'' +
                ", specialization='" + specialization + '\'' +
                ", personalAvailability=" + personalAvailability +
                '}';
    }
}