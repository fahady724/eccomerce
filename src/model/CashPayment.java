package model;

public class CashPayment extends Payment {
    public CashPayment(double amount) {
        super(amount);
    }

    @Override
    public boolean processPayment() {
        System.out.println("Cash payment processing...");
        setStatus("PAID");
        return true;
    }
}       