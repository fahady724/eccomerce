package model;

public class CreditCardPay extends Payment{
    private String cardNumber;
    private String cardHolderName;
    private String cvv;
    private String expiry;

    public String getCardNumber(){
        return cardNumber;
    }
    public String getCardHolderName(){
        return cardHolderName;
    }
    public String getCvv(){
        return cvv;
    }
    public String getExpiry(){
        return expiry;
    }

    public CreditCardPay(double amount, String cardNumber, String cardHolderName, String cvv, String expiry){
        super(amount);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.cvv = cvv;
        this.expiry = expiry;
    }


    @Override
    public boolean processPayment() {
        // Validate card number — must be 16 digits
        if (cardNumber == null || cardNumber.length() != 16) {
            setStatus("FAILED");
            System.out.println("Invalid card number!");
            return false;
        }

         if (cvv == null || cvv.length() != 3) {
            setStatus("FAILED");
            System.out.println("Invalid CVV!");
            return false;
        }            
        
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            setStatus("FAILED");
            System.out.println("Invalid expiry date! Use MM/YY format.");
            return false;
        }

        if (Math.random() < 0.9) {
            setStatus("PAID");
            System.out.println("Credit card payment successful!");
            return true;
        } else {
            setStatus("FAILED");
            System.out.println("Card declined!");
            return false;
        }
    }

}
