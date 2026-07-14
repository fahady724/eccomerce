package model;

import java.util.List;

public class Order {

    private int orderId;
    private int customerId;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private String status;
    private String orderDate;

    public void setStatus(String status){
        this.status = status;
    }

    public int getOrderId(){
        return orderId;
    }
    public int getCustomerId(){
        return customerId;
    }
    public double getTotalAmount(){
        return totalAmount;
    }
    public String getStatus(){
        return status;
    }
    public List<OrderItem> getOrderItems() { 
        return orderItems; 
    }
    public String getOrderDate() { 
        return orderDate; 
    }  
    
    public Order(int orderId, int customerId, double totalAmount, List<OrderItem> orderItems, String orderDate) {
        this(orderId, customerId, totalAmount, orderItems, "PENDING", orderDate);
    }

    public Order(int orderId, int customerId, double totalAmount, List<OrderItem> orderItems, String status, String orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.status = status;
        this.orderDate = orderDate;
    }

}
