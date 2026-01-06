package com.example.jamat_project_simulator;

import android.os.Bundle;
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

public class ViewAvailableJammatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<JammatModel> list;
    private UserJammatAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private TextView txtJamatCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_available_jammats);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.jamatRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        txtJamatCount = findViewById(R.id.txtJamatCount);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();

        // Load jamats
        loadJamats();
    }

    private void loadJamats() {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        db.collection("Jammat")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);

                    list.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        JammatModel model = doc.toObject(JammatModel.class);

                        // VERY IMPORTANT: Set document ID
                        if (model != null) {
                            model.setId(doc.getId());
                            list.add(model);
                        }
                    }

                    // Update UI based on data
                    if (list.isEmpty()) {
                        // Show empty state
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        txtJamatCount.setText("No jamats available");
                    } else {
                        // Show RecyclerView with data
                        emptyStateLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        // Update count
                        int count = list.size();
                        txtJamatCount.setText(count + (count == 1 ? " jamat available" : " jamats available"));

                        // Set adapter with current user ID
                        String currentUserId = auth.getCurrentUser() != null ?
                                auth.getCurrentUser().getUid() : "";

                        adapter = new UserJammatAdapter(
                                ViewAvailableJammatsActivity.this,
                                list,
                                currentUserId
                        );
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(e -> {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);

                    // Show error message
                    Toast.makeText(ViewAvailableJammatsActivity.this,
                            "Failed to load jamats: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Show empty state
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    txtJamatCount.setText("Error loading jamats");
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload jamats when returning to this activity
        loadJamats();
    }
}