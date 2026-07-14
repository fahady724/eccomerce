package service;

import exception.InvalidQuantityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.OrderItem;
import model.Product;
import util.DBConnection;

public class CartService {

    // Add item to cart — if already exists, update quantity
    public static boolean addToCart(int customerId, int productId, int quantity) throws InvalidQuantityException {
        // Check stock first
        Product product = ProductService.getProductById(productId);
        if (product == null) {
            return false;
        }
        if (quantity > product.getStockQuantity()) {
            throw new InvalidQuantityException(quantity, product.getStockQuantity());
        }

        try {
            Connection conn = DBConnection.getConnection();

            // Check if already in cart
            String checkSql = "SELECT id, quantity FROM cart WHERE customer_id = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, customerId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Already in cart — validate the combined quantity before updating
                int existingQty = rs.getInt("quantity");
                int cartId = rs.getInt("id");
                int newQuantity = existingQty + quantity;
                checkStmt.close();

                if (newQuantity > product.getStockQuantity()) {
                    throw new InvalidQuantityException(newQuantity, product.getStockQuantity());
                }

                String updateSql = "UPDATE cart SET quantity = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, cartId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Not in cart — insert new
                checkStmt.close();
                String insertSql = "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, customerId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
                insertStmt.close();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Cart error: " + e.getMessage());
            return false;
        }
    }

    /** Returns the total quantity of items currently stored in a customer's cart. */
    public static int getCartItemCount(int customerId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE customer_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            int count = rs.next() ? rs.getInt(1) : 0;
            stmt.close();
            return count;
        } catch (SQLException e) {
            System.err.println("Cart count error: " + e.getMessage());
            return 0;
        }
    }

    // Get all items in cart for a customer
    public static List<OrderItem> getCart(int customerId) {
        List<OrderItem> cartItems = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT c.quantity, p.id, p.name, p.description, " + "p.price, p.stock, p.category, p.image_path " + "FROM cart c JOIN products p ON c.product_id = p.id " + "WHERE c.customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String category = rs.getString("category");
                String imagePath = rs.getString("image_path");

                Product product = new Product(id, name, description, price, stock, category, imagePath);
                cartItems.add(new OrderItem(product, quantity));
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Cart retrieval error: " + e.getMessage());
        }
        return cartItems;
    }

    // Remove one product from cart
    public static void removeFromCart(int customerId, int productId) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM cart WHERE customer_id = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Cart removal error: " + e.getMessage());
        }
    }

    // Clear entire cart
    public static void clearCart(int customerId) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM cart WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Cart clear error: " + e.getMessage());
        }
    }

    // Checkout — convert cart to order then clear cart
    public static void checkout(int customerId) throws InvalidQuantityException {
        List<OrderItem> items = getCart(customerId);
        if (items.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        OrderService.placeOrder(customerId, items);
        clearCart(customerId);
    }

    // Get total price of cart
    public static double getCartTotal(int customerId) {
        List<OrderItem> items = getCart(customerId);
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotal();
        }
        return total;
    }
}