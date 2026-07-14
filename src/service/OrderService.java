package service;

import exception.InvalidQuantityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderItem;
import util.DBConnection;

public class OrderService {

    public static void placeOrder(int customerId, List<OrderItem> items) throws InvalidQuantityException {
        placeOrderAndReturnId(customerId, items);
    }

    public static int placeOrderAndReturnId(int customerId, List<OrderItem> items) throws InvalidQuantityException {
        // Step 1 validate stock before doing anything
        for (OrderItem item : items) {
            int available = item.getProduct().getStockQuantity();
            int requested = item.getQuantity();
            if (requested > available) {
                throw new InvalidQuantityException(requested, available);
            }
        }

        // Step 2 calculate total
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotal();
        }

        Connection conn = DBConnection.getConnection();
        try {
            // Step 3 start transaction
            conn.setAutoCommit(false);

            // Step 4 insert order
            String sql = "INSERT INTO orders (customer_id, total_amount, status, order_date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, customerId);
            stmt.setDouble(2, total);
            stmt.setString(3, "PENDING");
            stmt.setString(4, java.time.LocalDateTime.now().toString());
            stmt.executeUpdate();

            // Step 5 get generated order ID
            ResultSet keys = stmt.getGeneratedKeys();
            int orderId = keys.getInt(1);
            stmt.close();

            // Step 6 insert order items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            for (OrderItem item : items) {
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getProduct().getId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.executeUpdate();
                itemStmt.close();
            }

            // Step 7 update stock for each product
            String stockSql = "UPDATE products SET stock=? WHERE id=?";
            for (OrderItem item : items) {
                PreparedStatement stockStmt = conn.prepareStatement(stockSql);
                int newStock = item.getProduct().getStockQuantity() - item.getQuantity();
                stockStmt.setInt(1, newStock);
                stockStmt.setInt(2, item.getProduct().getId());
                stockStmt.executeUpdate();
                stockStmt.close();
            }

            // Step 8 commit everything
            conn.commit();
            conn.setAutoCommit(true);
            return orderId;

        } catch (SQLException e) {
            // Step 9 rollback if anything fails
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Rollback error: " + ex.getMessage());
            }
            System.out.println("Order error: " + e.getMessage());
            return -1;
        }
    }

    public static List<Order> getOrdersByCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int custId = rs.getInt("customer_id");
                double totalAmount = rs.getDouble("total_amount");
                String status = rs.getString("status");
                String orderDate = rs.getString("order_date");
                orders.add(new Order(id, custId, totalAmount, null, status, orderDate));
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Order retrieval error: " + e.getMessage());
        }
        return orders;
    }

    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int custId = rs.getInt("customer_id");
                double totalAmount = rs.getDouble("total_amount");
                String status = rs.getString("status");
                String orderDate = rs.getString("order_date");
                orders.add(new Order(id, custId, totalAmount, null, status, orderDate));
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Order retrieval error: " + e.getMessage());
        }
        return orders;
    }

    public static void updateOrderStatus(int orderId, String status) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE orders SET status=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Status update error: " + e.getMessage());
        }
    }


    public static List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock, p.category, " + "oi.quantity, oi.price AS ordered_price FROM order_items oi " + "JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.Product product = new model.Product(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getDouble("ordered_price"), rs.getInt("stock"), rs.getString("category"));
                items.add(new OrderItem(product, rs.getInt("quantity")));
            }
            stmt.close();
        } catch (SQLException e) { 
            System.out.println("Order item retrieval error: " + e.getMessage()); 
        }
        return items;
    }
}