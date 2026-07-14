package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.DBConnection;

public class ProductService {
    public static void addProduct(String name, String description, double price, int stock, String category) {
        addProduct(name, description, price, stock, category, null);
    }

    public static void addProduct(String name, String description, double price, int stock, String category, String imagePath) {
        String sql = "INSERT INTO products (name, description, price, stock, category, image_path) " + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setInt(4, stock);
            stmt.setString(5, category);
            stmt.setString(6, imagePath);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Product insert error: " + e.getMessage());
        }
    }

    public static List<Product> getAllProducts() {
        return findProducts("SELECT * FROM products ORDER BY id DESC", null, null);
    }

    public static List<Product> searchProduct(String name) {
        return findProducts("SELECT * FROM products WHERE name LIKE ? ORDER BY id DESC", "%" + name + "%", null);
    }

    public static List<Product> searchProduct(String name, String category) {
        return findProducts(
                "SELECT * FROM products WHERE name LIKE ? AND category LIKE ? ORDER BY id DESC", "%" + name + "%", "%" + category + "%");
    }

    private static List<Product> findProducts(String sql, String first, String second) {
        List<Product> products = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (first != null) stmt.setString(1, first);
            if (second != null) stmt.setString(2, second);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) products.add(mapProduct(rs));
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Product finding error: " + e.getMessage());
        }
        return products;
    }

    public static void updateProduct(int id, String name, String description, double price, int stock, String category) {
        updateProduct(id, name, description, price, stock, category, null);
    }

    public static void updateProduct(int id, String name, String description, double price, int stock, String category, String imagePath) {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock=?, category=?, "
                + "image_path=? WHERE id=?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setInt(4, stock);
            stmt.setString(5, category);
            stmt.setString(6, imagePath);
            stmt.setInt(7, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    public static void deleteProduct(int id) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    public static Product getProductById(int id) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            Product product = rs.next() ? mapProduct(rs) : null;
            stmt.close();
            return product;
        } catch (SQLException e) {
            System.out.println("Product retrieval error: " + e.getMessage());
            return null;
        }
    }

    private static Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                rs.getDouble("price"), rs.getInt("stock"), rs.getString("category"),
                rs.getString("image_path"));
    }
}
