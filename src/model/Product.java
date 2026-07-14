package model;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String category;
    private String imagePath;

    public Product(String name, String description, double price, int stockQuantity, String category) {
        this(name, description, price, stockQuantity, category, null);
    }

    public Product(String name, String description, double price, int stockQuantity, String category, String imagePath) {
        this(0, name, description, price, stockQuantity, category, imagePath);
    }

    public Product(int id, String name, String description, double price, int stockQuantity, String category) {
        this(id, name, description, price, stockQuantity, category, null);
    }

    public Product(int id, String name, String description, double price, int stockQuantity, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imagePath = imagePath;
    }

    public int getId() { 
        return id;
    }

    public String getName() { 
        return name; 
    }

    public String getDescription() { 
        return description; 
    }

    public double getPrice() { 
        return price; 
    }

    public int getStockQuantity() { 
        return stockQuantity; 
    }

    public String getCategory() { 
        return category; 
    }

    public String getImagePath() { 
        return imagePath;
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public void setPrice(double price) { 
        this.price = price; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public void setStockQuantity(int stockQuantity) { 
        this.stockQuantity = stockQuantity; 
    }

    public void setCategory(String category) { 
        this.category = category; 
    }
    
    public void setImagePath(String imagePath) { 
        this.imagePath = imagePath; 
    }

    public void reduceStock(int quantity) {
        if (quantity <= stockQuantity) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Not enough stock!");
        }
    }

    @Override
    public String toString() {
        return "Product[id=" + id + ", name=" + name + ", price=" + price + ", stock=" + stockQuantity + "]";
    }
}
