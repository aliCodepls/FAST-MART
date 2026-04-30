package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private CartAdapter adapter;
    private List<DatabaseHelper.CartItem> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rvCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCart();

        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void loadCart() {
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(getContext());
            cartItems = db.getAllCartItems();
            if (cartItems == null) cartItems = new ArrayList<>();

            adapter = new CartAdapter(getContext(), cartItems, db, this::updateTotal);
            rvCart.setAdapter(adapter);
            updateTotal();
            updateEmptyState();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateEmptyState() {
        try {
            View view = getView();
            if (view != null) {
                View emptyState = view.findViewById(R.id.llEmptyState);
                View checkoutCard = view.findViewById(R.id.cvCheckout);
                
                boolean isEmpty = cartItems == null || cartItems.isEmpty();
                if (emptyState != null) emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                if (rvCart != null) rvCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                if (checkoutCard != null) checkoutCard.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotal() {
        try {
            double total = 0;
            if (cartItems != null) {
                for (DatabaseHelper.CartItem item : cartItems) {
                    if (item != null && item.product != null) {
                        total += item.product.getPrice() * item.quantity;
                    }
                }
            }
            if (tvTotalPrice != null) {
                tvTotalPrice.setText("Total: $" + String.format("%.2f", total));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getActivity().getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        String buyerId = prefs.getString("uid", "");

        StringBuilder sms = new StringBuilder("FastMart Order Summary:\n");
        double total = 0;
        Map<String, Order.OrderItem> orderItemsMap = new HashMap<>();
        Map<String, String> sellerMap = new HashMap<>();

        int itemCount = 1;
        for (DatabaseHelper.CartItem item : cartItems) {
            double itemTotal = item.product.getPrice() * item.quantity;
            total += itemTotal;
            sms.append(item.product.getName())
                    .append(" x").append(item.quantity)
                    .append(" = $").append(String.format("%.2f", itemTotal)).append("\n");

            Order.OrderItem orderItem = new Order.OrderItem(
                    item.product.getProductId(), item.product.getName(),
                    item.product.getPrice(), item.quantity);
            orderItemsMap.put("item" + itemCount, orderItem);
            itemCount++;

            sellerMap.put(item.product.getSellerId(), item.product.getSellerId());
        }
        sms.append("Total: $").append(String.format("%.2f", total));

        // ✅ Use orderDate + totalAmount to match Firebase schema
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String orderDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

        for (String sellerId : sellerMap.keySet()) {
            Order order = new Order();
            order.setOrderId(orderId);
            order.setBuyerId(buyerId);
            order.setSellerId(sellerId);
            order.setStatus("Processing");
            order.setOrderDate(orderDate);
            order.setTotalAmount(total);
            order.setItems(orderItemsMap);

            // ✅ Store flat at /orders/{orderId}
            FirebaseDatabase.getInstance().getReference("orders")
                    .child(orderId).setValue(order);
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+923000000000", null, sms.toString(), null, null);
            Toast.makeText(getContext(), "Order placed! SMS sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Order placed!", Toast.LENGTH_SHORT).show();
        }

        DatabaseHelper.getInstance(getContext()).clearCart();
        cartItems.clear();
        adapter.notifyDataSetChanged();
        updateTotal();
        updateEmptyState();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }
}