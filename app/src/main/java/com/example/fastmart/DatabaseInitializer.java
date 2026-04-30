package com.example.fastmart;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInitializer {

    public static void initialize() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // 1. Add Products
        Map<String, Product> products = new HashMap<>();
        products.put("prod1", new Product("prod1", "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "Gaming Laptop", "Electronics", "High-performance gaming laptop with RTX 4060, 16GB RAM, 1TB SSD", 1299.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Flaptop1.jpg?alt=media"));
        products.put("prod2", new Product("prod2", "zIk8wlVd82VQMQJl9h1Gpuy3ahn1", "Cotton T-Shirt", "Clothing", "Premium cotton casual t-shirt, breathable fabric", 25.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Ftshirt1.jpg?alt=media"));
        products.put("prod3", new Product("prod3", "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "Wireless Headphones", "Electronics", "Bluetooth noise-cancelling headphones with 30hr battery life", 89.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fheadphones1.jpg?alt=media"));
        products.put("prod4", new Product("prod4", "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "Smart Watch", "Electronics", "Fitness tracker with heart rate monitor, GPS, 7-day battery", 149.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fwatch1.jpg?alt=media"));
        products.put("prod5", new Product("prod5", "zIk8wlVd82VQMQJl9h1Gpuy3ahn1", "Denim Jeans", "Clothing", "Classic blue denim jeans, slim fit, durable material", 55.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fjeans1.jpg?alt=media"));
        products.put("prod6", new Product("prod6", "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "Coffee Maker", "Home & Kitchen", "Automatic drip coffee maker with thermal carafe, 12-cup capacity", 79.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fcoffee.jpg?alt=media"));
        products.put("prod7", new Product("prod7", "zIk8wlVd82VQMQJl9h1Gpuy3ahn1", "Running Shoes", "Footwear", "Lightweight running shoes with cushioned sole, breathable mesh", 85.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fshoes1.jpg?alt=media"));
        products.put("prod8", new Product("prod8", "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "Backpack", "Accessories", "Water-resistant laptop backpack with USB charging port", 45.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fbackpack1.jpg?alt=media"));
        products.put("prod9", new Product("prod9", "zIk8wlVd82VQMQJl9h1Gpuy3ahn1", "Sunglasses", "Accessories", "Polarized UV protection sunglasses, classic aviator style", 35.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fsunglasses1.jpg?alt=media"));
        products.put("prod10", new Product("prod10", "ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "Wireless Mouse", "Electronics", "Ergonomic wireless mouse with adjustable DPI, silent clicks", 18.0, "https://firebasestorage.googleapis.com/v0/b/fastmart-dab4b.appspot.com/o/products%2Fmouse1.jpg?alt=media"));

        ref.child("products").updateChildren((Map) products);

        // 2. Add Users
        ref.child("users").child("ZRJYNIXTp1XNjPMG1RyU5K4h6JT2").setValue(new User("ZRJYNIXTp1XNjPMG1RyU5K4h6JT2", "John Seller", "seller@fastmart.com", "123 Market Street", "Male", "15/03/1985", "+1234567890", "United States", "Seller"));
        ref.child("users").child("zIk8wlVd82VQMQJl9h1Gpuy3ahn1").setValue(new User("zIk8wlVd82VQMQJl9h1Gpuy3ahn1", "Sarah Seller", "sarah@fastmart.com", "456 Fashion Avenue", "Female", "22/07/1990", "+442345678901", "United Kingdom", "Seller"));
        ref.child("users").child("buyer1").setValue(new User("buyer1", "Mike Buyer", "buyer@fastmart.com", "789 Customer Road", "Male", "10/12/1995", "+1987654321", "Canada", "Buyer"));
    }
}
