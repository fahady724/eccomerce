package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import util.DBConnection;

/** Stores payment results linked to an order. */
public final class PaymentService {
    private PaymentService() {}

    public static boolean savePayment(int orderId, double amount, String method, String status) {
        String sql = "INSERT INTO payments (order_id, amount, method, status, payment_date) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            statement.setDouble(2, amount);
            statement.setString(3, method);
            statement.setString(4, status);
            statement.setString(5, LocalDateTime.now().toString());
            int rows = statement.executeUpdate();
            statement.close();
            return rows == 1;
        } catch (SQLException exception) {
            System.err.println("Payment save error: " + exception.getMessage());
            return false;
        }
    }
}
