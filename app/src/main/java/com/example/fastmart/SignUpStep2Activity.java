package com.example.fastmart;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignUpStep2Activity extends AppCompatActivity {

    private EditText etFullName, etPhone, etCountry, etAddress, etDob;
    private Spinner spinnerAccountType;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSaveProfile;
    private CheckBox cbTerms;

    private String uid, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etCountry = findViewById(R.id.etCountry);
        etAddress = findViewById(R.id.etAddress);
        etDob = findViewById(R.id.etDob);
        spinnerAccountType = findViewById(R.id.spinnerAccountType);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        cbTerms = findViewById(R.id.cbTerms);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);

        etDob.setOnClickListener(v -> showDatePicker());

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                etDob.setText(day + "/" + (month + 1) + "/" + year),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String accountType = spinnerAccountType.getSelectedItem().toString();
        String gender = rbMale.isChecked() ? "Male" : rbFemale.isChecked() ? "Female" : "";

        if (name.isEmpty() || phone.isEmpty() || country.isEmpty() || address.isEmpty()
                || dob.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(uid, name, email, address, gender, dob, phone, country, accountType);

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE).edit();
                        editor.putString("uid", uid);
                        editor.putString("name", name);
                        editor.putString("accountType", accountType);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        if ("Seller".equals(accountType)) {
                            startActivity(new Intent(this, SellerMainActivity.class));
                        } else {
                            startActivity(new Intent(this, BuyerMainActivity.class));
                        }
                        finishAffinity();
                    } else {
                        Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}