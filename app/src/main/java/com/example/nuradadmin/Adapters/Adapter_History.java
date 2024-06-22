package com.example.nuradadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.Activity_BookingInvoice;
import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRoom;
import com.example.nuradadmin.Activities.SideMenu.Activity_BookingCalendar;
import com.example.nuradadmin.Models.Model_AddressInfo;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_History;
import com.example.nuradadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Adapter_History extends RecyclerView.Adapter<Adapter_History.MyViewHolder>{
    private Context context;
    private List<Model_History> historyList;
    private DatabaseReference bookings_DBref;
    private DatabaseReference contacts_DBref;
    private DatabaseReference address_DBref;
    public Adapter_History(Context context, List<Model_History> historyList ) {
        this.context = context;
        this.historyList = historyList;
        bookings_DBref = FirebaseDatabase.getInstance().getReference("Booking");
        contacts_DBref = FirebaseDatabase.getInstance().getReference("Contact Information");
        address_DBref = FirebaseDatabase.getInstance().getReference("Address Information");
    }

    @NonNull
    @Override
    public Adapter_History.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new Adapter_History.MyViewHolder(view);
    }

    private ArrayList<String> mapKeysToList(Map<String, String> map) {
        if (map == null) {
            Log.e("mapKeysToList", "Map is null");
            return new ArrayList<>();
        }
        return new ArrayList<>(map.keySet());
    }

    private ArrayList<String> mapValuesToList(Map<String, String> map) {
        if (map == null) {
            Log.e("mapValuesToList", "Map is null");
            return new ArrayList<>();
        }
        return new ArrayList<>(map.values());
    }

    private void showDeleteConfirmationDialog(Model_History history) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Selected Booking History")
                .setMessage("Are you sure you want to delete this data?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteRecord(history.getHistory_id(), "History");
                    historyList.remove(history);
                    notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteRecord(String primaryKey, String path) {
        FirebaseDatabase.getInstance().getReference(path).child(primaryKey)
                .removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Adapter_History", "Record deleted successfully from " + path))
                .addOnFailureListener(e -> Log.e("Adapter_History", "Failed to delete record from " + path + ": " + e.getMessage()));
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_History.MyViewHolder holder, int position) {
        Model_History history = historyList.get(position);
        holder.roomName.setText(history.getRoomName());
        fetchBooking(history.getBooking_id(), holder);

        holder.optionMenu_Btn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu_history, popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.delete) {
                    showDeleteConfirmationDialog(history);
                } else {
                    Log.e("Adapter_History", "Error! couldn't identify popup menu option.");
                    return false;
                }
                return true;
            });
        });

        holder.relativeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Activity_BookingInvoice.class);
            bookings_DBref.child(history.getBooking_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Model_Booking booking = snapshot.getValue(Model_Booking.class);
                        if (booking != null) {
                            Intent intent = new Intent(context, Activity_BookingInvoice.class);
                            intent.putExtra("Booking ID", booking.getBooking_id());
                            intent.putExtra("Room", booking.getRoomTitle() + " (" + booking.getRoom() + ")");
                            intent.putExtra("CheckIn Date", booking.getCheckInDate() + " (" + booking.getCheckInTime() + ")");
                            intent.putExtra("CheckOut Date", booking.getCheckOutDate() + " (" + booking.getCheckOutTime() + ")");
                            intent.putExtra("Total", "₱ " + formatPrice(booking.getTotalValue()));
                            intent.putExtra("Status", booking.getStatus());
                            intent.putExtra("Booking Date", booking.getBookingDate());
                            intent.putExtra("Room Price", "₱ " + formatPrice(booking.getRoomPrice()));
                            intent.putExtra("Adult QTY", booking.getAdultCount() + " x Adult");
                            intent.putExtra("Child QTY", booking.getChildCount() + " x Child");
                            intent.putExtra("Adult Price", "₱ " + formatPrice(booking.getExtraAdultPrice()));
                            intent.putExtra("Child Price", "₱ " + formatPrice(booking.getExtraChildPrice()));
                            intent.putExtra("Notes", booking.getNote());
                            intent.putExtra("SubPrice","₱ " + formatPrice(booking.getSubtotalValue()));
                            intent.putExtra("Tax", "₱ " + formatPrice(booking.getVatValue()));
                            intent.putExtra("DisCode", "₱ " + formatPrice(booking.getVoucherValueValue()));
                            intent.putExtra("DisVal", "₱ " + formatPrice(booking.getVoucherValueValue()));
                            // Convert selected add-ons map to lists for intent extra
                            ArrayList<String> addOnsNames = mapKeysToList(booking.getSelectedAddOns());
                            ArrayList<String> addOnsPrices = mapValuesToList(booking.getSelectedAddOns());
                            intent.putStringArrayListExtra("AddOnsNames", addOnsNames);
                            intent.putStringArrayListExtra("AddOnsPrices", addOnsPrices);
                            System.out.println("Values List: " + addOnsNames);
                            System.out.println("Values List: " + addOnsPrices);
                            contacts_DBref.child(booking.getContact_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Model_ContactInfo contactInfo = snapshot.getValue(Model_ContactInfo.class);
                                        if (contactInfo != null) {
                                                intent.putExtra("Full Name", contactInfo.getPrefix() + " " + contactInfo.getFirstName() + " " + contactInfo.getLastName());
                                                intent.putExtra("Email", contactInfo.getEmail());
                                                intent.putExtra("contactInfo", contactInfo.getMobilePhone() + " or " + contactInfo.getPhone());

                                            address_DBref.child(booking.getAddress_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        Model_AddressInfo addressInfo = snapshot.getValue(Model_AddressInfo.class);
                                                        if (addressInfo != null) {
                                                            String address1 = addressInfo.getAddress1();
                                                            String address2 = addressInfo.getAddress2();
                                                            String city = addressInfo.getCity();
                                                            String region = addressInfo.getRegion();
                                                            String country = addressInfo.getCountry();
                                                            String zip = addressInfo.getZipCode();
                                                            String fullAddress = address1 + "/" + address2 + "; " + city + ", " + region + ", " + country + ", " + zip;
                                                            intent.putExtra("Address", fullAddress);
                                                            context.startActivity(intent);
                                                        } else {
                                                            Log.e("Adapter_History", "Address is null for History Record: " + history.getBooking_id());
                                                        }
                                                    } else {
                                                        Log.e("Adapter_History", "Address does not exist for History Record: " + history.getBooking_id());
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e("Adapter_History", "Failed to retrieve booking: " + error.getMessage());
                                                }
                                            });
                                        } else {
                                            Log.e("Adapter_History", "Contact is null for History Record: " + history.getBooking_id());
                                        }
                                    } else {
                                        Log.e("Adapter_History", "Contact does not exist for History Record: " + history.getBooking_id());
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Adapter_History", "Failed to retrieve booking: " + error.getMessage());
                                }
                            });
                        } else {
                            Log.e("Adapter_History", "Booking is null for History Record: " + history.getBooking_id());
                        }

                    } else {
                        Log.e("Adapter_History", "Booking does not exist for History Record: " + history.getBooking_id());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Adapter_History", "Failed to retrieve booking: " + error.getMessage());
                }
            });
        });
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
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
        private ImageButton optionMenu_Btn;
        private RelativeLayout relativeLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            stay = itemView.findViewById(R.id.stay);
            revenue = itemView.findViewById(R.id.totalPrice);
            optionMenu_Btn = itemView.findViewById(R.id.menubtn);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
