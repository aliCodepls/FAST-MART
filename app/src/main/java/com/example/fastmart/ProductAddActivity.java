package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class ProductAddActivity extends AppCompatActivity {

    private EditText etProductName, etProductPrice, etProductDescription, etProductImageUrl;
    private Spinner spinnerProductType;
    private Button btnAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodcutadd);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.add_product));

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductImageUrl = findViewById(R.id.etProductImageUrl);
        spinnerProductType = findViewById(R.id.spinnerProductType);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductType.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> addProduct());
    }

    private void addProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String type = spinnerProductType.getSelectedItem().toString();
        String imageUrl = etProductImageUrl.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        String sellerId = prefs.getString("uid", "");

        String productId = "prod_" + UUID.randomUUID().toString().substring(0, 8);
        imageUrl = convertDriveLink(imageUrl);

        // ✅ Use imageUrl (converted if drive link)
        Product product = new Product(productId, sellerId, name, type, description, price, imageUrl);
        product.stock = 10; // Default stock
        product.rating = 4.5; // Default rating for new product

        FirebaseDatabase.getInstance().getReference("products").child(productId)
                .setValue(product).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String convertDriveLink(String link) {
        if (link == null || link.isEmpty()) return "";
        if (link.contains("drive.google.com")) {
            String fileId = "";
            if (link.contains("/d/")) {
                int start = link.indexOf("/d/") + 3;
                int end = link.indexOf("/", start);
                if (end == -1) end = link.indexOf("?", start);
                if (end == -1) end = link.length();
                fileId = link.substring(start, end);
            } else if (link.contains("id=")) {
                int start = link.indexOf("id=") + 3;
                int end = link.indexOf("&", start);
                if (end == -1) end = link.length();
                fileId = link.substring(start, end);
            }
            if (!fileId.isEmpty()) {
                return "https://drive.google.com/uc?export=download&id=" + fileId;
            }
        }
        return link;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}