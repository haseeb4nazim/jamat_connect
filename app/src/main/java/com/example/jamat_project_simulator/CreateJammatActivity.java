package com.example.jamat_project_simulator;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CreateJammatActivity extends AppCompatActivity {

    private TextInputEditText jammatName, jammatDescription, startCity, endCity, startDate, endDate;
    private MaterialButton createJamatBtn;

    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_jammat);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        jammatName = findViewById(R.id.jammatName);
        jammatDescription = findViewById(R.id.jammatDescription);
        startCity = findViewById(R.id.startCity);
        endCity = findViewById(R.id.endCity);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        createJamatBtn = findViewById(R.id.createJamatBtn);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Jamat...");
        progressDialog.setCancelable(false);

        // Set up date pickers
        setupDatePickers();

        // Set click listener
        createJamatBtn.setOnClickListener(v -> saveJammat());
    }

    private void setupDatePickers() {
        // Start Date Picker
        startDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateJammatActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        startDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // End Date Picker
        endDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateJammatActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        endDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void saveJammat() {
        String name = jammatName.getText().toString().trim();
        String description = jammatDescription.getText().toString().trim();
        String start = startCity.getText().toString().trim();
        String end = endCity.getText().toString().trim();
        String sDate = startDate.getText().toString().trim();
        String eDate = endDate.getText().toString().trim();

        // Validate fields
        if (TextUtils.isEmpty(name)) {
            jammatName.setError("Jamat name is required");
            jammatName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            jammatDescription.setError("Description is required");
            jammatDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(start)) {
            startCity.setError("Start city is required");
            startCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(end)) {
            endCity.setError("End city is required");
            endCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sDate)) {
            startDate.setError("Start date is required");
            startDate.requestFocus();
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eDate)) {
            endDate.setError("End date is required");
            endDate.requestFocus();
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Create Jammat object
        Jammat newJammat = new Jammat(name, description, start, end, sDate, eDate);

        // Save to Firestore
        db.collection("Jammat")
                .add(newJammat)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Jamat created successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate back to Admin Dashboard
                    Intent intent = new Intent(CreateJammatActivity.this, AdminDashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to create Jamat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}