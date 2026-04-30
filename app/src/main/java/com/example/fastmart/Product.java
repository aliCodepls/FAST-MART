package com.example.fastmart;

import android.util.Log;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IgnoreExtraProperties
public class Product {
    private String productId;
    private String sellerId;
    private String name;
    private String type;
    private String description;
    private String imageUrl;
    private double price;
    private double rating;
    private int stock;
    private int imageResId;

    public Product() {}

    public Product(String productId, String sellerId, String name, String type,
                   String description, double price, String imageUrl) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        setImageUrl(imageUrl);
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { 
        return convertDriveLink(imageUrl); 
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = convertDriveLink(imageUrl);
    }

    private String convertDriveLink(String link) {
        if (link == null || link.isEmpty()) return link;
        
        // If it's already a direct lh3 link, return as is
        if (link.contains("lh3.googleusercontent.com")) {
            return link;
        }

        // Handle various Google Drive link formats
        if (link.contains("drive.google.com") || link.contains("docs.google.com")) {
            Log.d("DriveConvert", "Converting Drive link: " + link);
            try {
                String fileId = "";
                
                // Pattern 1: /d/FILE_ID/ (common in browser links)
                Pattern pattern1 = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
                Matcher matcher1 = pattern1.matcher(link);
                if (matcher1.find()) {
                    fileId = matcher1.group(1);
                }
                
                // Pattern 2: id=FILE_ID (common in sharing/direct links)
                if (fileId.isEmpty()) {
                    Pattern pattern2 = Pattern.compile("id=([a-zA-Z0-9_-]+)");
                    Matcher matcher2 = pattern2.matcher(link);
                    if (matcher2.find()) {
                        fileId = matcher2.group(1);
                    }
                }
                
                if (!fileId.isEmpty()) {
                    // Using lh3.googleusercontent.com/d/ is the most reliable way to bypass
                    // the Google Drive virus scan warning for images.
                    String directUrl = "https://lh3.googleusercontent.com/d/" + fileId;
                    Log.d("DriveConvert", "Success! ID: " + fileId);
                    return directUrl;
                } else {
                    Log.e("DriveConvert", "Could not extract File ID from link: " + link);
                }
            } catch (Exception e) {
                Log.e("DriveConvert", "Error converting Drive link", e);
            }
        }
        return link;
    }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}