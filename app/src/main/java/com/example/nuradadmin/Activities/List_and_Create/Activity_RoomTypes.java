package com.example.nuradadmin.Activities.List_and_Create;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Activity_RoomTypes extends AppCompatActivity implements Adapter_RoomType.OnLongItemClickListener {
    public static boolean isContextualModeEnabled = false;
    private ImageView back_icon, moveOutDeleteMode;
    private RecyclerView recyclerView;
    private List<Model_RoomType> modelRoomTypeList;
    private DatabaseReference roomType_DBref;
    private ValueEventListener eventListener;
    private TextView title, itemCounter;
    private FloatingActionButton floatingBtn;
    private Adapter_RoomType adapter;
    private View toolbar_normal;
    private View toolbar_deleteMode;
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
        moveOutDeleteMode = findViewById(R.id.back_btn);
        title = findViewById(R.id.title);
        floatingBtn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar_normal = findViewById(R.id.toolbar_normal);
        toolbar_deleteMode = findViewById(R.id.toolbar_deleteMode);
        itemCounter = findViewById(R.id.counter);

        title.setText("Room Types");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_RoomTypes.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        modelRoomTypeList = new ArrayList<>();
        adapter = new Adapter_RoomType(Activity_RoomTypes.this, modelRoomTypeList, this);
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

        moveOutDeleteMode.setOnClickListener(view -> {
            isContextualModeEnabled = false;
            toolbar_deleteMode.setVisibility(View.GONE);
            toolbar_normal.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
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
        isContextualModeEnabled = true;
        itemCounter.setText("0 Item(s) Selected");
        toolbar_normal.setVisibility(View.GONE);
        toolbar_deleteMode.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}
