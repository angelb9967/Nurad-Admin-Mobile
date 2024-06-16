package com.example.nuradadmin.Activities.SideMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
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

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRoom;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView title, todaysCheckedIn_TxtView, available_TxtView, inUse_TxtView, housekeeping_TxtView;
    private DatabaseReference checkInsPerDay_DBref, availableRooms_DBref, inUse_DBref, housekeeping_DBref;
    private ConstraintLayout bookingCalendar, available, inUse, housekeeping;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menu_icon;
    private Toolbar toolbar;
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

        setSupportActionBar(toolbar);
        title.setText("Dashboard");

        navigationView.setNavigationItemSelectedListener(this);

        menu_icon.setOnClickListener(View -> {
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

        bookingCalendar.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            startActivity(i);
            finish();
        });

        available.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            i.putExtra("Fragment", "Available");
            startActivity(i);
            finish();
        });

        inUse.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_BookingCalendar.class);
            i.putExtra("Fragment", "In Use");
            startActivity(i);
            finish();
        });

        housekeeping.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_Housekeeping.class);
            startActivity(i);
            finish();
        });
    }

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
        }  else if (id == R.id.language_menu) {
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
