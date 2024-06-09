package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_AddOns;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_Booking extends RecyclerView.Adapter<Adapter_Booking.MyViewHolder> {
    private Context context;
    private List<Model_Booking> bookingList;
    private DatabaseReference contactInfo_DBref;
    private DatabaseReference allRooms_DBref;
    private DatabaseReference recomm_DBref;
    public Adapter_Booking(Context context, List<Model_Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        contactInfo_DBref = FirebaseDatabase.getInstance().getReference("Contact Information");
        allRooms_DBref = FirebaseDatabase.getInstance().getReference("AllRooms");
        recomm_DBref = FirebaseDatabase.getInstance().getReference("RecommRooms");
    }

    @NonNull
    @Override
    public Adapter_Booking.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new Adapter_Booking.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Booking.MyViewHolder holder, int position) {
        Model_Booking booking = bookingList.get(position);
        holder.roomName.setText(booking.getRoom());
        holder.status.setText(booking.getStatus());
        fetchContactInfo(booking.getContact_id(), holder);
        fetchRoom(booking.getRoom(), holder);
    }

    private void fetchContactInfo(String contactID, Adapter_Booking.MyViewHolder holder) {
        contactInfo_DBref.child(contactID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_ContactInfo contactInfo = snapshot.getValue(Model_ContactInfo.class);
                    if (contactInfo != null) {
                        holder.guestName.setText(contactInfo.getFirstName() + " " + contactInfo.getLastName());
                    } else {
                        Log.e("Adapter_Booking", "Contact ID is null for booking: " + contactID);
                    }
                } else {
                    Log.e("Adapter_Booking", "Contact ID does not exist for booking: " + contactID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_Booking", "Failed to retrieve Contact ID: " + error.getMessage());
            }
        });
    }

    private void fetchRoom(String roomID, Adapter_Booking.MyViewHolder holder) {
        allRooms_DBref.child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_Room room = snapshot.getValue(Model_Room.class);
                    if (room != null) {
                        holder.roomTitle.setText((room.getTitle()));
                    } else {
                        Log.e("Adapter_Booking", "Room ID is null for booking: " + roomID);
                    }
                } else {
                    recomm_DBref.child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Model_Room room = snapshot.getValue(Model_Room.class);
                                if (room != null) {
                                    holder.roomTitle.setText((room.getTitle()));
                                } else {
                                    Log.e("Adapter_Booking", "Room ID is null for booking: " + roomID);
                                }
                            } else {
                                Log.e("Adapter_Booking", "Room ID does not exist for booking: " + roomID);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Adapter_Booking", "Failed to retrieve Room ID: " + error.getMessage());
                        }
                    });                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_Booking", "Failed to retrieve Room ID: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName, roomTitle, guestName, status;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            guestName = itemView.findViewById(R.id.guestName);
            status = itemView.findViewById(R.id.status);
        }
    }
}
