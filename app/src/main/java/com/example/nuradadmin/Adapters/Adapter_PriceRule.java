package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreatePriceRules;
import com.example.nuradadmin.Activities.List_and_Create.Activity_PriceRules;
import com.example.nuradadmin.Activities.List_and_Create.Activity_RoomTypes;
import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Adapter_PriceRule extends RecyclerView.Adapter<Adapter_PriceRule.MyViewHolder> {
    private Context context;
    private List<Model_PriceRule> modelPriceRulesList;
    private Adapter_PriceRule.OnLongItemClickListener longItemClickListener;
    private Adapter_PriceRule.OnSelectionChangedListener selectionChangedListener;
    private List<Integer> selectedItems = new ArrayList<>();

    public Adapter_PriceRule(@NonNull Context context, List<Model_PriceRule> modelPriceRulesList,
                             Adapter_PriceRule.OnLongItemClickListener longItemClickListener,
                             Adapter_PriceRule.OnSelectionChangedListener selectionChangedListener) {
        this.context = context;
        this.modelPriceRulesList = modelPriceRulesList;
        this.longItemClickListener = longItemClickListener;
        this.selectionChangedListener = selectionChangedListener;
    }

    @NonNull
    @Override
    public Adapter_PriceRule.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pricerule, parent, false);
        return new Adapter_PriceRule.MyViewHolder(view, longItemClickListener);


    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_PriceRule.MyViewHolder holder, int position) {
        Model_PriceRule modelPriceRule = modelPriceRulesList.get(position);
        holder.priceRule.setText(modelPriceRule.getRuleName());

        holder.checkbox.setOnCheckedChangeListener(null); // Clear previous listener

        if (Activity_PriceRules.isContextualModeEnabled && selectedItems.contains(position)) {
            holder.checkbox.setChecked(true);
            holder.background.setBackgroundColor(Color.parseColor("#FBE3F3"));
        } else {
            holder.checkbox.setChecked(false);
            holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        // Format the Price
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        holder.price.setText(decimalFormat.format(modelPriceRule.getPrice()));

        if (!Activity_PriceRules.isContextualModeEnabled) {
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

            holder.background.setOnLongClickListener(v -> {
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.checkbox.setChecked(false);
                return false;
            });
        }

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(position);
                holder.background.setBackgroundColor(Color.parseColor("#FBE3F3"));
            } else {
                selectedItems.remove((Integer) position);
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
            selectionChangedListener.onSelectionChanged(selectedItems.size());
        });

        holder.edit_btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, Activity_CreatePriceRules.class);
            intent.putExtra("Purpose", "View Details");
            intent.putExtra("Rule Name", modelPriceRulesList.get(holder.getAdapterPosition()).getRuleName());
            intent.putExtra("Price", modelPriceRulesList.get(holder.getAdapterPosition()).getPrice());
            intent.putExtra("Extra Adult Price", modelPriceRulesList.get(holder.getAdapterPosition()).getExtraAdult_price());
            intent.putExtra("Extra Child Price", modelPriceRulesList.get(holder.getAdapterPosition()).getExtraChild_price());
            intent.putExtra("Friday Price", modelPriceRulesList.get(holder.getAdapterPosition()).getFriday_price());
            intent.putExtra("Saturday Price", modelPriceRulesList.get(holder.getAdapterPosition()).getSaturday_price());
            intent.putExtra("Sunday Price", modelPriceRulesList.get(holder.getAdapterPosition()).getSunday_price());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelPriceRulesList.size();
    }

    public List<Model_PriceRule> getSelectedItems() {
        List<Model_PriceRule> selectedPriceRules = new ArrayList<>();
        for (int i : selectedItems) {
            selectedPriceRules.add(modelPriceRulesList.get(i));
        }
        return selectedPriceRules;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        CheckBox checkbox;
        RelativeLayout background;
        TextView priceRule, price;
        Button edit_btn;
        OnLongItemClickListener longItemClickListener;
        public MyViewHolder(@NonNull View itemView, Adapter_PriceRule.OnLongItemClickListener longItemClickListener) {
            super(itemView);
            priceRule = itemView.findViewById(R.id.priceRule);
            price = itemView.findViewById(R.id.price);
            edit_btn = itemView.findViewById(R.id.editBtn);
            checkbox = itemView.findViewById(R.id.priceRuleCheckBox);
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
