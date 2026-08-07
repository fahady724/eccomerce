package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shopping cart containing selected order items.
 */
public class Cart {

    private List<OrderItem> items;

    /**
     * Creates an empty shopping cart.
     */
    public Cart() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds a product to the cart if sufficient stock is available.
     */
    public void addItem(Product product, int quantity) {
        if (product.getStockQuantity() >= quantity) {
            OrderItem item = new OrderItem(product, quantity);
            items.add(item);
        }
        else{
            System.out.println("Stock out!");
        }
    }

    /**
     * Calculates the total cost of all items in the cart.
     */
    public double calculateTotal(){
        double total = 0.0;

        for(OrderItem item: items){
            total += item.getTotal();
        }

        return total;
    }

    /**
     * Removes the specified item from the cart.
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Clears all items from the cart.
     */
    public void clearCart() {
        items.clear();
    }

    /**
     * Checks whether the cart contains any items.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

}
