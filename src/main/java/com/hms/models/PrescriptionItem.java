package com.hms.models;

public class PrescriptionItem {

    private int itemId;
    private String prescriptionId;
    private int medicationId;
    private String dosage;
    private String instructions;
    private int quantity;

    public PrescriptionItem() {}

    public PrescriptionItem(int itemId, String prescriptionId, int medicationId,
                            String dosage, String instructions, int quantity) {
        this.itemId = itemId;
        this.prescriptionId = prescriptionId;
        this.medicationId = medicationId;
        this.dosage = dosage;
        this.instructions = instructions;
        this.quantity = quantity;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public int getMedicationId() { return medicationId; }
    public void setMedicationId(int medicationId) { this.medicationId = medicationId; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "PrescriptionItem{" +
                "itemId=" + itemId +
                ", prescriptionId='" + prescriptionId + '\'' +
                ", medicationId=" + medicationId +
                ", dosage='" + dosage + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}