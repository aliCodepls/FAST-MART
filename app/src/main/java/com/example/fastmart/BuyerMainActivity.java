package com.example.fastmart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BuyerMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyermain);

        bottomNav = findViewById(R.id.bottomNav);

        loadFragment(new BuyerHomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.navHome) {
                fragment = new BuyerHomeFragment();
            } else if (id == R.id.navFavourites) {
                fragment = new FavouritesFragment();
            } else if (id == R.id.navCart) {
                fragment = new CartFragment();
            } else {
                fragment = new BuyerAccountFragment();
            }
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}