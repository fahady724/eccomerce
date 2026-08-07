package model;

/**
 * Represents a customer account in the e-commerce system.
 */
public class Customer extends User {

    /**
     * Creates a new customer with the specified account details.
     */
    public Customer(String username, String password, String email, String phone, String address) {
        super(0, username, password, email, phone, address, "CUSTOMER");
    }

    /**
     * Creates a new customer with the specified identifier and account details.
     */
    public Customer(int id, String username, String password, String email, String phone, String address) {
        super(id, username, password, email, phone, address, "CUSTOMER");
    }

    /**
     * Returns the customer role for this user.
     */
    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}