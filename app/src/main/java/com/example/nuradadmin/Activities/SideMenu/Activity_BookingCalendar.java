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
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nuradadmin.Fragments.Fragment_Available;
import com.example.nuradadmin.Fragments.Fragment_Booking;
import com.example.nuradadmin.Fragments.Fragment_History;
import com.example.nuradadmin.Fragments.Fragment_Housekeeping;
import com.example.nuradadmin.Fragments.Fragment_InUse;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.example.nuradadmin.databinding.ActivityBookingCalendarBinding;
import com.google.android.material.navigation.NavigationView;


public class Activity_BookingCalendar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
   private ActivityBookingCalendarBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menu_icon;
    private Toolbar toolbar;
    private TextView title;
    private String moveToFragment = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBookingCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbarMenu);
        menu_icon = findViewById(R.id.menu_icon);
        title = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        // Set the initial fragment (default) to display when the activity is created
        title.setText("Booking");

        Intent intent = getIntent();
        moveToFragment = intent.getStringExtra("Fragment");
        if (moveToFragment != null) {
            switch (moveToFragment) {
                case "Available":
                    title.setText("Available");
                    replaceFragment(new Fragment_Available());
                    updateSelectedNavItem(R.id.available);
                    break;
                case "In Use":
                    title.setText("In Use");
                    replaceFragment(new Fragment_InUse());
                    updateSelectedNavItem(R.id.inUse);
                    break;
                case "Housekeeping":
                    title.setText("Housekeeping");
                    replaceFragment(new Fragment_Housekeeping());
                    updateSelectedNavItem(R.id.housekeeping);
                    break;
                default:
                    title.setText("Booking");
                    replaceFragment(new Fragment_Booking());
                    updateSelectedNavItem(R.id.booking);
                    break;
            }
        } else {
            title.setText("Booking");
            replaceFragment(new Fragment_Booking());
            updateSelectedNavItem(R.id.booking);
        }

        // Bottom Navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Check which menu item is selected and replace the current fragment accordingly
            if (item.getItemId() == R.id.booking) {
                title.setText("Booking");
                replaceFragment(new Fragment_Booking());
            } else if (item.getItemId() == R.id.available){
                title.setText("Available");
                replaceFragment(new Fragment_Available());
            } else if (item.getItemId() == R.id.inUse){
                title.setText("In Use");
                replaceFragment(new Fragment_InUse());
            } else if (item.getItemId() == R.id.history){
                title.setText("History");
                replaceFragment(new Fragment_History());
            }
            return true;
        });

        // Side Navigation
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = menuItem.getItemId();

        if (id == R.id.dashboard_menu) {
            startActivity(new Intent(this, Activity_Dashboard.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.bookingCalendar_menu) {
            drawerLayout.closeDrawer(GravityCompat.START);
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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void updateSelectedNavItem(int itemId) {
        binding.bottomNavigationView.setSelectedItemId(itemId);
    }
}