package model;

/**
 * Represents a single product entry within an order.
 */
public class OrderItem {
    private Product product;
    private int quantity;
    private double price;

    /**
     * Creates an order item for the specified product and quantity.
     */
    public OrderItem(Product product, int quantity){
        this.product = product;
        this.price = product.getPrice();
        this.quantity = quantity;
    }

    
    public Product getProduct(){
        return product;
    }

    public int getQuantity(){
        return quantity;
    }

    public double getPrice(){
        return price;
    }

    /**
     * Calculates the total price for this order item.
     */
    public double getTotal(){
        double total = getQuantity() * getPrice();
        return total;
    }

}