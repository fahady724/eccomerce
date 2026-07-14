package model;

public class Admin extends User {


    public Admin(String username, String password, String email, String phone, String address) {
        super(0, username, password, email, phone, address, "ADMIN");
    }


    public Admin(int id, String username, String password, String email, String phone, String address) {
        super(id, username, password, email, phone, address, "ADMIN");
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}