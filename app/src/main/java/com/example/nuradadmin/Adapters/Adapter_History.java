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
import com.example.nuradadmin.Models.Model_History;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_History extends RecyclerView.Adapter<Adapter_History.MyViewHolder>{
    private Context context;
    private List<Model_History> historyList;
    private DatabaseReference bookings_DBref;
    public Adapter_History(Context context, List<Model_History> historyList ) {
        this.context = context;
        this.historyList = historyList;
        bookings_DBref = FirebaseDatabase.getInstance().getReference("Booking");
    }

    @NonNull
    @Override
    public Adapter_History.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new Adapter_History.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_History.MyViewHolder holder, int position) {
        Model_History history = historyList.get(position);
        holder.roomName.setText(history.getRoomName());
        fetchBooking(history.getBooking_id(), holder);


    }

    private void fetchBooking(String bookingID, Adapter_History.MyViewHolder holder) {
        bookings_DBref.child(bookingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_Booking booking = snapshot.getValue(Model_Booking.class);
                    if (booking != null) {
                        holder.stay.setText(booking.getCheckInDate() + " to " + booking.getCheckOutDate());
                        holder.revenue.setText(String.valueOf(booking.getTotalValue()));
                    } else {
                        Log.e("Adapter_History", "Booking is null for History Record: " + bookingID);
                    }
                } else {
                    Log.e("Adapter_History", "Booking does not exist for History Record: " + bookingID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_History", "Failed to retrieve booking: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName, stay, revenue;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            stay = itemView.findViewById(R.id.stay);
            revenue = itemView.findViewById(R.id.totalPrice);
        }
    }
}
