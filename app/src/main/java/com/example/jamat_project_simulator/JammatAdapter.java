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

        // 🔹 Bind data
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
                    // 🔹 Open Update Activity
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
                    // 🔹 Show confirmation dialog
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Jammat")
                            .setMessage("Are you sure you want to delete this Jammat?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Delete from Firestore
                                db.collection("Jammat").document(model.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Deleted " + model.getName(), Toast.LENGTH_SHORT).show();
                                            list.remove(position);
                                            notifyItemRemoved(position);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to delete " + model.getName(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                    return true;
                }
                return false;
            });

            popup.show();
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
