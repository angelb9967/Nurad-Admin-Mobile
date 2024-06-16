package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_AddOns;
import com.example.nuradadmin.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_AddOn extends RecyclerView.Adapter<Adapter_AddOn.MyViewHolder> {
    private Context context;
    private List<Model_AddOns> addOnList;
    private Map<String, Model_AddOns> selectedAddOns;
    private OnAddOnSelectionChangedListener onAddOnSelectionChangedListener;

    public Adapter_AddOn(Context context, List<Model_AddOns> addOnList, OnAddOnSelectionChangedListener listener) {
        this.context = context;
        this.addOnList = addOnList;
        this.selectedAddOns = new HashMap<>();
        this.onAddOnSelectionChangedListener = listener;
    }

    @NonNull
    @Override
    public Adapter_AddOn.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addon, parent, false);
        return new Adapter_AddOn.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_AddOn.MyViewHolder holder, int position) {
        Model_AddOns addOns = addOnList.get(position);
        holder.addOn_Title.setText(addOns.getTitle());
        holder.addOn_Subheading.setText(addOns.getSubheading());
        holder.addOn_Description.setText(addOns.getDescription());
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        holder.addOn_Price.setText("₱" + decimalFormat.format(addOns.getPrice()));

        if (addOns.getImageUrl() != null && !addOns.getImageUrl().isEmpty()) {
            Picasso.get().load(addOns.getImageUrl()).into(holder.addOn_Image);
        } else {
            // Set a placeholder or error image if imageUrl is not valid
            holder.addOn_Image.setImageResource(R.drawable.logo_purple);
        }

        // Check if the add-on is selected and update UI
        if (selectedAddOns.containsKey(addOns.getId())) {
            holder.addOn_Btn.setText("✔");
            holder.addOn_background.setBackgroundColor(Color.parseColor("#FBE3F3"));
        } else {
            holder.addOn_Btn.setText("ADD");
            holder.addOn_background.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        }

        holder.addOn_Btn.setOnClickListener(v -> {
            if (selectedAddOns.containsKey(addOns.getId())) {
                selectedAddOns.remove(addOns.getId());
            } else {
                selectedAddOns.put(addOns.getId(), addOns);
            }
            notifyDataSetChanged();
            if (onAddOnSelectionChangedListener != null) {
                onAddOnSelectionChangedListener.onSelectionChanged(calculateTotalPrice());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addOnList.size();
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (Model_AddOns addOn : selectedAddOns.values()) {
            total += addOn.getPrice();
        }
        return total;
    }

    public Map<String, Model_AddOns> getSelectedAddOns() {
        return selectedAddOns;
    }

    public void clearSelectedAddOns() {
        selectedAddOns.clear();
        notifyDataSetChanged();
        if (onAddOnSelectionChangedListener != null) {
            onAddOnSelectionChangedListener.onSelectionChanged(0);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout addOn_background;
        private TextView addOn_Title, addOn_Subheading, addOn_Description, addOn_Price;
        private ImageView addOn_Image;
        private Button addOn_Btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            addOn_background = itemView.findViewById(R.id.relativeLayout);
            addOn_Title = itemView.findViewById(R.id.addOnTitle);
            addOn_Subheading = itemView.findViewById(R.id.addOnSubheading);
            addOn_Description = itemView.findViewById(R.id.addOnDescription);
            addOn_Price = itemView.findViewById(R.id.addOnPrice);
            addOn_Image = itemView.findViewById(R.id.addOnImage);
            addOn_Btn = itemView.findViewById(R.id.addBtn);
        }
    }

    public interface OnAddOnSelectionChangedListener {
        void onSelectionChanged(double totalPrice);
    }
}