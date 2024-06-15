package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_AvailableRooms;
import com.example.nuradadmin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Adapter_AvailableRooms extends RecyclerView.Adapter<Adapter_AvailableRooms.MyViewHolder> {
    private Context context;
    private List<Model_AvailableRooms> availableRoomsList;

    public Adapter_AvailableRooms(Context context, List<Model_AvailableRooms> availableRoomsList) {
        this.context = context;
        this.availableRoomsList = availableRoomsList;
    }

    @NonNull
    @Override
    public Adapter_AvailableRooms.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available, parent, false);
        return new Adapter_AvailableRooms.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_AvailableRooms.MyViewHolder holder, int position) {
        Model_AvailableRooms availableRooms = availableRoomsList.get(position);
        holder.roomName.setText(availableRooms.getRoomName());
        holder.lastDateUsed.setText(availableRooms.getLastDateUsed());
        if(availableRooms.getLastTimeUsed().equalsIgnoreCase("Not used yet")){
            holder.lastTimeUsed.setText("");
            holder.indicator.setText("Not used");
        }else{
            holder.lastTimeUsed.setText(availableRooms.getLastTimeUsed());
            // Combine lastDateUsed and lastTimeUsed into a single datetime string
            String lastDateTimeUsedStr = availableRooms.getLastDateUsed() + " " + availableRooms.getLastTimeUsed();
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy h:mm a", Locale.ENGLISH); // Adjusted date format
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
            try {
                Date lastDateTimeUsed = sdf.parse(lastDateTimeUsedStr);
                long lastUsed = lastDateTimeUsed.getTime();
                long now = System.currentTimeMillis();

                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(lastUsed, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
                holder.indicator.setText(timeAgo);
            } catch (ParseException e) {
                e.printStackTrace();
                holder.indicator.setText("Unknown time");
            }
        }

        holder.status.setText(availableRooms.getStatus());

        String stats = (String) holder.status.getText();
        if(stats.equalsIgnoreCase("Not cleaned yet")){
            holder.icon.setImageResource(R.drawable.cross_small);
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if(stats.equalsIgnoreCase("Cleaned")){
            holder.icon.setImageResource(R.drawable.check);
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            holder.status.setText("null");
            holder.icon.setImageResource(R.drawable.circle);
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.lightgray));
        }

        holder.optionMenu_Btn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu_available, popupMenu.getMenu());

            popupMenu.show();

//            popupMenu.setOnMenuItemClickListener(item -> {
//                if (item.getItemId() == R.id.edit) {
//                    Toast.makeText(context, "You clicked edit", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.e("Adapter_Booking", "Error! couldn't identify popup menu option.");
//                    return false;
//                }
//                return true;
//            });
        });
    }

    @Override
    public int getItemCount() {
        return availableRoomsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView roomName, lastDateUsed, lastTimeUsed, indicator, status;
        private ImageView icon;
        private ImageButton optionMenu_Btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            lastDateUsed = itemView.findViewById(R.id.lastDateUsed);
            lastTimeUsed = itemView.findViewById(R.id.lastTimeUsed);
            icon = itemView.findViewById(R.id.icon2);
            indicator = itemView.findViewById(R.id.indicator);
            status = itemView.findViewById(R.id.CleanStatus);
            optionMenu_Btn = itemView.findViewById(R.id.menubtn);
        }
    }
}
