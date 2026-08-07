package model;

/**
 * Provides a common base for payment implementations.
 */
 public abstract class Payment implements Payable {
    private String paymentId;
    private double amount;
    private String status;

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public String getPaymentId(){
        return paymentId;
    }

    public double getAmount(){
        return amount;
    }

    /**
     * Creates a payment with the specified amount and a pending status.
     */
    public Payment(double amount){
        this.status = "PENDING";
        this.paymentId = java.util.UUID.randomUUID().toString();
        this.amount = amount;
    }

    /**
     * Processes the payment according to the specific implementation.
     */
    public abstract boolean processPayment();

    @Override
    public String getPaymentStatus() {
        return getStatus();
    }
}
