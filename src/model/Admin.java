package model;

/**
 * Represents an administrator account in the e-commerce system.
 */
public class Admin extends User {

    /**
     * Creates a new administrator with the specified account details.
     */
    public Admin(String username, String password, String email, String phone, String address) {
        super(0, username, password, email, phone, address, "ADMIN");
    }

    /**
     * Creates a new administrator with the specified identifier and account details.
     */
    public Admin(int id, String username, String password, String email, String phone, String address) {
        super(id, username, password, email, phone, address, "ADMIN");
    }

    /**
     * Returns the administrator role for this user.
     */
    @Override
    public String getRole() {
        return "ADMIN";
    }
}