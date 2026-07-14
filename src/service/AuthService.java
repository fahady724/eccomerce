package service;

import exception.DuplicateUsernameException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Admin;
import model.Customer;
import model.User;
import util.DBConnection;


public class AuthService {
    

    public static void register(String username, String password, String email, String phone, String address) throws DuplicateUsernameException {
        if(usernameExists(username)){
            throw new DuplicateUsernameException(username);
        }
        else{
            try {
                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO users (username, password, role, email, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, hashPassword(password));
                stmt.setString(3, "CUSTOMER");
                stmt.setString(4, email);
                stmt.setString(5,phone);
                stmt.setString(6, address);
                
                stmt.executeUpdate();
                
                stmt.close();
            } catch (SQLException e) {
                System.out.println("Register error: " + e.getMessage());
            }
        }
    }
    public static User login(String username, String password){
        
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); 
            
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String uname = rs.getString("username");
                String pass = rs.getString("password");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String role = rs.getString("role");
                stmt.close();

                if (role.equals("ADMIN")) {
                    return new Admin(id, uname, pass, email, phone, address);
                } else {
                    return new Customer(id, uname, pass, email, phone, address);
                }
            }
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }
    
    
    public static boolean  usernameExists(String username){
        
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                stmt.close();
                return true;
            }
            else{
                stmt.close();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Username error: " + e.getMessage());
            return false;
        }

    }

    public static void addAdmin(String username, String password, String email, String phone, String address) throws DuplicateUsernameException{
        if(usernameExists(username)){
            throw new DuplicateUsernameException(username);
        }
        else{
            try {
                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO users (username, password, role, email, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, hashPassword(password));
                stmt.setString(3, "ADMIN");
                stmt.setString(4, email);
                stmt.setString(5,phone);
                stmt.setString(6, address);
                
                stmt.executeUpdate();
                
                stmt.close();
            } catch (SQLException e) {
                System.out.println("Register error: " + e.getMessage());
            }
        }
    }

    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }
}
