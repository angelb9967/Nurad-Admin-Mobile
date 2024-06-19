package com.example.nuradadmin.Activities.SideMenu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Activity_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView title, todaysCheckedIn_TxtView, available_TxtView, inUse_TxtView, housekeeping_TxtView;
    private DatabaseReference checkInsPerDay_DBref, availableRooms_DBref, inUse_DBref, housekeeping_DBref;
    private ConstraintLayout bookingCalendar, available, inUse, housekeeping;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menu_icon;
    private Toolbar toolbar;
    private BarChart barChart;
    private Spinner filter_spinner;
    private ArrayList<String> filter_list;
    private ArrayAdapter filter_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        // Initialize Views
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbarMenu);
        menu_icon = findViewById(R.id.menu_icon);
        title = findViewById(R.id.title);
        todaysCheckedIn_TxtView = findViewById(R.id.todaysCheckedIn_TextView);
        available_TxtView = findViewById(R.id.available_TextView);
        inUse_TxtView = findViewById(R.id.inUse_TextView);
        housekeeping_TxtView = findViewById(R.id.housekeeping_TextView);
        bookingCalendar = findViewById(R.id.moveToBookingCalendar);
        available = findViewById(R.id.moveToAvailable);
        inUse = findViewById(R.id.moveToInUse);
        housekeeping = findViewById(R.id.moveToHousekeeping);
        barChart = findViewById(R.id.barChart);
        filter_spinner = findViewById(R.id.spinner);

        setSupportActionBar(toolbar);
        title.setText("Dashboard");

        navigationView.setNavigationItemSelectedListener(this);

        menu_icon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Initialize Firebase
        checkInsPerDay_DBref = FirebaseDatabase.getInstance().getReference("CheckInsPerDay");
        availableRooms_DBref = FirebaseDatabase.getInstance().getReference("Available Rooms");
        inUse_DBref = FirebaseDatabase.getInstance().getReference("InUse Rooms");
        housekeeping_DBref = FirebaseDatabase.getInstance().getReference("Housekeeping");

        getDBCount(availableRooms_DBref, available_TxtView);
        getDBCount(inUse_DBref, inUse_TxtView);
        getDBCount(housekeeping_DBref, housekeeping_TxtView);
        getTodayCheckInCount();

        bookingCalendar.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            startActivity(i);
            finish();
        });

        available.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            i.putExtra("Fragment", "Available");
            startActivity(i);
            finish();
        });

        inUse.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            i.putExtra("Fragment", "In Use");
            startActivity(i);
            finish();
        });

        housekeeping.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_Housekeeping.class);
            startActivity(i);
            finish();
        });

        setupSpinner();
    }

    private void setupSpinner() {
        String[] value = {"Day", "Month", "Year"};
        filter_list = new ArrayList<>(Arrays.asList(value));
        filter_adapter = new ArrayAdapter(this, R.layout.style_spinner_smaller, filter_list);
        filter_spinner.setAdapter(filter_adapter);

        filter_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        filter_spinner.setAdapter(filter_adapter);

        // Set listener to handle spinner item selection
        filter_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = filter_list.get(position);
                switch (selectedItem) {
                    case "Day":
                        // Show data for last 7 days
                        setupDailyStatistics();
                        break;
                    case "Month":
                        // Show data for current month
//                        setupMonthlyStatistics();
                        break;
                    case "Year":
                        // Show data for current year
//                        setupYearlyStatistics();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection if needed
            }
        });
    }

    private void setupDailyStatistics() {
        // Fetch data for the last 7 days
        SparseArray<BarEntry> tempEntries = new SparseArray<>();

        // Get the current date and time in the Asia/Manila time zone
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.getDefault());

        // Set the time zone to Asia/Manila
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        for (int i = 0; i <= 6; i++) {
            // Format the date
            String formattedDate = dateFormatter.format(cal.getTime());
            int currentDay = Integer.parseInt(dayFormatter.format(cal.getTime())); // Get the current day as an int

            DatabaseReference dailyRef = checkInsPerDay_DBref.child(formattedDate).child("count");
            final int finalI = i;
            dailyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = 0;
                    if (snapshot.exists()) {
                        count = snapshot.getValue(Long.class);
                    }
                    tempEntries.put(finalI, new BarEntry(currentDay, count));

                    // Ensure all entries are fetched before updating the chart
                    if (tempEntries.size() == 7) {
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        for (int j = 0; j <= 6; j++) {
                            entries.add(tempEntries.get(j));
                        }
                        updateChart(entries);
                    }

                    // Log the x and y values of the BarEntry
                    Log.d("DEBUG", "BarEntry added: x = " + currentDay + ", y = " + count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tempEntries.put(finalI, new BarEntry(currentDay, 0));
                    Log.e("Firebase", "Error fetching data for index: " + finalI, error.toException());

                    if (tempEntries.size() == 7) { // Last entry fetched
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        for (int j = 0; j <= 6; j++) {
                            entries.add(tempEntries.get(j));
                        }
                        updateChart(entries);
                    }
                }
            });

            // Move to previous day
            cal.add(Calendar.DATE, -1);
        }
    }

    private void updateChart(ArrayList<BarEntry> entries) {
        // Ensure entries are not null and not empty before updating the chart
        if (entries != null && !entries.isEmpty()) {
            BarDataSet dataSet = new BarDataSet(entries, "Data");
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(12f);

            BarData data = new BarData(dataSet);
            barChart.getDescription().setText("No. of Check-ins");
            barChart.setData(data);

            // Log each entry in the chart
            for (BarEntry entry : entries) {
                Log.d("DEBUG", "Chart Entry: x = " + entry.getX() + ", y = " + entry.getY());
            }

            // Notify and refresh chart
            barChart.notifyDataSetChanged();
            barChart.invalidate();
        } else {
            // Handle case where entries list is empty or null
            Log.w("DEBUG", "No data to display in chart.");
            barChart.clear(); // Optionally clear existing data
            barChart.invalidate(); // Refresh chart
        }
    }

