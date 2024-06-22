package com.example.nuradadmin.Activities;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.SideMenu.Activity_SystemManagement;
import com.example.nuradadmin.Adapters.Adapter_Vouchers;
import com.example.nuradadmin.Models.Model_Voucher;
import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_ExpiredVouchers extends AppCompatActivity {
    private ImageView back_icon;
    private TextView title;
    private RecyclerView recyclerView;
    private DatabaseReference activeVouchers_DBref;
    private Adapter_Vouchers adapter;
    private List<Model_Voucher> modelVoucherList = new ArrayList<>();
    private ValueEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expired_vouchers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SystemUIUtil.setupSystemUI(this);

        back_icon = findViewById(R.id.back_icon);
        title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);

        title.setText("Expired Vouchers");

        activeVouchers_DBref = FirebaseDatabase.getInstance().getReference("Expired Vouchers");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Set the adapter after initializing the list
        adapter = new Adapter_Vouchers(Activity_ExpiredVouchers.this, modelVoucherList);
        recyclerView.setAdapter(adapter);

        eventListener = activeVouchers_DBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelVoucherList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Model_Voucher modelVoucher = itemSnapshot.getValue(Model_Voucher.class);
                    if (modelVoucher != null) {
                        modelVoucherList.add(modelVoucher);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        back_icon.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_SystemManagement.class);
            startActivity(i);
            finish();
        });
    }
}