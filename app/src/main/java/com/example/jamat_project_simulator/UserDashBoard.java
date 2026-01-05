package com.example.jamat_project_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDashBoard extends AppCompatActivity {

    private TextView txtWelcome;
    private FirebaseAuth auth;
    private CardView cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Views
        txtWelcome = findViewById(R.id.txtWelcome);
        cardLogout = findViewById(R.id.cardLogout);

        // Get current user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            // If name is null, extract name from email
            if (name == null || name.isEmpty()) {
                if (email != null && email.contains("@")) {
                    // Extract the part before @ as name
                    name = email.substring(0, email.indexOf("@"));
                    // Capitalize first letter
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                } else {
                    name = "User";
                }
            }

            txtWelcome.setText("Welcome Back, " + name + "!");
        } else {
            // If no user is logged in, redirect to login
            Intent intent = new Intent(UserDashBoard.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Logout functionality
        cardLogout.setOnClickListener(v -> {
            auth.signOut(); // Sign out user
            Toast.makeText(UserDashBoard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to LoginActivity
            Intent intent = new Intent(UserDashBoard.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish();
        });
    }
}