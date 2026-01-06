package com.example.jamat_project_simulator;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MyRequestsAdapter extends RecyclerView.Adapter<MyRequestsAdapter.ViewHolder> {

    private ArrayList<JoinRequestModel> list;
    private Context context;

    public MyRequestsAdapter(Context context, ArrayList<JoinRequestModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JoinRequestModel model = list.get(position);

        // Bind data
        holder.txtJammatName.setText(model.getJammatName());

        // Format date
        if (model.getRequestDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            holder.txtRequestDate.setText("Requested on: " + sdf.format(model.getRequestDate()));
        } else {
            holder.txtRequestDate.setText("Requested: Just now");
        }

        // Set status with color
        String status = model.getStatus();
        if (status == null) {
            status = "pending";
        }
        holder.txtStatus.setText(status.toUpperCase());

        if ("pending".equals(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
        } else if ("accepted".equals(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        } else if ("rejected".equals(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#F44336")); // Red
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
        }

        // Show response date if available
        if (model.getResponseDate() != null && !"pending".equals(status)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            holder.txtResponseDate.setVisibility(View.VISIBLE);
            holder.txtResponseDate.setText("Response on: " + sdf.format(model.getResponseDate()));
        } else {
            holder.txtResponseDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtJammatName, txtRequestDate, txtStatus, txtResponseDate;
        CardView statusCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJammatName = itemView.findViewById(R.id.txtJammatName);
            txtRequestDate = itemView.findViewById(R.id.txtRequestDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtResponseDate = itemView.findViewById(R.id.txtResponseDate);
            statusCard = itemView.findViewById(R.id.statusCard);
        }
    }
}