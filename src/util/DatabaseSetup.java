package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createTables() {
        Connection conn = DBConnection.getConnection();
        try {
            Statement stmt = conn.createStatement();

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users 
                (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT NOT NULL, email TEXT, phone TEXT, address TEXT)
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products 
                (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT, price REAL NOT NULL, stock INTEGER NOT NULL, category TEXT, image_path TEXT)
            """);

            try {
                stmt.execute("ALTER TABLE products ADD COLUMN image_path TEXT");
            } catch (SQLException ignored) {
                
            }

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL, total_amount REAL,status TEXT, order_date TEXT, FOREIGN KEY (customer_id) REFERENCES users(id))
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER, product_id INTEGER, quantity INTEGER, price REAL, FOREIGN KEY (order_id) REFERENCES orders(id), FOREIGN KEY (product_id) REFERENCES products(id))
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cart (id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER, product_id INTEGER, quantity INTEGER, FOREIGN KEY (customer_id) REFERENCES users(id), FOREIGN KEY (product_id) REFERENCES products(id))
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payments (id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER, amount REAL, method TEXT, status TEXT, payment_date TEXT, FOREIGN KEY (order_id) REFERENCES orders(id))
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS wishlist (id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL, product_id INTEGER NOT NULL, UNIQUE(customer_id, product_id), FOREIGN KEY (customer_id) REFERENCES users(id), FOREIGN KEY (product_id) REFERENCES products(id))
            """);

            System.out.println("All tables created successfully!");
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}