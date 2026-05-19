public class MobileMoneyPayment implements PaymentMethod {
    private String phoneNumber;
    
    public MobileMoneyPayment(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("📱 Paid $" + amount + " via mobile money to " + phoneNumber);
    }
}