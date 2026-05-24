public class HospitalBillingSystem {
    public static void main(String[] args) {
        // 1. Create a bill for a patient
        PatientBill bill = new PatientBill("John Doe");

        // 2. Add charges
        bill.addItem(new BillItem("Consultation", 50));
        bill.addItem(new BillItem("Blood Test", 30));
        bill.addItem(new BillItem("Medicine", 25));

        // 3. Apply discount & insurance
        bill.setDiscount(10);
        bill.setInsuranceCoverage(40);

        // 4. Print bill
        bill.printBill();

        // 5. Pay using a payment method
        double amountDue = bill.getTotalAfterInsurance();
        PaymentMethod payment = new CardPayment("1234");
        payment.pay(amountDue);
    }
}