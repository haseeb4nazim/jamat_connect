package com.example.jamat_project_simulator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class JoinRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<JoinRequestModel> allList;  // Store all requests
    private ArrayList<JoinRequestModel> filteredList;  // Store filtered requests
    private JoinRequestAdapter adapter;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private TextView txtRequestCount;
    private ChipGroup chipGroupFilter;
    private Chip chipAll, chipPending, chipAccepted, chipRejected;

    private String currentFilter = "all";  // all, pending, accepted, rejected
    private static final String TAG = "JoinRequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_join_request);

            // Initialize Firestore
            db = FirebaseFirestore.getInstance();

            // Initialize views
            recyclerView = findViewById(R.id.requestRecyclerView);
            progressBar = findViewById(R.id.progressBar);
            emptyStateLayout = findViewById(R.id.emptyStateLayout);
            txtRequestCount = findViewById(R.id.txtRequestCount);
            chipGroupFilter = findViewById(R.id.chipGroupFilter);
            chipAll = findViewById(R.id.chipAll);
            chipPending = findViewById(R.id.chipPending);
            chipAccepted = findViewById(R.id.chipAccepted);
            chipRejected = findViewById(R.id.chipRejected);

            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);

            allList = new ArrayList<>();
            filteredList = new ArrayList<>();

            // Setup filter chips
            setupFilters();

            // Load all requests
            loadAllRequests();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupFilters() {
        // Set default selection
        chipAll.setChecked(true);

        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return; // Don't allow no selection
            }

            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chipAll) {
                currentFilter = "all";
            } else if (checkedId == R.id.chipPending) {
                currentFilter = "pending";
            } else if (checkedId == R.id.chipAccepted) {
                currentFilter = "accepted";
            } else if (checkedId == R.id.chipRejected) {
                currentFilter = "rejected";
            }

            filterRequests();
        });
    }

    private void loadAllRequests() {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        // Query ALL requests (no status filter)
        db.collection("JoinRequests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);

                    allList.clear();

                    Log.d(TAG, "Found " + queryDocumentSnapshots.size() + " total requests");

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        JoinRequestModel model = doc.toObject(JoinRequestModel.class);

                        // VERY IMPORTANT: Set document ID
                        if (model != null) {
                            model.setRequestId(doc.getId());
                            allList.add(model);
                            Log.d(TAG, "Request: " + model.getUserName() + " - " + model.getJammatName() + " - Status: " + model.getStatus());
                        }
                    }

                    // Apply current filter
                    filterRequests();
                })
                .addOnFailureListener(e -> {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);

                    Log.e(TAG, "Failed to load requests: " + e.getMessage(), e);

                    // Show error message
                    Toast.makeText(JoinRequestActivity.this,
                            "Failed to load requests: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Show empty state
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    txtRequestCount.setText("Error loading requests");
                });
    }

    private void filterRequests() {
        filteredList.clear();

        // Filter based on current selection
        for (JoinRequestModel model : allList) {
            String status = model.getStatus();
            if (status == null) {
                status = "pending";
            }

            if (currentFilter.equals("all")) {
                filteredList.add(model);
            } else if (currentFilter.equals(status)) {
                filteredList.add(model);
            }
        }

        Log.d(TAG, "Filtered " + filteredList.size() + " requests with filter: " + currentFilter);

        // Update UI based on filtered data
        if (filteredList.isEmpty()) {
            // Show empty state
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            String emptyMessage = getEmptyMessage();
            txtRequestCount.setText(emptyMessage);
        } else {
            // Show RecyclerView with data
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Update count
            int count = filteredList.size();
            String countText = count + (count == 1 ? " request" : " requests");

            if (!currentFilter.equals("all")) {
                countText += " (" + currentFilter + ")";
            }

            txtRequestCount.setText(countText);

            // Set adapter with filtered list
            adapter = new JoinRequestAdapter(JoinRequestActivity.this, filteredList);
            recyclerView.setAdapter(adapter);
        }
    }

    private String getEmptyMessage() {
        switch (currentFilter) {
            case "pending":
                return "No pending requests";
            case "accepted":
                return "No accepted requests";
            case "rejected":
                return "No rejected requests";
            default:
                return "No requests found";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload requests when returning to this activity
        loadAllRequests();
    }
}