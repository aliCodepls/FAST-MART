package com.example.fastmart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class ProductDescriptionActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private TextView tvProductName, tvProductType, tvProductPrice, tvProductDescription;
    private Button btnBuyNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.product_details));

        ivProduct = findViewById(R.id.ivProduct);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductType = findViewById(R.id.tvProductType);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        btnBuyNow = findViewById(R.id.btnBuyNow);

        String productId = getIntent().getStringExtra("productId");
        String name = getIntent().getStringExtra("name");
        String type = getIntent().getStringExtra("type");
        double price = getIntent().getDoubleExtra("price", 0);
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String sellerId = getIntent().getStringExtra("sellerId");
        boolean isSeller = getIntent().getBooleanExtra("isSeller", false);

        // Load image via Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Re-fetch through Product model for safety
            Product temp = new Product();
            temp.setImageUrl(imageUrl);
            
            String url0 = temp.getUsableImageUrl(0);
            String url1 = temp.getUsableImageUrl(1);
            String url2 = temp.getUsableImageUrl(2);

            GlideUrl glideUrl0 = new GlideUrl(url0, new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .build());

            // Stage 1
            RequestBuilder<Drawable> request = Glide.with(this).load(glideUrl0);
            
            // Stage 2 fallback
            if (!url1.isEmpty()) {
                request = request.error(Glide.with(this)
                        .load(new GlideUrl(url1, new LazyHeaders.Builder()
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .build())));
            }

            // Stage 3 fallback
            if (!url2.isEmpty()) {
                request = request.error(Glide.with(this)
                        .load(new GlideUrl(url2, new LazyHeaders.Builder()
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .build())));
            }

            request.placeholder(R.drawable.product_default)
                   .error(R.drawable.product_default)
                   .centerCrop()
                   .into(ivProduct);
        } else {
            ivProduct.setImageResource(R.drawable.product_default);
        }

        tvProductName.setText(name);
        tvProductType.setText(type);
        tvProductPrice.setText("$" + String.format("%.2f", price));
        tvProductDescription.setText(description);

        if (isSeller) {
            btnBuyNow.setText(getString(R.string.close));
            btnBuyNow.setOnClickListener(v -> finish());
        } else {
            btnBuyNow.setText(getString(R.string.buy_now));
            btnBuyNow.setOnClickListener(v -> {
                Product p = new Product(productId, sellerId, name, type, description, price, imageUrl);
                DatabaseHelper.getInstance(this).addToCart(p);
                Toast.makeText(this, name + " added to cart", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}