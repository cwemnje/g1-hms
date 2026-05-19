import java.util.ArrayList;
import java.util.List;

public class PatientBill {
    private String patientName;
    private List<BillItem> items = new ArrayList<>();
    private double taxRate = 0.05; // 5% tax
    private double discount = 0;
    private double insuranceCoverage = 0;

    public PatientBill(String patientName) {
        this.patientName = patientName;
    }

    public void addItem(BillItem item) {
        items.add(item);
    }

    public double getSubtotal() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getAmount();
        }
        return total;
    }

    public double getTax() {
        return getSubtotal() * taxRate;
    }

    public double getTotalBeforeInsurance() {
        return getSubtotal() + getTax() - discount;
    }

    public double getTotalAfterInsurance() {
        double total = getTotalBeforeInsurance() - insuranceCoverage;
        return total < 0 ? 0 : total;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setInsuranceCoverage(double coverage) {
        this.insuranceCoverage = coverage;
    }

    public void printBill() {
        System.out.println("\n=== HOSPITAL BILL ===");
        System.out.println("Patient: " + patientName);
        System.out.println("---------------------");
        for (BillItem item : items) {
            System.out.println(item);
        }
        System.out.println("---------------------");
        System.out.println("Subtotal: $" + getSubtotal());
        System.out.println("Tax: $" + getTax());
        System.out.println("Discount: $" + discount);
        System.out.println("Insurance: $" + insuranceCoverage);
        System.out.println("Total Due: $" + getTotalAfterInsurance());
    }
}