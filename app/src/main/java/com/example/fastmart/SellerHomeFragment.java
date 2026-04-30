package com.example.fastmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SellerHomeFragment extends Fragment {

    private RecyclerView rvSellerProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FloatingActionButton fabAddProduct;
    private TextView tvGreeting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSellerProducts = view.findViewById(R.id.rvSellerProducts);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        tvGreeting = view.findViewById(R.id.tvGreeting);

        SharedPreferences prefs = getActivity().getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        String name = prefs.getString("name", "Seller");
        tvGreeting.setText("Hello, " + name + "!");

        rvSellerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), productList, true);
        rvSellerProducts.setAdapter(adapter);

        // ✅ DYNAMIC UID: Get directly from Firebase Auth
        String currentUid = FirebaseAuth.getInstance().getUid();
        if (currentUid != null) {
            loadSellerProducts(currentUid);
        }

        adapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(getContext(), ProductDescriptionActivity.class);
            intent.putExtra("productId", product.productId);
            intent.putExtra("sellerId", product.sellerId);
            intent.putExtra("name", product.name);
            intent.putExtra("type", product.type);
            intent.putExtra("price", product.price);
            intent.putExtra("description", product.description);
            intent.putExtra("imageUrl", product.imageUrl);  // ✅ pass imageUrl
            intent.putExtra("isSeller", true);
            startActivity(intent);
        });

        fabAddProduct.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ProductAddActivity.class)));
    }

    private void loadSellerProducts(String sellerId) {
        if (sellerId == null || sellerId.isEmpty()) {
            View emptyState = getView().findViewById(R.id.llEmptyState);
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
            return;
        }

        FirebaseDatabase.getInstance().getReference("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        
                        productList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            try {
                                Product product = snap.getValue(Product.class);
                                if (product != null) {
                                    productList.add(product);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        
                        // Handle empty state
                        View view = getView();
                        if (view != null) {
                            View emptyState = view.findViewById(R.id.llEmptyState);
                            if (emptyState != null) {
                                boolean isEmpty = productList.isEmpty();
                                emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                                rvSellerProducts.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}