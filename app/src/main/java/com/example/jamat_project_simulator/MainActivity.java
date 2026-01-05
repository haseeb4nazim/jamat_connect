package com.example.jamat_project_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextInputEditText name, email, password;
    private AutoCompleteTextView city;
    private MaterialButton registerBtn;
    private TextView login;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // List of Pakistani cities
    private static final String[] CITIES = {
            "Karachi", "Lahore", "Islamabad", "Rawalpindi", "Faisalabad",
            "Multan", "Peshawar", "Quetta", "Sialkot", "Gujranwala",
            "Hyderabad", "Bahawalpur", "Sargodha", "Sukkur", "Larkana",
            "Sheikhupura", "Jhang", "Rahim Yar Khan", "Gujrat", "Mardan",
            "Kasur", "Sahiwal", "Okara", "Wah Cantt", "Dera Ghazi Khan"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating your account...");
        progressDialog.setCancelable(false);

        // Initialize views
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        city = findViewById(R.id.city);
        registerBtn = findViewById(R.id.signupBtn);
        login = findViewById(R.id.loginRedirect);

        // Setup city dropdown
        setupCityDropdown();

        // Register button listener
        registerBtn.setOnClickListener(v -> registerUser());

        // Login redirect listener
        login.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupCityDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                CITIES
        );
        city.setAdapter(adapter);

        // Show dropdown when clicked
        city.setOnClickListener(v -> city.showDropDown());
    }

    private void registerUser() {
        String n = name.getText().toString().trim();
        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();
        String c = city.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(n)) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(e)) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(p)) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (p.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(c)) {
            Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show();
            city.requestFocus();
            return;
        }

        // Disable button and show progress
        registerBtn.setEnabled(false);
        progressDialog.show();

        // Create Firebase user
        auth.createUserWithEmailAndPassword(e, p)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        String uid = user.getUid();

                        // Set display name in Firebase Auth
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(n)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(profileTask -> {
                                    // Save user data to Firestore
                                    saveUserToFirestore(uid, n, e, c);
                                });
                    }
                })
                .addOnFailureListener(e1 -> {
                    progressDialog.dismiss();
                    registerBtn.setEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Registration failed: " + e1.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserToFirestore(String uid, String name, String email, String city) {
        UserModel user = new UserModel(uid, name, email, city);

        // Save with UID as document ID
        db.collection("user")
                .document(uid)
                .set(user)
                .addOnSuccessListener(task -> {
                    progressDialog.dismiss();
                    registerBtn.setEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Account created successfully!",
                            Toast.LENGTH_SHORT).show();

                    // Redirect to Login
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    registerBtn.setEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Failed to save user data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}