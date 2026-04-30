package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashactivity);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            String accountType = prefs.getString("accountType", "");

            if (isLoggedIn) {
                if ("Seller".equals(accountType)) {
                    startActivity(new Intent(this, SellerMainActivity.class));
                } else {
                    startActivity(new Intent(this, BuyerMainActivity.class));
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}