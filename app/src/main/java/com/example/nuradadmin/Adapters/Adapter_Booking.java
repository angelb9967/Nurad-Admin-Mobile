package com.example.nuradadmin.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Fragments.Fragment_Booking;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_InUse;
import com.example.nuradadmin.Models.Model_Room;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Adapter_Booking extends RecyclerView.Adapter<Adapter_Booking.MyViewHolder> {
    private Context context;
    private List<Model_Booking> bookingList;
    private DatabaseReference contactInfo_DBref;
    private DatabaseReference allRooms_DBref;
    private DatabaseReference recomm_DBref;
    private DatabaseReference inUse_DBref;
    private DatabaseReference availableRooms_DBref;
    private DatabaseReference checkIns_DBref;
    private Fragment_Booking fragment;
    public Adapter_Booking(Context context, List<Model_Booking> bookingList, Fragment_Booking fragment) {
        this.context = context;
        this.bookingList = bookingList;
        contactInfo_DBref = FirebaseDatabase.getInstance().getReference("Contact Information");
        allRooms_DBref = FirebaseDatabase.getInstance().getReference("AllRooms");
        recomm_DBref = FirebaseDatabase.getInstance().getReference("RecommRooms");
        inUse_DBref = FirebaseDatabase.getInstance().getReference("InUse Rooms");
        availableRooms_DBref = FirebaseDatabase.getInstance().getReference("Available Rooms");
        checkIns_DBref = FirebaseDatabase.getInstance().getReference("CheckInsPerDay");
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Adapter_Booking.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull Adapter_Booking.MyViewHolder holder, int position) {
        Model_Booking booking = bookingList.get(position);
        holder.roomName.setText(booking.getRoom());
        holder.status.setText(booking.getStatus());
        fetchContactInfo(booking.getContact_id(), holder);
        fetchRoom(booking.getRoom(), holder);

        holder.optionMenu_Btn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu_booking, popupMenu.getMenu());
            MenuItem checkInItem = popupMenu.getMenu().findItem(R.id.checkIn);
            MenuItem deleteItem = popupMenu.getMenu().findItem(R.id.delete);
            MenuItem cancelBookingItem = popupMenu.getMenu().findItem(R.id.cancelBooking);

            // Check if check-in is allowed
            String checkInDateStr = booking.getCheckInDate();
            String checkInTimeStr = booking.getCheckInTime();
            String checkOutDateStr = booking.getCheckOutDate();
            String checkOutTimeStr = booking.getCheckOutTime();

            // Correct date format
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d/M/yyyy h:mm a", Locale.ENGLISH);
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Manila");

            Calendar checkInDateTime = Calendar.getInstance(timeZone);
            Calendar checkOutDateTime = Calendar.getInstance(timeZone);
            Calendar currentDateTime = Calendar.getInstance(timeZone);

            currentDateTime.setTimeInMillis(System.currentTimeMillis());
            dateTimeFormat.setTimeZone(timeZone);
            String currentTimeStr = dateTimeFormat.format(currentDateTime.getTime());

            try {
                // Combine date and time strings for check-in
                String checkInDateTimeStr = checkInDateStr + " " + checkInTimeStr;
                Date checkInDate = dateTimeFormat.parse(checkInDateTimeStr);
                checkInDateTime.setTime(checkInDate);

                // Combine date and time strings for check-out
                String checkOutDateTimeStr = checkOutDateStr + " " + checkOutTimeStr;
                Date checkOutDate = dateTimeFormat.parse(checkOutDateTimeStr);
                checkOutDateTime.setTime(checkOutDate);
            } catch (ParseException e) {
                Log.e("Adapter_Booking", "Failed to parse date or time: " + e.getMessage());
                checkInItem.setEnabled(false);
                return;
            }

            // Check if the current time is within the allowed check-in period
            boolean isCheckInAllowed = !currentDateTime.before(checkInDateTime) && !currentDateTime.after(checkOutDateTime);
            checkInItem.setEnabled(isCheckInAllowed);

            if (booking.getStatus().equalsIgnoreCase("Checked In")){
                checkInItem.setEnabled(false);
                cancelBookingItem.setEnabled(false);
                deleteItem.setEnabled(false);
            }

            if (booking.getStatus().equalsIgnoreCase("Booked")){
                checkInItem.setEnabled(true);
                cancelBookingItem.setEnabled(true);
                deleteItem.setEnabled(true);
            }

            if (booking.getStatus().equalsIgnoreCase("Cancelled") || booking.getStatus().equalsIgnoreCase("Checked Out")){
                checkInItem.setEnabled(false);
                cancelBookingItem.setEnabled(false);
                deleteItem.setEnabled(true);
            }

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.checkIn) {

                    if (checkInItem.isEnabled()) {
                        booking.setStatus("Checked In");
                        updateBookingStatus(booking.getBooking_id(), "Checked In");

                        // Save the Room to In Use Rooms
                        Model_InUse model_inUse = new Model_InUse(booking.getBooking_id(), booking.getRoom(), currentTimeStr);
                        saveToInUse(booking.getRoom(), model_inUse);

                        // Remove the Room from the Available Rooms because it is now occupied.
                        DatabaseReference nodeReference = availableRooms_DBref.child(booking.getRoom());
                        nodeReference.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Adapter_Booking", "Node deleted successfully.");
                            } else {
                                Log.e("Adapter_Booking", "Failed to delete node.");
                            }
                        });

                        // Add check-in count;
                        addCheckIn();
                    }
                } else if (item.getItemId() == R.id.cancelBooking) {

                    booking.setStatus("Cancelled");
                    updateBookingStatus(booking.getBooking_id(), "Cancelled");

                } else if (item.getItemId() == R.id.delete) {
                    showDeleteConfirmationDialog(booking);
                } else {
                    Log.e("Adapter_Booking", "Error! couldn't identify popup menu option.");
                    return false;
                }
                return true;
            });
        });
    }

    public void addCheckIn() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference todayRef = checkIns_DBref.child(today);

        todayRef.child("count").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentCount = currentData.getValue(Integer.class);
                if (currentCount == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(currentCount + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e("Adapter_Booking", "The system encountered an error: " + error);
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(Model_Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Booking");
        builder.setMessage("Are you sure you want to delete this booking?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBooking(booking);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBooking(Model_Booking booking) {
        deleteRecord(booking.getBooking_id(), "Booking");
        deleteRecord(booking.getContact_id(), "Contact Information");
        deleteRecord(booking.getAddress_id(), "Address Information");
        deleteRecord(booking.getPayment_id(), "Payment Information");

        bookingList.remove(booking);
        notifyDataSetChanged();
    }

    private void deleteRecord(String primaryKey, String path) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path).child(primaryKey);
        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Notify the fragment about the deletion
                    if (path.equalsIgnoreCase("Booking")){
                        fragment.onBookingDeleted();
                    }
                    Log.d("Adapter_Booking", "Record deleted successfully from " + path);
                })
                .addOnFailureListener(e -> {
                    Log.e("Adapter_Booking", "Failed to delete record from " + path + ": " + e.getMessage());
                });
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

    private void fetchContactInfo(String contactID, Adapter_Booking.MyViewHolder holder) {
        contactInfo_DBref.child(contactID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_ContactInfo contactInfo = snapshot.getValue(Model_ContactInfo.class);
                    if (contactInfo != null) {
                        holder.guestName.setText(contactInfo.getFirstName() + " " + contactInfo.getLastName());
                    } else {
                        Log.e("Adapter_Booking", "Contact Information is null for Contact ID: " + contactID);
                    }
                } else {
                    Log.e("Adapter_Booking", "Contact Information is null for Contact ID: " + contactID);
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
                        Log.e("Adapter_Booking", "Room is null for roomId: " + roomID);
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
                                    Log.e("Adapter_Booking", "Room is null for roomId: " + roomID);
                                }
                            } else {
                                Log.e("Adapter_Booking", "Room does not exist for roomId: " + roomID);
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

    private void saveToInUse(String roomName, Model_InUse model_inUse){
        inUse_DBref.child(roomName).setValue(model_inUse)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Model_Booking> newBookingList) {
        this.bookingList = newBookingList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName, roomTitle, guestName, status;
        private ImageButton optionMenu_Btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            guestName = itemView.findViewById(R.id.guestName);
            status = itemView.findViewById(R.id.status);
            optionMenu_Btn = itemView.findViewById(R.id.menubtn);
        }
    }
}
