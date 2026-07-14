package model;

public class MobilePayment extends Payment {
    private String mobileNumber;
    private String provider; 

    public MobilePayment(double amount, String mobileNumber, String provider) {
        super(amount);
        this.mobileNumber = mobileNumber;
        this.provider = provider;
    }

    public String getMobileNumber() { 
        return mobileNumber; 
    }

    public String getProvider() { 
        return provider; 
    }

    @Override
    public boolean processPayment() {
        if (mobileNumber == null) {
            setStatus("FAILED");
            System.out.println("Invalid mobile number!");
            return false;
        }

        // Strip +880 if present and replace with 0
        String normalizedNumber = mobileNumber;
        if (mobileNumber.startsWith("+880")) {
            normalizedNumber = "0" + mobileNumber.substring(4);
        }

        // Now check it's 11 digits starting with 01
        if (normalizedNumber.length() != 11 || !normalizedNumber.startsWith("01")) {
            setStatus("FAILED");
            System.out.println("Invalid Bangladesh mobile number!");
            return false;
        }

        // Validate provider
        if (!provider.equals("bKash") && !provider.equals("Nagad")) {
            setStatus("FAILED");
            System.out.println("Invalid provider! Use bKash or Nagad.");
            return false;
        }
        
    }
}