package model;

public class Customer extends User {


    public Customer(String username, String password, String email, String phone, String address) {
        super(0, username, password, email, phone, address, "CUSTOMER");
    }


    public Customer(int id, String username, String password, String email, String phone, String address) {
        super(id, username, password, email, phone, address, "CUSTOMER");
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}