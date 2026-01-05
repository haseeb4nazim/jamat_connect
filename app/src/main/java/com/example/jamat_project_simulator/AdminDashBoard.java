package com.example.jamat_project_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashBoard extends AppCompatActivity {

    private CardView createJamat, viewJamats, requests, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        createJamat = findViewById(R.id.cardCreateJamat);
        viewJamats = findViewById(R.id.cardViewJamats);
        requests = findViewById(R.id.cardRequests);
        logout = findViewById(R.id.cardLogout);

        createJamat.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Create Jammat...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashBoard.this, CreateJammatActivity.class);
            startActivity(intent);
        });

        viewJamats.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashBoard.this, ViewJammatsActivity.class));
        });

        requests.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashBoard.this, JoinRequestActivity.class));
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(AdminDashBoard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashBoard.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
