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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRoomType;
import com.example.nuradadmin.Activities.List_and_Create.Activity_RoomTypes;
import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_RoomType extends RecyclerView.Adapter<Adapter_RoomType.MyViewHolder> {
    private Context context;
    private List<Model_RoomType> modelRoomTypeList;
    private OnLongItemClickListener longItemClickListener;
    private OnSelectionChangedListener selectionChangedListener;
    private List<Integer> selectedItems = new ArrayList<>();

    public Adapter_RoomType(@NonNull Context context, List<Model_RoomType> modelRoomTypeList,
                            OnLongItemClickListener longItemClickListener,
                            OnSelectionChangedListener selectionChangedListener) {
        this.context = context;
        this.modelRoomTypeList = modelRoomTypeList;
        this.longItemClickListener = longItemClickListener;
        this.selectionChangedListener = selectionChangedListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roomtype, parent, false);
        return new MyViewHolder(view, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_RoomType modelRoomType = modelRoomTypeList.get(position);
        holder.roomType.setText(modelRoomType.getRoomType_Name());
        holder.roomDescrip.setText(modelRoomType.getDescription());

        holder.checkbox.setOnCheckedChangeListener(null); // Clear previous listener

        if (Activity_RoomTypes.isContextualModeEnabled && selectedItems.contains(position)) {
            holder.checkbox.setChecked(true);
            holder.background.setBackgroundColor(Color.parseColor("#FBE3F3"));
        } else {
            holder.checkbox.setChecked(false);
            holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!Activity_RoomTypes.isContextualModeEnabled) {
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
                Activity_RoomTypes.isContextualModeEnabled = true;
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
            Intent intent = new Intent(context, Activity_CreateRoomType.class);
            intent.putExtra("Purpose", "View Details");
            intent.putExtra("Room Type Name", modelRoomTypeList.get(holder.getAdapterPosition()).getRoomType_Name());
            intent.putExtra("Room Type Description", modelRoomTypeList.get(holder.getAdapterPosition()).getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelRoomTypeList.size();
    }

    public List<Model_RoomType> getSelectedItems() {
        List<Model_RoomType> selectedRoomTypes = new ArrayList<>();
        for (int i : selectedItems) {
            selectedRoomTypes.add(modelRoomTypeList.get(i));
        }
        return selectedRoomTypes;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        CheckBox checkbox;
        RelativeLayout background;
        TextView roomType, roomDescrip;
        Button edit_btn;
        OnLongItemClickListener longItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnLongItemClickListener longItemClickListener) {
            super(itemView);
            roomType = itemView.findViewById(R.id.roomType);
            roomDescrip = itemView.findViewById(R.id.roomTypeDescription);
            edit_btn = itemView.findViewById(R.id.editBtn);
            checkbox = itemView.findViewById(R.id.roomTypeCheckBox);
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
