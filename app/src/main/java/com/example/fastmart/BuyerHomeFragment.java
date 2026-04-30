package com.example.fastmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class BuyerHomeFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FloatingActionButton fabChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buyer_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rvProducts);
        fabChat = view.findViewById(R.id.fabChat);

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), productList, false);
        rvProducts.setAdapter(adapter);

        adapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(getContext(), ProductDescriptionActivity.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("sellerId", product.getSellerId());
            intent.putExtra("name", product.getName());
            intent.putExtra("type", product.getType());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("imageUrl", product.getImageUrl());   // ✅ pass imageUrl
            intent.putExtra("isSeller", false);
            startActivity(intent);
        });

        adapter.setOnFavouriteClickListener(product -> {
            DatabaseHelper db = DatabaseHelper.getInstance(getContext());
            if (db.isFavourite(product.getProductId())) {
                db.removeFavourite(product.getProductId());
                Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
            } else {
                db.addFavourite(product);
                Toast.makeText(getContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        });

        fabChat.setOnClickListener(v -> showSellersList());

        loadProducts();
    }

    private void loadProducts() {
        FirebaseDatabase.getInstance().getReference("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product product = snap.getValue(Product.class);
                            if (product != null) productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSellersList() {
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> sellerNames = new ArrayList<>();
                        List<String> sellerIds = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            User user = snap.getValue(User.class);
                            if (user != null && "Seller".equals(user.accountType)) {
                                sellerNames.add(user.name);
                                sellerIds.add(user.uid);
                            }
                        }
                        if (sellerNames.isEmpty()) {
                            Toast.makeText(getContext(), "No sellers available", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String[] namesArr = sellerNames.toArray(new String[0]);
                        new android.app.AlertDialog.Builder(getContext())
                                .setTitle("Chat with Seller")
                                .setItems(namesArr, (dialog, which) -> {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("receiverId", sellerIds.get(which));
                                    intent.putExtra("receiverName", sellerNames.get(which));
                                    startActivity(intent);
                                }).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}