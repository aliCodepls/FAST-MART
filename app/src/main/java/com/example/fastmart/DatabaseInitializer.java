package com.example.fastmart;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInitializer {

    public static void initialize() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String currentUid = FirebaseAuth.getInstance().getUid();

        // 1. Add Products
        Map<String, Object> products = new HashMap<>();
        
        // Use current UID if logged in, otherwise fallback to a default seller ID
        String sellerId = (currentUid != null) ? currentUid : "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2";

        products.put("prod1", new Product("prod1", sellerId, "Gaming Laptop", "Electronics", "High-performance gaming laptop with RTX 4060, 16GB RAM, 1TB SSD", 1299.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Flaptop1.jpg?alt=media"));
        products.put("prod2", new Product("prod2", sellerId, "Cotton T-Shirt", "Clothing", "Premium cotton casual t-shirt, breathable fabric", 25.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Ftshirt1.jpg?alt=media"));
        products.put("prod3", new Product("prod3", sellerId, "Wireless Headphones", "Electronics", "Bluetooth noise-cancelling headphones with 30hr battery life", 89.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fheadphones1.jpg?alt=media"));
        products.put("prod4", new Product("prod4", sellerId, "Smart Watch", "Electronics", "Fitness tracker with heart rate monitor, GPS, 7-day battery", 149.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fwatch1.jpg?alt=media"));
        products.put("prod5", new Product("prod5", sellerId, "Denim Jeans", "Clothing", "Classic blue denim jeans, slim fit, durable material", 55.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fjeans1.jpg?alt=media"));
        
        ref.child("products").updateChildren(products);

        // 3. Add Orders
        if (currentUid != null) {
            Map<String, Object> orders = new HashMap<>();
            
            Order order1 = new Order();
            order1.orderId = "ord_101";
            order1.buyerId = "buyer1";
            order1.sellerId = currentUid;
            order1.status = "Delivered";
            order1.orderDate = "30/04/2026";
            order1.totalAmount = 1299.0;
            
            Map<String, Order.OrderItem> items1 = new HashMap<>();
            items1.put("item1", new Order.OrderItem("prod1", "Gaming Laptop", 1299.0, 1));
            order1.items = items1;
            
            orders.put(order1.orderId, order1);
            
            Order order2 = new Order();
            order2.orderId = "ord_102";
            order2.buyerId = "buyer1";
            order2.sellerId = currentUid;
            order2.status = "Processing";
            order2.orderDate = "30/04/2026";
            order2.totalAmount = 238.0;
            
            Map<String, Order.OrderItem> items2 = new HashMap<>();
            items2.put("item1", new Order.OrderItem("prod3", "Wireless Headphones", 89.0, 2));
            items2.put("item2", new Order.OrderItem("prod8", "Backpack", 45.0, 1));
            order2.items = items2;
            
            orders.put(order2.orderId, order2);
            
            ref.child("orders").updateChildren(orders);
        }

        // 4. Add Users (Legacy/Fallback)
        if (currentUid == null) {
            ref.child("users").child("ZRJYNIXTp1XNjPMG1RyU5K4h6JT2").setValue(new User("ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "John Seller", "seller@fastmart.com", "123 Market Street", "Male", "15/03/1985", "+1234567890", "United States", "Seller"));
            ref.child("users").child("buyer1").setValue(new User("buyer1", "Mike Buyer", "buyer@fastmart.com", "789 Customer Road", "Male", "10/12/1995", "+1987654321", "Canada", "Buyer"));
        }
    }
}
