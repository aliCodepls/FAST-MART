package com.example.fastmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView rvFavourites;
    private FavouritesAdapter adapter;
    private List<Product> favouritesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavourites = view.findViewById(R.id.rvFavourites);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavourites();
    }

    private void loadFavourites() {
        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        favouritesList = db.getAllFavourites();

        adapter = new FavouritesAdapter(getContext(), favouritesList);
        rvFavourites.setAdapter(adapter);

        adapter.setOnDeleteClickListener(product -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Remove Favourite")
                    .setMessage("Do you want to delete this product from favourites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.removeFavourite(product.getProductId());
                        favouritesList.remove(product);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        adapter.setOnCartClickListener(product -> {
            db.addToCart(product);
            Toast.makeText(getContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavourites();
    }
}