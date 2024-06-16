package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_History;
import com.example.nuradadmin.Models.Model_InUse;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Adapter_InUse extends RecyclerView.Adapter<Adapter_InUse.MyViewHolder> {
    private Context context;
    private List<Model_InUse> inUseList;
    private DatabaseReference bookings_DBref;
    private DatabaseReference contactInfo_DBref;
    private DatabaseReference history_DBref;
    private DatabaseReference inUse_DBref;

    public Adapter_InUse(Context context, List<Model_InUse> inUseList) {
        this.context = context;
        this.inUseList = inUseList;
        bookings_DBref = FirebaseDatabase.getInstance().getReference("Booking");
        contactInfo_DBref = FirebaseDatabase.getInstance().getReference("Contact Information");
        history_DBref = FirebaseDatabase.getInstance().getReference("History");
        inUse_DBref = FirebaseDatabase.getInstance().getReference("InUse Rooms");
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

        // Parse the actual check-in date and time
        String actualCheckInDateTime = inUse.getActualCheckInDateTime(); // e.g., "12/6/2024 8:59 AM"
        Log.d("Adapter_InUse", "Actual check-in date and time: " + actualCheckInDateTime);

        SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy h:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Assuming the original date is in Manila time

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d/M/yyyy h:mm a", Locale.ENGLISH);
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Manila");
        Calendar currentDateTime = Calendar.getInstance(timeZone);
        currentDateTime.setTimeInMillis(System.currentTimeMillis());
        dateTimeFormat.setTimeZone(timeZone);
        String currentTimeStr = dateTimeFormat.format(currentDateTime.getTime());

        try {
            Date checkInDate = formatter.parse(actualCheckInDateTime);
            Log.d("Adapter_InUse", "Parsed check-in date: " + checkInDate);

            // Get the current date and time in Manila time zone
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
            Log.d("Adapter_InUse", "Current date and time in Manila: " + now.getTime());

            // Calculate the duration between the check-in date and the current date
            long durationMillis = now.getTimeInMillis() - checkInDate.getTime();
            long days = durationMillis / (1000 * 60 * 60 * 24);
            long hours = (durationMillis / (1000 * 60 * 60)) % 24;
            long minutes = (durationMillis / (1000 * 60)) % 60;
            long seconds = (durationMillis / 1000) % 60; // Calculate seconds

            // Display the duration of stay
            String durationText = (days > 0 ? days + " days " : "") +
                    (hours > 0 ? hours + " hours " : "") +
                    (minutes > 0 ? minutes + " minutes " : "") +
                    (seconds > 0 ? seconds + " seconds" : "");
            holder.durationOfStay.setText(durationText.trim());
            Log.d("Adapter_InUse", "Duration of stay: " + durationText);

        } catch (ParseException e) {
            Log.e("Adapter_InUse", "Error parsing check-in date: " + e.getMessage());
        }

        holder.optionMenu_Btn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu_inuse, popupMenu.getMenu());

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.checkOut) {

                    updateBookingStatus(inUse.getBooking_id(), "Checked Out");

                    // Save the Room to History
                    String historyId = history_DBref.push().getKey();
                    Model_History modelHistory = new Model_History(historyId, inUse.getBooking_id(), inUse.getRoomName(), (String) holder.durationOfStay.getText(), currentTimeStr, inUse.getActualCheckInDateTime());
                    saveToHistory(historyId, modelHistory);

                    // Remove the Room from the In Use Rooms once checked out.
                    DatabaseReference nodeReference = inUse_DBref.child(inUse.getRoomName());
                    nodeReference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Adapter_InUse", "Node deleted successfully.");
                        } else {
                            Log.e("Adapter_InUse", "Failed to delete node.");
                        }
                    });

                } else if (item.getItemId() == R.id.requestCleaning) {

                } else if (item.getItemId() == R.id.delete) {

                } else {
                    Log.e("Adapter_InUse", "Error! couldn't identify popup menu option.");
                    return false;
                }
                return true;
            });
        });
    }

    private void saveToHistory(String history_id, Model_History modelHistory){
        history_DBref.child(history_id).setValue(modelHistory)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateBookingStatus(String bookingId, String status) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Booking").child(bookingId);
        bookingRef.child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Booking status updated to " + status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update booking status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBooking(String bookingID, Adapter_InUse.MyViewHolder holder) {
        bookings_DBref.child(bookingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_Booking booking = snapshot.getValue(Model_Booking.class);
                    if (booking != null) {
                        holder.booking_price.setText(String.valueOf(booking.getTotalValue()));
                        fetchContactInfo(booking.getContact_id(), holder);
                        holder.checkInDate.setText(booking.getCheckInDate());
                        holder.checkOutDate.setText(booking.getCheckOutDate());
                    } else {
                        Log.e("Adapter_InUse", "Booking is null for In-Use Room: " + bookingID);
                    }
                } else {
                    Log.e("Adapter_InUse", "Booking does not exist for In-Use Room: " + bookingID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_InUse", "Failed to retrieve booking: " + error.getMessage());
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
                        Log.e("Adapter_InUse", "Contact is null for booking: " + contactID);
                    }
                } else {
                    Log.e("Adapter_InUse", "Contact does not exist for booking: " + contactID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_InUse", "Failed to retrieve contact: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return inUseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName, guestName, booking_price, checkInDate, checkOutDate, durationOfStay;
        private ImageButton optionMenu_Btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            guestName = itemView.findViewById(R.id.guestName);
            booking_price = itemView.findViewById(R.id.bookingPrice);
            checkInDate = itemView.findViewById(R.id.checkInDate);
            checkOutDate = itemView.findViewById(R.id.checkOutDate);
            durationOfStay = itemView.findViewById(R.id.durationOfStay);
            optionMenu_Btn = itemView.findViewById(R.id.menubtn);
        }
    }
}
