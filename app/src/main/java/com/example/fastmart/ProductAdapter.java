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
import com.bumptech.glide.RequestBuilder;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;
import android.util.Log;
import java.util.List;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

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
        // Final fallback chain: lh3 -> uc -> thumbnail -> default
        String url0 = product.getUsableImageUrl(0);
        String url1 = product.getUsableImageUrl(1);
        String url2 = product.getUsableImageUrl(2);

        if (!url0.isEmpty()) {
            GlideUrl glideUrl0 = new GlideUrl(url0, new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .build());

            // Stage 1
            RequestBuilder<Drawable> request = Glide.with(context).load(glideUrl0);
            
            // Stage 2 fallback
            if (!url1.isEmpty()) {
                request = request.error(Glide.with(context)
                        .load(new GlideUrl(url1, new LazyHeaders.Builder()
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .build())));
            }

            // Stage 3 fallback
            if (!url2.isEmpty()) {
                request = request.error(Glide.with(context)
                        .load(new GlideUrl(url2, new LazyHeaders.Builder()
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .build())));
            }

            request.placeholder(R.drawable.product_default)
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