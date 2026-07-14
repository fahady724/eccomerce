package model;

public class OrderItem {
    private Product product;
    private int quantity;
    private double price;

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

    public double getTotal(){
        double total = getQuantity() * getPrice();
        return total;
    }

}