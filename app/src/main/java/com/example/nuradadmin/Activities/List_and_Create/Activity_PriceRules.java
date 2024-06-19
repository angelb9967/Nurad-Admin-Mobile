package com.example.nuradadmin.Activities.List_and_Create;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.SideMenu.Activity_SystemManagement;
import com.example.nuradadmin.Adapters.Adapter_PriceRule;
import com.example.nuradadmin.Models.Model_PriceRule;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_PriceRules extends AppCompatActivity implements Adapter_PriceRule.OnLongItemClickListener, Adapter_PriceRule.OnSelectionChangedListener{
    public static boolean isContextualModeEnabled = false;
    private ImageView back_icon, moveOutDeleteMode, delete_icon;
    private TextView title, itemCounter;
    private RecyclerView recyclerView;
    private List<Model_PriceRule> modelPriceRulesList;
    private DatabaseReference priceRules_DBref;
    private DatabaseReference allRooms_DBref;
    private DatabaseReference recommendedRooms_DBref;
    private ValueEventListener eventListener;
    private FloatingActionButton floatingBtn;
    private Adapter_PriceRule adapter;
    private View toolbar_normal;
    private View toolbar_deleteMode;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_price_rules);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        moveOutDeleteMode = findViewById(R.id.back_btn);
        title = findViewById(R.id.title);
        floatingBtn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar_normal = findViewById(R.id.toolbar_normal);
        toolbar_deleteMode = findViewById(R.id.toolbar_deleteMode);
        itemCounter = findViewById(R.id.counter);
        delete_icon = findViewById(R.id.delete_icon);

        title.setText("Price Rules");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_PriceRules.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        modelPriceRulesList = new ArrayList<>();
        adapter = new Adapter_PriceRule(Activity_PriceRules.this, modelPriceRulesList, this, this);
        recyclerView.setAdapter(adapter);

        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");
        allRooms_DBref = FirebaseDatabase.getInstance().getReference("AllRooms");
        recommendedRooms_DBref = FirebaseDatabase.getInstance().getReference("RecommRooms");

        eventListener = priceRules_DBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPriceRulesList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Model_PriceRule modelPriceRule = itemSnapshot.getValue(Model_PriceRule.class);
                    if (modelPriceRule != null) {
                        modelPriceRulesList.add(modelPriceRule);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        back_icon.setOnClickListener(View -> {
            Intent i = new Intent(this, Activity_SystemManagement.class);
            startActivity(i);
            finish();
        });

        moveOutDeleteMode.setOnClickListener(view -> {
            isContextualModeEnabled = false;
            toolbar_deleteMode.setVisibility(View.GONE);
            toolbar_normal.setVisibility(View.VISIBLE);
            adapter.clearSelectedItems(); // Clear selected items
            adapter.notifyDataSetChanged();
            updateItemCounter(0);
        });

        delete_icon.setOnClickListener(view -> {
            List<Model_PriceRule> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show();
            } else {
                checkPriceRulesUsage(selectedItems);
            }
        });

        floatingBtn.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_CreatePriceRules.class);
            startActivity(i);
            finish();
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (priceRules_DBref != null && eventListener != null) {
            priceRules_DBref.removeEventListener(eventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isContextualModeEnabled = false;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLongItemClick(int position) {
        if (!adapter.getSelectedItems().contains(position)) {
            isContextualModeEnabled = true;
            updateItemCounter(1);
            toolbar_normal.setVisibility(View.GONE);
            toolbar_deleteMode.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        updateItemCounter(count);
    }

    private void updateItemCounter(int count) {
        itemCounter.setText(count + " Item(s) Selected");
    }

    private void checkPriceRulesUsage(List<Model_PriceRule> selectedItems) {
        final int[] checksCompleted = {0};
        final boolean[] isInUse = {false};

        for (Model_PriceRule priceRule : selectedItems) {
            checkPriceRuleUsageInDatabase(allRooms_DBref, priceRule, selectedItems.size(), checksCompleted, isInUse);
            checkPriceRuleUsageInDatabase(recommendedRooms_DBref, priceRule, selectedItems.size(), checksCompleted, isInUse);
        }
    }

    private void checkPriceRuleUsageInDatabase(DatabaseReference databaseRef, Model_PriceRule priceRule, int totalSelectedItems, int[] checksCompleted, boolean[] isInUse) {
        databaseRef.orderByChild("priceRule").equalTo(priceRule.getRuleName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checksCompleted[0]++;
                if (snapshot.exists()) {
                    isInUse[0] = true;
                }
                if (checksCompleted[0] == totalSelectedItems * 2) {
                    handlePriceRulesUsage(isInUse[0], totalSelectedItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void handlePriceRulesUsage(boolean isInUse, int totalSelectedItems) {
        if (isInUse) {
            Toast.makeText(this, "The item/s can't be deleted because they are in use.", Toast.LENGTH_SHORT).show();
        } else {
            showDeleteItemDialog(this);
        }
    }

    private void showDeleteItemDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete the selected item(s)?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete selected item(s)
                for (Model_PriceRule priceRule : adapter.getSelectedItems()) {
                    priceRules_DBref.child(priceRule.getRuleName()).removeValue();
                }
                // Reset contextual mode and update UI
                isContextualModeEnabled = false;
                toolbar_deleteMode.setVisibility(View.GONE);
                toolbar_normal.setVisibility(View.VISIBLE);
                adapter.clearSelectedItems();
                adapter.notifyDataSetChanged();
                floatingBtn.show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }
}
