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
    // IMPORTANT: Variable name is cardRequests (matches XML id)
    private CardView cardNearby, cardRequests, cardRecommended, cardProfile, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Views - All IDs match your XML exactly
        txtWelcome = findViewById(R.id.txtWelcome);
        cardNearby = findViewById(R.id.cardNearby);
        cardRequests = findViewById(R.id.cardRequests);  // ✅ This matches your XML
        cardRecommended = findViewById(R.id.cardRecommended);
        cardProfile = findViewById(R.id.cardProfile);
        cardLogout = findViewById(R.id.cardLogout);

        // Get current user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            // If name is null, extract name from email
            if (name == null || name.isEmpty()) {
                if (email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf("@"));
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                } else {
                    name = "User";
                }
            }

            txtWelcome.setText("Welcome Back, " + name + "!");
        } else {
            Intent intent = new Intent(UserDashBoard.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Nearby Jamats - Opens ViewAvailableJammatsActivity
        cardNearby.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashBoard.this, ViewAvailableJammatsActivity.class);
            startActivity(intent);
        });

        // My Join Requests - Opens MyRequestsActivity
        cardRequests.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashBoard.this, MyRequestsActivity.class);
            startActivity(intent);
        });

        // Recommended Jamats - Coming Soon
        cardRecommended.setOnClickListener(v -> {
            Toast.makeText(this, "Coming Soon! AI-powered recommendations", Toast.LENGTH_SHORT).show();
        });

        // Profile Settings - Coming Soon
        cardProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Coming Soon! Profile settings", Toast.LENGTH_SHORT).show();
        });

        // Logout
        cardLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(UserDashBoard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserDashBoard.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}