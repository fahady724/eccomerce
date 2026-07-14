package exception;

public class PaymentFailedException extends Exception {
    private String paymentMethod;
    private String reason;

    public PaymentFailedException(String paymentMethod, String reason) {
        super("Payment failed via " + paymentMethod + ". Reason: " + reason);
        this.paymentMethod = paymentMethod;
        this.reason = reason;
    }

    public String getPaymentMethod() { 
        return paymentMethod; 
    }
    public String getReason() { 
        return reason; 
    }
}