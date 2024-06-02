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

import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.material.navigation.NavigationView;

public class Activity_Language extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menu_icon;
    private Toolbar toolbar;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language);
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

        title.setText("Language");

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
            startActivity(new Intent(this, Activity_BookingCalendar.class));
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
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.logout_menu) {
            // Logout
        } else {
            return false;
        }
        return true;
    }
}