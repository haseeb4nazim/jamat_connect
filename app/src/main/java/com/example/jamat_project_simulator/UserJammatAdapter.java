package com.example.jamat_project_simulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UserJammatAdapter extends RecyclerView.Adapter<UserJammatAdapter.ViewHolder> {

    private ArrayList<JammatModel> list;
    private Context context;
    private String currentUserId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserJammatAdapter(Context context, ArrayList<JammatModel> list, String currentUserId) {
        this.context = context;
        this.list = list;
        this.currentUserId = currentUserId;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_jamat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JammatModel model = list.get(position);

        // Bind data
        holder.txtName.setText(model.getName());
        holder.txtRoute.setText(model.getStartCity() + " → " + model.getEndCity());
        holder.txtDates.setText(model.getStartDate() + " to " + model.getEndDate());
        holder.txtDescription.setText(model.getDescription());

        // Check request status for this Jamat
        checkRequestStatus(holder, model.getId());

        // Request button click
        holder.btnRequest.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            sendJoinRequest(holder, model);
        });
    }

    private void checkRequestStatus(ViewHolder holder, String jammatId) {
        // Query to check if user already sent request for this Jamat
        db.collection("JoinRequests")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("jammatId", jammatId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Request exists - get status
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                        String status = doc.getString("status");

                        if ("pending".equals(status)) {
                            // Request pending
                            holder.btnRequest.setText("Request Sent");
                            holder.btnRequest.setEnabled(false);
                            holder.btnRequest.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                        } else if ("accepted".equals(status)) {
                            // Request accepted
                            holder.btnRequest.setText("Joined ✓");
                            holder.btnRequest.setEnabled(false);
                            holder.btnRequest.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                        } else if ("rejected".equals(status)) {
                            // Request rejected - allow re-request
                            holder.btnRequest.setText("Request Again");
                            holder.btnRequest.setEnabled(true);
                        }
                    } else {
                        // No request found - show normal button
                        holder.btnRequest.setText("Request to Join");
                        holder.btnRequest.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    // If error, show normal button
                    holder.btnRequest.setText("Request to Join");
                    holder.btnRequest.setEnabled(true);
                });
    }

    private void sendJoinRequest(ViewHolder holder, JammatModel model) {
        // Disable button temporarily
        holder.btnRequest.setEnabled(false);
        holder.btnRequest.setText("Sending...");

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            holder.btnRequest.setEnabled(true);
            holder.btnRequest.setText("Request to Join");
            return;
        }

        // Get user info
        String userName = user.getDisplayName();
        String userEmail = user.getEmail();

        // If name is null, extract from email
        if (userName == null || userName.isEmpty()) {
            if (userEmail != null && userEmail.contains("@")) {
                userName = userEmail.substring(0, userEmail.indexOf("@"));
                userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
            } else {
                userName = "User";
            }
        }

        // Create join request object
        JoinRequestModel request = new JoinRequestModel(
                model.getId(),           // jammatId
                model.getName(),         // jammatName
                user.getUid(),          // userId
                userEmail,              // userEmail
                userName,               // userName
                "pending"               // status
        );

        // Save to Firestore
        db.collection("JoinRequests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show();

                    // Update button state
                    holder.btnRequest.setText("Request Sent");
                    holder.btnRequest.setEnabled(false);
                    holder.btnRequest.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to send request: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Reset button
                    holder.btnRequest.setEnabled(true);
                    holder.btnRequest.setText("Request to Join");
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtRoute, txtDates, txtDescription;
        MaterialButton btnRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtRoute = itemView.findViewById(R.id.txtRoute);
            txtDates = itemView.findViewById(R.id.txtDates);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnRequest = itemView.findViewById(R.id.btnRequest);
        }
    }
}