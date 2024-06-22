package com.example.nuradadmin.Activities.SideMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Activity_OtherRevenue extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Adapter_RevenueCost.OnLongItemClickListener, Adapter_RevenueCost.OnSelectionChangedListener {
    private static final String TAG = "Activity_OtherRevenue";
    public static boolean isContextualModeEnabled = false;
    private ImageView menu_icon, moveOutDeleteMode, delete_icon;
    private DatabaseReference revenueCost_DBref;
    private RecyclerView recyclerView;
    private List<Models_RevenueCost> modelRevenueCostList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ValueEventListener eventListener;
    private Toolbar toolbar;
    private TextView title, itemCounter;
    private Adapter_RevenueCost adapter;
    private FloatingActionButton floatingBtn;
    private View toolbar_normal;
    private View toolbar_deleteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
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
        toolbar_normal = findViewById(R.id.toolbar_normal);
        toolbar_deleteMode = findViewById(R.id.toolbar_deleteMode);
        itemCounter = findViewById(R.id.counter);
        delete_icon = findViewById(R.id.delete_icon);
        moveOutDeleteMode = findViewById(R.id.back_btn);

        setSupportActionBar(toolbar);
        modelRevenueCostList = new ArrayList<>();
        title.setText("Other Revenue & Expenses");

        revenueCost_DBref = FirebaseDatabase.getInstance().getReference("Revenue and Expenses");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_OtherRevenue.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new Adapter_RevenueCost(Activity_OtherRevenue.this, modelRevenueCostList, this, this);
        recyclerView.setAdapter(adapter);

        loadRevenueCostData();

        navigationView.setNavigationItemSelectedListener(this);

        menu_icon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
                Log.d(TAG, "Drawer closed");
            } else {
                drawerLayout.openDrawer(navigationView);
                Log.d(TAG, "Drawer opened");
            }
        });

        floatingBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Activity_CreateRevenueCost.class);
            startActivity(intent);
            Log.d(TAG, "Floating button clicked, starting Activity_CreateRevenueCost");
            finish();
        });

        moveOutDeleteMode.setOnClickListener(view -> {
            isContextualModeEnabled = false;
            toolbar_deleteMode.setVisibility(View.GONE);
            toolbar_normal.setVisibility(View.VISIBLE);
            adapter.clearSelectedItems(); // Clear selected items
            adapter.notifyDataSetChanged();
            updateItemCounter(0);
            Log.d(TAG, "Exited delete mode");
        });

        delete_icon.setOnClickListener(view -> {
            List<Models_RevenueCost> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delete icon clicked, but no items selected");
            } else {
                showDeleteItemDialog(this);
                Log.d(TAG, "Delete icon clicked, showing delete dialog");
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Log.d(TAG, "Back pressed, closing drawer");
                } else {
                    finish();
                    Log.d(TAG, "Back pressed, finishing activity");
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // show or hide floating button while scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && floatingBtn.isShown()) {
                    floatingBtn.hide();
                } else if (dy < 0 && !floatingBtn.isShown()) {
                    floatingBtn.show();
                }
            }
        });
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
                        Log.d(TAG, "Data added: " + modelRevenueCost.toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Data loading cancelled or failed: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (revenueCost_DBref != null && eventListener != null) {
            revenueCost_DBref.removeEventListener(eventListener);
            Log.d(TAG, "Event listener removed");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isContextualModeEnabled = false;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Activity stopped, notifying adapter");
        }
    }

    public void onLongItemClick(int position) {
        if (!adapter.getSelectedItems().contains(position)) {
            isContextualModeEnabled = true;
            updateItemCounter(1);
            toolbar_normal.setVisibility(View.GONE);
            toolbar_deleteMode.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Item long clicked, entering contextual mode");
        }
    }

    public void onSelectionChanged(int count) {
        updateItemCounter(count);
        Log.d(TAG, "Selection changed, item count: " + count);
    }

    private void updateItemCounter(int count) {
        itemCounter.setText(count + " Item(s) Selected");
        Log.d(TAG, "Item counter updated: " + count);
    }

    private void showDeleteItemDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete the selected item(s)?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Delete confirmed, deleting items");
                // Delete selected item(s)
                for (Models_RevenueCost revenueCost : adapter.getSelectedItems()) {
                    revenueCost_DBref.child(revenueCost.getPrimaryKey()).removeValue();
                    Log.d(TAG, "Item deleted: " + revenueCost.getPrimaryKey());
                }
                // Reset contextual mode and update UI
                isContextualModeEnabled = false;
                toolbar_deleteMode.setVisibility(View.GONE);
                toolbar_normal.setVisibility(View.VISIBLE);
                adapter.clearSelectedItems();
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Log.d(TAG, "Delete cancelled");
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = menuItem.getItemId();
        Log.d(TAG, "Navigation item selected, id: " + id);

        if (id == R.id.dashboard_menu) {
            startActivity(new Intent(this, Activity_Dashboard.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.bookingCalendar_menu) {
            startActivity(new Intent(this, Activity_BookingCalendar.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.housekeeping_menu) {
            startActivity(new Intent(this, Activity_Housekeeping.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.otherRevenue_menu) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.systemManagement_menu) {
            startActivity(new Intent(this, Activity_SystemManagement.class));
            overridePendingTransition(0, 0);
        } else {
            return false;
        }
        return true;
    }
}
