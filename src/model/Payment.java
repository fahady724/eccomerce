package model;

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

    public Payment(double amount){
        this.status = "PENDING";
        this.paymentId = java.util.UUID.randomUUID().toString();
        this.amount = amount;
    }

    public abstract boolean processPayment();

    @Override
    public String getPaymentStatus() {
        return getStatus();
    }
}
