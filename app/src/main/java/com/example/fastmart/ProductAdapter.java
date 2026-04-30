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

import java.util.List;

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

        if (isSeller) {
            holder.ibFavourite.setVisibility(View.GONE);
        } else {
            holder.ibFavourite.setVisibility(View.VISIBLE);
            boolean isFav = DatabaseHelper.getInstance(context).isFavourite(product.productId);
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