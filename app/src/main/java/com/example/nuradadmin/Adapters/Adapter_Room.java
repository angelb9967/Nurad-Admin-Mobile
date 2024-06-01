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

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRoom;
import com.example.nuradadmin.Activities.List_and_Create.Activity_RoomTypes;
import com.example.nuradadmin.Activities.List_and_Create.Activity_Rooms;
import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.Models.Model_RoomType;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Models.Model_Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_Room extends RecyclerView.Adapter<Adapter_Room.RoomViewHolder> {
    private DatabaseReference priceRules_DBref;
    private Context context;
    private List<Model_Room> roomList;
    private Adapter_Room.OnLongItemClickListener longItemClickListener;
    private Adapter_Room.OnSelectionChangedListener selectionChangedListener;
    private List<Integer> selectedItems = new ArrayList<>();

    public Adapter_Room(Context context, List<Model_Room> roomList,
                        Adapter_Room.OnLongItemClickListener longItemClickListener,
                        Adapter_Room.OnSelectionChangedListener selectionChangedListener) {
        this.context = context;
        this.roomList = roomList;
        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");
        this.longItemClickListener = longItemClickListener;
        this.selectionChangedListener = selectionChangedListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view,longItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Model_Room room = roomList.get(position);
        holder.roomTitle.setText(room.getTitle());
        holder.roomDescription.setText(room.getDescription());
        if (room.isDepositRequired()) {
            holder.depositRequired.setVisibility(View.VISIBLE);
        } else {
            holder.depositRequired.setText("Deposit not required");
        }

        // Ensure imageUrl is not null or empty before loading
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Picasso.get().load(room.getImageUrl()).into(holder.roomImage);
        } else {
            // Set a placeholder or error image if imageUrl is not valid
            holder.roomImage.setImageResource(R.drawable.logo_purple);
        }

        // Fetch price rule for the room
        fetchPriceRule(room.getPriceRule(), holder);

        holder.checkbox.setOnCheckedChangeListener(null); // Clear previous listener

        if (Activity_Rooms.isContextualModeEnabled && selectedItems.contains(position)) {
            holder.checkbox.setChecked(true);
            holder.background.setBackgroundColor(Color.parseColor("#FBE3F3"));
        } else {
            holder.checkbox.setChecked(false);
            holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!Activity_Rooms.isContextualModeEnabled) {
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

        holder.editBtn.setOnClickListener(view ->{
            Intent intent = new Intent(context, Activity_CreateRoom.class);
            intent.putExtra("Purpose", "View Details");
            intent.putExtra("Image", roomList.get(holder.getAdapterPosition()).getImageUrl());
            intent.putExtra("Room Number", roomList.get(holder.getAdapterPosition()).getRoomName());
            intent.putExtra("Room Title",roomList.get(holder.getAdapterPosition()).getTitle());
            intent.putExtra("Description",roomList.get(holder.getAdapterPosition()).getDescription());
            intent.putExtra("Room Type",roomList.get(holder.getAdapterPosition()).getRoomType());
            intent.putExtra("Price Rule",roomList.get(holder.getAdapterPosition()).getPriceRule());
            intent.putExtra("Deposit Required?", roomList.get(holder.getAdapterPosition()).isDepositRequired());
            intent.putExtra("Recommend Room?", roomList.get(holder.getAdapterPosition()).isRecommended());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public List<Model_Room> getSelectedItems() {
        List<Model_Room> selectedRooms = new ArrayList<>();
        for (int i : selectedItems) {
            selectedRooms.add(roomList.get(i));
        }
        return selectedRooms;
    }

    private void fetchPriceRule(String priceRuleName, RoomViewHolder holder) {
        priceRules_DBref.child(priceRuleName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_PriceRule priceRule = snapshot.getValue(Model_PriceRule.class);
                    if (priceRule != null) {
                        // Get the appropriate price based on the current day of the week
                        double price = getPriceForCurrentDay(priceRule);
                        String formattedPrice = formatPrice(price);
                        holder.roomPrice.setText("₱" + formattedPrice);
                    } else {
                        Log.e("Adapter_Room", "Price rule is null for room: " + priceRuleName);
                    }
                } else {
                    Log.e("Adapter_Room", "Price rule does not exist for room: " + priceRuleName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_Room", "Failed to retrieve price rule: " + error.getMessage());
            }
        });
    }

    private double getPriceForCurrentDay(Model_PriceRule priceRule) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.FRIDAY:
                return priceRule.getFriday_price();
            case Calendar.SATURDAY:
                return priceRule.getSaturday_price();
            case Calendar.SUNDAY:
                return priceRule.getSunday_price();
            default:
                return priceRule.getPrice();
        }
    }

    private String formatPrice(double price) {
        // Format the price
        return String.format(Locale.US, "%.2f", price);
    }

    public void updateData(List<Model_Room> newRoomList) {
        roomList.clear();
        roomList.addAll(newRoomList);
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        CheckBox checkbox;
        RelativeLayout background;
        ImageView roomImage;
        TextView roomTitle;
        TextView depositRequired;
        TextView roomDescription;
        TextView roomPrice;
        TextView priceDetails;
        Button editBtn;
        Adapter_Room.OnLongItemClickListener longItemClickListener;
        public RoomViewHolder(@NonNull View itemView, Adapter_Room.OnLongItemClickListener longItemClickListener) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            depositRequired = itemView.findViewById(R.id.depositRequired);
            roomDescription = itemView.findViewById(R.id.roomDescription);
            roomPrice = itemView.findViewById(R.id.roomPrice);
            priceDetails = itemView.findViewById(R.id.priceDetails);
            editBtn = itemView.findViewById(R.id.editBtn);
            checkbox = itemView.findViewById(R.id.roomCheckBox);
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