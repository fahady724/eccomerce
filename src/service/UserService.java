package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Customer;
import util.DBConnection;

/** Database operations related to customer profiles and administration. */
public final class UserService {
    private UserService() {}

    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY id DESC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new Customer(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("email"), rs.getString("phone"), rs.getString("address")));
            }
        } catch (SQLException e) {
            System.out.println("Customer load error: " + e.getMessage());
        }
        return customers;
    }

    public static boolean updateProfile(int userId, String email, String phone, String address) {
        String sql = "UPDATE users SET email = ?, phone = ?, address = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Profile update error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'CUSTOMER'";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Customer delete error: " + e.getMessage());
            return false;
        }
    }
}