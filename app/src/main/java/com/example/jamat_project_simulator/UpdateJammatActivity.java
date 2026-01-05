package com.example.jamat_project_simulator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateJammatActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtDescription, edtStartCity, edtEndCity, edtStartDate, edtEndDate;
    private MaterialButton btnUpdate;
    private FirebaseFirestore db;
    private String jammatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_jammat);

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtStartCity = findViewById(R.id.edtStartCity);
        edtEndCity = findViewById(R.id.edtEndCity);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        if (getIntent() != null) {
            jammatId = getIntent().getStringExtra("jammatId");
            edtName.setText(getIntent().getStringExtra("jammatName"));
            edtDescription.setText(getIntent().getStringExtra("description"));
            edtStartCity.setText(getIntent().getStringExtra("startCity"));
            edtEndCity.setText(getIntent().getStringExtra("endCity"));
            edtStartDate.setText(getIntent().getStringExtra("startDate"));
            edtEndDate.setText(getIntent().getStringExtra("endDate"));
        }

        // Update button click
        btnUpdate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String startCity = edtStartCity.getText().toString().trim();
            String endCity = edtEndCity.getText().toString().trim();
            String startDate = edtStartDate.getText().toString().trim();
            String endDate = edtEndDate.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || startCity.isEmpty() || endCity.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(UpdateJammatActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("jammatName", name);
            updates.put("description", description);
            updates.put("startCity", startCity);
            updates.put("endCity", endCity);
            updates.put("startDate", startDate);
            updates.put("endDate", endDate);

            db.collection("Jammat").document(jammatId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UpdateJammatActivity.this, "Jammat updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UpdateJammatActivity.this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
