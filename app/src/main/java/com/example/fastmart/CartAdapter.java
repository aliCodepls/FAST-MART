package com.example.fastmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<DatabaseHelper.CartItem> cartItems;
    private DatabaseHelper db;
    private OnTotalUpdateListener totalUpdateListener;

    public interface OnTotalUpdateListener {
        void onUpdate();
    }

    public CartAdapter(Context context, List<DatabaseHelper.CartItem> cartItems,
                       DatabaseHelper db, OnTotalUpdateListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.db = db;
        this.totalUpdateListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        DatabaseHelper.CartItem item = cartItems.get(position);
        Product product = item.product;

        // ✅ Load from imageUrl with Glide
        if (product.imageUrl != null && !product.imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.product_default)
                    .error(R.drawable.product_default)
                    .centerCrop()
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(
                    product.imageResId != 0 ? product.imageResId : R.drawable.product_default);
        }

        holder.tvProductName.setText(product.name);
        holder.tvProductPrice.setText("$" + String.format("%.2f", product.price));
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        holder.btnIncrease.setOnClickListener(v -> {
            item.quantity++;
            db.updateCartQuantity(product.productId, item.quantity);
            holder.tvQuantity.setText(String.valueOf(item.quantity));
            if (totalUpdateListener != null) totalUpdateListener.onUpdate();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                db.updateCartQuantity(product.productId, item.quantity);
                holder.tvQuantity.setText(String.valueOf(item.quantity));
                if (totalUpdateListener != null) totalUpdateListener.onUpdate();
            }
        });

        holder.ibMoreOptions.setOnClickListener(v -> showDeleteConfirmation(holder, product));
    }

    private void showDeleteConfirmation(CartViewHolder holder, Product product) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Item")
                .setMessage("Remove " + product.name + " from cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.removeFromCart(product.productId);
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        cartItems.remove(pos);
                        notifyItemRemoved(pos);
                        if (totalUpdateListener != null) totalUpdateListener.onUpdate();
                        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvQuantity;
        ImageButton btnIncrease, btnDecrease, ibMoreOptions;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            ibMoreOptions = itemView.findViewById(R.id.ibMoreOptions);
        }
    }
}