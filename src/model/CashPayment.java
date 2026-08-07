package model;

/**
 * Represents a cash payment for an order.
 */
public class CashPayment extends Payment {
    /**
     * Creates a cash payment for the specified amount.
     */
    public CashPayment(double amount) {
        super(amount);
    }

    /**
     * Processes the cash payment and marks it as paid.
     */
    @Override
    public boolean processPayment() {
        System.out.println("Cash payment processing...");
        setStatus("PAID");
        return true;
    }
}       