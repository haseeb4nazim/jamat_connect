package com.example.jamat_project_simulator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class JammatAdapter extends RecyclerView.Adapter<JammatAdapter.ViewHolder> {

    private ArrayList<JammatModel> list;
    private Context context;
    private FirebaseFirestore db;

    public JammatAdapter(Context context, ArrayList<JammatModel> list) {
        this.context = context;
        this.list = list;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jamat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JammatModel model = list.get(position);

        // Bind data
        holder.txtName.setText(model.getName());
        holder.txtRoute.setText(model.getStartCity() + " → " + model.getEndCity());
        holder.txtDate.setText(model.getStartDate());

        // Dots menu click
        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.jammat_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_update) {
                    // Open Update Activity
                    Intent intent = new Intent(context, UpdateJammatActivity.class);
                    intent.putExtra("jammatId", model.getId());
                    intent.putExtra("name", model.getName());
                    intent.putExtra("description", model.getDescription());
                    intent.putExtra("startCity", model.getStartCity());
                    intent.putExtra("endCity", model.getEndCity());
                    intent.putExtra("startDate", model.getStartDate());
                    intent.putExtra("endDate", model.getEndDate());
                    context.startActivity(intent);
                    return true;

                } else if (id == R.id.menu_delete) {
                    // Show confirmation dialog with request count
                    showDeleteConfirmation(model, position);
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    private void showDeleteConfirmation(JammatModel model, int position) {
        // First, count how many requests are associated with this Jamat
        db.collection("JoinRequests")
                .whereEqualTo("jammatId", model.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int requestCount = queryDocumentSnapshots.size();

                    // Show confirmation dialog with count
                    String message = "Are you sure you want to delete this Jammat?";
                    if (requestCount > 0) {
                        message += "\n\nThis will also delete " + requestCount +
                                (requestCount == 1 ? " related request." : " related requests.");
                    }

                    new AlertDialog.Builder(context)
                            .setTitle("Delete Jammat")
                            .setMessage(message)
                            .setPositiveButton("Delete", (dialog, which) -> {
                                deleteJammatAndRequests(model, position, requestCount);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .addOnFailureListener(e -> {
                    // If counting fails, still allow deletion
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Jammat")
                            .setMessage("Are you sure you want to delete this Jammat?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                deleteJammatAndRequests(model, position, 0);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });
    }

    private void deleteJammatAndRequests(JammatModel model, int position, int requestCount) {
        // Step 1: Delete the Jamat
        db.collection("Jammat").document(model.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Step 2: Delete all related requests
                    if (requestCount > 0) {
                        deleteRelatedRequests(model.getId(), model.getName(), position);
                    } else {
                        // No requests to delete, just update UI
                        Toast.makeText(context, "Deleted " + model.getName(), Toast.LENGTH_SHORT).show();
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete " + model.getName(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteRelatedRequests(String jammatId, String jammatName, int position) {
        db.collection("JoinRequests")
                .whereEqualTo("jammatId", jammatId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int deletedCount = 0;
                    int totalRequests = queryDocumentSnapshots.size();

                    if (totalRequests == 0) {
                        // No requests found
                        Toast.makeText(context, "Deleted " + jammatName, Toast.LENGTH_SHORT).show();
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                        return;
                    }

                    // Delete each request
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Request deleted
                                })
                                .addOnFailureListener(e -> {
                                    // Ignore individual failures
                                });
                    }

                    // Show success message and update UI
                    String message = "Deleted " + jammatName;
                    if (totalRequests > 0) {
                        message += " and " + totalRequests + (totalRequests == 1 ? " request" : " requests");
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                })
                .addOnFailureListener(e -> {
                    // Even if request deletion fails, Jamat is already deleted
                    Toast.makeText(context, "Deleted " + jammatName + " (some requests may remain)",
                            Toast.LENGTH_LONG).show();
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtRoute, txtDate;
        ImageView btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtRoute = itemView.findViewById(R.id.txtRoute);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}