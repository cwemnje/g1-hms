package com.hms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Vitals {

    private int vitalsId;
    private String recordId;
    private String nurseId;
    private String bloodPressure;
    private BigDecimal temperature;
    private int pulseRate;
    private int respiratoryRate;
    private BigDecimal weight;
    private BigDecimal height;
    private LocalDateTime recordedAt;

    public Vitals() {}

    public Vitals(int vitalsId, String recordId, String nurseId, String bloodPressure,
                  BigDecimal temperature, int pulseRate, int respiratoryRate,
                  BigDecimal weight, BigDecimal height, LocalDateTime recordedAt) {
        this.vitalsId = vitalsId;
        this.recordId = recordId;
        this.nurseId = nurseId;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.pulseRate = pulseRate;
        this.respiratoryRate = respiratoryRate;
        this.weight = weight;
        this.height = height;
        this.recordedAt = recordedAt;
    }

    public int getVitalsId() { return vitalsId; }
    public void setVitalsId(int vitalsId) { this.vitalsId = vitalsId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getNurseId() { return nurseId; }
    public void setNurseId(String nurseId) { this.nurseId = nurseId; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public int getPulseRate() { return pulseRate; }
    public void setPulseRate(int pulseRate) { this.pulseRate = pulseRate; }

    public int getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(int respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    @Override
    public String toString() {
        return "Vitals{" +
                "vitalsId=" + vitalsId +
                ", recordId='" + recordId + '\'' +
                ", nurseId='" + nurseId + '\'' +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", temperature=" + temperature +
                ", pulseRate=" + pulseRate +
                '}';
    }
}