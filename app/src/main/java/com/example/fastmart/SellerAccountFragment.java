package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerAccountFragment extends Fragment {

    private TextView tvName, tvAddress, tvCountry, tvDob, tvGender, tvPhone;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selleraccount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvCountry = view.findViewById(R.id.tvCountry);
        tvDob = view.findViewById(R.id.tvDob);
        tvGender = view.findViewById(R.id.tvGender);
        tvPhone = view.findViewById(R.id.tvPhone);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadUserData();
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        String uid = prefs.getString("uid", "");

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            tvName.setText(user.name);
                            tvAddress.setText(user.address);
                            tvCountry.setText(user.country);
                            tvDob.setText(user.dob);
                            tvGender.setText(user.gender);
                            tvPhone.setText(user.phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void logout() {
        // ✅ FIX: Only sign out and clear prefs — do NOT delete user data from Firebase
        FirebaseAuth.getInstance().signOut();
        SharedPreferences prefs = getActivity().getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finishAffinity();
    }
}
