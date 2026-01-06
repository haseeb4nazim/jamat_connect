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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<JoinRequestModel> list;
    private MyRequestsAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private TextView txtRequestCount;

    private static final String TAG = "MyRequestsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_my_requests);

            // Initialize Firebase
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

            // Initialize views
            recyclerView = findViewById(R.id.requestRecyclerView);
            progressBar = findViewById(R.id.progressBar);
            emptyStateLayout = findViewById(R.id.emptyStateLayout);
            txtRequestCount = findViewById(R.id.txtRequestCount);

            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);

            list = new ArrayList<>();

            // Load user's requests
            loadMyRequests();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadMyRequests() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Loading requests for userId: " + userId);

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        // Query ALL user's requests (pending, accepted, rejected)
        db.collection("JoinRequests")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);

                    list.clear();

                    Log.d(TAG, "Found " + queryDocumentSnapshots.size() + " requests");

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        JoinRequestModel model = doc.toObject(JoinRequestModel.class);

                        // Set document ID
                        if (model != null) {
                            model.setRequestId(doc.getId());
                            list.add(model);
                            Log.d(TAG, "Request: " + model.getJammatName() + " - Status: " + model.getStatus());
                        }
                    }

                    // Update UI
                    if (list.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        txtRequestCount.setText("No requests found");
                        Log.d(TAG, "No requests found - showing empty state");
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        int count = list.size();
                        txtRequestCount.setText(count + (count == 1 ? " request" : " requests"));

                        adapter = new MyRequestsAdapter(MyRequestsActivity.this, list);
                        recyclerView.setAdapter(adapter);

                        Log.d(TAG, "Showing " + count + " requests in RecyclerView");
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Failed to load requests: " + e.getMessage(), e);
                    Toast.makeText(MyRequestsActivity.this,
                            "Failed to load requests: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    txtRequestCount.setText("Error loading requests");
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            loadMyRequests();
        }
    }
}