//    private void setupYearlyStatistics() {
//        ArrayList<BarEntry> entries = new ArrayList<>();
//
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MONTH, Calendar.JANUARY); // Set to the first month of the year
//
//        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
//            cal.set(Calendar.MONTH, i);
//            Date startDate = cal.getTime();
//            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Set to last day of the month
//            Date endDate = cal.getTime();
//
//            // Example path assuming monthly data is stored under a "MonthlyCounts" node
//            DatabaseReference monthlyRef = FirebaseDatabase.getInstance()
//                    .getReference("MonthlyCounts")
//                    .child(new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(startDate));
//
//            final int finalI = i;
//            monthlyRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    long totalCount = 0;
//                    for (DataSnapshot monthSnapshot : snapshot.getChildren()) {
//                        long count = monthSnapshot.getValue(Long.class);
//                        totalCount += count;
//                    }
//
//                    // Add entry only if data is available
//                    entries.add(new BarEntry(finalI, totalCount));
//
//                    // Update chart if all entries are fetched
//                    if (finalI == Calendar.DECEMBER) {
//                        updateChart(entries);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle onCancelled event
//                    entries.add(new BarEntry(finalI, 0));
//
//                    // Update chart if all entries are fetched
//                    if (finalI == Calendar.DECEMBER) {
//                        updateChart(entries);
//                    }
//                }
//            });
//
//            cal.add(Calendar.MONTH, 1); // Move to the next month
//        }
//    }

    private void getDBCount(DatabaseReference database, TextView textview) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                if (snapshot.exists()) {
                    count = snapshot.getChildrenCount();
                }
                textview.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textview.setText("0");
            }
        });
    }

    private void getTodayCheckInCount() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference todayRef = checkInsPerDay_DBref.child(today).child("count");

        todayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                if (snapshot.exists()) {
                    count = snapshot.getValue(Long.class);
                }
                todaysCheckedIn_TxtView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                todaysCheckedIn_TxtView.setText("0");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = menuItem.getItemId();

        if (id == R.id.dashboard_menu) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.bookingCalendar_menu) {
            startActivity(new Intent(this, Activity_BookingCalendar.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.housekeeping_menu) {
            startActivity(new Intent(this, Activity_Housekeeping.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.otherRevenue_menu) {
            startActivity(new Intent(this, Activity_OtherRevenue.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.systemManagement_menu) {
            startActivity(new Intent(this, Activity_SystemManagement.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.statistics_menu) {
            startActivity(new Intent(this, Activity_Statistics.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.language_menu) {
            startActivity(new Intent(this, Activity_Language.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.logout_menu) {
            // Logout
        } else {
            return false;
        }
        return true;
    }
}
