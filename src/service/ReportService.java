package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.DBConnection;

public class ReportService {

    public static double getTotalSales() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT SUM(total_amount) FROM orders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Report error: " + e.getMessage());
        }
        return 0.0;
    }

    public static int getTotalOrders() {
         try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM orders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Report error: " + e.getMessage());
        }
        return 0;
    }

    public static int getTotalCustomers() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
           
            int result = 0;

            if (rs.next()) {
                result = rs.getInt(1);
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Report error: " + e.getMessage());
        }
        return 0;
    }

    public static List<Product> getLowStockProducts(){
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products WHERE stock < 10";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String category = rs.getString("category");
                products.add(new Product(id, name, description, price, stock, category));
            }
            stmt.close();
            return products;
        } catch (SQLException e) {
            System.out.println("Report error: " + e.getMessage());
        }
        return new ArrayList<>();
    }
    public static List<Product> getTopProducts(){
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT p.id, p.name, p.description, p.price, p.stock, p.category, SUM(oi.quantity) as total_ordered " + "FROM order_items oi JOIN products p ON oi.product_id = p.id " + "GROUP BY oi.product_id ORDER BY total_ordered DESC LIMIT 5";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String category = rs.getString("category");
                products.add(new Product(id, name, description, price, stock, category));
            }
            stmt.close();
            return products;
        } catch (SQLException e) {
             System.out.println("Report error: " + e.getMessage());
        }
        return new ArrayList<>();
    }


    public static List<Object[]> getSalesByMonth() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT substr(order_date,1,7) AS month, COUNT(*) AS orders, " + "COALESCE(SUM(total_amount),0) AS revenue FROM orders " + "GROUP BY substr(order_date,1,7) ORDER BY month DESC LIMIT 12";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rows.add(new Object[]{rs.getString("month"), rs.getInt("orders"), rs.getDouble("revenue")});
            stmt.close();
        } catch (SQLException e) { System.out.println("Monthly report error: " + e.getMessage()); }
        return rows;
    }

    public static List<Object[]> getSalesByCategory() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT p.category, COALESCE(SUM(oi.quantity),0) AS units, " + "COALESCE(SUM(oi.quantity * oi.price),0) AS revenue " + "FROM order_items oi JOIN products p ON p.id=oi.product_id " + "GROUP BY p.category ORDER BY revenue DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rows.add(new Object[]{rs.getString("category"), rs.getInt("units"), rs.getDouble("revenue")});
            stmt.close();
        } catch (SQLException e) { 
            System.out.println("Category report error: " + e.getMessage()); 
        }
        return rows;
    }

    public static List<Object[]> getOrderStatusSummary() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) AS total FROM orders GROUP BY status ORDER BY total DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rows.add(new Object[]{rs.getString("status"), rs.getInt("total")});
            stmt.close();
        } catch (SQLException e) { 
            System.out.println("Status report error: " + e.getMessage()); 
        }
        return rows;
    }
}