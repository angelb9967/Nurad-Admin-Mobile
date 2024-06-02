package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRevenueCost;
import com.example.nuradadmin.Models.Models_RevenueCost;
import com.example.nuradadmin.R;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.List;

public class Adapter_RevenueCost extends RecyclerView.Adapter<Adapter_RevenueCost.MyViewHolder>{
    private DatabaseReference revenueCost_DBref;
    private Context context;
    private List<Models_RevenueCost> revenueCostList;

    public Adapter_RevenueCost(Context context, List<Models_RevenueCost> revenueCostList) {
        this.context = context;
        this.revenueCostList = revenueCostList;
    }

    @NonNull
    @Override
    public Adapter_RevenueCost.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenuecost, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_RevenueCost.MyViewHolder holder, int position) {
        Models_RevenueCost modelRevenueCost = revenueCostList.get(position);

        // Format the Amount
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        holder.amount.setText("₱" + decimalFormat.format(modelRevenueCost.getAmount()));

        holder.date.setText(modelRevenueCost.getDate() + " " + modelRevenueCost.getTime());
        holder.note.setText(modelRevenueCost.getNote());

        String type = modelRevenueCost.getTransactionType();
        if (type.equalsIgnoreCase("Revenue")){
            holder.icon.setImageResource(R.drawable.arrow_trend_up);
        } else if (type.equalsIgnoreCase("Cost")) {
            holder.icon.setImageResource(R.drawable.arrow_trend_down);
        } else {
            Log.e("Adapter_RevenueCost.java", "Can't identify Transaction Type");
        }

        holder.edit_btn.setOnClickListener(view ->{
            Intent intent = new Intent(context, Activity_CreateRevenueCost.class);
            intent.putExtra("Purpose", "View Details");
            intent.putExtra("Primary Key", revenueCostList.get(holder.getAdapterPosition()).getPrimaryKey());
            intent.putExtra("Date", revenueCostList.get(holder.getAdapterPosition()).getDate());
            intent.putExtra("Time", revenueCostList.get(holder.getAdapterPosition()).getTime());
            intent.putExtra("Transaction Type", revenueCostList.get(holder.getAdapterPosition()).getTransactionType());
            intent.putExtra("Amount", revenueCostList.get(holder.getAdapterPosition()).getAmount());
            intent.putExtra("Note",revenueCostList.get(holder.getAdapterPosition()).getNote());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return revenueCostList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView amount, date, note;
        Button edit_btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            note = itemView.findViewById(R.id.note);
            edit_btn = itemView.findViewById(R.id.editBtn);
        }
    }
}
