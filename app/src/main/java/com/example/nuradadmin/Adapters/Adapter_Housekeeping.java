package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_Housekeeping;
import com.example.nuradadmin.R;

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

    public Adapter_Housekeeping(Context context, List<Model_Housekeeping> housekeepingList) {
        this.context = context;
        this.housekeepingList = housekeepingList;
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

            // Display the duration of stay
            String durationText = (days > 0 ? days + " days " : "") +
                    (hours > 0 ? hours + " hours " : "") +
                    (minutes > 0 ? minutes + " minutes" : "");
            holder.requestTime.setText(durationText + " ago");

        } catch (ParseException e) {
            Log.e("Adapter_Housekeeping", "Error parsing request date: " + e.getMessage());
        }
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
