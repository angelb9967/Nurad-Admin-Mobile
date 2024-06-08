package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_InUse;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_InUse extends RecyclerView.Adapter<Adapter_InUse.MyViewHolder> {
    private Context context;
    private List<Model_InUse> inUseList;
    private DatabaseReference bookings_DBref;
    private DatabaseReference contactInfo_DBref;

    public Adapter_InUse(Context context, List<Model_InUse> inUseList) {
        this.context = context;
        this.inUseList = inUseList;
        bookings_DBref = FirebaseDatabase.getInstance().getReference("Booking");
        contactInfo_DBref = FirebaseDatabase.getInstance().getReference("Contact Information");
    }

    @NonNull
    @Override
    public Adapter_InUse.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inuse, parent, false);
        return new Adapter_InUse.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_InUse.MyViewHolder holder, int position) {
        Model_InUse inUse = inUseList.get(position);
        holder.roomName.setText(inUse.getRoomName());
        fetchBooking(inUse.getBooking_id(), holder);
    }

    private void fetchBooking(String bookingID, Adapter_InUse.MyViewHolder holder){
        bookings_DBref.child(bookingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_Booking booking = snapshot.getValue(Model_Booking.class);
                    if (booking != null) {
                        holder.booking_price.setText(String.valueOf(booking.getBookingPrice()));
                        fetchContactInfo(booking.getContact_id(), holder);
                        holder.checkInDate.setText(booking.getCheckInDate());
                        holder.checkOutDate.setText(booking.getCheckOutDate());
                    } else {
                        Log.e("Adapter_InUse", "Booking ID is null for In-Use Room: " + bookingID);
                    }
                } else {
                    Log.e("Adapter_InUse", "Booking ID does not exist for In-Use Room: " + bookingID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_InUse", "Failed to retrieve Contact ID: " + error.getMessage());
            }
        });
    }

    private void fetchContactInfo(String contactID, Adapter_InUse.MyViewHolder holder) {
        contactInfo_DBref.child(contactID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_ContactInfo contactInfo = snapshot.getValue(Model_ContactInfo.class);
                    if (contactInfo != null) {
                        holder.guestName.setText(contactInfo.getFirstName() + " " + contactInfo.getLastName());
                    } else {
                        Log.e("Adapter_InUse", "Contact ID is null for booking: " + contactID);
                    }
                } else {
                    Log.e("Adapter_InUse", "Contact ID does not exist for booking: " + contactID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_InUse", "Failed to retrieve Contact ID: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return inUseList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView roomName, guestName, booking_price, checkInDate, checkOutDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            guestName = itemView.findViewById(R.id.guestName);
            booking_price = itemView.findViewById(R.id.bookingPrice);
            checkInDate = itemView.findViewById(R.id.checkInDate);
            checkOutDate = itemView.findViewById(R.id.checkOutDate);
        }
    }
}
