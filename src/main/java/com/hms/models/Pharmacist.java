package com.hms.models;

public class Pharmacist {
    private String pharmacistId;
    private String inventoryLevelAccess;

    // Default constructor
    public Pharmacist() {}

    // Parameterized constructor
    public Pharmacist(String pharmacistId, String inventoryLevelAccess) {
        this.pharmacistId = pharmacistId;
        this.inventoryLevelAccess = inventoryLevelAccess;
    }

    // Getters and Setters
    public String getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(String pharmacistId) { this.pharmacistId = pharmacistId; }  
    public String getInventoryLevelAccess() { return inventoryLevelAccess; }
    public void setInventoryLevelAccess(String inventoryLevelAccess) { this.inventoryLevelAccess = inventoryLevelAccess; }
    
    // toString
    @Override
    public String toString() {
        return "Pharmacist{" +
                "pharmacistId='" + pharmacistId + '\'' +
                ", inventoryLevelAccess='" + inventoryLevelAccess + '\'' +
                '}';
    }
}
