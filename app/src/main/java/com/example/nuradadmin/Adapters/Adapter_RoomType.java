package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRoom;
import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRoomType;
import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;

import java.util.List;

public class Adapter_RoomType extends RecyclerView.Adapter<Adapter_RoomType.MyViewHolder> {
    private Context context;
    private List<Model_RoomType> modelRoomTypeList;

    public Adapter_RoomType(@NonNull Context context, List<Model_RoomType> modelRoomTypeList) {
        this.context = context;
        this.modelRoomTypeList = modelRoomTypeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roomtype, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_RoomType modelRoomType = modelRoomTypeList.get(position);
        holder.roomTitle.setText(modelRoomType.getRoomType_Name());
        holder.roomDescrip.setText(modelRoomType.getDescription());

        holder.roomType_layout.setOnClickListener(v -> {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView roomTitle, roomDescrip;
        CardView roomType_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            roomDescrip = itemView.findViewById(R.id.roomTypeDescription);
            roomType_layout = itemView.findViewById(R.id.roomType_layout);
        }
    }
}

