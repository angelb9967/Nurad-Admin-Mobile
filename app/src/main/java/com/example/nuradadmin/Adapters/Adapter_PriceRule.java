package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreatePriceRules;
import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.R;

import java.util.List;

public class Adapter_PriceRule extends RecyclerView.Adapter<Adapter_PriceRule.MyViewHolder> {
    private Context context;
    private List<Model_PriceRule> modelPriceRulesList;

    public Adapter_PriceRule(Context context, List<Model_PriceRule> modelPriceRulesList) {
        this.context = context;
        this.modelPriceRulesList = modelPriceRulesList;
    }

    @NonNull
    @Override
    public Adapter_PriceRule.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pricerule, parent, false);
        return new Adapter_PriceRule.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_PriceRule.MyViewHolder holder, int position) {
        Model_PriceRule modelPriceRule = modelPriceRulesList.get(position);
        holder.priceRule.setText(modelPriceRule.getRuleName());
        holder.price.setText(String.valueOf(modelPriceRule.getPrice()));

        holder.priceRule_layout.setOnClickListener(v -> {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView priceRule, price;
        CardView priceRule_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            priceRule = itemView.findViewById(R.id.priceRule);
            price = itemView.findViewById(R.id.price);
            priceRule_layout = itemView.findViewById(R.id.priceRule_layout);
        }
    }
}
