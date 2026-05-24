public class CardPayment implements PaymentMethod {
    private String cardNumber;
    
    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("💳 Paid $" + amount + " using card ending in " + cardNumber);
    }
}