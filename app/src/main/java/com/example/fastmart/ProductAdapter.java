package com.example.fastmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.util.Log;
import java.util.List;

import androidx.annotation.Nullable;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private boolean isSeller;
    private OnProductClickListener productClickListener;
    private OnFavouriteClickListener favouriteClickListener;

    public interface OnProductClickListener {
        void onClick(Product product);
    }

    public interface OnFavouriteClickListener {
        void onFavourite(Product product);
    }

    public ProductAdapter(Context context, List<Product> products, boolean isSeller) {
        this.context = context;
        this.products = products;
        this.isSeller = isSeller;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    public void setOnFavouriteClickListener(OnFavouriteClickListener listener) {
        this.favouriteClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Load image: prefer imageUrl from Firebase, fallback to local drawable
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "IMAGE FAILED: " + product.getImageUrl());
                            if (e != null) e.logRootCauses("GlideError");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("GlideSuccess", "Loaded: " + product.getImageUrl());
                            return false;
                        }
                    })
                    .placeholder(R.drawable.product_default)
                    .error(R.drawable.product_default)
                    .centerCrop()
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(
                    product.getImageResId() != 0 ? product.getImageResId() : R.drawable.product_default);
        }

        holder.tvProductName.setText(product.getName());
        holder.tvProductType.setText(product.getType());
        holder.tvProductPrice.setText("$" + String.format("%.2f", product.getPrice()));

        if (isSeller) {
            holder.ibFavourite.setVisibility(View.GONE);
        } else {
            holder.ibFavourite.setVisibility(View.VISIBLE);
            boolean isFav = DatabaseHelper.getInstance(context).isFavourite(product.getProductId());
            holder.ibFavourite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

            holder.ibFavourite.setOnClickListener(v -> {
                if (favouriteClickListener != null) favouriteClickListener.onFavourite(product);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) productClickListener.onClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductType, tvProductPrice;
        ImageButton ibFavourite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductType = itemView.findViewById(R.id.tvProductType);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ibFavourite = itemView.findViewById(R.id.ibFavourite);
        }
    }
}