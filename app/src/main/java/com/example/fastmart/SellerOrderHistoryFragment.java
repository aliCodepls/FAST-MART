package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderHistoryFragment extends Fragment {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sellerorderhistory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderAdapter(getContext(), orderList);
        rvOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        String currentUid = FirebaseAuth.getInstance().getUid();

        if (currentUid == null || currentUid.isEmpty()) return;

        FirebaseDatabase.getInstance().getReference("orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        orderList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Order order = snap.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        
                        View view = getView();
                        if (view != null) {
                            View emptyState = view.findViewById(R.id.llEmptyState);
                            if (emptyState != null) {
                                boolean isEmpty = orderList.isEmpty();
                                emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                                rvOrders.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded()) Toast.makeText(getContext(), "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}