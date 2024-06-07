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
import androidx.core.content.ContextCompat;
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
    private Map<String, String> selectedAddOns; // To store selected add-ons with their ID and Title
    public Adapter_AddOn(Context context, List<Model_AddOns> addOnList) {
        this.context = context;
        this.addOnList = addOnList;
        selectedAddOns = new HashMap<>();
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

        holder.addOn_Btn.setOnClickListener(v ->{
            if(holder.addOn_Btn.getText().equals("ADD")){
                holder.addOn_Btn.setText("✔");
                holder.addOn_background.setBackgroundColor(Color.parseColor("#FBE3F3"));
                selectedAddOns.put(addOns.getId(), addOns.getTitle());
            } else {
                holder.addOn_Btn.setText("ADD");
                holder.addOn_background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                selectedAddOns.remove(addOns.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addOnList.size();
    }
    public Map<String, String> getSelectedAddOns() {
        return selectedAddOns;
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
}
