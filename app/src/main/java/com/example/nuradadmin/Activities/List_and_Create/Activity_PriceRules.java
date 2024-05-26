package com.example.nuradadmin.Activities.List_and_Create;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

public class Activity_PriceRules extends AppCompatActivity {
    private ImageView back_icon;
    private TextView title;
    private RecyclerView recyclerView;
    private List<Model_PriceRule> modelPriceRulesList;
    private DatabaseReference priceRules_DBref;
    private ValueEventListener eventListener;
    private FloatingActionButton floatingBtn;
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
        title = findViewById(R.id.title);
        floatingBtn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);

        title.setText("Price Rules");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_PriceRules.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        modelPriceRulesList = new ArrayList<>();
        Adapter_PriceRule adapter = new Adapter_PriceRule(Activity_PriceRules.this, modelPriceRulesList);
        recyclerView.setAdapter(adapter);

        priceRules_DBref = FirebaseDatabase.getInstance().getReference("Price Rules");

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

        floatingBtn.setOnClickListener(view ->{
            Intent i = new Intent(this, Activity_CreatePriceRules.class);
            startActivity(i);
            finish();
        });
    }
}