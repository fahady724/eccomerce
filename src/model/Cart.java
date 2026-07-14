package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<OrderItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        if (product.getStockQuantity() >= quantity) {
            OrderItem item = new OrderItem(product, quantity);
            items.add(item);
        }
        else{
            System.out.println("Stock out!");
        }
    }

    public double calculateTotal(){
        double total = 0.0;

        for(OrderItem item: items){
            total += item.getTotal();
        }

        return total;
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void clearCart() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

}
