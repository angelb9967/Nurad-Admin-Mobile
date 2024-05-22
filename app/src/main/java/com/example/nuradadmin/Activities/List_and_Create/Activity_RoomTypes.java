package com.example.nuradadmin.Activities.List_and_Create;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Activities.SideMenu.Activity_SystemManagement;
import com.example.nuradadmin.Adapters.Adapter_RoomType;
import com.example.nuradadmin.Models.Model_RoomType;
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

public class Activity_RoomTypes extends AppCompatActivity {
    private ImageView back_icon;
    private RecyclerView recyclerView;
    private List<Model_RoomType> modelRoomTypeList;
    private DatabaseReference roomType_DBref;
    private ValueEventListener eventListener;
    private TextView title;
    private FloatingActionButton floatingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_types);
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

        title.setText("Room Types");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_RoomTypes.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        modelRoomTypeList = new ArrayList<>();
        Adapter_RoomType adapter = new Adapter_RoomType(Activity_RoomTypes.this, modelRoomTypeList);
        recyclerView.setAdapter(adapter);

        roomType_DBref = FirebaseDatabase.getInstance().getReference("Room Types");

        eventListener = roomType_DBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelRoomTypeList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Model_RoomType modelRoomType = itemSnapshot.getValue(Model_RoomType.class);
                    if (modelRoomType != null) {
                        modelRoomTypeList.add(modelRoomType);
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

        floatingBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, Activity_CreateRoomType.class);
            startActivity(i);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomType_DBref != null && eventListener != null) {
            roomType_DBref.removeEventListener(eventListener);
        }
    }
}
