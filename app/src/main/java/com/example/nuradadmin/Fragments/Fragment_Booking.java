package com.example.nuradadmin.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateBooking;
import com.example.nuradadmin.Adapters.Adapter_AvailableRooms;
import com.example.nuradadmin.Adapters.Adapter_Booking;
import com.example.nuradadmin.Models.Model_AvailableRooms;
import com.example.nuradadmin.Models.Model_Booking;
import com.example.nuradadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Fragment_Booking extends Fragment {
    private FloatingActionButton floatingBtn;
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private List<Model_Booking> modelBookingList;
    private DatabaseReference booking_DBref;
    private Adapter_Booking adapter;
    private TextView date, indicator;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Fragment_Booking() {
        // Required empty public constructor
    }

    public static Fragment_Booking newInstance(String param1, String param2) {
        Fragment_Booking fragment = new Fragment_Booking();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__booking, container, false);

        floatingBtn = view.findViewById(R.id.floatingActionButton);
        calendarView = view.findViewById(R.id.calendarView);
        date = view.findViewById(R.id.date_Etxt);
        indicator = view.findViewById(R.id.indicator_Etxt);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the current date
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // Set the default selected day to the current date
        calendarView.setDate(currentCalendar.getTimeInMillis(), false, true);

        // Format the current date [TextView label on top of the Recycler View]
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        String formattedMonth = monthFormat.format(currentCalendar.getTime());

        // Set the formatted date to the date EditText
        date.setText(formattedMonth + " " + currentDay);

        modelBookingList = new ArrayList<>();
        adapter = new Adapter_Booking(getContext(), modelBookingList);
        recyclerView.setAdapter(adapter);

        booking_DBref = FirebaseDatabase.getInstance().getReference("Booking");
        booking_DBref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelBookingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model_Booking booking = snapshot.getValue(Model_Booking.class);
                    modelBookingList.add(booking);
                }
                adapter.notifyDataSetChanged();
                // Set the default selected day to the current date
                filterBookingsByDate(currentYear, currentMonth, currentDay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        floatingBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), Activity_CreateBooking.class);
            startActivity(i);
        });

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Format the selected date
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            SimpleDateFormat selectedMonthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            String formattedSelectedMonth = selectedMonthFormat.format(selectedCalendar.getTime());

            // Set the formatted date to the date EditText
            date.setText(formattedSelectedMonth + " " + dayOfMonth);

            setIndicatorText(year, month, dayOfMonth);

            // Filter bookings based on the selected date
            filterBookingsByDate(year, month + 1, dayOfMonth);
        });
        return view;
    }

    private void filterBookingsByDate(int year, int month, int dayOfMonth) {
        List<Model_Booking> filteredList = new ArrayList<>();
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month - 1, dayOfMonth);
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        for (Model_Booking booking : modelBookingList) {
            // Assuming Model_Booking has getCheckInDate() and getCheckOutDate() methods
            Calendar checkinCalendar = Calendar.getInstance();
            Calendar checkoutCalendar = Calendar.getInstance();

            try {
                // Assuming the dates are stored in the format "dd/MM/yyyy"
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                checkinCalendar.setTime(Objects.requireNonNull(sdf.parse(booking.getCheckInDate())));
                checkinCalendar.set(Calendar.HOUR_OF_DAY, 0);
                checkinCalendar.set(Calendar.MINUTE, 0);
                checkinCalendar.set(Calendar.SECOND, 0);
                checkinCalendar.set(Calendar.MILLISECOND, 0);

                checkoutCalendar.setTime(Objects.requireNonNull(sdf.parse(booking.getCheckOutDate())));
                checkoutCalendar.set(Calendar.HOUR_OF_DAY, 0);
                checkoutCalendar.set(Calendar.MINUTE, 0);
                checkoutCalendar.set(Calendar.SECOND, 0);
                checkoutCalendar.set(Calendar.MILLISECOND, 0);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("Fragment_Booking", e.toString());
            }

            if ((selectedCalendar.equals(checkinCalendar) || selectedCalendar.after(checkinCalendar))
                    && (selectedCalendar.equals(checkoutCalendar) || selectedCalendar.before(checkoutCalendar))) {
                filteredList.add(booking);
            }
        }

        adapter.updateList(filteredList);
    }

    private void setIndicatorText(int year, int month, int dayOfMonth) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // Check if the selected date is today, yesterday, or tomorrow
        if (year == currentYear && month == currentMonth && dayOfMonth == currentDayOfMonth) {
            indicator.setText("Today");
        } else if (year == currentYear && month == currentMonth && dayOfMonth == currentDayOfMonth - 1) {
            indicator.setText("Yesterday");
        } else if (year == currentYear && month == currentMonth && dayOfMonth == currentDayOfMonth + 1) {
            indicator.setText("Tomorrow");
        } else {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            String dayOfWeek = dayFormat.format(selectedCalendar.getTime());
            indicator.setText(dayOfWeek);
        }
    }
}