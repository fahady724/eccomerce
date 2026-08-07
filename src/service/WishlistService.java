package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.DBConnection;

/**
 * Provides database operations for each customer's saved products.
 */
public final class WishlistService {
    private WishlistService() {}

    /**
     * Adds a product to the customer's wishlist.
     */
    public static boolean add(int customerId, int productId) {
        String sql = "INSERT OR IGNORE INTO wishlist (customer_id, product_id) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Wishlist add error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a product from the customer's wishlist.
     */
    public static boolean remove(int customerId, int productId) {
        String sql = "DELETE FROM wishlist WHERE customer_id = ? AND product_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            boolean removed = stmt.executeUpdate() > 0;
            stmt.close();
            return removed;
        } catch (SQLException e) {
            System.err.println("Wishlist remove error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether a product is already saved in the customer's wishlist.
     */
    public static boolean contains(int customerId, int productId) {
        String sql = "SELECT 1 FROM wishlist WHERE customer_id = ? AND product_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            stmt.close();
            return exists;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns all products saved in the customer's wishlist.
     */
    public static List<Product> getAll(int customerId) {
        List<Product> products = new ArrayList<>();
        String sql ="""
        SELECT p.id, p.name, p.description, p.price, p.stock, p.category, p.image_path
        FROM wishlist w
        JOIN products p ON w.product_id = p.id
        WHERE w.customer_id = ?
        """;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                    rs.getDouble("price"), rs.getInt("stock"), rs.getString("category"),
                    rs.getString("image_path")
                ));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Wishlist retrieval error: " + e.getMessage());
        }
        return products;
    }
}
