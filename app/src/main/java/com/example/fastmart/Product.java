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
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) {
        if (imageUrl != null && (imageUrl.contains("drive.google.com") || 
                                imageUrl.contains("docs.google.com") || 
                                imageUrl.contains("google.com/file/d/"))) {
            String fileId = ImageUtils.extractFileId(imageUrl);
            if (!fileId.isEmpty()) {
                // Automatically convert to the best format (Stage 0)
                this.imageUrl = ImageUtils.getDriveFormat(fileId, 0);
                return;
            }
        }
        this.imageUrl = imageUrl;
    }

    /**
     * Helper to get a working image URL.
     * @param attempt 0 for primary, 1/2 for fallbacks
     */
    public String getUsableImageUrl(int attempt) {
        if (imageUrl == null || imageUrl.isEmpty()) return "";
        
        // If not a Drive link, return original on first attempt
        if (!imageUrl.contains("drive.google.com") && 
            !imageUrl.contains("docs.google.com") && 
            !imageUrl.contains("google.com/file/d/")) {
            return attempt == 0 ? imageUrl : "";
        }

        String fileId = ImageUtils.extractFileId(imageUrl);
        return ImageUtils.getDriveFormat(fileId, attempt);
    }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}