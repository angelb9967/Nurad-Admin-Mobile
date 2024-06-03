package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRevenueCost;
import com.example.nuradadmin.Activities.List_and_Create.Activity_PriceRules;
import com.example.nuradadmin.Activities.SideMenu.Activity_OtherRevenue;
import com.example.nuradadmin.Models.Models_RevenueCost;
import com.example.nuradadmin.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Adapter_RevenueCost extends RecyclerView.Adapter<Adapter_RevenueCost.MyViewHolder>{
    private Context context;
    private List<Models_RevenueCost> revenueCostList;
    private Adapter_RevenueCost.OnLongItemClickListener longItemClickListener;
    private Adapter_RevenueCost.OnSelectionChangedListener selectionChangedListener;
    private List<Integer> selectedItems = new ArrayList<>();

    public Adapter_RevenueCost(Context context, List<Models_RevenueCost> revenueCostList,
                               Adapter_RevenueCost.OnLongItemClickListener longItemClickListener,
                               Adapter_RevenueCost.OnSelectionChangedListener selectionChangedListener) {
        this.context = context;
        this.revenueCostList = revenueCostList;
        this.longItemClickListener = longItemClickListener;
        this.selectionChangedListener = selectionChangedListener;
    }

    @NonNull
    @Override
    public Adapter_RevenueCost.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenuecost, parent, false);
        return new Adapter_RevenueCost.MyViewHolder(view, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_RevenueCost.MyViewHolder holder, int position) {
        Models_RevenueCost modelRevenueCost = revenueCostList.get(position);

        holder.checkbox.setOnCheckedChangeListener(null); // Clear previous listener

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

        if (Activity_OtherRevenue.isContextualModeEnabled && selectedItems.contains(position)) {
            holder.checkbox.setChecked(true);
            holder.background.setBackgroundColor(Color.parseColor("#FBE3F3"));
        } else {
            holder.checkbox.setChecked(false);
            holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!Activity_OtherRevenue.isContextualModeEnabled) {
            // CONTEXTUAL MODE: FALSE
            holder.checkbox.setVisibility(View.GONE);
            holder.background.setOnClickListener(v -> {
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            });

            holder.background.setOnLongClickListener(v -> {
                if (!selectedItems.contains(position)) {
                    // Add the item to selectedItems only if it's not already selected
                    selectedItems.add(position);
                    selectionChangedListener.onSelectionChanged(selectedItems.size());
                }
                Activity_PriceRules.isContextualModeEnabled = true;
                longItemClickListener.onLongItemClick(position);
                notifyDataSetChanged();
                return true;
            });
        } else {
            // CONTEXTUAL MODE: TRUE
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.background.setOnClickListener(v -> {
                if (holder.checkbox.isChecked()) {
                    selectedItems.remove((Integer) position);
                } else {
                    selectedItems.add(position);
                }
                selectionChangedListener.onSelectionChanged(selectedItems.size());
                notifyDataSetChanged();
            });
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

    public List<Models_RevenueCost> getSelectedItems() {
        List<Models_RevenueCost> selectedRevenueCost = new ArrayList<>();
        for (int i : selectedItems) {
            selectedRevenueCost.add(revenueCostList.get(i));
        }
        return selectedRevenueCost;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        CheckBox checkbox;
        RelativeLayout background;
        ImageView icon;
        TextView amount, date, note;
        Button edit_btn;
        Adapter_RevenueCost.OnLongItemClickListener longItemClickListener;
        public MyViewHolder(@NonNull View itemView, Adapter_RevenueCost.OnLongItemClickListener longItemClickListener) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            note = itemView.findViewById(R.id.note);
            edit_btn = itemView.findViewById(R.id.editBtn);
            checkbox = itemView.findViewById(R.id.revenueCostcheckbox);
            background = itemView.findViewById(R.id.relativeLayout);
            this.longItemClickListener = longItemClickListener;
            itemView.setOnLongClickListener(this);
        }
        @Override
        public boolean onLongClick(View v) {
            longItemClickListener.onLongItemClick(getAdapterPosition());
            return true;
        }
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(int position);
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public void clearSelectedItems() {
        selectedItems.clear();
    }
}
