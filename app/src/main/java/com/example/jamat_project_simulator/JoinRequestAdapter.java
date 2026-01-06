package com.example.jamat_project_simulator;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.ViewHolder> {

    private ArrayList<JoinRequestModel> list;
    private Context context;
    private FirebaseFirestore db;

    public JoinRequestAdapter(Context context, ArrayList<JoinRequestModel> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_join_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JoinRequestModel model = list.get(position);

        // Bind data
        holder.txtUserName.setText(model.getUserName());
        holder.txtUserEmail.setText(model.getUserEmail());
        holder.txtJammatName.setText("Jamat: " + model.getJammatName());

        // Format date
        if (model.getRequestDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.txtRequestDate.setText("Requested: " + sdf.format(model.getRequestDate()));
        } else {
            holder.txtRequestDate.setText("Requested: Just now");
        }

        // Get status
        String status = model.getStatus();
        if (status == null) {
            status = "pending";
        }

        // Delete button - always visible
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Request")
                    .setMessage("Are you sure you want to delete this request from " + model.getUserName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteRequest(model, position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Show/hide buttons based on status
        if ("pending".equals(status)) {
            // Show accept/reject buttons
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.statusBadge.setVisibility(View.GONE);

            // Accept button
            holder.btnAccept.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Accept Request")
                        .setMessage("Accept " + model.getUserName() + "'s request to join " + model.getJammatName() + "?")
                        .setPositiveButton("Accept", (dialog, which) -> {
                            acceptRequest(model, position);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            // Reject button
            holder.btnReject.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Reject Request")
                        .setMessage("Reject " + model.getUserName() + "'s request?")
                        .setPositiveButton("Reject", (dialog, which) -> {
                            rejectRequest(model, position);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        } else {
            // Hide buttons and show status badge
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.statusBadge.setVisibility(View.VISIBLE);
            holder.txtStatusBadge.setText(status.toUpperCase());

            // Set badge color
            if ("accepted".equals(status)) {
                holder.txtStatusBadge.setTextColor(Color.parseColor("#4CAF50")); // Green
                holder.statusBadge.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            } else if ("rejected".equals(status)) {
                holder.txtStatusBadge.setTextColor(Color.parseColor("#F44336")); // Red
                holder.statusBadge.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            }
        }
    }

    private void deleteRequest(JoinRequestModel model, int position) {
        // Delete the request from Firestore
        db.collection("JoinRequests")
                .document(model.getRequestId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Request deleted", Toast.LENGTH_SHORT).show();

                    // Remove from list and update UI
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete request: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void acceptRequest(JoinRequestModel model, int position) {
        // 1. Update request status to "accepted"
        db.collection("JoinRequests")
                .document(model.getRequestId())
                .update("status", "accepted", "responseDate", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> {

                    // 2. Add user to JammatMembers collection
                    JammatMemberModel member = new JammatMemberModel(
                            model.getJammatId(),
                            model.getJammatName(),
                            model.getUserId(),
                            model.getUserEmail(),
                            model.getUserName()
                    );

                    db.collection("JammatMembers")
                            .add(member)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(context,
                                        model.getUserName() + " accepted to " + model.getJammatName(),
                                        Toast.LENGTH_SHORT).show();

                                // Update the model in the list
                                model.setStatus("accepted");
                                notifyItemChanged(position);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context,
                                        "Failed to add member: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                            "Failed to accept request: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void rejectRequest(JoinRequestModel model, int position) {
        // Update request status to "rejected"
        db.collection("JoinRequests")
                .document(model.getRequestId())
                .update("status", "rejected", "responseDate", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context,
                            "Request rejected",
                            Toast.LENGTH_SHORT).show();

                    // Update the model in the list
                    model.setStatus("rejected");
                    notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                            "Failed to reject request: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserEmail, txtJammatName, txtRequestDate, txtStatusBadge;
        MaterialButton btnAccept, btnReject;
        CardView statusBadge;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtJammatName = itemView.findViewById(R.id.txtJammatName);
            txtRequestDate = itemView.findViewById(R.id.txtRequestDate);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            txtStatusBadge = itemView.findViewById(R.id.txtStatusBadge);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}