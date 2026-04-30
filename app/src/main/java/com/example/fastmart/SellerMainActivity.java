package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class SellerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellermain);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView tvNavName = headerView.findViewById(R.id.tvNavName);
        TextView tvNavEmail = headerView.findViewById(R.id.tvNavEmail);

        SharedPreferences prefs = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        tvNavName.setText(prefs.getString("name", "Seller"));
        tvNavEmail.setText(prefs.getString("email", ""));

        // REMOVED THE PROBLEM LINE BELOW
        // View btnLight = navigationView.getMenu().findItem(R.id.navTheme).getActionView();

        loadFragment(new SellerHomeFragment());
        navigationView.setCheckedItem(R.id.navSellerHome);
    }

    public void applyTheme(boolean isDark) {
        SharedPreferences.Editor editor = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isDarkTheme", isDark);
        editor.apply();
        recreate();
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("isDarkTheme", false);
        if (isDark) {
            setTheme(R.style.Theme_FastMart_Dark);
        } else {
            setTheme(R.style.Theme_FastMart);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navSellerHome) {
            loadFragment(new SellerHomeFragment());
        } else if (id == R.id.navOrderHistory) {
            loadFragment(new SellerOrderHistoryFragment());
        } else if (id == R.id.navSellerAccount) {
            loadFragment(new SellerAccountFragment());
        } else if (id == R.id.navMessages) {
            loadFragment(new SellerChatFragment());
        } else if (id == R.id.navThemeLight) {
            applyTheme(false);
        } else if (id == R.id.navThemeDark) {
            applyTheme(true);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sellerFragmentContainer, fragment)
                .commit();
    }
}