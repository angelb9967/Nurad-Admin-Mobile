package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_Housekeeping;
import com.example.nuradadmin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Adapter_Housekeeping extends RecyclerView.Adapter<Adapter_Housekeeping.MyViewHolder>{
    private Context context;
    private List<Model_Housekeeping> housekeepingList;
    private DatabaseReference housekeeping_DBref;
    public Adapter_Housekeeping(Context context, List<Model_Housekeeping> housekeepingList) {
        this.context = context;
        this.housekeepingList = housekeepingList;
        housekeeping_DBref = FirebaseDatabase.getInstance().getReference("Housekeeping");
    }

    @NonNull
    @Override
    public Adapter_Housekeeping.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_housekeeping, parent, false);
        return new Adapter_Housekeeping.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Housekeeping.MyViewHolder holder, int position) {
        Model_Housekeeping housekeeping = housekeepingList.get(position);
        holder.roomName.setText(housekeeping.getRoomName());
        holder.roomStatus.setText(housekeeping.getStatus());

        String requestTime = housekeeping.getRequestTime();
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy h:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Assuming the original date is in Manila time

        try {
            Date requestDate = formatter.parse(requestTime);
            Log.d("Adapter_InUse", "Parsed check-in date: " + requestDate);

            // Get the current date and time in Manila time zone
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
            Log.d("Adapter_InUse", "Current date and time in Manila: " + now.getTime());

            // Calculate the duration between the request date and the current date
            long durationMillis = now.getTimeInMillis() - requestDate.getTime();
            long days = durationMillis / (1000 * 60 * 60 * 24);
            long hours = (durationMillis / (1000 * 60 * 60)) % 24;
            long minutes = (durationMillis / (1000 * 60)) % 60;
            long seconds = (durationMillis / 1000) % 60;

            // Display the duration of stay
            String durationText = (days > 0 ? days + " days " : "") +
                    (hours > 0 ? hours + " hours " : "") +
                    (minutes > 0 ? minutes + " minutes " : "") +
                    (days == 0 && hours == 0 && minutes == 0 && seconds > 0 ? seconds + " seconds " : "");
            holder.requestTime.setText(durationText + "ago");

        } catch (ParseException e) {
            Log.e("Adapter_Housekeeping", "Error parsing request date: " + e.getMessage());
        }

        holder.optionMenu_Btn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu_housekeeping, popupMenu.getMenu());

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.cleaned) {
                    housekeeping.setStatus("Cleaned");
                    updateAvailableRoomStatus(housekeeping.getRoomName(), "Cleaned");

                    // Remove the Request from the Housekeeping if done cleaning.
                    DatabaseReference nodeReference = housekeeping_DBref.child(housekeeping.getRoomName());
                    nodeReference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Adapter_Housekeeping", "Node deleted successfully.");
                        } else {
                            Log.e("Adapter_Housekeeping", "Failed to delete node.");
                        }
                    });
                } else {
                    Log.e("Adapter_Housekeeping", "Error! couldn't identify popup menu option.");
                    return false;
                }
                return true;
            });
        });
    }

    private void updateAvailableRoomStatus(String roomName, String status) {
        DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("Available Rooms").child(roomName);

        // Get the current date and time in Manila time zone
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
        long currentMillis = System.currentTimeMillis(); // Current time in milliseconds

        SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy h:mm a", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String currentDateTime = formatter.format(currentMillis);

        availableRef.child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Adapter_AvailableRooms", "Room status updated to " + status);
            } else {
                Log.d("Adapter_AvailableRooms", "Failed to update room status " + status);
            }
        });

        availableRef.child("lastCleaned").setValue(currentDateTime).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Adapter_AvailableRooms", "Room last cleaned time updated to " + currentDateTime);
            } else {
                Log.d("Adapter_AvailableRooms", "Failed to update room last cleaned time to " + currentDateTime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return housekeepingList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName, requestTime, roomStatus;
        private ImageButton optionMenu_Btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            requestTime = itemView.findViewById(R.id.requestTime);
            roomStatus = itemView.findViewById(R.id.roomStatus);
            optionMenu_Btn = itemView.findViewById(R.id.menubtn);
        }
    }
}
