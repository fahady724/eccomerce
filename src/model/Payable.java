package model;

/**
 * Defines the contract for payment processing operations.
 */
public interface Payable {
    /**
     * Processes a payment and returns whether it succeeded.
     */
    boolean processPayment();

    /**
     * Returns the current payment status.
     */
    String getPaymentStatus();
}