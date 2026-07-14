package model;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;

    public User(int id, String username, String password, String email, String phone, String address, String role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public void setPassword(String password){
        this.password = password;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setAddress(String address){
        this.address = address;
    }
    
    public int getId(){
        return id;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
    public String getPhone(){
        return phone;
    } 
    public String getAddress(){
        return address;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return "User[id=" + id + ", username=" + username + ", role=" + role + "]";
    }

}
