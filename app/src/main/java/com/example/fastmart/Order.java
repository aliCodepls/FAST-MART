package com.example.fastmart;

import java.util.Map;

public class Order {
    public String orderId, buyerId, sellerId, status, orderDate;
    public double totalAmount;
    public Map<String, OrderItem> items;

    public Order() {}

    public static class OrderItem {
        public String productId, productName;
        public double price, subtotal;
        public int quantity;

        public OrderItem() {}

        public OrderItem(String productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = price * quantity;
        }
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Map<String, OrderItem> getItems() { return items; }
    public void setItems(Map<String, OrderItem> items) { this.items = items; }
}