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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.List_and_Create.Activity_CreateRevenueCost;
import com.example.nuradadmin.Adapters.Adapter_RevenueCost;
import com.example.nuradadmin.Models.Models_RevenueCost;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_OtherRevenue extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference revenueCost_DBref;
    private RecyclerView recyclerView;
    private List<Models_RevenueCost> modelRevenueCostList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ValueEventListener eventListener;
    private ImageView menu_icon;
    private Toolbar toolbar;
    private TextView title;
    private Adapter_RevenueCost adapter;
    private FloatingActionButton floatingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_revenue);
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
        floatingBtn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);

        setSupportActionBar(toolbar);
        modelRevenueCostList = new ArrayList<>();
        title.setText("Other Revenue & Expenses");

        revenueCost_DBref = FirebaseDatabase.getInstance().getReference("Revenue and Expenses");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_OtherRevenue.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new Adapter_RevenueCost(Activity_OtherRevenue.this, modelRevenueCostList);
        recyclerView.setAdapter(adapter);

        loadRevenueCostData();

        navigationView.setNavigationItemSelectedListener(this);

        menu_icon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        floatingBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Activity_CreateRevenueCost.class);
            startActivity(intent);
            finish();
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

    private void loadRevenueCostData() {
        eventListener = revenueCost_DBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelRevenueCostList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Models_RevenueCost modelRevenueCost = itemSnapshot.getValue(Models_RevenueCost.class);
                    if (modelRevenueCost != null) {
                        modelRevenueCostList.add(modelRevenueCost);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
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
            drawerLayout.closeDrawer(GravityCompat.START);
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
