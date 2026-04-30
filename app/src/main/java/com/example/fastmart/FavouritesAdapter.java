package com.example.fastmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavViewHolder> {

    private Context context;
    private List<Product> favourites;
    private OnDeleteClickListener deleteClickListener;
    private OnCartClickListener cartClickListener;

    public interface OnDeleteClickListener {
        void onDelete(Product product);
    }

    public interface OnCartClickListener {
        void onAddToCart(Product product);
    }

    public FavouritesAdapter(Context context, List<Product> favourites) {
        this.context = context;
        this.favourites = favourites;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnCartClickListener(OnCartClickListener listener) {
        this.cartClickListener = listener;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        Product product = favourites.get(position);

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
        holder.tvProductType.setText(product.type);
        holder.tvProductPrice.setText("$" + String.format("%.2f", product.price));

        holder.ibCartFav.setOnClickListener(v -> {
            if (cartClickListener != null) cartClickListener.onAddToCart(product);
        });

        holder.ibMoreOptions.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove from Favourites")
                    .setMessage("Remove " + product.name + " from favourites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (deleteClickListener != null) deleteClickListener.onDelete(product);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductType, tvProductPrice;
        ImageButton ibCartFav, ibMoreOptions;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductType = itemView.findViewById(R.id.tvProductType);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ibCartFav = itemView.findViewById(R.id.ibCartFav);
            ibMoreOptions = itemView.findViewById(R.id.ibMoreOptions);
        }
    }
}